<template>
  <div class="flex h-svh w-full overflow-hidden bg-background text-foreground">
    <!-- Sidebar -->
    <AppSidebar
      :conversations="conversations"
      :skills="skills"
      :current-skill-id="currentSkillId"
      :conversation-id="currentConversationId"
      :current-route="currentRouteName"
      :open="sidebarOpen"
      @new-conversation="handleNewConversation"
      @select-conversation="handleSelectConversation"
      @delete-conversation="handleDeleteConversation"
      @select-skill="handleSelectSkill"
      @toggle="toggleSidebar"
    />

    <!-- 主内容区 (SidebarInset) -->
    <main class="flex min-w-0 flex-1 flex-col overflow-hidden">
      <!-- 统一 Header (与 cowork-fe 一致) -->
      <header class="relative flex h-14 shrink-0 items-center gap-2 border-b border-border/50 px-4">
        <Button variant="ghost" size="icon" class="-ml-1 size-8" @click="toggleSidebar">
          <PanelLeft class="size-4" />
        </Button>
        <div class="ml-auto flex items-center gap-2">
          <span class="text-xs text-muted-foreground">{{ currentSkillName }}</span>
        </div>
      </header>

      <!-- 内容区 -->
      <div class="min-h-0 flex-1 overflow-hidden">
        <router-view
          :skill-id="currentSkillId"
          :skill-name="currentSkillName"
          :conversation-id="currentConversationId"
          :models="models"
          :model-id="selectedModelId"
          @update:conversation-id="handleUpdateConversationId"
          @update:model-id="handleUpdateModelId"
          @conversation-updated="handleConversationUpdated"
        />
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import AppSidebar from '@/components/app-sidebar.vue'
import Button from '@/components/ui/button.vue'
import { PanelLeft, Loader2 } from 'lucide-vue-next'
import { getSkills, getConversations, getConversation, getModels, clearConversation } from '@/api.js'

const router = useRouter()
const route = useRoute()

// ==================== 全局状态 ====================
const skills = ref([])
const conversations = ref([])
const currentSkillId = ref('default')
const currentConversationId = ref('')
const models = ref([])
const selectedModelId = ref('')
const sidebarOpen = ref(true)

const currentSkill = computed(() => {
  return skills.value.find(s => s.id === currentSkillId.value) || { id: 'default', name: '默认助手' }
})

const currentSkillName = computed(() => currentSkill.value.name)
const currentRouteName = computed(() => route.name || 'chat')

// ==================== 数据加载 ====================
async function loadSkills() {
  try {
    const data = await getSkills()
    skills.value = data || []
  } catch (e) {
    console.error('加载技能失败:', e)
  }
}

async function loadConversations() {
  try {
    const data = await getConversations()
    conversations.value = data || []
  } catch (e) {
    console.error('加载会话列表失败:', e)
  }
}

async function loadModels() {
  try {
    const data = await getModels()
    models.value = data || []
    // 默认选中第一个启用的模型
    const enabledModels = models.value.filter(m => m.enabled)
    if (enabledModels.length > 0 && !selectedModelId.value) {
      selectedModelId.value = enabledModels[0].id
    }
  } catch (e) {
    console.error('加载模型列表失败:', e)
  }
}

onMounted(() => {
  loadSkills()
  loadConversations()
  loadModels()
})

// ==================== 事件处理 ====================
function handleNewConversation() {
  currentConversationId.value = ''
  router.push('/chat')
}

async function handleSelectConversation(conv) {
  currentConversationId.value = conv.id
  currentSkillId.value = conv.skillId || 'default'
  if (route.path !== '/chat') {
    router.push('/chat')
  }
}

async function handleDeleteConversation(id) {
  try {
    await clearConversation(id)
    if (currentConversationId.value === id) {
      currentConversationId.value = ''
    }
    await loadConversations()
  } catch (e) {
    console.error('删除会话失败:', e)
  }
}

function handleSelectSkill(id) {
  currentSkillId.value = id
  currentConversationId.value = ''
  router.push('/chat')
}

async function handleConversationUpdated() {
  await loadConversations()
}

function handleUpdateConversationId(id) {
  currentConversationId.value = id
}

function handleUpdateModelId(id) {
  selectedModelId.value = id
}

function toggleSidebar() {
  sidebarOpen.value = !sidebarOpen.value
}
</script>