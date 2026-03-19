<template>
  <div class="config-view">
    <div class="config-header">
      <h2>⚙️ 技能 (Skill) 配置</h2>
      <button class="primary-btn" @click="openAddDialog">➕ 添加技能</button>
    </div>

    <div class="config-list">
      <div v-for="skill in skills" :key="skill.id" class="config-card">
        <div class="card-header">
          <div class="card-title">
            <h3>{{ skill.name }}</h3>
            <span v-if="skill.id === 'default'" class="default-badge">默认</span>
            <span class="status-badge" :class="skill.enabled ? 'enabled' : 'disabled'">
              {{ skill.enabled ? '已启用' : '已禁用' }}
            </span>
          </div>
          <div class="card-actions">
            <button class="icon-btn" @click="openEditDialog(skill)" title="编辑">✏️</button>
            <button v-if="skill.id !== 'default'" class="icon-btn danger" @click="handleDelete(skill.id)" title="删除">🗑️</button>
          </div>
        </div>

        <p class="card-desc">{{ skill.description || '暂无描述' }}</p>

        <div class="card-detail">
          <div class="detail-label">系统提示词</div>
          <div class="detail-value prompt-preview">{{ truncate(skill.systemPrompt, 150) }}</div>
        </div>

        <div class="card-detail">
          <div class="detail-label">关联 MCP Server</div>
          <div class="mcp-tags">
            <span v-if="!skill.mcpServerIds?.length" class="text-muted">未关联</span>
            <span
              v-for="serverId in skill.mcpServerIds"
              :key="serverId"
              class="mcp-tag"
            >
              {{ getMcpServerName(serverId) }}
            </span>
          </div>
        </div>
      </div>

      <div v-if="skills.length === 0" class="empty-state">
        <p>暂无技能配置，请点击"添加技能"创建。</p>
      </div>
    </div>

    <!-- 添加/编辑对话框 -->
    <div v-if="dialogVisible" class="dialog-overlay" @click.self="dialogVisible = false">
      <div class="dialog">
        <div class="dialog-header">
          <h3>{{ editingSkill.id ? '编辑技能' : '添加技能' }}</h3>
          <button class="close-btn" @click="dialogVisible = false">✕</button>
        </div>
        <div class="dialog-body">
          <div class="form-group">
            <label>名称 <span class="required">*</span></label>
            <input v-model="editingSkill.name" type="text" placeholder="技能名称" />
          </div>
          <div class="form-group">
            <label>描述</label>
            <input v-model="editingSkill.description" type="text" placeholder="技能描述" />
          </div>
          <div class="form-group">
            <label>系统提示词</label>
            <textarea v-model="editingSkill.systemPrompt" rows="6" placeholder="定义助手的角色和行为..."></textarea>
          </div>
          <div class="form-group">
            <label>关联 MCP Server</label>
            <div class="checkbox-group">
              <label v-for="server in mcpServers" :key="server.id" class="checkbox-label">
                <input
                  type="checkbox"
                  :value="server.id"
                  v-model="editingSkill.mcpServerIds"
                />
                {{ server.name }} ({{ server.url }})
              </label>
              <span v-if="mcpServers.length === 0" class="text-muted">暂无可用的 MCP Server，请先在 MCP 配置中添加。</span>
            </div>
          </div>
          <div class="form-group">
            <label class="checkbox-label">
              <input type="checkbox" v-model="editingSkill.enabled" />
              启用此技能
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
import { listSkills, saveSkill, updateSkill, deleteSkill as deleteSkillApi } from '../api.js'

const props = defineProps({
  mcpServers: Array
})

const emit = defineEmits(['refresh'])

const skills = ref([])
const dialogVisible = ref(false)
const editingSkill = ref(createEmptySkill())

function createEmptySkill() {
  return {
    id: '',
    name: '',
    description: '',
    systemPrompt: '',
    mcpServerIds: [],
    enabled: true
  }
}

function getMcpServerName(id) {
  const server = props.mcpServers?.find(s => s.id === id)
  return server ? server.name : id
}

function truncate(text, len) {
  if (!text) return '未设置'
  return text.length > len ? text.substring(0, len) + '...' : text
}

function openAddDialog() {
  editingSkill.value = createEmptySkill()
  dialogVisible.value = true
}

function openEditDialog(skill) {
  editingSkill.value = { ...skill, mcpServerIds: [...(skill.mcpServerIds || [])] }
  dialogVisible.value = true
}

async function handleSave() {
  if (!editingSkill.value.name?.trim()) {
    alert('请输入技能名称')
    return
  }
  try {
    if (editingSkill.value.id) {
      await updateSkill(editingSkill.value.id, editingSkill.value)
    } else {
      await saveSkill(editingSkill.value)
    }
    dialogVisible.value = false
    await loadSkills()
    emit('refresh')
  } catch (e) {
    alert('保存失败: ' + (e.response?.data?.message || e.message))
  }
}

async function handleDelete(id) {
  if (!confirm('确认删除此技能？')) return
  try {
    await deleteSkillApi(id)
    await loadSkills()
    emit('refresh')
  } catch (e) {
    alert('删除失败: ' + (e.response?.data?.message || e.message))
  }
}

async function loadSkills() {
  try {
    const res = await listSkills()
    skills.value = res.data.data || []
  } catch (e) {
    console.error('加载技能列表失败:', e)
  }
}

onMounted(() => {
  loadSkills()
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

.default-badge {
  font-size: 10px;
  background: var(--accent);
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
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

.card-desc {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 12px;
}

.card-detail {
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

.prompt-preview {
  background: var(--bg-primary);
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  font-family: monospace;
  font-size: 12px;
  color: var(--text-secondary);
  max-height: 80px;
  overflow: hidden;
}

.mcp-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.mcp-tag {
  font-size: 12px;
  background: var(--accent-light);
  color: var(--accent);
  padding: 2px 8px;
  border-radius: 4px;
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
.form-group textarea {
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

.form-group input[type="text"]:focus,
.form-group textarea:focus {
  border-color: var(--accent);
}

.form-group textarea {
  resize: vertical;
}

.checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
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
