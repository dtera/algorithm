package cn.cstn.algorithm.workclaw.service;

import cn.cstn.algorithm.workclaw.model.ChatRequest;
import cn.cstn.algorithm.workclaw.model.ChatResponse;
import cn.cstn.algorithm.workclaw.model.ModelConfig;
import cn.cstn.algorithm.workclaw.model.SkillConfig;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ReactorClientHttpRequestFactory;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天服务
 * <p>
 * 核心聊天逻辑：根据 Skill 配置组装系统提示词和 MCP 工具，
 * 调用 Spring AI ChatModel 完成对话。支持多轮对话记忆和流式输出。
 * </p>
 *
 * @author zhaohuiqiang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

  private final ChatModel defaultChatModel;
  private final ConfigService configService;
  private final McpClientManager mcpClientManager;

  /**
   * 动态创建的 ChatModel 缓存
   */
  private final Map<String, ChatModel> modelCache = new ConcurrentHashMap<>();

  /**
   * 会话记忆存储 key: conversationId, value: 历史消息列表
   */
  private final Map<String, List<ChatMessage>> conversationMemory = new ConcurrentHashMap<>();

  /**
   * 会话持久化文件路径
   */
  private Path conversationsFile;

  @PostConstruct
  void init() {
    try {
      Path configDir = Paths.get(System.getProperty("user.dir")).getParent().resolve("workclaw-config");
      Files.createDirectories(configDir);
      conversationsFile = configDir.resolve("conversations.json");
      loadConversations();
    } catch (IOException e) {
      log.warn("无法加载会话历史: {}", e.getMessage());
    }
  }

  /**
   * 非流式聊天
   */
  public ChatResponse chat(ChatRequest request) {
    String conversationId = resolveConversationId(request.getConversationId());
    String skillId = request.getSkillId() != null ? request.getSkillId() : "default";

    SkillConfig skill = configService.getSkill(skillId)
      .orElseThrow(() -> new IllegalArgumentException("Skill 不存在: " + skillId));

    List<ToolCallback> toolCallbacks = mcpClientManager.getToolCallbacksForSkill(skillId);
    List<ChatMessage> history = conversationMemory.computeIfAbsent(conversationId, k -> new ArrayList<>());
    ChatModel chatModel = resolveModel(request.getModelId());

    ChatClient.Builder clientBuilder = ChatClient.builder(chatModel);
    if (skill.getSystemPrompt() != null && !skill.getSystemPrompt().isBlank()) {
      clientBuilder.defaultSystem(skill.getSystemPrompt());
    }
    if (!toolCallbacks.isEmpty()) {
      clientBuilder.defaultToolCallbacks(toolCallbacks.toArray(new ToolCallback[0]));
    }

    ChatClient client = clientBuilder.build();

    // 构建包含历史消息的用户 prompt
    String fullPrompt = buildPromptWithHistory(history, request.getMessage());

    String response = client.prompt()
      .user(fullPrompt)
      .advisors(advisor -> advisor.param("chat_memory_conversation_id", conversationId))
      .call()
      .content();

    // 保存到会话记忆
    history.add(new ChatMessage("user", request.getMessage()));
    history.add(new ChatMessage("assistant", response));
    trimHistory(history);
    saveConversations();

    return ChatResponse.builder()
      .content(response)
      .conversationId(conversationId)
      .skillId(skillId)
      .build();
  }

  /**
   * 流式聊天 - 返回结构化 SSE 事件
   * <p>使用 Flux.defer 包裹，确保同步异常也能被转为 SSE 错误事件，
   * 避免全局异常处理器在 text/event-stream 上下文中无法序列化 ApiResult。</p>
   * <p>当有 MCP 工具时，先通过非流式 call() 完成工具调用回合（确保 function calling 正确执行），
   * 再将最终结果以模拟流式方式逐块输出给前端。无工具时仍使用原生流式输出。</p>
   */
  public Flux<ServerSentEvent<String>> chatStreamSSE(ChatRequest request) {
    return Flux.defer(() -> {
      String conversationId = resolveConversationId(request.getConversationId());
      String skillId = request.getSkillId() != null ? request.getSkillId() : "default";

      SkillConfig skill = configService.getSkill(skillId)
        .orElseThrow(() -> new IllegalArgumentException("Skill 不存在: " + skillId));

      List<ToolCallback> toolCallbacks = mcpClientManager.getToolCallbacksForSkill(skillId);
      List<ChatMessage> history = conversationMemory.computeIfAbsent(conversationId, k -> new ArrayList<>());
      ChatModel chatModel = resolveModel(request.getModelId());

      log.info("流式聊天: skill={}, 工具数={}, model={}", skillId, toolCallbacks.size(),
        request.getModelId() != null ? request.getModelId() : "default");

      ChatClient.Builder clientBuilder = ChatClient.builder(chatModel);
      if (skill.getSystemPrompt() != null && !skill.getSystemPrompt().isBlank()) {
        clientBuilder.defaultSystem(skill.getSystemPrompt());
      }
      if (!toolCallbacks.isEmpty()) {
        clientBuilder.defaultToolCallbacks(toolCallbacks.toArray(new ToolCallback[0]));
      }

      ChatClient client = clientBuilder.build();
      String fullPrompt = buildPromptWithHistory(history, request.getMessage());

      // 1. 先发送 meta 事件（包含 conversationId）
      Flux<ServerSentEvent<String>> metaEvent = Flux.just(
        ServerSentEvent.<String>builder()
          .event("meta")
          .data("{\"conversationId\":\"" + conversationId + "\"}")
          .build()
      );

      Flux<ServerSentEvent<String>> contentEvents;

      if (!toolCallbacks.isEmpty()) {
        // === 有工具时：在异步线程中用 call() 完成工具调用，再逐字符推送给前端 ===
        log.info("检测到 {} 个 MCP 工具，使用非流式 call() 确保工具正确调用", toolCallbacks.size());
        contentEvents = Flux.create(sink -> Thread.ofVirtual().name("mcp-call-" + conversationId).start(() -> {
          try {
            // 先发送一个"思考中"的提示
            sink.next(ServerSentEvent.<String>builder()
              .event("delta")
              .data("🔧 正在调用工具，请稍候...\n\n")
              .build());

            String response = client.prompt()
              .user(fullPrompt)
              .advisors(advisor -> advisor.param("chat_memory_conversation_id", conversationId))
              .call()
              .content();

            log.info("工具调用完成，响应长度: {}", response != null ? response.length() : 0);

            if (response != null && !response.isEmpty()) {
              // 先发送一个清除"思考中"提示的特殊事件
              sink.next(ServerSentEvent.<String>builder()
                .event("clear")
                .data("")
                .build());

              // 逐字符推送，模拟真正的流式效果
              int chunkSize = 8; // 每次推送 8 个字符
              for (int i = 0; i < response.length(); i += chunkSize) {
                String chunk = response.substring(i, Math.min(i + chunkSize, response.length()));
                sink.next(ServerSentEvent.<String>builder()
                  .event("delta")
                  .data(chunk)
                  .build());
                // 每块之间短暂停顿，让前端有时间渲染
                try { Thread.sleep(10); } catch (InterruptedException ignored) {}
              }
            }

            // 保存会话记忆
            history.add(new ChatMessage("user", request.getMessage()));
            history.add(new ChatMessage("assistant", response != null ? response : ""));
            trimHistory(history);
            saveConversations();

            sink.complete();
          } catch (Exception e) {
            log.error("工具调用过程异常", e);
            sink.next(ServerSentEvent.<String>builder()
              .event("error")
              .data(e.getMessage() != null ? e.getMessage() : "工具调用失败")
              .build());
            sink.complete();
          }
        }));
      } else {
        // === 无工具时：使用原生流式输出 ===
        StringBuilder fullResponse = new StringBuilder();
        contentEvents = client.prompt()
          .user(fullPrompt)
          .advisors(advisor -> advisor.param("chat_memory_conversation_id", conversationId))
          .stream()
          .content()
          .map(chunk -> {
            fullResponse.append(chunk);
            return ServerSentEvent.<String>builder()
              .event("delta")
              .data(chunk)
              .build();
          })
          .doOnComplete(() -> {
            history.add(new ChatMessage("user", request.getMessage()));
            history.add(new ChatMessage("assistant", fullResponse.toString()));
            trimHistory(history);
            saveConversations();
          });
      }

      // 3. 结束事件
      Flux<ServerSentEvent<String>> doneEvent = Flux.just(
        ServerSentEvent.<String>builder()
          .event("done")
          .data("[DONE]")
          .build()
      );

      return Flux.concat(metaEvent, contentEvents, doneEvent);
    }).onErrorResume(e -> {
      log.error("流式聊天异常", e);
      // 将异常转为 SSE error 事件，避免全局异常处理器在 SSE 上下文中序列化失败
      return Flux.just(
        ServerSentEvent.<String>builder()
          .event("error")
          .data(e.getMessage() != null ? e.getMessage() : "未知错误")
          .build(),
        ServerSentEvent.<String>builder()
          .event("done")
          .data("[DONE]")
          .build()
      );
    });
  }

  /**
   * 获取所有会话列表（附带标题摘要）
   */
  public List<Map<String, String>> listConversationsWithTitle() {
    List<Map<String, String>> result = new ArrayList<>();
    for (Map.Entry<String, List<ChatMessage>> entry : conversationMemory.entrySet()) {
      List<ChatMessage> msgs = entry.getValue();
      if (msgs.isEmpty()) continue;
      // 取第一条用户消息作为标题
      String title = msgs.stream()
        .filter(m -> "user".equals(m.role()))
        .map(ChatMessage::content)
        .findFirst()
        .orElse("新对话");
      if (title.length() > 50) title = title.substring(0, 50) + "...";
      result.add(Map.of(
        "id", entry.getKey(),
        "title", title,
        "messageCount", String.valueOf(msgs.size())
      ));
    }
    // 按消息数量降序
    result.sort((a, b) -> Integer.parseInt(b.get("messageCount")) - Integer.parseInt(a.get("messageCount")));
    return result;
  }

  /**
   * 获取指定会话的历史消息
   */
  public List<Map<String, String>> getConversationMessages(String conversationId) {
    List<ChatMessage> msgs = conversationMemory.get(conversationId);
    if (msgs == null) return List.of();
    return msgs.stream()
      .map(m -> Map.of("role", m.role(), "content", m.content()))
      .toList();
  }

  /**
   * 清除会话记忆
   */
  public void clearConversation(String conversationId) {
    conversationMemory.remove(conversationId);
    saveConversations();
  }

  /**
   * 获取所有会话 ID 列表
   */
  public Set<String> listConversations() {
    return conversationMemory.keySet();
  }

  /**
   * 根据 modelId 解析对应的 ChatModel
   */
  private ChatModel resolveModel(String modelId) {
    if (modelId == null || modelId.isBlank()) {
      return defaultChatModel;
    }
    return modelCache.computeIfAbsent(modelId, id -> {
      ModelConfig config = configService.getModel(id)
        .orElseThrow(() -> new IllegalArgumentException("模型不存在: " + id));
      return createChatModel(config);
    });
  }

  /**
   * 根据 ModelConfig 动态创建 OpenAiChatModel
   */
  private ChatModel createChatModel(ModelConfig config) {
    HttpHeaders customHeaders = new HttpHeaders();
    if (config.getHeaders() != null && !config.getHeaders().isEmpty()) {
      config.getHeaders().forEach(customHeaders::add);
    }

    // 配置自定义 RestClient，设置更长的超时时间（MCP 工具调用涉及多轮交互，耗时较长）
    // 使用配置好的 HttpClient，避免 macOS 上的 UDP DNS 解析问题
    ReactorClientHttpRequestFactory requestFactory = new ReactorClientHttpRequestFactory(
      cn.cstn.algorithm.workclaw.config.NettyDnsConfig.createHttpClient()
    );
    requestFactory.setReadTimeout(Duration.ofMinutes(5));
    requestFactory.setConnectTimeout(Duration.ofSeconds(30));

    RestClient.Builder restClientBuilder = RestClient.builder()
      .requestFactory(requestFactory);

    OpenAiApi openAiApi = OpenAiApi.builder()
      .baseUrl(config.getBaseUrl())
      .apiKey(config.getApiKey())
      .headers(customHeaders)
      .restClientBuilder(restClientBuilder)
      .build();

    OpenAiChatOptions options = OpenAiChatOptions.builder()
      .model(config.getModel())
      .temperature(config.getTemperature())
      .build();

    log.info("动态创建 ChatModel: {} -> {} (model={})", config.getName(), config.getBaseUrl(), config.getModel());
    return OpenAiChatModel.builder()
      .openAiApi(openAiApi)
      .defaultOptions(options)
      .build();
  }

  /**
   * 清除模型缓存（当模型配置变更时调用）
   */
  public void evictModelCache(String modelId) {
    modelCache.remove(modelId);
    log.info("已清除模型缓存: {}", modelId);
  }

  /**
   * 构建包含历史上下文的 prompt
   */
  private String buildPromptWithHistory(List<ChatMessage> history, String currentMessage) {
    if (history.isEmpty()) return currentMessage;
    StringBuilder sb = new StringBuilder();
    sb.append("[以下是之前的对话历史]\n");
    for (ChatMessage msg : history) {
      sb.append(msg.role().equals("user") ? "用户: " : "助手: ");
      sb.append(msg.content()).append("\n\n");
    }
    sb.append("[以下是当前问题]\n");
    sb.append(currentMessage);
    return sb.toString();
  }

  private String resolveConversationId(String id) {
    return (id != null && !id.isBlank()) ? id :
      UUID.randomUUID().toString().replace("-", "").substring(0, 16);
  }

  private void trimHistory(List<ChatMessage> history) {
    while (history.size() > 20) {
      history.removeFirst();
    }
  }

  /**
   * 加载会话历史
   */
  private void loadConversations() {
    if (conversationsFile == null || !Files.exists(conversationsFile)) return;
    try {
      String json = FileUtil.readUtf8String(conversationsFile.toFile());
      JSONObject data = JSONUtil.parseObj(json);
      data.forEach((convId, value) -> {
        List<ChatMessage> list = new ArrayList<>();
        JSONArray msgs = (JSONArray) value;
        for (int i = 0; i < msgs.size(); i++) {
          JSONObject m = msgs.getJSONObject(i);
          list.add(new ChatMessage(m.getStr("role"), m.getStr("content")));
        }
        conversationMemory.put(convId, list);
      });
      log.info("已加载 {} 个会话历史", conversationMemory.size());
    } catch (Exception e) {
      log.warn("加载会话历史失败: {}", e.getMessage());
    }
  }

  /**
   * 保存会话历史
   */
  private void saveConversations() {
    if (conversationsFile == null) return;
    try {
      JSONObject data = new JSONObject(true); // 有序
      conversationMemory.forEach((convId, msgs) -> {
        JSONArray arr = new JSONArray();
        for (ChatMessage m : msgs) {
          JSONObject obj = new JSONObject();
          obj.set("role", m.role());
          obj.set("content", m.content());
          arr.add(obj);
        }
        data.set(convId, arr);
      });
      FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(data), conversationsFile.toFile());
    } catch (Exception e) {
      log.warn("保存会话历史失败: {}", e.getMessage());
    }
  }

  record ChatMessage(String role, String content) {
  }

}
