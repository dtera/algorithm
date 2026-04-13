package cn.cstn.algorithm.workclaw.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Skill 配置模型
 * <p>
 * 表示一个 Skill（技能），包含名称、描述、系统提示词等信息。
 * </p>
 *
 * @author zhaohuiqiang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillConfig {

  /**
   * Skill 唯一标识
   */
  private String id;

  /**
   * Skill 名称
   */
  private String name;

  /**
   * Skill 描述
   */
  private String description;

  /**
   * 系统提示词
   */
  private String systemPrompt;

  /**
   * Skill 关联的 MCP Server ID 列表
   */
  private List<String> mcpServerIds;

  /**
   * 额外参数
   */
  private Map<String, Object> parameters;

  /**
   * 是否启用
   */
  @Builder.Default
  private Boolean enabled = true;

}
