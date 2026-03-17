package cn.cstn.algorithm.mcp.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * MCP 工具提供者标记注解
 * <p>
 * 将此注解标记在包含 {@code @Tool} 方法的 Service 类上，
 * 框架会自动扫描并将其注册为 MCP 可调用的工具。
 * 后续扩展新的 MCP 功能时，只需：
 * <ol>
 *   <li>创建新的 Service 类</li>
 *   <li>在类上标记 {@code @McpToolProvider}</li>
 *   <li>在方法上使用 {@code @Tool} 和 {@code @ToolParam} 注解</li>
 * </ol>
 * 即可自动注册为 MCP 工具，无需修改任何配置类。
 * </p>
 *
 * @author zhaohuiqiang
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface McpToolProvider {
  /**
   * 工具提供者的名称（可选），默认为类名首字母小写
   */
  String value() default "";

}
