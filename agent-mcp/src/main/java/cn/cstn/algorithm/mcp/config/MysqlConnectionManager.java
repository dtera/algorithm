package cn.cstn.algorithm.mcp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MySQL 连接管理器
 * <p>
 * 根据headers中的配置动态创建和管理数据库连接
 * </p>
 *
 * @author zhaohuiqiang
 */
@Slf4j
@Component
public class MysqlConnectionManager {

  private final McpMySqlProperties properties;
  private final Map<String, JdbcTemplate> connectionCache = new ConcurrentHashMap<>();

  public MysqlConnectionManager(McpMySqlProperties properties) {
    this.properties = properties;
  }

  /**
   * 根据headers获取数据库连接
   *
   * @param headers MCP请求的headers
   * @return JdbcTemplate实例
   */
  public JdbcTemplate getConnection(Map<String, String> headers) {
    McpMySqlProperties.HeaderConnectionConfig config = extractConnectionConfig(headers);
    if (config.isValid()) {
      return getDynamicConnection(config);
    }

    return null;
  }

  /**
   * 从headers中提取连接配置
   */
  private McpMySqlProperties.HeaderConnectionConfig extractConnectionConfig(Map<String, String> headers) {
    McpMySqlProperties.HeaderConnectionConfig config = new McpMySqlProperties.HeaderConnectionConfig();

    if (headers != null) {
      config.setUrl(headers.get(properties.getUrlHeader()));
      config.setUsername(headers.get(properties.getUsernameHeader()));
      config.setPassword(headers.get(properties.getPasswordHeader()));
    }

    return config;
  }

  /**
   * 获取动态连接（基于headers配置）
   */
  private JdbcTemplate getDynamicConnection(McpMySqlProperties.HeaderConnectionConfig config) {
    String connectionKey = generateConnectionKey(config);

    return connectionCache.computeIfAbsent(connectionKey, key -> {
      log.info("[MCP] 创建动态数据库连接: {}", config.getUrl());

      DriverManagerDataSource dataSource = new DriverManagerDataSource();
      dataSource.setDriverClassName("com.zaxxer.hikari.HikariDataSource");
      dataSource.setUrl(config.getUrl());
      dataSource.setUsername(config.getUsername());
      dataSource.setPassword(config.getPassword());

      JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
      jdbcTemplate.setQueryTimeout(properties.getQueryTimeout());

      return jdbcTemplate;
    });
  }

  /**
   * 生成连接缓存键
   */
  private String generateConnectionKey(McpMySqlProperties.HeaderConnectionConfig config) {
    return String.format("%s@%s", config.getUsername(), config.getUrl());
  }

  /**
   * 清理连接缓存
   */
  public void clearCache() {
    connectionCache.clear();
    log.info("[MCP] 数据库连接缓存已清理");
  }

}