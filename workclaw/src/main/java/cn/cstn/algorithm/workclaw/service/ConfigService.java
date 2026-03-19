package cn.cstn.algorithm.workclaw.service;

import cn.cstn.algorithm.workclaw.config.WorkClawProperties;
import cn.cstn.algorithm.workclaw.model.McpServerConfig;
import cn.cstn.algorithm.workclaw.model.ModelConfig;
import cn.cstn.algorithm.workclaw.model.SkillConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置管理服务
 * <p>
 * 管理 Skill 和 MCP Server 的配置，支持增删改查，并持久化到本地 JSON 文件。
 * </p>
 *
 * @author zhaohuiqiang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigService {

  private final WorkClawProperties properties;

  private final Map<String, SkillConfig> skills = new ConcurrentHashMap<>();
  private final Map<String, McpServerConfig> mcpServers = new ConcurrentHashMap<>();
  private final Map<String, ModelConfig> models = new ConcurrentHashMap<>();

  @PostConstruct
  public void init() {
    try {
      String configDir = properties.getConfigDir();
      Path dir = Paths.get(configDir);
      if (!Files.exists(dir)) {
        Files.createDirectories(dir);
      }
      loadSkills();
      loadMcpServers();
      loadModels();
      // 加载预置模型（仅当模型列表为空时写入）
      initPresetModels();
      // 如果没有任何Skill，创建一个默认Skill
      if (skills.isEmpty()) {
        SkillConfig defaultSkill = SkillConfig.builder()
          .id("default")
          .name("默认助手")
          .description("通用智能助手，可以回答各种问题")
          .systemPrompt("你是一个有用的智能助手。请用中文回答用户的问题。")
          .mcpServerIds(new ArrayList<>())
          .enabled(true)
          .build();
        skills.put(defaultSkill.getId(), defaultSkill);
        saveSkills();
      }
      log.info("配置加载完成，Skills: {}, MCP Servers: {}, Models: {}", skills.size(), mcpServers.size(), models.size());
    } catch (IOException e) {
      log.error("配置初始化失败", e);
    }
  }

  // ==================== Skill 管理 ====================

  public List<SkillConfig> listSkills() {
    return new ArrayList<>(skills.values());
  }

  public Optional<SkillConfig> getSkill(String id) {
    return Optional.ofNullable(skills.get(id));
  }

  public SkillConfig saveSkill(SkillConfig skill) {
    if (skill.getId() == null || skill.getId().isBlank()) {
      skill.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 12));
    }
    skills.put(skill.getId(), skill);
    saveSkills();
    return skill;
  }

  public boolean deleteSkill(String id) {
    if ("default".equals(id)) {
      return false; // 不允许删除默认Skill
    }
    SkillConfig removed = skills.remove(id);
    if (removed != null) {
      saveSkills();
      return true;
    }
    return false;
  }

  // ==================== MCP Server 管理 ====================

  public List<McpServerConfig> listMcpServers() {
    return new ArrayList<>(mcpServers.values());
  }

  public Optional<McpServerConfig> getMcpServer(String id) {
    return Optional.ofNullable(mcpServers.get(id));
  }

  public McpServerConfig saveMcpServer(McpServerConfig server) {
    if (server.getId() == null || server.getId().isBlank()) {
      server.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 12));
    }
    mcpServers.put(server.getId(), server);
    saveMcpServers();
    return server;
  }

  public boolean deleteMcpServer(String id) {
    McpServerConfig removed = mcpServers.remove(id);
    if (removed != null) {
      saveMcpServers();
      // 同时从所有Skill中移除该MCP Server的引用
      skills.values().forEach(skill -> {
        if (skill.getMcpServerIds() != null) {
          skill.getMcpServerIds().remove(id);
        }
      });
      saveSkills();
      return true;
    }
    return false;
  }

  // ==================== 模型管理 ====================

  public List<ModelConfig> listModels() {
    return new ArrayList<>(models.values());
  }

  public Optional<ModelConfig> getModel(String id) {
    return Optional.ofNullable(models.get(id));
  }

  /**
   * 获取默认模型配置
   */
  public Optional<ModelConfig> getDefaultModel() {
    // 优先返回第一个启用的模型
    return models.values().stream().filter(ModelConfig::isEnabled).findFirst();
  }

  public ModelConfig saveModel(ModelConfig model) {
    if (model.getId() == null || model.getId().isBlank()) {
      model.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 12));
    }
    models.put(model.getId(), model);
    saveModels();
    return model;
  }

  public boolean deleteModel(String id) {
    ModelConfig removed = models.remove(id);
    if (removed != null) {
      saveModels();
      return true;
    }
    return false;
  }

  /**
   * 获取Skill关联的所有已启用的MCP Server配置
   */
  public List<McpServerConfig> getEnabledMcpServersForSkill(String skillId) {
    SkillConfig skill = skills.get(skillId);
    if (skill == null || skill.getMcpServerIds() == null) {
      return Collections.emptyList();
    }
    return skill.getMcpServerIds().stream()
      .map(mcpServers::get)
      .filter(Objects::nonNull)
      .filter(McpServerConfig::isEnabled)
      .toList();
  }

  // ==================== 持久化 ====================

  private String configDir() {
    return properties.getConfigDir();
  }

  /**
   * 初始化预置模型配置
   */
  private void initPresetModels() {
    if (!models.isEmpty()) {
      return; // 已有模型配置，跳过预置
    }
    if (properties.getPresetModels() != null && !properties.getPresetModels().isEmpty()) {
      for (ModelConfig preset : properties.getPresetModels()) {
        if (preset.getId() != null && !preset.getId().isBlank()) {
          models.put(preset.getId(), preset);
        }
      }
      saveModels();
      log.info("已加载 {} 个预置模型配置", models.size());
    }
  }

  private void loadSkills() {
    Path file = Paths.get(configDir(), "skills.json");
    if (Files.exists(file)) {
      try {
        String json = Files.readString(file);
        Map<String, SkillConfig> loaded = JSON.parseObject(json, new TypeReference<>() {});
        if (loaded != null) {
          skills.putAll(loaded);
        }
      } catch (IOException e) {
        log.error("加载 Skills 配置失败", e);
      }
    }
  }

  private void saveSkills() {
    try {
      Path file = Paths.get(configDir(), "skills.json");
      Files.writeString(file, JSON.toJSONString(skills, true));
    } catch (IOException e) {
      log.error("保存 Skills 配置失败", e);
    }
  }

  private void loadMcpServers() {
    Path file = Paths.get(configDir(), "mcp-servers.json");
    if (Files.exists(file)) {
      try {
        String json = Files.readString(file);
        Map<String, McpServerConfig> loaded = JSON.parseObject(json, new TypeReference<>() {});
        if (loaded != null) {
          mcpServers.putAll(loaded);
        }
      } catch (IOException e) {
        log.error("加载 MCP Servers 配置失败", e);
      }
    }
  }

  private void saveMcpServers() {
    try {
      Path file = Paths.get(configDir(), "mcp-servers.json");
      Files.writeString(file, JSON.toJSONString(mcpServers, true));
    } catch (IOException e) {
      log.error("保存 MCP Servers 配置失败", e);
    }
  }

  private void loadModels() {
    Path file = Paths.get(configDir(), "models.json");
    if (Files.exists(file)) {
      try {
        String json = Files.readString(file);
        Map<String, ModelConfig> loaded = JSON.parseObject(json, new TypeReference<>() {});
        if (loaded != null) {
          models.putAll(loaded);
        }
      } catch (IOException e) {
        log.error("加载 Models 配置失败", e);
      }
    }
  }

  private void saveModels() {
    try {
      Path file = Paths.get(configDir(), "models.json");
      Files.writeString(file, JSON.toJSONString(models, true));
    } catch (IOException e) {
      log.error("保存 Models 配置失败", e);
    }
  }

}
