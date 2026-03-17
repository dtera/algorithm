package cn.cstn.algorithm.mcp.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * MCP 工具统一返回结果
 * <p>
 * 所有 MCP 工具方法统一使用此类型作为返回值，
 * 便于 AI 模型解析和前端展示。
 * </p>
 *
 * @author zhaohuiqiang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResult {

  /**
   * 是否成功
   */
  private boolean success;

  /**
   * 错误信息（失败时有值）
   */
  private String errorMessage;

  /**
   * 结果数据
   */
  private List<Map<String, Object>> data;

  /**
   * 结果行数
   */
  private int rowCount;

  /**
   * 执行时间
   */
  private String timestamp;

  /**
   * 创建成功结果
   */
  public static QueryResult success(List<Map<String, Object>> data) {
    return success(data, data != null ? data.size() : 0);
  }

  /**
   * 创建成功结果（带行数）
   */
  public static QueryResult success(List<Map<String, Object>> data, int rowCount) {
    return QueryResult.builder()
      .success(true)
      .data(data != null ? data : Collections.emptyList())
      .rowCount(rowCount)
      .timestamp(LocalDateTime.now().toString())
      .build();
  }

  /**
   * 创建失败结果
   */
  public static QueryResult error(String errorMessage) {
    return QueryResult.builder()
      .success(false)
      .errorMessage(errorMessage)
      .data(Collections.emptyList())
      .rowCount(0)
      .timestamp(LocalDateTime.now().toString())
      .build();
  }

}
