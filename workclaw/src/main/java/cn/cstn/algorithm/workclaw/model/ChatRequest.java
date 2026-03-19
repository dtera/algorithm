package cn.cstn.algorithm.workclaw.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天请求体
 *
 * @author zhaohuiqiang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

  /**
   * 用户消息
   */
  private String message;

  /**
   * 会话 ID（用于多轮对话）
   */
  private String conversationId;

  /**
   * 使用的 Skill ID（为空则使用默认 Skill）
   */
  private String skillId;

  /**
   * 使用的模型 ID（为空则使用默认模型）
   */
  private String modelId;

  /**
   * 是否流式输出
   */
  @Builder.Default
  private boolean stream = true;

}
