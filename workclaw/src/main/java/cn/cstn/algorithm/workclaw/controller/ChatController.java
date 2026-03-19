package cn.cstn.algorithm.workclaw.controller;

import cn.cstn.algorithm.workclaw.model.ApiResult;
import cn.cstn.algorithm.workclaw.model.ChatRequest;
import cn.cstn.algorithm.workclaw.model.ChatResponse;
import cn.cstn.algorithm.workclaw.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * 聊天 API 控制器
 *
 * @author zhaohuiqiang
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

  private final ChatService chatService;

  /**
   * 非流式聊天
   */
  @PostMapping
  public ApiResult<ChatResponse> chat(@RequestBody ChatRequest request) {
    return ApiResult.ok(chatService.chat(request));
  }

  /**
   * 流式聊天（SSE）
   * 返回结构化的 SSE 事件：
   * - event: meta → 包含 conversationId
   * - event: delta → 文本增量
   * - event: done → 流结束
   */
  @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ServerSentEvent<String>> chatStream(@RequestBody ChatRequest request) {
    return chatService.chatStreamSSE(request);
  }

  /**
   * 获取会话列表（包含标题摘要）
   */
  @GetMapping("/conversations")
  public ApiResult<List<Map<String, String>>> listConversations() {
    return ApiResult.ok(chatService.listConversationsWithTitle());
  }

  /**
   * 获取会话历史消息
   */
  @GetMapping("/conversations/{conversationId}/messages")
  public ApiResult<List<Map<String, String>>> getConversationMessages(@PathVariable String conversationId) {
    return ApiResult.ok(chatService.getConversationMessages(conversationId));
  }

  /**
   * 清除指定会话记忆
   */
  @DeleteMapping("/conversations/{conversationId}")
  public ApiResult<Void> clearConversation(@PathVariable String conversationId) {
    chatService.clearConversation(conversationId);
    return ApiResult.ok();
  }

}
