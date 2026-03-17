package cn.cstn.algorithm.mcp.tool;

import cn.cstn.algorithm.mcp.annotation.McpToolProvider;
import cn.cstn.algorithm.mcp.config.McpMySqlProperties;
import cn.cstn.algorithm.mcp.config.MysqlConnectionManager;
import cn.cstn.algorithm.mcp.support.QueryResult;
import cn.cstn.algorithm.mcp.support.SqlValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * MySQL MCP 工具集
 * <p>
 * 提供通过 MCP 协议操作 MySQL 数据库的工具方法。
 * 所有方法通过 {@code @Tool} 注解暴露给 AI 模型调用。
 * </p>
 *
 * @author zhaohuiqiang
 */
@SuppressWarnings("SqlSourceToSinkFlow")
@Slf4j
@McpToolProvider("mysqlMcpTool")
@RequiredArgsConstructor
public class MySqlMcpTool {

  private final MysqlConnectionManager connectionManager;
  private final McpMySqlProperties properties;
  private final SqlValidator sqlValidator;

  /**
   * 获取当前请求的headers
   */
  private Map<String, String> getCurrentHeaders() {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      HttpServletRequest request = attributes.getRequest();
      Map<String, String> headers = new java.util.HashMap<>();

      // 从请求头中提取数据库连接配置
      java.util.Collections.list(request.getHeaderNames())
        .forEach(headerName -> headers.put(headerName, request.getHeader(headerName)));

      return headers;
    }
    return Collections.emptyMap();
  }

  public JdbcTemplate getJdbcTemplate() {
    return connectionManager.getConnection(getCurrentHeaders());
  }

  // ==================== 数据库元数据查询 ====================

  @Tool(description = "获取当前 MySQL 服务器上的所有数据库列表")
  public QueryResult listDatabases() {
    log.info("[MCP] 查询所有数据库");
    try {
      // 从MCP请求的headers中获取数据库连接
      JdbcTemplate jdbcTemplate = getJdbcTemplate();
      List<Map<String, Object>> databases = jdbcTemplate.queryForList("SHOW DATABASES");
      return QueryResult.success(databases);
    } catch (Exception e) {
      log.error("[MCP] 查询数据库列表失败", e);
      return QueryResult.error("查询数据库列表失败: " + e.getMessage());
    }
  }

  @Tool(description = "获取指定数据库下的所有表名列表")
  public QueryResult listTables(
    @ToolParam(description = "数据库名称") String database) {
    log.info("[MCP] 查询数据库 [{}] 的表列表", database);
    try {
      sqlValidator.validateDatabaseAccess(database);
      JdbcTemplate jdbcTemplate = getJdbcTemplate();
      List<Map<String, Object>> tables = jdbcTemplate.queryForList(
        "SELECT TABLE_NAME, TABLE_COMMENT, TABLE_ROWS, ENGINE " +
        "FROM information_schema.TABLES WHERE TABLE_SCHEMA = ?", database);
      return QueryResult.success(tables);
    } catch (Exception e) {
      log.error("[MCP] 查询表列表失败", e);
      return QueryResult.error("查询表列表失败: " + e.getMessage());
    }
  }

  @Tool(description = "获取指定表的结构信息，包括列名、数据类型、是否可为空、键信息、默认值和注释等")
  public QueryResult describeTable(
    @ToolParam(description = "数据库名称") String database,
    @ToolParam(description = "表名称") String tableName) {
    log.info("[MCP] 查询表结构: {}.{}", database, tableName);
    try {
      sqlValidator.validateDatabaseAccess(database);
      sqlValidator.validateTableAccess(tableName);
      JdbcTemplate jdbcTemplate = getJdbcTemplate();
      List<Map<String, Object>> columns = jdbcTemplate.queryForList(
        "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_TYPE, IS_NULLABLE, " +
        "COLUMN_KEY, COLUMN_DEFAULT, COLUMN_COMMENT, EXTRA " +
        "FROM information_schema.COLUMNS " +
        "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? " +
        "ORDER BY ORDINAL_POSITION", database, tableName);
      return QueryResult.success(columns);
    } catch (Exception e) {
      log.error("[MCP] 查询表结构失败", e);
      return QueryResult.error("查询表结构失败: " + e.getMessage());
    }
  }

  @Tool(description = "获取指定表的索引信息")
  public QueryResult showIndexes(
    @ToolParam(description = "数据库名称") String database,
    @ToolParam(description = "表名称") String tableName) {
    log.info("[MCP] 查询表索引: {}.{}", database, tableName);
    try {
      sqlValidator.validateDatabaseAccess(database);
      sqlValidator.validateTableAccess(tableName);
      JdbcTemplate jdbcTemplate = getJdbcTemplate();
      List<Map<String, Object>> indexes = jdbcTemplate.queryForList(
        "SELECT INDEX_NAME, COLUMN_NAME, NON_UNIQUE, INDEX_TYPE, SEQ_IN_INDEX " +
        "FROM information_schema.STATISTICS " +
        "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? " +
        "ORDER BY INDEX_NAME, SEQ_IN_INDEX", database, tableName);
      return QueryResult.success(indexes);
    } catch (Exception e) {
      log.error("[MCP] 查询索引信息失败", e);
      return QueryResult.error("查询索引信息失败: " + e.getMessage());
    }
  }

  @Tool(description = "获取指定表的建表语句 (CREATE TABLE)")
  public QueryResult showCreateTable(
    @ToolParam(description = "数据库名称") String database,
    @ToolParam(description = "表名称") String tableName) {
    log.info("[MCP] 查询建表语句: {}.{}", database, tableName);
    try {
      sqlValidator.validateDatabaseAccess(database);
      sqlValidator.validateTableAccess(tableName);
      JdbcTemplate jdbcTemplate = getJdbcTemplate();
      String fullTableName = String.format("`%s`.`%s`", database, tableName);
      List<Map<String, Object>> result = jdbcTemplate.queryForList(
        "SHOW CREATE TABLE " + fullTableName);
      return QueryResult.success(result);
    } catch (Exception e) {
      log.error("[MCP] 查询建表语句失败", e);
      return QueryResult.error("查询建表语句失败: " + e.getMessage());
    }
  }

  // ==================== 数据查询 ====================

  @Tool(description = "执行 SELECT 查询语句，返回查询结果。仅支持只读的 SELECT 语句，" +
                      "结果行数受限于配置的最大行数。" +
                      "示例: SELECT * FROM users WHERE age > 18 LIMIT 10")
  public QueryResult executeQuery(
    @ToolParam(description = "要执行的 SELECT SQL 语句") String sql) {
    log.info("[MCP] 执行查询: {}", sql);
    try {
      sqlValidator.validateSelectSql(sql);
      JdbcTemplate jdbcTemplate = getJdbcTemplate();
      String limitedSql = sqlValidator.ensureLimit(sql, properties.getMaxRows());
      jdbcTemplate.setQueryTimeout(properties.getQueryTimeout());
      List<Map<String, Object>> result = jdbcTemplate.queryForList(limitedSql);
      return QueryResult.success(result, result.size());
    } catch (Exception e) {
      log.error("[MCP] 执行查询失败", e);
      return QueryResult.error("执行查询失败: " + e.getMessage());
    }
  }

  @Tool(description = "统计指定表的总行数")
  public QueryResult countRows(
    @ToolParam(description = "数据库名称") String database,
    @ToolParam(description = "表名称") String tableName) {
    log.info("[MCP] 统计行数: {}.{}", database, tableName);
    try {
      sqlValidator.validateDatabaseAccess(database);
      sqlValidator.validateTableAccess(tableName);
      JdbcTemplate jdbcTemplate = getJdbcTemplate();
      String fullTableName = String.format("`%s`.`%s`", database, tableName);
      Integer count = jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM " + fullTableName, Integer.class);
      return QueryResult.success(List.of(Map.of("count", count != null ? count : 0)));
    } catch (Exception e) {
      log.error("[MCP] 统计行数失败", e);
      return QueryResult.error("统计行数失败: " + e.getMessage());
    }
  }

  @Tool(description = "查询指定表的数据，支持条件过滤和分页。" +
                      "例如: database=mydb, tableName=users, whereClause=age > 18, limit=10, offset=0")
  public QueryResult queryTable(
    @ToolParam(description = "数据库名称") String database,
    @ToolParam(description = "表名称") String tableName,
    @ToolParam(description = "WHERE 条件子句，不包含 WHERE 关键字，为空则查全表", required = false) String whereClause,
    @ToolParam(description = "返回行数限制，默认 100", required = false) Integer limit,
    @ToolParam(description = "偏移量，默认 0", required = false) Integer offset) {
    log.info("[MCP] 查询表数据: {}.{}, where={}, limit={}, offset={}", database, tableName, whereClause, limit, offset);
    try {
      sqlValidator.validateDatabaseAccess(database);
      sqlValidator.validateTableAccess(tableName);

      int actualLimit = (limit != null && limit > 0) ? Math.min(limit, properties.getMaxRows()) : 100;
      int actualOffset = (offset != null && offset >= 0) ? offset : 0;

      JdbcTemplate jdbcTemplate = getJdbcTemplate();
      String fullTableName = String.format("`%s`.`%s`", database, tableName);
      StringBuilder sql = new StringBuilder("SELECT * FROM ").append(fullTableName);

      if (whereClause != null && !whereClause.isBlank()) {
        sqlValidator.validateWhereClause(whereClause);
        sql.append(" WHERE ").append(whereClause);
      }

      sql.append(" LIMIT ").append(actualLimit).append(" OFFSET ").append(actualOffset);

      jdbcTemplate.setQueryTimeout(properties.getQueryTimeout());
      List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString());
      return QueryResult.success(result, result.size());
    } catch (Exception e) {
      log.error("[MCP] 查询表数据失败", e);
      return QueryResult.error("查询表数据失败: " + e.getMessage());
    }
  }

  // ==================== 数据写入（非只读模式） ====================

  @Tool(description = "执行数据修改语句（INSERT / UPDATE / DELETE）。" +
                      "仅在非只读模式下可用，只读模式下调用会返回错误提示。")
  public QueryResult executeUpdate(
    @ToolParam(description = "要执行的 INSERT / UPDATE / DELETE SQL 语句") String sql) {
    log.info("[MCP] 执行更新: {}", sql);
    try {
      if (properties.isReadOnly()) {
        return QueryResult.error("当前为只读模式，不允许执行数据修改操作。" +
                                 "请修改配置 mcp.mysql.read-only=false 以启用写操作。");
      }
      sqlValidator.validateModifySql(sql);
      JdbcTemplate jdbcTemplate = getJdbcTemplate();
      jdbcTemplate.setQueryTimeout(properties.getQueryTimeout());
      int affected = jdbcTemplate.update(sql);
      return QueryResult.success(
        List.of(Map.of("affectedRows", affected)),
        affected
      );
    } catch (Exception e) {
      log.error("[MCP] 执行更新失败", e);
      return QueryResult.error("执行更新失败: " + e.getMessage());
    }
  }

  // ==================== 辅助功能 ====================

  @Tool(description = "获取当前 MCP MySQL 服务的配置信息，包括只读模式、最大行数限制等")
  public QueryResult getServerConfig() {
    log.info("[MCP] 获取服务器配置");
    return QueryResult.success(List.of(Map.of(
      "readOnly", properties.isReadOnly(),
      "maxRows", properties.getMaxRows(),
      "queryTimeout", properties.getQueryTimeout(),
      "allowedDatabases", properties.getAllowedDatabases(),
      "blockedTables", properties.getBlockedTables(),
      "urlHeader", properties.getUrlHeader(),
      "usernameHeader", properties.getUsernameHeader(),
      "passwordHeader", properties.getPasswordHeader()
    )));
  }

  @Tool(description = "对指定的 SQL 进行语法分析，使用 EXPLAIN 查看执行计划")
  public QueryResult explainSql(
    @ToolParam(description = "要分析的 SQL 语句") String sql) {
    log.info("[MCP] EXPLAIN: {}", sql);
    try {
      sqlValidator.validateSelectSql(sql);
      JdbcTemplate jdbcTemplate = getJdbcTemplate();
      List<Map<String, Object>> result = jdbcTemplate.queryForList("EXPLAIN " + sql);
      return QueryResult.success(result);
    } catch (Exception e) {
      log.error("[MCP] EXPLAIN 失败", e);
      return QueryResult.error("EXPLAIN 失败: " + e.getMessage());
    }
  }

}
