<template>
  <div class="app">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <h1 v-if="!sidebarCollapsed" class="logo">🐾 WorkClaw</h1>
        <button class="toggle-btn" @click="sidebarCollapsed = !sidebarCollapsed">
          {{ sidebarCollapsed ? '→' : '←' }}
        </button>
      </div>

      <div v-if="!sidebarCollapsed" class="sidebar-content">
        <!-- 新建对话 -->
        <button class="new-chat-btn" @click="newConversation">
          ✨ 新建对话
        </button>

        <!-- 历史会话列表 -->
        <div class="section" v-if="conversations.length > 0">
          <div class="section-title">💬 历史会话</div>
          <div
            v-for="conv in conversations"
            :key="conv.id"
            class="conv-item"
            :class="{ active: conversationId === conv.id }"
            @click="selectConversation(conv)"
          >
            <span class="conv-title">{{ conv.title }}</span>
            <button class="conv-delete" @click.stop="deleteConversation(conv.id)" title="删除会话">×</button>
          </div>
        </div>

        <!-- Skill 选择 -->
        <div class="section">
          <div class="section-title">🎯 技能 (Skill)</div>
          <div
            v-for="skill in skills"
            :key="skill.id"
            class="skill-item"
            :class="{ active: currentSkillId === skill.id }"
            @click="selectSkill(skill.id)"
          >
            <span class="skill-name">{{ skill.name }}</span>
            <span v-if="skill.mcpServerIds?.length" class="badge">{{ skill.mcpServerIds.length }} MCP</span>
          </div>
        </div>

        <!-- 导航 -->
        <div class="section nav-section">
          <button class="nav-btn" :class="{ active: currentView === 'chat' }" @click="currentView = 'chat'">
            💬 对话
          </button>
          <button class="nav-btn" :class="{ active: currentView === 'skills' }" @click="currentView = 'skills'">
            ⚙️ 技能配置
          </button>
          <button class="nav-btn" :class="{ active: currentView === 'mcp' }" @click="currentView = 'mcp'">
            🔌 MCP 配置
          </button>
        </div>
      </div>
    </aside>

    <!-- 主内容区 -->
    <main class="main-content">
      <ChatView
        v-if="currentView === 'chat'"
        ref="chatViewRef"
        :skill-id="currentSkillId"
        :skill-name="currentSkillName"
        :conversation-id="conversationId"
        :models="models"
        :model-id="currentModelId"
        @update:conversation-id="onConversationIdChange"
        @update:model-id="currentModelId = $event"
        @conversationUpdated="loadConversationList"
      />
      <SkillConfigView
        v-if="currentView === 'skills'"
        :mcp-servers="mcpServers"
        @refresh="loadData"
      />
      <McpConfigView
        v-if="currentView === 'mcp'"
        @refresh="loadData"
      />
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { listSkills, listMcpServers, listModels, listConversations, getConversationMessages, clearConversation } from './api.js'
import ChatView from './components/ChatView.vue'
import SkillConfigView from './components/SkillConfigView.vue'
import McpConfigView from './components/McpConfigView.vue'

const currentView = ref('chat')
const sidebarCollapsed = ref(false)
const skills = ref([])
const mcpServers = ref([])
const models = ref([])
const conversations = ref([])
const currentSkillId = ref('default')
const currentModelId = ref('')
const conversationId = ref('')
const chatViewRef = ref(null)

const currentSkillName = computed(() => {
  const skill = skills.value.find(s => s.id === currentSkillId.value)
  return skill ? skill.name : '默认助手'
})

function selectSkill(id) {
  currentSkillId.value = id
  currentView.value = 'chat'
}

function newConversation() {
  conversationId.value = ''
  currentView.value = 'chat'
  // ChatView 会因 conversationId 变化自动清空消息
}

function onConversationIdChange(newId) {
  conversationId.value = newId
}

async function selectConversation(conv) {
  conversationId.value = conv.id
  currentView.value = 'chat'
  // 加载会话历史消息
  try {
    const res = await getConversationMessages(conv.id)
    const msgs = res.data.data || []
    await nextTick()
    if (chatViewRef.value) {
      chatViewRef.value.loadMessages(msgs)
    }
  } catch (e) {
    console.error('加载会话消息失败:', e)
  }
}

async function deleteConversation(id) {
  try {
    await clearConversation(id)
    conversations.value = conversations.value.filter(c => c.id !== id)
    if (conversationId.value === id) {
      conversationId.value = ''
    }
  } catch (e) {
    console.error('删除会话失败:', e)
  }
}

async function loadConversationList() {
  try {
    const res = await listConversations()
    conversations.value = res.data.data || []
  } catch (e) {
    console.error('加载会话列表失败:', e)
  }
}

async function loadData() {
  try {
    const [skillsRes, mcpRes, modelsRes] = await Promise.all([
      listSkills(),
      listMcpServers(),
      listModels()
    ])
    skills.value = skillsRes.data.data || []
    mcpServers.value = mcpRes.data.data || []
    models.value = modelsRes.data.data || []
    if (!currentModelId.value && models.value.length > 0) {
      const first = models.value.find(m => m.enabled)
      if (first) currentModelId.value = first.id
    }
  } catch (e) {
    console.error('加载配置失败:', e)
  }
}

onMounted(async () => {
  await loadData()
  await loadConversationList()
})
</script>

<style>
/* ==================== 全局样式 ==================== */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

:root {
  --bg-primary: #1a1a2e;
  --bg-secondary: #16213e;
  --bg-tertiary: #0f3460;
  --bg-card: #1e2746;
  --text-primary: #e8e8e8;
  --text-secondary: #a0a0b8;
  --accent: #6c63ff;
  --accent-hover: #5a52d5;
  --accent-light: rgba(108, 99, 255, 0.15);
  --success: #4caf50;
  --danger: #f44336;
  --warning: #ff9800;
  --border: #2a2f4a;
  --shadow: rgba(0, 0, 0, 0.3);
  --radius: 12px;
  --radius-sm: 8px;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  background: var(--bg-primary);
  color: var(--text-primary);
  overflow: hidden;
}

.app {
  display: flex;
  height: 100vh;
  width: 100vw;
}

/* ==================== 侧边栏 ==================== */
.sidebar {
  width: 280px;
  background: var(--bg-secondary);
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
}

.sidebar.collapsed {
  width: 50px;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid var(--border);
}

.logo {
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--accent), #e040fb);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.toggle-btn {
  background: none;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  font-size: 16px;
  padding: 4px 8px;
  border-radius: 4px;
}

.toggle-btn:hover {
  background: var(--accent-light);
  color: var(--accent);
}

.sidebar-content {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.new-chat-btn {
  width: 100%;
  padding: 10px;
  background: linear-gradient(135deg, var(--accent), #7c4dff);
  color: white;
  border: none;
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.2s;
  margin-bottom: 16px;
}

.new-chat-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(108, 99, 255, 0.4);
}

.section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 8px;
  padding: 0 4px;
}

/* 历史会话列表 */
.conv-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 2px;
}

.conv-item:hover {
  background: var(--accent-light);
}

.conv-item.active {
  background: var(--accent-light);
  border-left: 3px solid var(--accent);
}

.conv-title {
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.conv-delete {
  background: none;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  font-size: 16px;
  padding: 0 4px;
  opacity: 0;
  transition: all 0.2s;
}

.conv-item:hover .conv-delete {
  opacity: 1;
}

.conv-delete:hover {
  color: var(--danger);
}

/* Skill 列表 */
.skill-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 2px;
}

.skill-item:hover {
  background: var(--accent-light);
}

.skill-item.active {
  background: var(--accent-light);
  border-left: 3px solid var(--accent);
}

.skill-name {
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.badge {
  font-size: 10px;
  background: var(--accent);
  color: white;
  padding: 2px 6px;
  border-radius: 10px;
}

.nav-section {
  margin-top: auto;
}

.nav-btn {
  width: 100%;
  padding: 10px 12px;
  background: none;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  font-size: 14px;
  text-align: left;
  border-radius: var(--radius-sm);
  transition: all 0.2s;
  margin-bottom: 2px;
}

.nav-btn:hover {
  background: var(--accent-light);
  color: var(--text-primary);
}

.nav-btn.active {
  background: var(--accent-light);
  color: var(--accent);
  font-weight: 600;
}

/* ==================== 主内容区 ==================== */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* ==================== 滚动条 ==================== */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: var(--border);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: var(--text-secondary);
}
</style>
