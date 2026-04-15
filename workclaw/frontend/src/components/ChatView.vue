<template>
  <div class="chat-view">
    <!-- 顶部栏 -->
    <div class="chat-header">
      <div class="header-info">
        <h2>{{ skillName }}</h2>
        <span v-if="conversationId" class="conv-id">会话: {{ conversationId.substring(0, 8) }}...</span>
      </div>
      <div class="header-actions">
        <!-- 模型选择下拉框 -->
        <div class="model-selector">
          <label class="model-label">🧠 模型</label>
          <select v-model="selectedModelId" class="model-select" @change="onModelChange">
            <option v-for="m in enabledModels" :key="m.id" :value="m.id">
              {{ m.name }}
            </option>
          </select>
        </div>
        <button v-if="conversationId" class="clear-btn" @click="clearChat">🗑️ 清除记录</button>
      </div>
    </div>

    <!-- 消息列表 -->
    <div class="messages" ref="messagesRef">
      <div v-if="messages.length === 0 && !loading" class="welcome">
        <div class="welcome-icon">🐾</div>
        <h2>欢迎使用 WorkClaw</h2>
        <p>选择一个技能，开始智能对话</p>
      </div>

      <div
        v-for="(msg, index) in displayMessages"
        :key="index"
        class="message"
        :class="msg.role"
      >
        <div class="message-avatar">
          {{ msg.role === 'user' ? '👤' : '🤖' }}
        </div>
        <div class="message-body">
          <div v-if="msg.role === 'assistant'" class="markdown-body" v-html="renderMarkdown(msg.content)"></div>
          <div v-else class="user-text">{{ msg.content }}</div>
        </div>
      </div>

      <!-- 流式输出中：实时 Markdown 渲染 -->
      <div v-if="loading && isStreaming" class="message assistant">
        <div class="message-avatar">🤖</div>
        <div class="message-body">
          <template v-if="streamingHtml">
            <div class="markdown-body" v-html="streamingHtml"></div>
            <span class="streaming-cursor"></span>
          </template>
          <template v-else>
            <div class="typing-indicator">
              <span></span><span></span><span></span>
            </div>
          </template>
        </div>
      </div>

      <!-- 等待响应中的光标指示 -->
      <div v-if="loading && !isStreaming" class="message assistant">
        <div class="message-avatar">🤖</div>
        <div class="message-body">
          <div class="typing-indicator">
            <span></span><span></span><span></span>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="chat-input-area">
      <div class="input-wrapper">
        <textarea
          v-model="inputMessage"
          class="chat-input"
          placeholder="输入消息... (Enter 发送, Shift+Enter 换行)"
          @keydown="handleKeydown"
          rows="1"
          ref="inputRef"
        ></textarea>
        <button v-if="loading" class="stop-btn" @click="stopGeneration" title="停止生成">
          ⏹️
        </button>
        <button class="send-btn" @click="sendMessage" :disabled="loading || !inputMessage.trim()">
          {{ loading ? '⏳' : '🚀' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, watch, computed, onUnmounted } from 'vue'
import { chatStream, clearConversation } from '../api.js'
import { Marked } from 'marked'
import { markedHighlight } from 'marked-highlight'
import hljs from 'highlight.js'
import DOMPurify from 'dompurify'

// 配置 marked + 代码高亮扩展
const marked = new Marked(
  markedHighlight({
    langPrefix: 'hljs language-',
    highlight(code, lang) {
      if (lang && hljs.getLanguage(lang)) {
        try { return hljs.highlight(code, { language: lang }).value } catch {}
      }
      try { return hljs.highlightAuto(code).value } catch {}
      return code
    }
  }),
  {
    breaks: true,
    gfm: true
  }
)

const props = defineProps({
  skillId: String,
  skillName: String,
  conversationId: String,
  models: { type: Array, default: () => [] },
  modelId: String
})

const emit = defineEmits(['update:conversationId', 'update:modelId', 'conversationUpdated'])

const selectedModelId = ref(props.modelId || '')
const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const isStreaming = ref(false)
const streamingHtml = ref('')
const messagesRef = ref(null)
const inputRef = ref(null)
let currentRequest = null

// 显示消息列表
const displayMessages = computed(() => {
  return messages.value
})

// 实时渲染流式内容
function renderStreamingContent(content) {
  streamingHtml.value = renderMarkdown(content)
}

// 计算启用的模型列表
const enabledModels = computed(() => {
  return (props.models || []).filter(m => m.enabled)
})

// 同步父组件传入的 modelId
watch(() => props.modelId, (newVal) => {
  if (newVal) {
    selectedModelId.value = newVal
  } else {
    // 当父组件未指定modelId时，默认选中第一个启用的模型
    const firstEnabled = enabledModels.value[0]
    selectedModelId.value = firstEnabled ? firstEnabled.id : ''
  }
})

// 当模型列表变化时，如果当前没有选中模型，默认选中第一个
watch(enabledModels, (models) => {
  if (!selectedModelId.value && models.length > 0) {
    selectedModelId.value = models[0].id
    emit('update:modelId', selectedModelId.value)
  }
})

function onModelChange() {
  emit('update:modelId', selectedModelId.value)
}

// 监听 skillId 变化
watch(() => props.skillId, () => {
  messages.value = []
  isStreaming.value = false
  streamingHtml.value = ''
  emit('update:conversationId', '')
})

// 暴露 loadMessages 给父组件
function loadMessages(msgs) {
  messages.value = msgs.map(m => ({ role: m.role, content: m.content }))
  nextTick(() => scrollToBottom())
}

defineExpose({ loadMessages })

function renderMarkdown(content) {
  if (!content) return ''
  try {
    const html = marked.parse(content)
    return DOMPurify.sanitize(html)
  } catch {
    return content
  }
}

function handleKeydown(e) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

function stopGeneration() {
  if (currentRequest) {
    currentRequest.abort()
    currentRequest = null
  }
  isStreaming.value = false
  streamingHtml.value = ''
  loading.value = false
}

async function sendMessage() {
  const text = inputMessage.value.trim()
  if (!text || loading.value) return

  messages.value.push({ role: 'user', content: text })
  inputMessage.value = ''
  loading.value = true
  isStreaming.value = true
  streamingHtml.value = ''
  scrollToBottom()

  // 创建assistant消息，使用reactive确保Vue追踪属性变化
  const assistantMsg = reactive({ role: 'assistant', content: '' })
  messages.value.push(assistantMsg)

  const convId = props.conversationId || ''

  try {
    currentRequest = chatStream(
      { message: text, conversationId: convId, skillId: props.skillId, modelId: selectedModelId.value || undefined, stream: true },
      (chunk) => {
        assistantMsg.content += chunk
        renderStreamingContent(assistantMsg.content)
        scrollToBottom()
      },
      () => {
        // 流式完成：关闭流式状态
        isStreaming.value = false
        streamingHtml.value = ''
        loading.value = false
        currentRequest = null
        scrollToBottom()
        emit('conversationUpdated')
      },
      (err) => {
        console.error('流式消息错误:', err)
        if (!assistantMsg.content) {
          assistantMsg.content = '⚠️ 消息接收失败: ' + (err.message || '未知错误')
        }
        isStreaming.value = false
        streamingHtml.value = ''
        loading.value = false
        currentRequest = null
      },
      (meta) => {
        if (meta.conversationId && !props.conversationId) {
          emit('update:conversationId', meta.conversationId)
        }
      },
      () => {
        // clear事件
        assistantMsg.content = ''
        streamingHtml.value = ''
      }
    )
  } catch (e) {
    console.error('发送失败:', e)
    assistantMsg.content = '⚠️ 发送失败: ' + (e.message || '未知错误')
    isStreaming.value = false
    streamingHtml.value = ''
    loading.value = false
  }
}

async function clearChat() {
  if (props.conversationId) {
    try {
      await clearConversation(props.conversationId)
    } catch (e) {
      console.error('清除会话失败:', e)
    }
  }
  messages.value = []
  isStreaming.value = false
  streamingHtml.value = ''
  emit('update:conversationId', '')
  emit('conversationUpdated')
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

onUnmounted(() => {
  if (currentRequest) {
    currentRequest.abort()
  }
})
</script>

<style scoped>
.chat-view {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  border-bottom: 1px solid var(--border);
  background: var(--bg-secondary);
}

.header-info h2 {
  font-size: 16px;
  font-weight: 600;
}

.conv-id {
  font-size: 12px;
  color: var(--text-secondary);
}

.clear-btn {
  background: none;
  border: 1px solid var(--border);
  color: var(--text-secondary);
  padding: 6px 12px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}

.clear-btn:hover {
  border-color: var(--danger);
  color: var(--danger);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.model-selector {
  display: flex;
  align-items: center;
  gap: 6px;
}

.model-label {
  font-size: 12px;
  color: var(--text-secondary);
  white-space: nowrap;
}

.model-select {
  background: var(--bg-card);
  border: 1px solid var(--border);
  color: var(--text-primary);
  padding: 5px 10px;
  border-radius: var(--radius-sm);
  font-size: 13px;
  cursor: pointer;
  outline: none;
  min-width: 140px;
  transition: border-color 0.2s;
}

.model-select:focus {
  border-color: var(--accent);
}

.model-select option {
  background: var(--bg-secondary);
  color: var(--text-primary);
}

/* ==================== 消息区域 ==================== */
.messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px 0;
}

.welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  color: var(--text-secondary);
}

.welcome-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.welcome h2 {
  font-size: 24px;
  margin-bottom: 8px;
  color: var(--text-primary);
}

.message {
  display: flex;
  gap: 16px;
  padding: 24px 48px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.message.user {
  background: rgba(108, 99, 255, 0.04);
}

.message.assistant {
  background: transparent;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
  background: var(--bg-card);
  border: 1px solid var(--border);
}

.message.user .message-avatar {
  background: var(--accent);
  border: none;
}

.message-body {
  flex: 1;
  min-width: 0;
  line-height: 1.7;
  font-size: 15px;
  padding-top: 6px;
}

.user-text {
  white-space: pre-wrap;
  word-break: break-word;
}

/* ==================== Markdown 美化 (仿 DeepSeek 风格) ==================== */
.markdown-body :deep(p) {
  margin: 0 0 12px;
  line-height: 1.8;
}

.markdown-body :deep(p:last-child) {
  margin-bottom: 0;
}

/* 代码块 */
.markdown-body :deep(pre) {
  background: var(--code-bg, #0d1117);
  border: 1px solid var(--code-border, #30363d);
  border-radius: 8px;
  padding: 16px;
  overflow-x: auto;
  margin: 12px 0;
  position: relative;
}

.markdown-body :deep(pre code) {
  font-family: 'JetBrains Mono', 'Fira Code', 'SF Mono', 'Cascadia Code', Menlo, Consolas, monospace;
  font-size: 13px;
  line-height: 1.6;
  color: var(--code-text, #c9d1d9);
  background: none;
  padding: 0;
  border: none;
  border-radius: 0;
}

/* 行内代码 */
.markdown-body :deep(code) {
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 13px;
  background: rgba(110, 118, 129, 0.2);
  color: #f0883e;
  padding: 2px 6px;
  border-radius: 4px;
  border: 1px solid rgba(110, 118, 129, 0.15);
}

/* 列表 */
.markdown-body :deep(ul),
.markdown-body :deep(ol) {
  padding-left: 24px;
  margin: 8px 0 12px;
}

.markdown-body :deep(li) {
  margin-bottom: 4px;
  line-height: 1.7;
}

.markdown-body :deep(li > p) {
  margin-bottom: 4px;
}

/* 标题 */
.markdown-body :deep(h1) {
  font-size: 1.5em;
  font-weight: 700;
  margin: 20px 0 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border);
}

.markdown-body :deep(h2) {
  font-size: 1.3em;
  font-weight: 600;
  margin: 18px 0 10px;
  padding-bottom: 6px;
  border-bottom: 1px solid var(--border);
}

.markdown-body :deep(h3) {
  font-size: 1.15em;
  font-weight: 600;
  margin: 16px 0 8px;
}

.markdown-body :deep(h4),
.markdown-body :deep(h5),
.markdown-body :deep(h6) {
  font-size: 1em;
  font-weight: 600;
  margin: 12px 0 6px;
}

/* 引用块 */
.markdown-body :deep(blockquote) {
  border-left: 4px solid var(--accent);
  padding: 8px 16px;
  margin: 12px 0;
  color: var(--text-secondary);
  background: rgba(108, 99, 255, 0.05);
  border-radius: 0 8px 8px 0;
}

.markdown-body :deep(blockquote p:last-child) {
  margin-bottom: 0;
}

/* 表格 */
.markdown-body :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 12px 0;
  border: 1px solid var(--border);
  border-radius: 8px;
  overflow: hidden;
}

.markdown-body :deep(thead) {
  background: var(--bg-secondary);
}

.markdown-body :deep(th) {
  padding: 10px 14px;
  text-align: left;
  font-weight: 600;
  font-size: 13px;
  border-bottom: 2px solid var(--border);
}

.markdown-body :deep(td) {
  padding: 8px 14px;
  border-bottom: 1px solid var(--border);
  font-size: 14px;
}

.markdown-body :deep(tr:last-child td) {
  border-bottom: none;
}

.markdown-body :deep(tr:hover td) {
  background: rgba(108, 99, 255, 0.03);
}

/* 水平线 */
.markdown-body :deep(hr) {
  border: none;
  border-top: 1px solid var(--border);
  margin: 20px 0;
}

/* 链接 */
.markdown-body :deep(a) {
  color: var(--accent);
  text-decoration: none;
}

.markdown-body :deep(a:hover) {
  text-decoration: underline;
}

/* 图片 */
.markdown-body :deep(img) {
  max-width: 100%;
  border-radius: 8px;
  margin: 8px 0;
}

/* 加粗和斜体 */
.markdown-body :deep(strong) {
  font-weight: 600;
  color: var(--text-primary);
}

.markdown-body :deep(em) {
  font-style: italic;
  color: var(--text-secondary);
}

/* ==================== highlight.js 语法高亮 (GitHub Dark 主题) ==================== */
.markdown-body :deep(.hljs-comment),
.markdown-body :deep(.hljs-quote) {
  color: #8b949e;
}

.markdown-body :deep(.hljs-variable),
.markdown-body :deep(.hljs-template-variable),
.markdown-body :deep(.hljs-tag),
.markdown-body :deep(.hljs-name),
.markdown-body :deep(.hljs-selector-id),
.markdown-body :deep(.hljs-selector-class),
.markdown-body :deep(.hljs-regexp),
.markdown-body :deep(.hljs-deletion) {
  color: #ff7b72;
}

.markdown-body :deep(.hljs-number),
.markdown-body :deep(.hljs-built_in),
.markdown-body :deep(.hljs-literal),
.markdown-body :deep(.hljs-type),
.markdown-body :deep(.hljs-params),
.markdown-body :deep(.hljs-meta),
.markdown-body :deep(.hljs-link) {
  color: #d2a8ff;
}

.markdown-body :deep(.hljs-attribute) {
  color: #79c0ff;
}

.markdown-body :deep(.hljs-string),
.markdown-body :deep(.hljs-symbol),
.markdown-body :deep(.hljs-bullet),
.markdown-body :deep(.hljs-addition) {
  color: #a5d6ff;
}

.markdown-body :deep(.hljs-title),
.markdown-body :deep(.hljs-section) {
  color: #d2a8ff;
  font-weight: bold;
}

.markdown-body :deep(.hljs-keyword),
.markdown-body :deep(.hljs-selector-tag) {
  color: #ff7b72;
}

.markdown-body :deep(.hljs-emphasis) {
  font-style: italic;
}

.markdown-body :deep(.hljs-strong) {
  font-weight: bold;
}

/* ==================== 打字指示器 ==================== */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 8px 0;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: var(--text-secondary);
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

/* 流式输出光标闪烁 */
.streaming-cursor {
  display: inline-block;
  width: 2px;
  height: 1em;
  background: var(--accent);
  margin-left: 2px;
  vertical-align: text-bottom;
  animation: cursor-blink 1s step-end infinite;
}

@keyframes cursor-blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

@keyframes typing {
  0%, 60%, 100% { opacity: 0.3; transform: scale(0.8); }
  30% { opacity: 1; transform: scale(1); }
}

/* ==================== 输入区域 ==================== */
.chat-input-area {
  padding: 16px 24px 20px;
  border-top: 1px solid var(--border);
  background: var(--bg-secondary);
}

.input-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 8px 12px;
  transition: border-color 0.2s;
  max-width: 1200px;
  margin: 0 auto;
}

.input-wrapper:focus-within {
  border-color: var(--accent);
  box-shadow: 0 0 0 2px rgba(108, 99, 255, 0.15);
}

.chat-input {
  flex: 1;
  background: none;
  border: none;
  color: var(--text-primary);
  font-size: 14px;
  line-height: 1.5;
  resize: none;
  outline: none;
  max-height: 120px;
  font-family: inherit;
}

.chat-input::placeholder {
  color: var(--text-secondary);
}

.send-btn, .stop-btn {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;
}

.send-btn {
  background: var(--accent);
}

.send-btn:hover:not(:disabled) {
  background: var(--accent-hover);
  transform: scale(1.05);
}

.send-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.stop-btn {
  background: var(--danger);
  color: white;
}

.stop-btn:hover {
  background: #d32f2f;
  transform: scale(1.05);
}
</style>
