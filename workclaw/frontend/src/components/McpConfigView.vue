<template>
  <div class="config-view">
    <div class="config-header">
      <h2>🔌 MCP Server 配置</h2>
      <button class="primary-btn" @click="openAddDialog">➕ 添加 MCP Server</button>
    </div>

    <div class="config-list">
      <div v-for="server in servers" :key="server.id" class="config-card">
        <div class="card-header">
          <div class="card-title">
            <h3>{{ server.name }}</h3>
            <span class="type-badge">{{ server.type || 'sse' }}</span>
            <span class="status-badge" :class="server.enabled ? 'enabled' : 'disabled'">
              {{ server.enabled ? '已启用' : '已禁用' }}
            </span>
          </div>
          <div class="card-actions">
            <button class="icon-btn" @click="handleTest(server.id)" :disabled="testing === server.id" title="测试连接">
              {{ testing === server.id ? '⏳' : '🔗' }}
            </button>
            <button class="icon-btn" @click="openEditDialog(server)" title="编辑">✏️</button>
            <button class="icon-btn danger" @click="handleDelete(server.id)" title="删除">🗑️</button>
          </div>
        </div>

        <p class="card-desc">{{ server.description || '暂无描述' }}</p>

        <div class="card-detail">
          <div class="detail-label">URL</div>
          <div class="detail-value url-value">{{ server.url }}</div>
        </div>

        <div class="card-detail-row">
          <div class="card-detail">
            <div class="detail-label">连接超时</div>
            <div class="detail-value">{{ server.connectTimeout || 10 }}s</div>
          </div>
          <div class="card-detail">
            <div class="detail-label">请求超时</div>
            <div class="detail-value">{{ server.requestTimeout || 60 }}s</div>
          </div>
        </div>

        <div v-if="server.headers && Object.keys(server.headers).length" class="card-detail">
          <div class="detail-label">请求头</div>
          <div class="headers-list">
            <div v-for="(value, key) in server.headers" :key="key" class="header-item">
              <span class="header-key">{{ key }}:</span>
              <span class="header-value">{{ maskValue(value) }}</span>
            </div>
          </div>
        </div>

        <!-- 连接测试结果 -->
        <div v-if="testResults[server.id] !== undefined" class="test-result" :class="testResults[server.id] ? 'success' : 'fail'">
          {{ testResults[server.id] ? '✅ 连接成功' : '❌ 连接失败' }}
        </div>
      </div>

      <div v-if="servers.length === 0" class="empty-state">
        <p>暂无 MCP Server 配置。</p>
        <p class="text-muted">添加 MCP Server 后，可以在技能配置中关联使用。</p>
      </div>
    </div>

    <!-- 添加/编辑对话框 -->
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="dialogVisible = false">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ editingServer.id ? '编辑 MCP Server' : '添加 MCP Server' }}</h3>
          <button class="close-btn" @click="dialogVisible = false">✕</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>名称 <span class="required">*</span></label>
            <input v-model="editingServer.name" type="text" placeholder="MCP Server 名称" />
          </div>
          <div class="form-group">
            <label>描述</label>
            <input v-model="editingServer.description" type="text" placeholder="MCP Server 描述" />
          </div>
          <div class="form-group">
            <label>连接类型</label>
            <select v-model="editingServer.type">
              <option value="sse">SSE</option>
              <option value="streamable-http">Streamable-HTTP</option>
            </select>
          </div>
          <div class="form-group">
            <label>URL <span class="required">*</span></label>
            <input v-model="editingServer.url" type="text" placeholder="http://localhost:8088/sse" />
          </div>
          <div class="form-group">
            <label>连接超时（秒）</label>
            <input v-model.number="editingServer.connectTimeout" type="number" min="1" max="300" />
          </div>
          <div class="form-group">
            <label>请求超时（秒）</label>
            <input v-model.number="editingServer.requestTimeout" type="number" min="1" max="600" />
          </div>

          <!-- 请求头配置 -->
          <div class="form-group">
            <label>请求头</label>
            <div class="headers-editor">
              <div v-for="(header, index) in headersList" :key="index" class="header-row">
                <input v-model="header.key" type="text" placeholder="Header 名称" />
                <input v-model="header.value" type="text" placeholder="Header 值" />
                <button class="icon-btn danger" @click="headersList.splice(index, 1)">✕</button>
              </div>
              <button class="add-header-btn" @click="headersList.push({ key: '', value: '' })">
                + 添加请求头
              </button>
            </div>
          </div>

          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" v-model="editingServer.enabled" />
              启用此 MCP Server
            </label>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="secondary-btn" @click="dialogVisible = false">取消</button>
          <button class="primary-btn" @click="handleSave">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  listMcpServers,
  saveMcpServer,
  updateMcpServer,
  deleteMcpServer as deleteMcpServerApi,
  testMcpConnection
} from '../api.js'

const emit = defineEmits(['refresh'])

const servers = ref([])
const dialogVisible = ref(false)
const editingServer = ref(createEmptyServer())
const headersList = ref([])
const testing = ref('')
const testResults = ref({})

function createEmptyServer() {
  return {
    id: '',
    name: '',
    description: '',
    type: 'sse',
    url: '',
    headers: {},
    connectTimeout: 10,
    requestTimeout: 60,
    enabled: true
  }
}

function maskValue(value) {
  if (!value || value.length <= 8) return '****'
  return value.substring(0, 4) + '****' + value.substring(value.length - 4)
}

function openAddDialog() {
  editingServer.value = createEmptyServer()
  headersList.value = []
  dialogVisible.value = true
}

function openEditDialog(server) {
  editingServer.value = { ...server }
  headersList.value = server.headers
    ? Object.entries(server.headers).map(([key, value]) => ({ key, value }))
    : []
  dialogVisible.value = true
}

async function handleSave() {
  if (!editingServer.value.name?.trim()) {
    alert('请输入 MCP Server 名称')
    return
  }
  if (!editingServer.value.url?.trim()) {
    alert('请输入 MCP Server URL')
    return
  }

  // 构建 headers
  const headers = {}
  headersList.value.forEach(h => {
    if (h.key?.trim()) {
      headers[h.key.trim()] = h.value || ''
    }
  })
  editingServer.value.headers = headers

  try {
    if (editingServer.value.id) {
      await updateMcpServer(editingServer.value.id, editingServer.value)
    } else {
      await saveMcpServer(editingServer.value)
    }
    dialogVisible.value = false
    await loadServers()
    emit('refresh')
  } catch (e) {
    alert('保存失败: ' + (e.response?.data?.message || e.message))
  }
}

async function handleDelete(id) {
  if (!confirm('确认删除此 MCP Server？')) return
  try {
    await deleteMcpServerApi(id)
    await loadServers()
    emit('refresh')
  } catch (e) {
    alert('删除失败: ' + (e.response?.data?.message || e.message))
  }
}

async function handleTest(id) {
  testing.value = id
  delete testResults.value[id]
  try {
    const res = await testMcpConnection(id)
    testResults.value[id] = res.data.data?.connected || false
  } catch (e) {
    testResults.value[id] = false
  } finally {
    testing.value = ''
  }
}

async function loadServers() {
  try {
    const res = await listMcpServers()
    servers.value = res.data.data || []
  } catch (e) {
    console.error('加载 MCP Server 列表失败:', e)
  }
}

onMounted(() => {
  loadServers()
})
</script>

<style scoped>
.config-view {
  padding: 24px;
  height: 100%;
  overflow-y: auto;
}

.config-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.config-header h2 {
  font-size: 20px;
}

.config-list {
  display: grid;
  gap: 16px;
}

.config-card {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 20px;
  transition: all 0.2s;
}

.config-card:hover {
  border-color: var(--accent);
  box-shadow: 0 4px 12px var(--shadow);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-title h3 {
  font-size: 16px;
}

.type-badge {
  font-size: 10px;
  background: var(--bg-tertiary);
  color: var(--text-secondary);
  padding: 2px 6px;
  border-radius: 4px;
  text-transform: uppercase;
}

.status-badge {
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
}

.status-badge.enabled {
  background: rgba(76, 175, 80, 0.2);
  color: var(--success);
}

.status-badge.disabled {
  background: rgba(244, 67, 54, 0.2);
  color: var(--danger);
}

.card-actions {
  display: flex;
  gap: 4px;
}

.icon-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s;
}

.icon-btn:hover {
  background: var(--accent-light);
}

.icon-btn.danger:hover {
  background: rgba(244, 67, 54, 0.2);
}

.icon-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.card-desc {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 12px;
}

.card-detail {
  margin-bottom: 8px;
}

.card-detail-row {
  display: flex;
  gap: 20px;
  margin-bottom: 8px;
}

.detail-label {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 4px;
}

.detail-value {
  font-size: 13px;
}

.url-value {
  font-family: monospace;
  font-size: 13px;
  color: var(--accent);
}

.headers-list {
  font-size: 13px;
}

.header-item {
  margin-bottom: 2px;
}

.header-key {
  color: var(--accent);
  font-family: monospace;
}

.header-value {
  color: var(--text-secondary);
  font-family: monospace;
  margin-left: 4px;
}

.test-result {
  margin-top: 12px;
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  font-size: 13px;
  font-weight: 600;
}

.test-result.success {
  background: rgba(76, 175, 80, 0.15);
  color: var(--success);
}

.test-result.fail {
  background: rgba(244, 67, 54, 0.15);
  color: var(--danger);
}

.text-muted {
  color: var(--text-secondary);
  font-size: 13px;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: var(--text-secondary);
}

/* ==================== 对话框 ==================== */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: var(--bg-secondary);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  width: 560px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 8px 32px var(--shadow);
}

.dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border);
}

.dialog-header h3 {
  font-size: 16px;
}

.close-btn {
  background: none;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  font-size: 18px;
  padding: 4px 8px;
  border-radius: 4px;
}

.close-btn:hover {
  background: var(--accent-light);
  color: var(--text-primary);
}

.dialog-body {
  padding: 20px;
  overflow-y: auto;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 16px 20px;
  border-top: 1px solid var(--border);
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 6px;
  color: var(--text-secondary);
}

.required {
  color: var(--danger);
}

.form-group input[type="text"],
.form-group input[type="number"],
.form-group select {
  width: 100%;
  padding: 10px 12px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  color: var(--text-primary);
  font-size: 14px;
  font-family: inherit;
  outline: none;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group select:focus {
  border-color: var(--accent);
}

.form-group select {
  cursor: pointer;
}

.headers-editor {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.header-row {
  display: flex;
  gap: 8px;
  align-items: center;
}

.header-row input {
  flex: 1;
  padding: 8px 10px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  color: var(--text-primary);
  font-size: 13px;
  outline: none;
}

.header-row input:focus {
  border-color: var(--accent);
}

.add-header-btn {
  background: none;
  border: 1px dashed var(--border);
  color: var(--text-secondary);
  padding: 8px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s;
}

.add-header-btn:hover {
  border-color: var(--accent);
  color: var(--accent);
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  accent-color: var(--accent);
}

/* ==================== 按钮 ==================== */
.primary-btn {
  padding: 8px 16px;
  background: var(--accent);
  color: white;
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.2s;
}

.primary-btn:hover {
  background: var(--accent-hover);
}

.secondary-btn {
  padding: 8px 16px;
  background: none;
  color: var(--text-secondary);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.secondary-btn:hover {
  border-color: var(--text-primary);
  color: var(--text-primary);
}
</style>
