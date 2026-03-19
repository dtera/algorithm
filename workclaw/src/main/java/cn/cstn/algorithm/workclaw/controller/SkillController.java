package cn.cstn.algorithm.workclaw.controller;

import cn.cstn.algorithm.workclaw.model.ApiResult;
import cn.cstn.algorithm.workclaw.model.SkillConfig;
import cn.cstn.algorithm.workclaw.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Skill 配置 API 控制器
 *
 * @author zhaohuiqiang
 */
@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

  private final ConfigService configService;

  /**
   * 获取所有 Skill 列表
   */
  @GetMapping
  public ApiResult<List<SkillConfig>> list() {
    return ApiResult.ok(configService.listSkills());
  }

  /**
   * 获取指定 Skill
   */
  @GetMapping("/{id}")
  public ApiResult<SkillConfig> get(@PathVariable("id") String id) {
    return configService.getSkill(id)
      .map(ApiResult::ok)
      .orElse(ApiResult.error(404, "Skill 不存在: " + id));
  }

  /**
   * 创建或更新 Skill
   */
  @PostMapping
  public ApiResult<SkillConfig> save(@RequestBody SkillConfig skill) {
    return ApiResult.ok(configService.saveSkill(skill));
  }

  /**
   * 更新 Skill
   */
  @PutMapping("/{id}")
  public ApiResult<SkillConfig> update(@PathVariable("id") String id, @RequestBody SkillConfig skill) {
    skill.setId(id);
    return ApiResult.ok(configService.saveSkill(skill));
  }

  /**
   * 删除 Skill
   */
  @DeleteMapping("/{id}")
  public ApiResult<Void> delete(@PathVariable("id") String id) {
    if (configService.deleteSkill(id)) {
      return ApiResult.ok();
    }
    return ApiResult.error(400, "无法删除该 Skill（默认 Skill 不可删除）");
  }

}
