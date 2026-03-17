package cn.cstn.algorithm.mcp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * MCP MySQL 配置属性
 *
 * @author zhaohuiqiang
 */
@Data
@Component
@ConfigurationProperties(prefix = "mcp.mysql")
public class McpMySqlProperties {

  /**
   * 是否为只读模式，默认 true（安全起见）
   */
  private boolean readOnly = true;

  /**
   * 查询结果最大行数
   */
  private int maxRows = 1000;

  /**
   * 查询超时时间（秒）
   */
  private int queryTimeout = 300;

  /**
   * 允许的数据库列表（为空表示全部允许）
   */
  private List<String> allowedDatabases = new ArrayList<>();

  /**
   * 禁止的表列表
   */
  private List<String> blockedTables = new ArrayList<>();

  /**
   * 默认数据库连接URL（当headers中未提供时使用）
   */
  private String defaultUrl;

  /**
   * 默认用户名（当headers中未提供时使用）
   */
  private String defaultUsername;

  /**
   * 默认密码（当headers中未提供时使用）
   */
  private String defaultPassword;

  /**
   * headers中数据库URL的参数名
   */
  private String urlHeader = "Mysql-Url";

  /**
   * headers中用户名的参数名
   */
  private String usernameHeader = "Mysql-Username";

  /**
   * headers中密码的参数名
   */
  private String passwordHeader = "Mysql-Password";

  /**
   * 从headers中获取的数据库连接配置
   */
  @Data
  public static class HeaderConnectionConfig {
    private String url;
    private String username;
    private String password;

    public boolean isValid() {
      return url != null && !url.trim().isEmpty() &&
             username != null && !username.trim().isEmpty() &&
             password != null && !password.trim().isEmpty();
    }
  }

}
