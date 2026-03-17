package cn.cstn.algorithm.mcp.config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MCP 工具自动注册配置
 * <p>
 * 自动扫描所有实现了 {@code @McpToolProvider} 标记的 Bean，
 * 并将其注册为 MCP 可调用的工具。后续扩展新的 MCP 功能，
 * 只需新增一个带有 @Tool 注解方法的 Service，并标记为 Spring Bean 即可自动注册。
 * </p>
 *
 * @author zhaohuiqiang
 */
@Configuration
public class McpToolAutoConfiguration {

  /**
   * 自动注册所有标记了 {@link cn.cstn.algorithm.mcp.annotation.McpToolProvider} 注解的 Bean
   * 为 MCP 工具回调
   */
  @Bean
  public ToolCallbackProvider toolCallbackProvider(ApplicationContext applicationContext) {
    // 扫描所有带 @McpToolProvider 注解的 Bean
    Map<String, Object> toolBeans = applicationContext
      .getBeansWithAnnotation(cn.cstn.algorithm.mcp.annotation.McpToolProvider.class);

    List<Object> toolObjects = new ArrayList<>(toolBeans.values());

    return MethodToolCallbackProvider.builder()
      .toolObjects(toolObjects.toArray())
      .build();
  }

}
