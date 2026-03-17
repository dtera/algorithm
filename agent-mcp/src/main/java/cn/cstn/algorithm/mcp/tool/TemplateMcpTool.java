package cn.cstn.algorithm.mcp.tool;

import cn.cstn.algorithm.mcp.annotation.McpToolProvider;
import cn.cstn.algorithm.mcp.support.QueryResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * MCP 工具扩展模板
 * <p>
 * 这是一个展示如何快速扩展新 MCP 工具的模板类。
 * 后续开发新的 MCP 功能时，可以参考此模板：
 * <ol>
 *   <li>复制本文件并重命名</li>
 *   <li>修改类名和 {@code @McpToolProvider} 的 value</li>
 *   <li>实现具体的 {@code @Tool} 方法</li>
 *   <li>无需修改任何配置，重启即可生效</li>
 * </ol>
 * </p>
 *
 * @author zhaohuiqiang
 */
@Slf4j
@McpToolProvider("templateTool")
public class TemplateMcpTool {

  /**
   * 示例工具方法 - 获取当前服务器时间
   * <p>
   * 每个 @Tool 方法即为一个可被 AI 模型调用的工具。
   * description 参数描述工具功能，帮助 AI 模型理解何时调用此工具。
   * </p>
   */
  @Tool(description = "获取当前服务器时间，返回格式化的日期时间字符串")
  public QueryResult getCurrentTime() {
    log.info("[MCP] 获取当前时间");
    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    return QueryResult.success(List.of(Map.of("currentTime", now)));
  }

  /**
   * 示例工具方法（带参数）- 问候
   * <p>
   * {@code @ToolParam} 注解描述参数的含义，帮助 AI 模型正确传参。
   * required = false 的参数为可选参数。
   * </p>
   */
  @Tool(description = "向指定用户发送问候语")
  public QueryResult greet(
    @ToolParam(description = "用户名称") String name,
    @ToolParam(description = "问候语，默认为'你好'", required = false) String greeting) {
    log.info("[MCP] 问候: name={}, greeting={}", name, greeting);
    String msg = (greeting != null && !greeting.isBlank() ? greeting : "你好") + ", " + name + "!";
    return QueryResult.success(List.of(Map.of("message", msg)));
  }

}
