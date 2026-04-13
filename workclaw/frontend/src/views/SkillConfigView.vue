<script setup>
import { ref, onMounted } from 'vue'
import { getSkills, createSkill, updateSkill, deleteSkill, getMcpServers } from '@/api.js'
import { cn } from '@/lib/utils'
import Button from '@/components/ui/button.vue'
import Input from '@/components/ui/input.vue'
import Textarea from '@/components/ui/textarea.vue'
import Dialog from '@/components/ui/dialog.vue'
import Checkbox from '@/components/ui/checkbox.vue'
import Label from '@/components/ui/label.vue'
import Badge from '@/components/ui/badge.vue'
import Card from '@/components/ui/card.vue'
import Separator from '@/components/ui/separator.vue'
import Select from '@/components/ui/select.vue'
import { Plus, Pencil, Trash2, Sparkles, Server, X } from 'lucide-vue-next'

const skills = ref([])
const mcpServers = ref([])
const loading = ref(false)
const editDialogOpen = ref(false)
const editingSkill = ref(null)
const form = ref({ name: '', description: '', systemPrompt: '', mcpServerIds: [] })

async function loadSkills() {
  loading.value = true
  try {
    const data = await getSkills()
    skills.value = data || []
  } catch (e) {
    console.error('加载技能失败:', e)
  } finally {
    loading.value = false
  }
}

async function loadMcpServers() {
  try {
    const data = await getMcpServers()
    mcpServers.value = data || []
  } catch (e) {
    console.error('加载 MCP 服务器失败:', e)
  }
}

onMounted(() => {
  loadSkills()
  loadMcpServers()
})

function openCreate() {
  editingSkill.value = null
  form.value = { name: '', description: '', systemPrompt: '', mcpServerIds: [] }
  editDialogOpen.value = true
}

function openEdit(skill) {
  editingSkill.value = skill
  form.value = {
    name: skill.name,
    description: skill.description || '',
    systemPrompt: skill.systemPrompt || '',
    mcpServerIds: skill.mcpServerIds || []
  }
  editDialogOpen.value = true
}

async function handleSave() {
  try {
    if (editingSkill.value) {
      await updateSkill(editingSkill.value.id, form.value)
    } else {
      await createSkill(form.value)
    }
    editDialogOpen.value = false
    await loadSkills()
  } catch (e) {
    console.error('保存技能失败:', e)
  }
}

async function handleDelete(id) {
  if (!confirm('确定删除此技能？')) return
  try {
    await deleteSkill(id)
    await loadSkills()
  } catch (e) {
    console.error('删除技能失败:', e)
  }
}

function toggleMcpServer(serverId) {
  const idx = form.value.mcpServerIds.indexOf(serverId)
  if (idx >= 0) {
    form.value.mcpServerIds.splice(idx, 1)
  } else {
    form.value.mcpServerIds.push(serverId)
  }
}
</script>

<template>
  <div class="flex h-full min-h-0 flex-col overflow-hidden">
    <!-- 头部 -->
    <header class="flex h-14 shrink-0 items-center justify-between border-b border-border/50 px-6">
      <div class="flex items-center gap-2">
        <Sparkles class="size-4 text-muted-foreground" />
        <h2 class="text-sm font-semibold">技能配置</h2>
        <Badge variant="secondary" class="text-[10px]">{{ skills.length }}</Badge>
      </div>
      <Button class="gap-2 rounded-lg" @click="openCreate">
        <Plus class="size-4" />
        新建技能
      </Button>
    </header>

    <!-- 技能列表 -->
    <div class="flex-1 overflow-y-auto p-6">
      <div v-if="skills.length === 0" class="flex flex-col items-center gap-4 pt-[15vh] text-center">
        <div class="flex size-16 items-center justify-center rounded-2xl bg-muted">
          <Sparkles class="size-8 text-muted-foreground/50" />
        </div>
        <div>
          <p class="text-sm font-medium">暂无技能</p>
          <p class="mt-1 text-xs text-muted-foreground">创建一个技能，定义系统提示和可用的 MCP 工具</p>
        </div>
        <Button variant="outline" class="gap-2 mt-2" @click="openCreate">
          <Plus class="size-4" />
          创建第一个技能
        </Button>
      </div>

      <div v-else class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <Card v-for="skill in skills" :key="skill.id" class="group relative overflow-hidden p-0 transition-all hover:shadow-md hover:border-primary/20">
          <div class="p-5">
            <div class="flex items-start justify-between">
              <div class="flex items-center gap-2.5">
                <div class="flex size-8 items-center justify-center rounded-lg bg-primary/10">
                  <Sparkles class="size-4 text-primary" />
                </div>
                <div>
                  <h3 class="font-semibold text-sm leading-none">{{ skill.name }}</h3>
                  <p v-if="skill.description" class="mt-1 text-xs text-muted-foreground line-clamp-1">{{ skill.description }}</p>
                </div>
              </div>
              <div class="flex gap-0.5 opacity-0 transition-opacity group-hover:opacity-100">
                <Button variant="ghost" size="icon" class="size-7" @click="openEdit(skill)">
                  <Pencil class="size-3" />
                </Button>
                <Button variant="ghost" size="icon" class="size-7 hover:text-destructive" @click="handleDelete(skill.id)">
                  <Trash2 class="size-3" />
                </Button>
              </div>
            </div>

            <div v-if="skill.systemPrompt" class="mt-3 rounded-md bg-muted/50 px-3 py-2">
              <p class="text-xs text-muted-foreground line-clamp-2 font-mono">{{ skill.systemPrompt }}</p>
            </div>

            <div v-if="skill.mcpServerIds?.length" class="mt-3 flex flex-wrap gap-1.5">
              <Badge v-for="id in skill.mcpServerIds" :key="id" variant="secondary" class="gap-1 text-[10px] rounded-md">
                <Server class="size-2.5" />
                {{ mcpServers.find(s => s.id === id)?.name || id }}
              </Badge>
            </div>
          </div>
        </Card>
      </div>
    </div>

    <!-- 编辑对话框 -->
    <Dialog v-model:open="editDialogOpen" class="max-w-lg">
      <div class="space-y-4">
        <div class="flex items-center justify-between">
          <h3 class="text-lg font-semibold">{{ editingSkill ? '编辑技能' : '新建技能' }}</h3>
          <Button variant="ghost" size="icon" class="size-8" @click="editDialogOpen = false">
            <X class="size-4" />
          </Button>
        </div>
        <Separator />
        <div class="space-y-4">
          <div>
            <Label class="mb-2 block text-sm">名称</Label>
            <Input v-model="form.name" placeholder="技能名称" class="rounded-lg" />
          </div>
          <div>
            <Label class="mb-2 block text-sm">描述</Label>
            <Input v-model="form.description" placeholder="简短描述技能的用途" class="rounded-lg" />
          </div>
          <div>
            <Label class="mb-2 block text-sm">系统提示</Label>
            <Textarea v-model="form.systemPrompt" placeholder="定义 AI 的角色和行为..." :rows="5" class="rounded-lg" />
          </div>
          <div>
            <Label class="mb-2 block text-sm">MCP 服务器</Label>
            <div class="space-y-2 rounded-lg border p-3">
              <div v-if="mcpServers.length === 0" class="text-xs text-muted-foreground py-1">暂无可用的 MCP 服务器</div>
              <div v-for="server in mcpServers" :key="server.id" class="flex items-center gap-3 rounded-md px-2 py-1.5 hover:bg-muted/50">
                <Checkbox :model-value="form.mcpServerIds.includes(server.id)" @update:model-value="toggleMcpServer(server.id)" :id="'skill-mcp-' + server.id" />
                <Label :for="'skill-mcp-' + server.id" class="text-sm cursor-pointer flex-1">{{ server.name }}</Label>
                <span class="text-[10px] text-muted-foreground">{{ server.type || 'SSE' }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="flex justify-end gap-2 pt-2">
          <Button variant="outline" @click="editDialogOpen = false" class="rounded-lg">取消</Button>
          <Button @click="handleSave" :disabled="!form.name.trim()" class="rounded-lg">保存</Button>
        </div>
      </div>
    </Dialog>
  </div>
</template>
