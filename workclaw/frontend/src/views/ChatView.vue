<script setup>
import { ref, nextTick, watch, computed, onUnmounted } from 'vue'
import { chatStream, clearConversation, getConversation } from '@/api.js'
import { Marked } from 'marked'
import { markedHighlight } from 'marked-highlight'
import hljs from 'highlight.js'
import DOMPurify from 'dompurify'
import { cn } from '@/lib/utils'
import Button from '@/components/ui/button.vue'
import Select from '@/components/ui/select.vue'
import { Send, Square, Trash2, Bot, User, Paperclip, Sparkles, X } from 'lucide-vue-next'

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
const streamingContent = ref('')
const renderedStreamingHtml = ref('')
const messagesRef = ref(null)
const inputRef = ref(null)
const messagesEndRef = ref(null)
const fileInputRef = ref(null)
const attachedFiles = ref([])
let currentRequest = null
let renderTimer = null

const enabledModels = computed(() => {
  return (props.models || []).filter(m => m.enabled)
})

let lastRenderedContent = ''
function throttledRenderStreaming(content) {
  if (content === lastRenderedContent) return
  if (!renderTimer) {
    lastRenderedContent = content
    renderedStreamingHtml.value = renderMarkdown(content)
    renderTimer = setTimeout(() => {
      renderTimer = null
      if (streamingContent.value && streamingContent.value !== lastRenderedContent) {
        lastRenderedContent = streamingContent.value
        renderedStreamingHtml.value = renderMarkdown(streamingContent.value)
      }
    }, 100)
  }
}

watch(() => props.modelId, (newVal) => {
  selectedModelId.value = newVal || ''
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
  streamingContent.value = ''
  emit('update:conversationId', '')
})

watch(() => props.conversationId, async (newId) => {
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
  streamingContent.value = ''
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
  streamingContent.value = ''
  scrollToBottom()

  const assistantMsg = { role: 'assistant', content: '' }
  messages.value.push(assistantMsg)

  const convId = props.conversationId || ''

  try {
    currentRequest = chatStream(
      { message: text, conversationId: convId, skillId: props.skillId, modelId: selectedModelId.value || undefined, stream: true },
      (chunk) => {
        assistantMsg.content += chunk
        streamingContent.value = assistantMsg.content
        throttledRenderStreaming(assistantMsg.content)
        scrollToBottom()
      },
      () => {
        if (renderTimer) { clearTimeout(renderTimer); renderTimer = null }
        loading.value = false
        streamingContent.value = ''
        renderedStreamingHtml.value = ''
        lastRenderedContent = ''
        currentRequest = null
        scrollToBottom()
        emit('conversationUpdated')
      },
      (err) => {
        console.error('流式消息错误:', err)
        if (!assistantMsg.content) {
          assistantMsg.content = '⚠️ 消息接收失败: ' + (err.message || '未知错误')
        }
        if (renderTimer) { clearTimeout(renderTimer); renderTimer = null }
        loading.value = false
        streamingContent.value = ''
        renderedStreamingHtml.value = ''
        lastRenderedContent = ''
        currentRequest = null
      },
      (meta) => {
        if (meta.conversationId && !props.conversationId) {
          emit('update:conversationId', meta.conversationId)
        }
      },
      () => {
        assistantMsg.content = ''
        streamingContent.value = ''
        renderedStreamingHtml.value = ''
        lastRenderedContent = ''
      }
    )
  } catch (e) {
    console.error('发送失败:', e)
    assistantMsg.content = '⚠️ 发送失败: ' + (e.message || '未知错误')
    loading.value = false
    streamingContent.value = ''
    lastRenderedContent = ''
  }
}

async function clearChat() {
  if (props.conversationId) {
    try { await clearConversation(props.conversationId) } catch (e) { console.error('清除会话失败:', e) }
  }
  messages.value = []
  streamingContent.value = ''
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
  if (renderTimer) { clearTimeout(renderTimer); renderTimer = null }
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
              <!-- 判断是否流式输出中（最后一条 assistant 消息） -->
              <template v-if="index === messages.length - 1 && loading && streamingContent">
                <div class="markdown-body text-[15px] leading-relaxed" v-html="renderedStreamingHtml"></div>
                <span class="inline-block w-[2px] h-[1.1em] bg-primary ml-0.5 animate-pulse align-text-bottom"></span>
              </template>
              <template v-else>
                <div class="markdown-body text-[15px] leading-relaxed" v-html="renderMarkdown(msg.content)"></div>
              </template>
            </div>
          </div>
        </template>

        <!-- 等待响应（无流式内容） -->
        <div v-if="loading && !streamingContent" class="mt-6 flex gap-3">
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
                <option value="">默认模型</option>
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
/* Markdown 样式 */
.markdown-body p { margin: 0 0 12px; line-height: 1.75; }
.markdown-body p:last-child { margin-bottom: 0; }
.markdown-body ul, .markdown-body ol { margin: 8px 0; padding-left: 24px; }
.markdown-body li { margin: 4px 0; line-height: 1.75; }
.markdown-body pre { background: oklch(0.145 0 0); border: 1px solid oklch(1 0 0 / 10%); border-radius: 10px; padding: 16px; overflow-x: auto; margin: 12px 0; }
.markdown-body pre code { font-family: 'JetBrains Mono', 'Fira Code', ui-monospace, monospace; font-size: 13px; line-height: 1.6; }
.markdown-body code { font-family: 'JetBrains Mono', 'Fira Code', ui-monospace, monospace; font-size: 13px; background: oklch(1 0 0 / 10%); padding: 2px 6px; border-radius: 4px; }
.markdown-body blockquote { border-left: 4px solid oklch(0.488 0.243 264.376); padding: 8px 16px; margin: 12px 0; color: oklch(0.708 0 0); }
.markdown-body table { width: 100%; border-collapse: collapse; margin: 12px 0; }
.markdown-body th { padding: 10px 14px; text-align: left; font-weight: 600; font-size: 13px; border-bottom: 2px solid oklch(1 0 0 / 10%); }
.markdown-body td { padding: 8px 14px; border-bottom: 1px solid oklch(1 0 0 / 10%); font-size: 14px; }
.markdown-body a { color: oklch(0.488 0.243 264.376); text-decoration: none; }
.markdown-body a:hover { text-decoration: underline; }
.markdown-body h1 { font-size: 1.5em; font-weight: 700; margin: 16px 0 8px; }
.markdown-body h2 { font-size: 1.3em; font-weight: 600; margin: 14px 0 6px; }
.markdown-body h3 { font-size: 1.15em; font-weight: 600; margin: 12px 0 4px; }
.markdown-body hr { border: none; border-top: 1px solid oklch(1 0 0 / 10%); margin: 16px 0; }

/* highlight.js 暗色主题 */
.markdown-body .hljs-comment, .markdown-body .hljs-quote { color: #8b949e; }
.markdown-body .hljs-keyword, .markdown-body .hljs-selector-tag { color: #ff7b72; }
.markdown-body .hljs-string, .markdown-body .hljs-addition { color: #a5d6ff; }
.markdown-body .hljs-title, .markdown-body .hljs-section { color: #d2a8ff; font-weight: bold; }
.markdown-body .hljs-number, .markdown-body .hljs-built_in, .markdown-body .hljs-literal, .markdown-body .hljs-type { color: #79c0ff; }
.markdown-body .hljs-attr, .markdown-body .hljs-variable { color: #ffa657; }
.markdown-body .hljs-function .hljs-title { color: #d2a8ff; }
</style>
