package cn.cstn.algorithm.workclaw.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * MCP Server 配置模型
 * <p>
 * 表示一个 MCP Server 连接配置，支持 SSE 和 Streamable-HTTP 协议。
 * </p>
 *
 * @author zhaohuiqiang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpServerConfig {

  /**
   * MCP Server 唯一标识
   */
  private String id;

  /**
   * MCP Server 名称
   */
  private String name;

  /**
   * MCP Server 描述
   */
  private String description;

  /**
   * 连接类型：sse / streamable-http
   */
  @Builder.Default
  private String type = "sse";

  /**
   * MCP Server URL（如 http://localhost:8088/mcp）
   */
  private String url;

  /**
   * 请求头（用于认证等）
   */
  private Map<String, String> headers;

  /**
   * 连接超时时间（秒）
   */
  @Builder.Default
  private int connectTimeout = 10;

  /**
   * 请求超时时间（秒）
   */
  @Builder.Default
  private int requestTimeout = 60;

  /**
   * 是否启用
   */
  @Builder.Default
  private boolean enabled = true;

}
