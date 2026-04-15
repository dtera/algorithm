<script setup>
import { ref, reactive, nextTick, watch, computed, onUnmounted } from 'vue'
import { chatStream, clearConversation, getConversation } from '@/api.js'
import { Marked } from 'marked'
import { markedHighlight } from 'marked-highlight'
import hljs from 'highlight.js'
import DOMPurify from 'dompurify'
import { cn } from '@/lib/utils'
import Button from '@/components/ui/button.vue'
import Select from '@/components/ui/select.vue'
import { Send, Square, Trash2, Bot, User, Paperclip, Sparkles, X, Copy, Check } from 'lucide-vue-next'

// 配置 marked + 代码高亮
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
  { breaks: true, gfm: true }
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
const messagesEndRef = ref(null)
const fileInputRef = ref(null)
const attachedFiles = ref([])
const copiedCodeBlocks = ref(new Set())
let currentRequest = null

const enabledModels = computed(() => {
  return (props.models || []).filter(m => m.enabled)
})

// 实时渲染流式内容
function renderStreamingContent(content) {
  streamingHtml.value = renderMarkdown(content)
}

// 复制代码块功能
function copyCodeBlock(code) {
  navigator.clipboard.writeText(code).then(() => {
    const timestamp = Date.now()
    copiedCodeBlocks.value.add(timestamp)
    setTimeout(() => {
      copiedCodeBlocks.value.delete(timestamp)
    }, 2000)
  })
}

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

function triggerFileInput() {
  fileInputRef.value?.click()
}

function handleFileSelect(event) {
  const files = event.target.files
  if (!files) return
  for (const file of files) {
    attachedFiles.value.push(file)
  }
  // 重置 input，允许再次选择相同文件
  event.target.value = ''
}

function removeAttachedFile(index) {
  attachedFiles.value.splice(index, 1)
}

function formatFileSize(bytes) {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

watch(() => props.skillId, () => {
  messages.value = []
  isStreaming.value = false
  streamingHtml.value = ''
  emit('update:conversationId', '')
})

watch(() => props.conversationId, async (newId) => {
  // 流式输出过程中，conversationId 变化是由 onMeta 回调触发的，
  // 此时不应重新加载消息，否则会覆盖正在进行的流式内容
  if (loading.value || isStreaming.value) {
    return
  }
  if (newId) {
    try {
      const data = await getConversation(newId)
      loadMessages(data || [])
    } catch (e) {
      console.error('加载会话消息失败:', e)
      messages.value = []
    }
  } else {
    messages.value = []
  }
  streamingHtml.value = ''
})

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
  if ((!text && attachedFiles.value.length === 0) || loading.value) return

  // 构建显示消息：附件信息 + 文本
  let displayContent = ''
  if (attachedFiles.value.length > 0) {
    const fileInfo = attachedFiles.value.map(f => `📎 ${f.name} (${formatFileSize(f.size)})`).join('\n')
    displayContent = fileInfo + (text ? '\n\n' + text : '')
  } else {
    displayContent = text
  }

  messages.value.push({ role: 'user', content: displayContent })
  inputMessage.value = ''
  const filesToSend = [...attachedFiles.value]
  attachedFiles.value = []
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
        // 流式完成：直接关闭流式状态，让模板切换到renderMarkdown(msg.content)
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
        // clear事件：清除之前的内容（如"思考中"提示）
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
    try { await clearConversation(props.conversationId) } catch (e) { console.error('清除会话失败:', e) }
  }
  messages.value = []
  isStreaming.value = false
  streamingHtml.value = ''
  emit('update:conversationId', '')
  emit('conversationUpdated')
}

function scrollToBottom() {
  nextTick(() => {
    messagesEndRef.value?.scrollIntoView({ behavior: 'smooth' })
  })
}

onUnmounted(() => {
  if (currentRequest) currentRequest.abort()
})
</script>

<template>
  <div class="flex h-full min-h-0 flex-col">
    <!-- ── 消息滚动区域 ── -->
    <div ref="messagesRef" class="flex-1 min-h-0 overflow-y-auto">
      <!-- 欢迎页 (参照 cowork-fe 的 chat/index.tsx) -->
      <div v-if="messages.length === 0 && !loading" class="flex h-full flex-col items-center gap-10 overflow-y-auto px-4 pb-8 pt-[20vh]">
        <div class="text-center">
          <div class="mx-auto mb-4 flex size-12 items-center justify-center rounded-2xl bg-primary text-primary-foreground">
            <Sparkles class="size-6" />
          </div>
          <h1 class="text-2xl font-bold tracking-tight text-foreground">有什么可以帮到你？</h1>
          <p class="mt-2 text-sm text-muted-foreground">选择一个技能，开始智能对话</p>
        </div>
      </div>

      <!-- 消息列表 (参照 cowork-fe 的 $sessionId.tsx) -->
      <div v-else class="mx-auto flex max-w-[85%] flex-col px-4 py-6">
        <template v-for="(msg, index) in messages" :key="index">
          <!-- 用户消息 -->
          <div v-if="msg.role === 'user'" :class="cn('flex justify-end', index > 0 && 'mt-6')">
            <div class="max-w-[80%] rounded-2xl bg-primary px-4 py-3 text-primary-foreground">
              <p class="whitespace-pre-wrap break-words text-[15px] leading-relaxed">{{ msg.content }}</p>
            </div>
          </div>

          <!-- AI 消息 -->
          <div v-else :class="cn('flex gap-3', index > 0 && 'mt-6')">
            <!-- AI 头像 -->
            <div class="flex size-7 shrink-0 items-center justify-center rounded-lg bg-muted">
              <Bot class="size-4 text-foreground" />
            </div>
            <!-- AI 消息内容 -->
            <div class="min-w-0 max-w-[80%]">
              <!-- 流式输出中，使用实时渲染的HTML -->
              <template v-if="index === messages.length - 1 && isStreaming">
                <template v-if="streamingHtml">
                  <div class="markdown-body text-[15px] leading-relaxed" v-html="streamingHtml"></div>
                  <span class="inline-block w-[2px] h-[1.1em] bg-primary ml-0.5 animate-pulse align-text-bottom"></span>
                </template>
                <template v-else>
                  <div class="flex items-center gap-1 pt-2">
                    <span class="size-2 rounded-full bg-muted-foreground/40 animate-bounce [animation-delay:0ms]"></span>
                    <span class="size-2 rounded-full bg-muted-foreground/40 animate-bounce [animation-delay:150ms]"></span>
                    <span class="size-2 rounded-full bg-muted-foreground/40 animate-bounce [animation-delay:300ms]"></span>
                  </div>
                </template>
              </template>
              <template v-else>
                <div class="markdown-body text-[15px] leading-relaxed" v-html="renderMarkdown(msg.content)"></div>
              </template>
            </div>
          </div>
        </template>

        <!-- 等待响应（发送请求后，SSE连接建立前） -->
        <div v-if="loading && !isStreaming" class="mt-6 flex gap-3">
          <div class="flex size-7 shrink-0 items-center justify-center rounded-lg bg-muted">
            <Bot class="size-4 text-foreground" />
          </div>
          <div class="flex items-center gap-1 pt-2">
            <span class="size-2 rounded-full bg-muted-foreground/40 animate-bounce [animation-delay:0ms]"></span>
            <span class="size-2 rounded-full bg-muted-foreground/40 animate-bounce [animation-delay:150ms]"></span>
            <span class="size-2 rounded-full bg-muted-foreground/40 animate-bounce [animation-delay:300ms]"></span>
          </div>
        </div>

        <div ref="messagesEndRef" />
      </div>
    </div>

    <!-- ── 统一输入框区域 (固定底部, 参照 cowork-fe) ── -->
    <div class="shrink-0">
      <div class="mx-auto max-w-[85%] px-4 pb-4">
        <div class="flex flex-col rounded-2xl border bg-card shadow-lg transition-shadow focus-within:shadow-xl focus-within:border-ring">
          <!-- 输入区域 -->
          <div class="flex items-end gap-2 p-3">
            <textarea
              ref="inputRef"
              v-model="inputMessage"
              class="flex-1 resize-none bg-transparent text-sm outline-none placeholder:text-muted-foreground max-h-[120px] leading-relaxed"
              placeholder="给 WorkClaw 发消息..."
              rows="1"
              @keydown="handleKeydown"
            />
            <input
              ref="fileInputRef"
              type="file"
              multiple
              class="hidden"
              @change="handleFileSelect"
            />
            <Button v-if="loading" variant="destructive" size="icon" class="size-8 shrink-0 rounded-full" @click="stopGeneration">
              <Square class="size-3.5" />
            </Button>
            <Button v-else size="icon" class="size-8 shrink-0 rounded-full" :disabled="!inputMessage.trim() && attachedFiles.length === 0" @click="sendMessage">
              <Send class="size-3.5" />
            </Button>
          </div>
          <!-- 附件预览 -->
          <div v-if="attachedFiles.length > 0" class="flex flex-wrap gap-1.5 px-3 pb-1">
            <div v-for="(file, i) in attachedFiles" :key="i" class="inline-flex items-center gap-1 rounded-md bg-muted px-2 py-1 text-xs text-muted-foreground">
              <Paperclip class="size-3" />
              <span class="max-w-[120px] truncate">{{ file.name }}</span>
              <span class="text-muted-foreground/60">{{ formatFileSize(file.size) }}</span>
              <button class="ml-0.5 rounded-sm hover:bg-muted-foreground/20" @click="removeAttachedFile(i)">
                <X class="size-3" />
              </button>
            </div>
          </div>
          <!-- 底部工具栏 -->
          <div class="flex items-center justify-between border-t border-border/50 px-3 py-1.5">
            <div class="flex items-center gap-1">
              <Button variant="ghost" size="sm" class="h-7 gap-1.5 px-2 text-xs text-muted-foreground" @click="triggerFileInput">
                <Paperclip class="size-3" />
                附件
              </Button>
            </div>
            <div class="flex items-center gap-2">
              <Select v-model="selectedModelId" class="h-7 min-w-[120px] border-0 bg-transparent text-xs shadow-none focus:ring-0" @update:model-value="onModelChange">
                <option v-for="m in enabledModels" :key="m.id" :value="m.id">{{ m.name }}</option>
              </Select>
              <Button v-if="conversationId" variant="ghost" size="sm" class="h-7 gap-1 px-2 text-xs text-muted-foreground hover:text-destructive" @click="clearChat">
                <Trash2 class="size-3" />
                清除
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
/* Markdown 样式优化 */
.markdown-body {
  line-height: 1.7;
  word-wrap: break-word;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
.markdown-body p { 
  margin: 0 0 16px; 
  line-height: 1.7; 
  color: oklch(0.2 0 0);
}
.dark .markdown-body p {
  color: oklch(0.9 0 0);
}
.markdown-body p:last-child { margin-bottom: 0; }
.markdown-body ul, .markdown-body ol { 
  margin: 12px 0; 
  padding-left: 28px; 
}
.markdown-body li { 
  margin: 6px 0; 
  line-height: 1.7; 
  color: oklch(0.2 0 0);
}
.dark .markdown-body li {
  color: oklch(0.9 0 0);
}
.markdown-body pre { 
  position: relative;
  background: oklch(0.97 0 0) !important; 
  border: 1px solid oklch(0.922 0 0) !important; 
  border-radius: 8px; 
  padding: 16px; 
  overflow-x: auto; 
  margin: 16px 0; 
  font-size: 14px;
  line-height: 1.5;
  box-shadow: 0 1px 3px oklch(0 0 0 / 5%);
}
.dark .markdown-body pre {
  background: oklch(0.145 0 0) !important;
  border-color: oklch(0.3 0 0) !important;
  box-shadow: 0 1px 3px oklch(0 0 0 / 20%);
}
.markdown-body pre code { 
  font-family: 'JetBrains Mono', 'Fira Code', 'Cascadia Code', 'SF Mono', ui-monospace, monospace; 
  font-size: 13px; 
  line-height: 1.5; 
  background: transparent !important;
  padding: 0;
  color: oklch(0.2 0 0);
}
.dark .markdown-body pre code {
  color: oklch(0.9 0 0);
}
.markdown-body code:not(pre code) { 
  font-family: 'JetBrains Mono', 'Fira Code', 'Cascadia Code', 'SF Mono', ui-monospace, monospace; 
  font-size: 13px; 
  background: oklch(0.97 0 0); 
  padding: 2px 6px; 
  border-radius: 4px; 
  border: 1px solid oklch(0.922 0 0);
  color: oklch(0.2 0 0);
}
.dark .markdown-body code:not(pre code) {
  background: oklch(0.269 0 0);
  border-color: oklch(0.4 0 0);
  color: oklch(0.9 0 0);
}
.markdown-body blockquote { 
  border-left: 4px solid oklch(0.488 0.243 264.376); 
  padding: 12px 20px; 
  margin: 16px 0; 
  color: oklch(0.6 0 0);
  background: oklch(0.97 0 0);
  border-radius: 0 8px 8px 0;
}
.dark .markdown-body blockquote {
  background: oklch(0.269 0 0);
  color: oklch(0.8 0 0);
}
.markdown-body table { 
  width: 100%; 
  border-collapse: collapse; 
  margin: 16px 0; 
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px oklch(0 0 0 / 10%);
}
.markdown-body th { 
  padding: 12px 16px; 
  text-align: left; 
  font-weight: 600; 
  font-size: 13px; 
  background: oklch(0.97 0 0);
  border-bottom: 2px solid oklch(0.922 0 0); 
  color: oklch(0.2 0 0);
}
.dark .markdown-body th {
  background: oklch(0.269 0 0);
  border-color: oklch(0.4 0 0);
  color: oklch(0.9 0 0);
}
.markdown-body td { 
  padding: 10px 16px; 
  border-bottom: 1px solid oklch(0.922 0 0); 
  font-size: 14px; 
  color: oklch(0.2 0 0);
}
.dark .markdown-body td {
  border-color: oklch(0.4 0 0);
  color: oklch(0.9 0 0);
}
.markdown-body a { 
  color: oklch(0.488 0.243 264.376); 
  text-decoration: none; 
  font-weight: 500;
}
.markdown-body a:hover { 
  text-decoration: underline; 
  text-underline-offset: 2px;
}
.markdown-body h1 { 
  font-size: 1.6em; 
  font-weight: 700; 
  margin: 24px 0 12px; 
  padding-bottom: 8px;
  border-bottom: 1px solid oklch(0.922 0 0);
  color: oklch(0.2 0 0);
}
.dark .markdown-body h1 {
  border-color: oklch(0.4 0 0);
  color: oklch(0.9 0 0);
}
.markdown-body h2 { 
  font-size: 1.4em; 
  font-weight: 600; 
  margin: 20px 0 10px; 
  color: oklch(0.2 0 0);
}
.dark .markdown-body h2 {
  color: oklch(0.9 0 0);
}
.markdown-body h3 { 
  font-size: 1.2em; 
  font-weight: 600; 
  margin: 16px 0 8px; 
  color: oklch(0.2 0 0);
}
.dark .markdown-body h3 {
  color: oklch(0.9 0 0);
}
.markdown-body hr { 
  border: none; 
  border-top: 1px solid oklch(0.922 0 0); 
  margin: 20px 0; 
}
.dark .markdown-body hr {
  border-color: oklch(0.4 0 0);
}

/* highlight.js 优化主题 */
.markdown-body .hljs {
  background: transparent !important;
  color: oklch(0.2 0 0) !important;
}
.dark .markdown-body .hljs {
  color: oklch(0.9 0 0) !important;
}
.markdown-body .hljs-comment, 
.markdown-body .hljs-quote { 
  color: oklch(0.6 0 0) !important; 
  font-style: italic;
}
.markdown-body .hljs-keyword, 
.markdown-body .hljs-selector-tag, 
.markdown-body .hljs-built_in { 
  color: oklch(0.696 0.17 162.48) !important; 
  font-weight: 600;
}
.markdown-body .hljs-string, 
.markdown-body .hljs-addition { 
  color: oklch(0.645 0.246 16.439) !important; 
}
.markdown-body .hljs-title, 
.markdown-body .hljs-section { 
  color: oklch(0.769 0.188 70.08) !important; 
  font-weight: 600; 
}
.markdown-body .hljs-number, 
.markdown-body .hljs-literal, 
.markdown-body .hljs-type { 
  color: oklch(0.627 0.265 303.9) !important; 
}
.markdown-body .hljs-attr, 
.markdown-body .hljs-variable { 
  color: oklch(0.645 0.246 16.439) !important; 
}
.markdown-body .hljs-function .hljs-title { 
  color: oklch(0.769 0.188 70.08) !important; 
}
.markdown-body .hljs-params { 
  color: oklch(0.6 0 0) !important;
}
.markdown-body .hljs-tag { 
  color: oklch(0.696 0.17 162.48) !important;
}
.markdown-body .hljs-attribute { 
  color: oklch(0.645 0.246 16.439) !important;
}
.markdown-body .hljs-meta { 
  color: oklch(0.6 0 0) !important;
}

/* 流式渲染动画 */
.markdown-body {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0.8; transform: translateY(2px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 响应式优化 */
@media (max-width: 768px) {
  .markdown-body {
    font-size: 14px;
  }
  .markdown-body pre {
    padding: 12px;
    font-size: 12px;
  }
  .markdown-body code:not(pre code) {
    font-size: 12px;
  }
}
</style>