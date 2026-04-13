<script setup>
import { computed } from 'vue'
import { cn } from '@/lib/utils'
import Button from '@/components/ui/button.vue'
import Separator from '@/components/ui/separator.vue'
import { useTheme } from '@/composables/use-theme.js'
import { useRouter } from 'vue-router'
import { PanelLeft, Plus, MessageSquare, Sun, Moon, Monitor, Sparkles, Settings, Plug, Search, ChevronDown, Bot } from 'lucide-vue-next'

// ==================== Props & State ====================
const props = defineProps({
  conversations: { type: Array, default: () => [] },
  skills: { type: Array, default: () => [] },
  currentSkillId: { type: String, default: 'default' },
  conversationId: { type: String, default: '' },
  currentRoute: { type: String, default: 'chat' },
  open: { type: Boolean, default: true }
})

const emit = defineEmits([
  'new-conversation',
  'select-conversation',
  'delete-conversation',
  'select-skill',
  'navigate',
  'toggle'
])

const router = useRouter()
const { mode, toggleMode } = useTheme()

const SIDEBAR_WIDTH = '16rem'
const SIDEBAR_WIDTH_ICON = '3rem'

function handleNavigate(route) {
  router.push(route === 'chat' ? '/chat' : route === 'skills' ? '/skills' : '/mcp')
}

function handleSelectConversation(conv) {
  emit('select-conversation', conv)
}

function handleDeleteConversation(id) {
  emit('delete-conversation', id)
}

function handleSelectSkill(id) {
  emit('select-skill', id)
}

const themeIcon = computed(() => {
  if (mode.value === 'light') return Sun
  if (mode.value === 'dark') return Moon
  return Monitor
})

const themeLabel = computed(() => {
  if (mode.value === 'light') return '浅色'
  if (mode.value === 'dark') return '深色'
  return '跟随系统'
})

// 会话按时间分组
const groupedConversations = computed(() => {
  const now = new Date()
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const weekAgo = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000)
  const twoWeeksAgo = new Date(today.getTime() - 14 * 24 * 60 * 60 * 1000)

  const groups = { recent: [], lastWeek: [], older: [] }
  
  for (const conv of props.conversations) {
    const date = new Date(conv.createdAt || conv.createTime || Date.now())
    if (date >= weekAgo) {
      groups.recent.push(conv)
    } else if (date >= twoWeeksAgo) {
      groups.lastWeek.push(conv)
    } else {
      groups.older.push(conv)
    }
  }

  return groups
})
</script>

<template>
  <!-- Sidebar 间隙占位 -->
  <div
    class="relative bg-transparent transition-[width] duration-200 ease-linear"
    :style="{ width: open ? SIDEBAR_WIDTH : SIDEBAR_WIDTH_ICON }"
  />
  <!-- Sidebar 容器 -->
  <div
    class="fixed inset-y-0 left-0 z-10 hidden h-svh transition-[width] duration-200 ease-linear md:flex"
    :style="{ width: open ? SIDEBAR_WIDTH : SIDEBAR_WIDTH_ICON }"
  >
    <div
      :class="cn(
        'bg-sidebar text-sidebar-foreground flex h-full w-full flex-col border-r border-sidebar-border',
        !open && 'items-center'
      )"
    >
      <!-- ── Sidebar Header ── -->
      <div class="flex h-14 items-center gap-2 border-b border-sidebar-border px-3" :class="!open && 'justify-center px-0'">
        <Button variant="ghost" size="icon" class="size-8 shrink-0" @click="$emit('toggle')">
          <PanelLeft class="size-4" />
        </Button>
        <div v-if="open" class="flex items-center gap-2 overflow-hidden">
          <div class="flex size-7 items-center justify-center rounded-lg bg-primary text-primary-foreground">
            <Bot class="size-4" />
          </div>
          <span class="text-sm font-bold tracking-tight">WorkClaw</span>
        </div>
      </div>

      <!-- ── Sidebar Content (展开状态) ── -->
      <div v-if="open" class="flex min-h-0 flex-1 flex-col gap-1 overflow-auto p-2">
        <!-- 新建对话 -->
        <Button class="w-full justify-start gap-2 rounded-lg" @click="$emit('new-conversation')">
          <Plus class="size-4" />
          新建对话
        </Button>

        <!-- 历史会话分组 -->
        <div v-if="conversations.length > 0" class="mt-2 flex flex-col gap-0.5">
          <!-- 最近 -->
          <div v-if="groupedConversations.recent.length > 0">
            <div class="flex h-8 items-center rounded-md px-2 text-[11px] font-medium uppercase tracking-wider text-sidebar-foreground/50">
              最近 7 天
            </div>
            <div
              v-for="conv in groupedConversations.recent"
              :key="conv.id"
              :class="cn(
                'group flex w-full items-center gap-2 overflow-hidden rounded-md px-2 py-1.5 text-left text-sm outline-none ring-sidebar-ring transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground cursor-pointer',
                conversationId === conv.id && 'bg-sidebar-accent text-sidebar-accent-foreground'
              )"
              @click="handleSelectConversation(conv)"
            >
              <MessageSquare class="size-3.5 shrink-0 text-sidebar-foreground/50" />
              <span class="truncate text-[13px]">{{ conv.title }}</span>
              <button
                class="ml-auto shrink-0 rounded-sm p-0.5 opacity-0 transition-opacity hover:bg-sidebar-accent hover:text-destructive group-hover:opacity-100"
                @click.stop="handleDeleteConversation(conv.id)"
              >
                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>
              </button>
            </div>
          </div>

          <!-- 上周 -->
          <div v-if="groupedConversations.lastWeek.length > 0">
            <div class="flex h-8 items-center rounded-md px-2 text-[11px] font-medium uppercase tracking-wider text-sidebar-foreground/50">
              上周
            </div>
            <div
              v-for="conv in groupedConversations.lastWeek"
              :key="conv.id"
              :class="cn(
                'group flex w-full items-center gap-2 overflow-hidden rounded-md px-2 py-1.5 text-left text-sm outline-none ring-sidebar-ring transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground cursor-pointer',
                conversationId === conv.id && 'bg-sidebar-accent text-sidebar-accent-foreground'
              )"
              @click="handleSelectConversation(conv)"
            >
              <MessageSquare class="size-3.5 shrink-0 text-sidebar-foreground/50" />
              <span class="truncate text-[13px]">{{ conv.title }}</span>
              <button
                class="ml-auto shrink-0 rounded-sm p-0.5 opacity-0 transition-opacity hover:bg-sidebar-accent hover:text-destructive group-hover:opacity-100"
                @click.stop="handleDeleteConversation(conv.id)"
              >
                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>
              </button>
            </div>
          </div>

          <!-- 更早 -->
          <div v-if="groupedConversations.older.length > 0">
            <div class="flex h-8 items-center rounded-md px-2 text-[11px] font-medium uppercase tracking-wider text-sidebar-foreground/50">
              更早
            </div>
            <div
              v-for="conv in groupedConversations.older"
              :key="conv.id"
              :class="cn(
                'group flex w-full items-center gap-2 overflow-hidden rounded-md px-2 py-1.5 text-left text-sm outline-none ring-sidebar-ring transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground cursor-pointer',
                conversationId === conv.id && 'bg-sidebar-accent text-sidebar-accent-foreground'
              )"
              @click="handleSelectConversation(conv)"
            >
              <MessageSquare class="size-3.5 shrink-0 text-sidebar-foreground/50" />
              <span class="truncate text-[13px]">{{ conv.title }}</span>
              <button
                class="ml-auto shrink-0 rounded-sm p-0.5 opacity-0 transition-opacity hover:bg-sidebar-accent hover:text-destructive group-hover:opacity-100"
                @click.stop="handleDeleteConversation(conv.id)"
              >
                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>
              </button>
            </div>
          </div>
        </div>

        <div v-else class="py-4 text-center text-xs text-sidebar-foreground/40">
          暂无历史会话
        </div>

        <!-- 分割线 -->
        <Separator class="my-1" />

        <!-- 技能列表 -->
        <div class="flex flex-col gap-0.5">
          <div class="flex h-8 items-center rounded-md px-2 text-[11px] font-medium uppercase tracking-wider text-sidebar-foreground/50">
            技能
          </div>
          <div
            v-for="skill in skills"
            :key="skill.id"
            :class="cn(
              'flex w-full items-center gap-2 overflow-hidden rounded-md px-2 py-1.5 text-[13px] transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground cursor-pointer',
              currentSkillId === skill.id && 'bg-sidebar-accent text-sidebar-accent-foreground font-medium'
            )"
            @click="handleSelectSkill(skill.id)"
          >
            <Sparkles class="size-3.5 shrink-0 text-sidebar-foreground/50" />
            <span class="truncate">{{ skill.name }}</span>
            <span v-if="skill.mcpServerIds?.length" class="ml-auto shrink-0 rounded-full bg-sidebar-primary px-1.5 py-0.5 text-[10px] font-medium text-sidebar-primary-foreground">
              {{ skill.mcpServerIds.length }}
            </span>
          </div>
          <div v-if="skills.length === 0" class="px-2 py-1 text-[12px] text-sidebar-foreground/40">
            暂无技能
          </div>
        </div>

        <!-- 底部导航 -->
        <div class="mt-auto">
          <Separator class="mb-1" />
          <div class="flex flex-col gap-0.5">
            <div
              :class="cn(
                'flex w-full items-center gap-2 rounded-md px-2 py-1.5 text-[13px] cursor-pointer transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground',
                currentRoute === 'chat' && 'bg-sidebar-accent text-sidebar-accent-foreground font-medium'
              )"
              @click="handleNavigate('chat')"
            >
              <MessageSquare class="size-3.5 shrink-0 text-sidebar-foreground/50" />
              <span>对话</span>
            </div>
            <div
              :class="cn(
                'flex w-full items-center gap-2 rounded-md px-2 py-1.5 text-[13px] cursor-pointer transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground',
                currentRoute === 'skills' && 'bg-sidebar-accent text-sidebar-accent-foreground font-medium'
              )"
              @click="handleNavigate('skills')"
            >
              <Settings class="size-3.5 shrink-0 text-sidebar-foreground/50" />
              <span>技能配置</span>
            </div>
            <div
              :class="cn(
                'flex w-full items-center gap-2 rounded-md px-2 py-1.5 text-[13px] cursor-pointer transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground',
                currentRoute === 'mcp' && 'bg-sidebar-accent text-sidebar-accent-foreground font-medium'
              )"
              @click="handleNavigate('mcp')"
            >
              <Plug class="size-3.5 shrink-0 text-sidebar-foreground/50" />
              <span>MCP 配置</span>
            </div>
          </div>
        </div>
      </div>

      <!-- ── Sidebar Content (折叠状态) ── -->
      <div v-else class="flex min-h-0 flex-1 flex-col items-center gap-1 overflow-hidden p-2 pt-3">
        <Button variant="ghost" size="icon" class="size-8" @click="$emit('new-conversation')" title="新建对话">
          <Plus class="size-4" />
        </Button>
        <Separator class="my-1 w-4" />
        <Button variant="ghost" size="icon" :class="cn('size-8', currentRoute === 'chat' && 'bg-sidebar-accent text-sidebar-accent-foreground')" @click="handleNavigate('chat')" title="对话">
          <MessageSquare class="size-4" />
        </Button>
        <Button variant="ghost" size="icon" :class="cn('size-8', currentRoute === 'skills' && 'bg-sidebar-accent text-sidebar-accent-foreground')" @click="handleNavigate('skills')" title="技能配置">
          <Settings class="size-4" />
        </Button>
        <Button variant="ghost" size="icon" :class="cn('size-8', currentRoute === 'mcp' && 'bg-sidebar-accent text-sidebar-accent-foreground')" @click="handleNavigate('mcp')" title="MCP 配置">
          <Plug class="size-4" />
        </Button>
      </div>

      <!-- ── Sidebar Footer ── -->
      <div class="border-t border-sidebar-border p-2" :class="!open && 'flex justify-center'">
        <Button variant="ghost" size="sm" class="w-full gap-2 rounded-md" :class="!open && 'size-8 !w-8 !p-0'" @click="toggleMode" :title="themeLabel">
          <component :is="themeIcon" class="size-3.5" />
          <span v-if="open" class="text-xs text-sidebar-foreground/70">{{ themeLabel }}</span>
        </Button>
      </div>
    </div>
  </div>
</template>
