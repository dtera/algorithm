package cn.cstn.algorithm.workclaw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cn.cstn.algorithm.workclaw.model.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;

/**
 * 全局异常处理
 *
 * @author zhaohuiqiang
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiResult<Void> handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) throws IllegalArgumentException {
    // SSE 请求不能返回 ApiResult，跳过处理交由 Flux onErrorResume 处理
    if (isSseRequest(request)) throw e;
    log.warn("参数错误: {}", e.getMessage());
    return ApiResult.error(400, e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiResult<Void> handleException(Exception e, HttpServletRequest request) throws Exception {
    // SSE 请求不能返回 ApiResult，跳过处理交由 Flux onErrorResume 处理
    if (isSseRequest(request)) throw e;
    log.error("系统异常", e);
    return ApiResult.error(500, "系统异常: " + e.getMessage());
  }

  /**
   * 判断是否为 SSE (text/event-stream) 请求
   */
  private boolean isSseRequest(HttpServletRequest request) {
    String accept = request.getHeader("Accept");
    return accept != null && accept.contains(MediaType.TEXT_EVENT_STREAM_VALUE);
  }

}
