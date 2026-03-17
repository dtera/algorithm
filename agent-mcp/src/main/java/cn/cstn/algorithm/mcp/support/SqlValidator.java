package cn.cstn.algorithm.mcp.support;

import cn.cstn.algorithm.mcp.config.McpMySqlProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * SQL 校验器
 * <p>
 * 对用户输入的 SQL 进行安全性校验，防止危险操作。
 * </p>
 *
 * @author zhaohuiqiang
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SqlValidator {

  private final McpMySqlProperties properties;

  /**
   * 危险关键字集合（大写）
   */
  private static final Set<String> DANGEROUS_KEYWORDS = Set.of(
    "DROP", "TRUNCATE", "ALTER", "GRANT", "REVOKE",
    "CREATE USER", "DROP USER", "SHUTDOWN", "KILL"
  );

  /**
   * SELECT 语句正则（忽略大小写，允许前导空白）
   */
  private static final Pattern SELECT_PATTERN = Pattern.compile(
    "^\\s*(SELECT|WITH)\\s+", Pattern.CASE_INSENSITIVE);

  /**
   * 修改语句正则
   */
  private static final Pattern MODIFY_PATTERN = Pattern.compile(
    "^\\s*(INSERT|UPDATE|DELETE)\\s+", Pattern.CASE_INSENSITIVE);

  /**
   * LIMIT 子句正则
   */
  private static final Pattern LIMIT_PATTERN = Pattern.compile(
    "\\bLIMIT\\s+\\d+", Pattern.CASE_INSENSITIVE);

  /**
   * 校验是否为合法的 SELECT 语句
   */
  public void validateSelectSql(String sql) {
    if (sql == null || sql.isBlank()) {
      throw new IllegalArgumentException("SQL 语句不能为空");
    }
    if (!SELECT_PATTERN.matcher(sql).find()) {
      throw new IllegalArgumentException("仅允许执行 SELECT / WITH 查询语句");
    }
    checkDangerousKeywords(sql);
  }

  /**
   * 校验是否为合法的修改语句（INSERT/UPDATE/DELETE）
   */
  public void validateModifySql(String sql) {
    if (sql == null || sql.isBlank()) {
      throw new IllegalArgumentException("SQL 语句不能为空");
    }
    if (!MODIFY_PATTERN.matcher(sql).find()) {
      throw new IllegalArgumentException("仅允许执行 INSERT / UPDATE / DELETE 语句");
    }
    checkDangerousKeywords(sql);
  }

  /**
   * 校验 WHERE 子句安全性
   */
  public void validateWhereClause(String whereClause) {
    if (whereClause == null || whereClause.isBlank()) {
      return;
    }
    checkDangerousKeywords(whereClause);
    // 防止子查询中包含修改语句
    String upper = whereClause.toUpperCase();
    if (upper.contains("INSERT") || upper.contains("UPDATE") || upper.contains("DELETE")) {
      throw new IllegalArgumentException("WHERE 子句中不允许包含数据修改语句");
    }
  }

  /**
   * 校验数据库访问权限
   */
  public void validateDatabaseAccess(String database) {
    if (database == null || database.isBlank()) {
      throw new IllegalArgumentException("数据库名称不能为空");
    }
    if (!properties.getAllowedDatabases().isEmpty()
        && !properties.getAllowedDatabases().contains(database)) {
      throw new IllegalArgumentException("无权访问数据库: " + database +
                                         "，允许的数据库列表: " + properties.getAllowedDatabases());
    }
  }

  /**
   * 校验表访问权限
   */
  public void validateTableAccess(String tableName) {
    if (tableName == null || tableName.isBlank()) {
      throw new IllegalArgumentException("表名称不能为空");
    }
    if (properties.getBlockedTables().contains(tableName)) {
      throw new IllegalArgumentException("禁止访问表: " + tableName);
    }
  }

  /**
   * 确保 SQL 语句带有 LIMIT 限制
   */
  public String ensureLimit(String sql, int maxRows) {
    if (LIMIT_PATTERN.matcher(sql).find()) {
      return sql;
    }
    return sql.trim().replaceAll(";\\s*$", "") + " LIMIT " + maxRows;
  }

  /**
   * 检查危险关键字
   */
  private void checkDangerousKeywords(String sql) {
    String upperSql = sql.toUpperCase();
    for (String keyword : DANGEROUS_KEYWORDS) {
      if (upperSql.contains(keyword)) {
        throw new IllegalArgumentException("SQL 包含禁止的危险操作: " + keyword);
      }
    }
  }

}
