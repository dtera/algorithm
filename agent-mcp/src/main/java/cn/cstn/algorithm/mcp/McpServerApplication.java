package cn.cstn.algorithm.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MCP Server 启动类
 * <p>
 * 基于 Spring AI 的 Streamable-HTTP 类型 MCP Server，
 * 提供 MySQL 数据库操作能力，并支持灵活扩展其他 MCP 工具。
 * </p>
 *
 * @author zhaohuiqiang
 */
@SpringBootApplication
public class McpServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(McpServerApplication.class, args);
  }

}