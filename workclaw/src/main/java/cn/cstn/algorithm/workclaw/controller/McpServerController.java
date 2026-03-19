package cn.cstn.algorithm.workclaw.controller;

import cn.cstn.algorithm.workclaw.model.ApiResult;
import cn.cstn.algorithm.workclaw.model.McpServerConfig;
import cn.cstn.algorithm.workclaw.service.ConfigService;
import cn.cstn.algorithm.workclaw.service.McpClientManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * MCP Server 配置 API 控制器
 *
 * @author zhaohuiqiang
 */
@RestController
@RequestMapping("/api/mcp-servers")
@RequiredArgsConstructor
public class McpServerController {

  private final ConfigService configService;
  private final McpClientManager mcpClientManager;

  /**
   * 获取所有 MCP Server 列表
   */
  @GetMapping
  public ApiResult<List<McpServerConfig>> list() {
    return ApiResult.ok(configService.listMcpServers());
  }

  /**
   * 获取指定 MCP Server
   */
  @GetMapping("/{id}")
  public ApiResult<McpServerConfig> get(@PathVariable("id") String id) {
    return configService.getMcpServer(id)
      .map(ApiResult::ok)
      .orElse(ApiResult.error(404, "MCP Server 不存在: " + id));
  }

  /**
   * 创建或更新 MCP Server
   */
  @PostMapping
  public ApiResult<McpServerConfig> save(@RequestBody McpServerConfig server) {
    McpServerConfig saved = configService.saveMcpServer(server);
    // 刷新客户端连接
    mcpClientManager.refreshClient(saved.getId());
    return ApiResult.ok(saved);
  }

  /**
   * 更新 MCP Server
   */
  @PutMapping("/{id}")
  public ApiResult<McpServerConfig> update(@PathVariable("id") String id, @RequestBody McpServerConfig server) {
    server.setId(id);
    McpServerConfig saved = configService.saveMcpServer(server);
    mcpClientManager.refreshClient(id);
    return ApiResult.ok(saved);
  }

  /**
   * 删除 MCP Server
   */
  @DeleteMapping("/{id}")
  public ApiResult<Void> delete(@PathVariable("id") String id) {
    mcpClientManager.closeClient(id);
    if (configService.deleteMcpServer(id)) {
      return ApiResult.ok();
    }
    return ApiResult.error(404, "MCP Server 不存在: " + id);
  }

  /**
   * 测试 MCP Server 连接
   */
  @PostMapping("/{id}/test")
  public ApiResult<Map<String, Object>> testConnection(@PathVariable("id") String id) {
    boolean connected = mcpClientManager.testConnection(id);
    return ApiResult.ok(Map.of("connected", connected));
  }

}
