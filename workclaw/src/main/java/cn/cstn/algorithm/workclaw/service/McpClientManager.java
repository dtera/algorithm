package cn.cstn.algorithm.workclaw.service;

import cn.cstn.algorithm.workclaw.model.McpServerConfig;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MCP 客户端管理器
 * <p>
 * 负责动态创建、管理和销毁 MCP 客户端连接。
 * 根据配置动态连接到不同的 MCP Server，并获取其提供的工具列表。
 * </p>
 *
 * @author zhaohuiqiang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class McpClientManager {

  private final ConfigService configService;
  private final Map<String, McpSyncClient> clientCache = new ConcurrentHashMap<>();

  /**
   * 获取指定 MCP Server 的同步客户端
   */
  public McpSyncClient getClient(String serverId) {
    return clientCache.computeIfAbsent(serverId, this::createClient);
  }

  /**
   * 获取指定 Skill 关联的所有 MCP 工具回调
   */
  public List<ToolCallback> getToolCallbacksForSkill(String skillId) {
    List<McpServerConfig> servers = configService.getEnabledMcpServersForSkill(skillId);
    if (servers.isEmpty()) {
      return Collections.emptyList();
    }

    List<ToolCallback> allCallbacks = new ArrayList<>();
    for (McpServerConfig server : servers) {
      try {
        McpSyncClient client = getClient(server.getId());
        ToolCallback[] callbacks = SyncMcpToolCallbackProvider.syncToolCallbacks(List.of(client)).toArray(new ToolCallback[0]);
        allCallbacks.addAll(Arrays.asList(callbacks));
        log.debug("从 MCP Server [{}] 加载了 {} 个工具", server.getName(), callbacks.length);
      } catch (Exception e) {
        log.error("从 MCP Server [{}] 加载工具失败: {}", server.getName(), e.getMessage());
      }
    }
    return allCallbacks;
  }

  /**
   * 刷新指定 MCP Server 的客户端连接
   */
  public void refreshClient(String serverId) {
    closeClient(serverId);
    clientCache.remove(serverId);
  }

  /**
   * 关闭指定 MCP Server 的客户端连接
   */
  public void closeClient(String serverId) {
    McpSyncClient client = clientCache.remove(serverId);
    if (client != null) {
      try {
        client.close();
        log.info("已关闭 MCP 客户端: {}", serverId);
      } catch (Exception e) {
        log.error("关闭 MCP 客户端失败: {}", serverId, e);
      }
    }
  }

  /**
   * 测试 MCP Server 连接
   */
  public boolean testConnection(String serverId) {
    try {
      McpSyncClient client = getClient(serverId);
      // 尝试列出工具来验证连接
      client.listTools();
      return true;
    } catch (Exception e) {
      log.error("MCP Server 连接测试失败: {}", e.getMessage());
      closeClient(serverId);
      return false;
    }
  }

  @PreDestroy
  public void destroy() {
    clientCache.forEach((id, client) -> {
      try {
        client.close();
      } catch (Exception e) {
        log.error("关闭 MCP 客户端异常: {}", id, e);
      }
    });
    clientCache.clear();
  }

  private McpSyncClient createClient(String serverId) {
    McpServerConfig config = configService.getMcpServer(serverId)
      .orElseThrow(() -> new IllegalArgumentException("MCP Server 不存在: " + serverId));

    McpClientTransport transport = createTransport(config);

    McpSyncClient client = McpClient.sync(transport)
      .requestTimeout(Duration.ofSeconds(config.getRequestTimeout()))
      .clientInfo(new McpSchema.Implementation("workclaw-client", "1.0.0"))
      .build();

    client.initialize();
    log.info("已创建 MCP 客户端: {} -> {}", config.getName(), config.getUrl());
    return client;
  }

  private McpClientTransport createTransport(McpServerConfig config) {
    String type = config.getType() != null ? config.getType() : "sse";
    Map<String, String> headers = config.getHeaders();
    
    // 注意：DNS 解析问题已通过 NettyDnsConfig 的 JVM 系统属性配置解决
    // MCP SDK 内部会使用配置好的 Netty DNS 解析器

    if ("streamable-http".equalsIgnoreCase(type)) {
      // 使用 Streamable HTTP 传输
      HttpClientStreamableHttpTransport.Builder builder = HttpClientStreamableHttpTransport.builder(config.getUrl());

      if (headers != null && !headers.isEmpty()) {
        // 使用 httpRequestCustomizer 确保每个请求（包括 tools/call）都携带自定义 headers
        builder.httpRequestCustomizer((requestBuilder, method, uri, body, context) ->
          headers.forEach(requestBuilder::header)
        );
        log.debug("已为 Streamable HTTP 传输配置 {} 个自定义 headers", headers.size());
      }

      return builder.build();
    } else {
      // 默认使用 SSE 传输
      HttpClientSseClientTransport.Builder builder = HttpClientSseClientTransport.builder(config.getUrl());

      if (headers != null && !headers.isEmpty()) {
        // 使用 httpRequestCustomizer 确保每个请求都携带自定义 headers
        builder.httpRequestCustomizer((requestBuilder, method, uri, body, context) ->
          headers.forEach(requestBuilder::header)
        );
        log.debug("已为 SSE 传输配置 {} 个自定义 headers", headers.size());
      }

      return builder.build();
    }
  }

}
