package cn.cstn.algorithm.mcp.tool;

import cn.cstn.algorithm.mcp.annotation.McpToolProvider;
import cn.cstn.algorithm.mcp.support.QueryResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * MySQL 数据分析 MCP 工具集（扩展示例）
 * <p>
 * 演示如何快速扩展新的 MCP 工具。
 * 只需创建新类、标记 {@code @McpToolProvider}、在方法上使用 {@code @Tool} 即可。
 * 框架会自动扫描并注册，无需修改任何配置类。
 * </p>
 *
 * @author zhaohuiqiang
 */
@Slf4j
@McpToolProvider("mysqlAnalysisTool")
@RequiredArgsConstructor
public class MySqlAnalysisTool {

  private final MySqlMcpTool mySqlMcpTool;

  public JdbcTemplate getJdbcTemplate() {
    return mySqlMcpTool.getJdbcTemplate();
  }

  @Tool(description = "获取指定数据库的整体统计信息，包括表数量、总数据大小、总索引大小等")
  public QueryResult getDatabaseStats(
    @ToolParam(description = "数据库名称") String database) {
    log.info("[MCP] 获取数据库统计: {}", database);
    try {
      List<Map<String, Object>> result = getJdbcTemplate().queryForList(
        "SELECT " +
        "  COUNT(*) AS table_count, " +
        "  SUM(DATA_LENGTH) AS total_data_size, " +
        "  SUM(INDEX_LENGTH) AS total_index_size, " +
        "  SUM(TABLE_ROWS) AS total_rows " +
        "FROM information_schema.TABLES " +
        "WHERE TABLE_SCHEMA = ?", database);
      return QueryResult.success(result);
    } catch (Exception e) {
      log.error("[MCP] 获取数据库统计失败", e);
      return QueryResult.error("获取数据库统计失败: " + e.getMessage());
    }
  }

  @Tool(description = "获取指定表中各列的基本统计信息（适用于数值列），包括最小值、最大值、平均值、总和等")
  public QueryResult getColumnStats(
    @ToolParam(description = "数据库名称") String database,
    @ToolParam(description = "表名称") String tableName,
    @ToolParam(description = "列名称") String columnName) {
    log.info("[MCP] 获取列统计: {}.{}.{}", database, tableName, columnName);
    try {
      String fullTable = String.format("`%s`.`%s`", database, tableName);
      String col = String.format("`%s`", columnName);
      String sql = String.format(
        "SELECT MIN(%s) AS min_val, MAX(%s) AS max_val, " +
        "AVG(%s) AS avg_val, SUM(%s) AS sum_val, COUNT(%s) AS count_val, " +
        "COUNT(DISTINCT %s) AS distinct_count " +
        "FROM %s",
        col, col, col, col, col, col, fullTable);
      List<Map<String, Object>> result = getJdbcTemplate().queryForList(sql);
      return QueryResult.success(result);
    } catch (Exception e) {
      log.error("[MCP] 获取列统计失败", e);
      return QueryResult.error("获取列统计失败: " + e.getMessage());
    }
  }

  @Tool(description = "获取指定表的数据量最大的前 N 列（按数据长度排序）")
  public QueryResult getTableSizeDetail(
    @ToolParam(description = "数据库名称") String database,
    @ToolParam(description = "表名称") String tableName) {
    log.info("[MCP] 获取表大小详情: {}.{}", database, tableName);
    try {
      List<Map<String, Object>> result = getJdbcTemplate().queryForList(
        "SELECT TABLE_NAME, TABLE_ROWS, DATA_LENGTH, INDEX_LENGTH, " +
        "DATA_LENGTH + INDEX_LENGTH AS total_size, " +
        "AUTO_INCREMENT, CREATE_TIME, UPDATE_TIME " +
        "FROM information_schema.TABLES " +
        "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?", database, tableName);
      return QueryResult.success(result);
    } catch (Exception e) {
      log.error("[MCP] 获取表大小详情失败", e);
      return QueryResult.error("获取表大小详情失败: " + e.getMessage());
    }
  }

  @Tool(description = "获取 MySQL 服务器的全局状态变量，用于了解服务器运行状况")
  public QueryResult getServerStatus() {
    log.info("[MCP] 获取服务器状态");
    try {
      List<Map<String, Object>> result = getJdbcTemplate().queryForList(
        "SHOW GLOBAL STATUS WHERE Variable_name IN (" +
        "'Uptime', 'Threads_connected', 'Threads_running', " +
        "'Questions', 'Slow_queries', 'Connections', " +
        "'Bytes_received', 'Bytes_sent')");
      return QueryResult.success(result);
    } catch (Exception e) {
      log.error("[MCP] 获取服务器状态失败", e);
      return QueryResult.error("获取服务器状态失败: " + e.getMessage());
    }
  }

}
