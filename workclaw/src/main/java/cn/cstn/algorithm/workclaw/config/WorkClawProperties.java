package cn.cstn.algorithm.workclaw.config;

import cn.cstn.algorithm.workclaw.model.ModelConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * WorkClaw 配置属性
 *
 * @author zhaohuiqiang
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "workclaw")
public class WorkClawProperties {

    /**
     * 配置文件持久化目录
     */
    private String configDir = "./workclaw-config";

    /**
     * 预置模型配置列表（首次启动时写入）
     */
    private List<ModelConfig> presetModels;

}
