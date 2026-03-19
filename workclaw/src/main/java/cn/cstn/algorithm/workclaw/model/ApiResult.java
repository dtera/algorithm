package cn.cstn.algorithm.workclaw.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一 API 响应体
 *
 * @author zhaohuiqiang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {

  private int code;
  private String message;
  private T data;

  public static <T> ApiResult<T> ok(T data) {
    return ApiResult.<T>builder().code(200).message("success").data(data).build();
  }

  public static <T> ApiResult<T> ok() {
    return ApiResult.<T>builder().code(200).message("success").build();
  }

  public static <T> ApiResult<T> error(String message) {
    return ApiResult.<T>builder().code(500).message(message).build();
  }

  public static <T> ApiResult<T> error(int code, String message) {
    return ApiResult.<T>builder().code(code).message(message).build();
  }

}
