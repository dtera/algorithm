package cn.cstn.algorithm.workclaw.controller;

import cn.cstn.algorithm.workclaw.model.ApiResult;
import cn.cstn.algorithm.workclaw.model.ModelConfig;
import cn.cstn.algorithm.workclaw.service.ChatService;
import cn.cstn.algorithm.workclaw.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 模型配置 API 控制器
 *
 * @author zhaohuiqiang
 */
@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
public class ModelController {

    private final ConfigService configService;
    private final ChatService chatService;

    /**
     * 获取所有模型配置
     */
    @GetMapping
    public ApiResult<List<ModelConfig>> listModels() {
        return ApiResult.ok(configService.listModels());
    }

    /**
     * 获取指定模型配置
     */
    @GetMapping("/{id}")
    public ApiResult<ModelConfig> getModel(@PathVariable("id") String id) {
        return ApiResult.ok(configService.getModel(id)
                .orElseThrow(() -> new IllegalArgumentException("模型不存在: " + id)));
    }

    /**
     * 创建/保存模型配置
     */
    @PostMapping
    public ApiResult<ModelConfig> saveModel(@RequestBody ModelConfig model) {
        return ApiResult.ok(configService.saveModel(model));
    }

    /**
     * 更新模型配置
     */
    @PutMapping("/{id}")
    public ApiResult<ModelConfig> updateModel(@PathVariable("id") String id, @RequestBody ModelConfig model) {
        model.setId(id);
        chatService.evictModelCache(id);
        return ApiResult.ok(configService.saveModel(model));
    }

    /**
     * 删除模型配置
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> deleteModel(@PathVariable("id") String id) {
        chatService.evictModelCache(id);
        configService.deleteModel(id);
        return ApiResult.ok();
    }

}
