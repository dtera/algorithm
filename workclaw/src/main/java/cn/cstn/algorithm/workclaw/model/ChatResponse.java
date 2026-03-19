package cn.cstn.algorithm.workclaw.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 聊天响应体
 *
 * @author zhaohuiqiang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

  /**
   * 回复内容
   */
  private String content;

  /**
   * 会话 ID
   */
  private String conversationId;

  /**
   * 使用的 Skill ID
   */
  private String skillId;

  /**
   * 工具调用信息（调试用）
   */
  private List<ToolCallInfo> toolCalls;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ToolCallInfo {
    private String name;
    private String arguments;
    private String result;
  }

}
