import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 60000
})

// ==================== Chat API ====================

export function chat(data) {
  return api.post('/chat', data)
}

/**
 * 流式聊天（SSE），支持结构化事件：meta / delta / done
 * @param {Object} data 请求体
 * @param {Function} onMessage 收到文本增量时的回调 (chunk: string)
 * @param {Function} onDone 流结束时的回调
 * @param {Function} onError 错误回调 (error)
 * @param {Function} onMeta 收到 meta 事件时的回调 ({ conversationId })
 */
export function chatStream(data, onMessage, onDone, onError, onMeta, onClear) {
  const controller = new AbortController()

  fetch('/api/chat/stream', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
    signal: controller.signal
  }).then(response => {
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`)
    }
    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let textBuffer = '' // 跨 chunk 的文本缓冲区
    let doneHandled = false

    /**
     * 处理一个完整的 SSE 事件块（由空行 \n\n 分隔）
     * 格式示例: event:delta\ndata:some text
     */
    function processEvent(block) {
      let eventType = ''
      const dataLines = []
      const lines = block.split('\n')
      for (const line of lines) {
        if (line.startsWith('event:')) {
          eventType = line.substring(6).trim()
        } else if (line.startsWith('data:')) {
          // SSE 规范：data: 后第一个空格是分隔符，之后的内容原样保留
          let val = line.substring(5)
          if (val.startsWith(' ')) {
            val = val.substring(1)
          }
          dataLines.push(val)
        }
      }
      // SSE 标准：多个 data: 行用换行符拼接
      const dataContent = dataLines.join('\n')

      if (!eventType && !dataContent) return

      if (eventType === 'meta') {
        try {
          const meta = JSON.parse(dataContent)
          onMeta && onMeta(meta)
        } catch (e) { /* ignore */ }
      } else if (eventType === 'error') {
        onError && onError(new Error(dataContent || '服务端异常'))
      } else if (eventType === 'done' || dataContent === '[DONE]') {
        if (!doneHandled) {
          doneHandled = true
          onDone && onDone()
        }
      } else if (eventType === 'clear') {
        // 清除之前的内容（如"思考中"提示）
        onClear && onClear()
      } else if (eventType === 'delta' || eventType === '') {
        // delta 事件或无类型事件，作为内容处理
        if (dataContent) {
          onMessage && onMessage(dataContent)
        }
      }
    }

    function read() {
      reader.read().then(({ done, value }) => {
        if (done) {
          // 处理缓冲区剩余数据
          if (textBuffer.trim()) {
            processEvent(textBuffer)
          }
          if (!doneHandled) {
            doneHandled = true
            onDone && onDone()
          }
          return
        }
        textBuffer += decoder.decode(value, { stream: true })

        // SSE 事件由空行（\n\n）分隔
        const parts = textBuffer.split('\n\n')
        // 最后一段可能是不完整的事件，留在 buffer 中
        textBuffer = parts.pop() || ''

        for (const part of parts) {
          const block = part.trim()
          if (block) {
            processEvent(block)
          }
        }
        read()
      }).catch(err => {
        if (err.name !== 'AbortError') {
          onError && onError(err)
        }
      })
    }

    read()
  }).catch(err => {
    if (err.name !== 'AbortError') {
      onError && onError(err)
    }
  })

  // 返回 abort 方法，用于取消请求
  return { abort: () => controller.abort() }
}

export function listConversations() {
  return api.get('/chat/conversations')
}

export function getConversationMessages(id) {
  return api.get(`/chat/conversations/${id}/messages`)
}

export function clearConversation(id) {
  return api.delete(`/chat/conversations/${id}`)
}

// ==================== Skill API ====================

export function listSkills() {
  return api.get('/skills')
}

export function getSkill(id) {
  return api.get(`/skills/${id}`)
}

export function saveSkill(skill) {
  return api.post('/skills', skill)
}

export function updateSkill(id, skill) {
  return api.put(`/skills/${id}`, skill)
}

export function deleteSkill(id) {
  return api.delete(`/skills/${id}`)
}

// ==================== MCP Server API ====================

export function listMcpServers() {
  return api.get('/mcp-servers')
}

export function getMcpServer(id) {
  return api.get(`/mcp-servers/${id}`)
}

export function saveMcpServer(server) {
  return api.post('/mcp-servers', server)
}

export function updateMcpServer(id, server) {
  return api.put(`/mcp-servers/${id}`, server)
}

export function deleteMcpServer(id) {
  return api.delete(`/mcp-servers/${id}`)
}

export function testMcpConnection(id) {
  return api.post(`/mcp-servers/${id}/test`)
}

// ==================== Model API ====================

export function listModels() {
  return api.get('/models')
}

export function getModel(id) {
  return api.get(`/models/${id}`)
}

export function saveModel(model) {
  return api.post('/models', model)
}

export function updateModel(id, model) {
  return api.put(`/models/${id}`, model)
}

export function deleteModel(id) {
  return api.delete(`/models/${id}`)
}

export default api
