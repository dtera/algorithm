package cn.cstn.algorithm.workclaw.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 模型配置
 * <p>
 * 存储 AI 模型的连接信息，支持多种 OpenAI 兼容的模型服务。
 * </p>
 *
 * @author zhaohuiqiang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelConfig {

    /**
     * 模型配置 ID
     */
    private String id;

    /**
     * 模型显示名称
     */
    private String name;

    /**
     * API Base URL
     */
    private String baseUrl;

    /**
     * API Key
     */
    private String apiKey;

    /**
     * 模型名称（如 qwen-plus, GLM-4.7-FP8-17853）
     */
    private String model;

    /**
     * 温度参数
     */
    @Builder.Default
    private double temperature = 0.7;

    /**
     * 自定义请求头（用于需要额外鉴权的服务）
     */
    private Map<String, String> headers;

    /**
     * 是否启用
     */
    @Builder.Default
    private boolean enabled = true;

}
