<script setup>
import { ref, onMounted } from 'vue'
import { getMcpServers, createMcpServer, updateMcpServer, deleteMcpServer, testMcpServerConnection } from '@/api.js'
import Button from '@/components/ui/button.vue'
import Input from '@/components/ui/input.vue'
import Textarea from '@/components/ui/textarea.vue'
import Dialog from '@/components/ui/dialog.vue'
import Card from '@/components/ui/card.vue'
import Separator from '@/components/ui/separator.vue'
import Badge from '@/components/ui/badge.vue'
import Label from '@/components/ui/label.vue'
import Checkbox from '@/components/ui/checkbox.vue'
import { Plus, Pencil, Trash2, Plug, CheckCircle2, XCircle, Loader2, X, Wifi, Wrench } from 'lucide-vue-next'

const servers = ref([])
const loading = ref(false)
const editDialogOpen = ref(false)
const editingServer = ref(null)
const form = ref({ name: '', url: '', type: 'SSE', description: '', headers: '', enabled: true })
const testing = ref(false)
const testResult = ref(null)

async function loadServers() {
  loading.value = true
  try {
    const data = await getMcpServers()
    servers.value = data || []
  } catch (e) {
    console.error('加载 MCP 服务器失败:', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadServers()
})

function openCreate() {
  editingServer.value = null
  form.value = { name: '', url: '', type: 'sse', description: '', headers: '', enabled: true }
  testResult.value = null
  editDialogOpen.value = true
}

function openEdit(server) {
  editingServer.value = server
  form.value = {
    name: server.name,
    url: server.url,
    type: server.type || 'sse',
    description: server.description || '',
    headers: typeof server.headers === 'string' ? server.headers : server.headers ? JSON.stringify(server.headers, null, 2) : '',
    enabled: server.enabled !== false
  }
  testResult.value = null
  editDialogOpen.value = true
}

async function handleSave() {
  try {
    let headers = form.value.headers
    if (typeof headers === 'string' && headers.trim()) {
      try { headers = JSON.parse(headers) } catch { headers = {} }
    } else {
      headers = {}
    }

    const payload = { ...form.value, headers }
    if (editingServer.value) {
      await updateMcpServer(editingServer.value.id, payload)
    } else {
      await createMcpServer(payload)
    }
    editDialogOpen.value = false
    await loadServers()
  } catch (e) {
    console.error('保存 MCP 服务器失败:', e)
  }
}

async function handleDelete(id) {
  if (!confirm('确定删除此 MCP 服务器？')) return
  try {
    await deleteMcpServer(id)
    await loadServers()
  } catch (e) {
    console.error('删除 MCP 服务器失败:', e)
  }
}

async function handleTest() {
  testing.value = true
  testResult.value = null
  try {
    await testMcpServerConnection(form.value)
    testResult.value = 'success'
  } catch (e) {
    testResult.value = 'error'
  } finally {
    testing.value = false
  }
}
</script>

<template>
  <div class="flex h-full min-h-0 flex-col overflow-hidden">
    <!-- 头部 -->
    <header class="flex h-14 shrink-0 items-center justify-between border-b border-border/50 px-6">
      <div class="flex items-center gap-2">
        <Plug class="size-4 text-muted-foreground" />
        <h2 class="text-sm font-semibold">MCP 服务器</h2>
        <Badge variant="secondary" class="text-[10px]">{{ servers.length }}</Badge>
      </div>
      <Button class="gap-2 rounded-lg" @click="openCreate">
        <Plus class="size-4" />
        新建服务器
      </Button>
    </header>

    <!-- 服务器列表 -->
    <div class="flex-1 overflow-y-auto p-6">
      <div v-if="servers.length === 0" class="flex flex-col items-center gap-4 pt-[15vh] text-center">
        <div class="flex size-16 items-center justify-center rounded-2xl bg-muted">
          <Plug class="size-8 text-muted-foreground/50" />
        </div>
        <div>
          <p class="text-sm font-medium">暂无 MCP 服务器</p>
          <p class="mt-1 text-xs text-muted-foreground">添加一个 MCP 服务器以扩展 AI 的工具能力</p>
        </div>
        <Button variant="outline" class="gap-2 mt-2" @click="openCreate">
          <Plus class="size-4" />
          添加第一个服务器
        </Button>
      </div>

      <div v-else class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <Card v-for="server in servers" :key="server.id" class="group relative overflow-hidden p-0 transition-all hover:shadow-md hover:border-primary/20">
          <div class="p-5">
            <div class="flex items-start justify-between">
              <div class="flex items-center gap-2.5">
                <div class="flex size-8 items-center justify-center rounded-lg" :class="server.enabled !== false ? 'bg-green-500/10' : 'bg-muted'">
                  <Wifi v-if="server.enabled !== false" class="size-4 text-green-600 dark:text-green-400" />
                  <Wifi v-else class="size-4 text-muted-foreground/50" />
                </div>
                <div>
                  <h3 class="font-semibold text-sm leading-none">{{ server.name }}</h3>
                  <p class="mt-1 text-xs text-muted-foreground font-mono truncate max-w-[200px]">{{ server.url }}</p>
                </div>
              </div>
              <div class="flex items-center gap-1.5">
                <Badge :variant="server.enabled !== false ? 'default' : 'outline'" class="text-[10px] rounded-md">
                  {{ server.enabled !== false ? '运行中' : '已禁用' }}
                </Badge>
                <div class="flex gap-0.5 opacity-0 transition-opacity group-hover:opacity-100">
                  <Button variant="ghost" size="icon" class="size-7" @click="openEdit(server)">
                    <Pencil class="size-3" />
                  </Button>
                  <Button variant="ghost" size="icon" class="size-7 hover:text-destructive" @click="handleDelete(server.id)">
                    <Trash2 class="size-3" />
                  </Button>
                </div>
              </div>
            </div>

            <p v-if="server.description" class="mt-3 text-xs text-muted-foreground line-clamp-2">{{ server.description }}</p>

            <div class="mt-3 flex items-center gap-2">
              <Badge variant="secondary" class="text-[10px] rounded-md gap-1">
                <Wifi class="size-2.5" />
                {{ (server.type || 'sse').toUpperCase() === 'STREAMABLE-HTTP' ? 'Streamable HTTP' : (server.type || 'sse').toUpperCase() }}
              </Badge>
              <Badge v-if="server.tools?.length" variant="secondary" class="text-[10px] rounded-md gap-1">
                <Wrench class="size-2.5" />
                {{ server.tools.length }} 个工具
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
          <h3 class="text-lg font-semibold">{{ editingServer ? '编辑 MCP 服务器' : '新建 MCP 服务器' }}</h3>
          <Button variant="ghost" size="icon" class="size-8" @click="editDialogOpen = false">
            <X class="size-4" />
          </Button>
        </div>
        <Separator />
        <div class="space-y-4">
          <div>
            <Label class="mb-2 block text-sm">名称</Label>
            <Input v-model="form.name" placeholder="服务器名称" class="rounded-lg" />
          </div>
          <div>
            <Label class="mb-2 block text-sm">URL</Label>
            <Input v-model="form.url" placeholder="https://example.com/mcp" class="rounded-lg font-mono text-xs" />
          </div>
          <div>
            <Label class="mb-2 block text-sm">类型</Label>
            <Select v-model="form.type" class="h-9 rounded-lg">
              <option value="sse">SSE</option>
              <option value="streamable-http">Streamable HTTP</option>
            </Select>
          </div>
          <div>
            <Label class="mb-2 block text-sm">描述</Label>
            <Input v-model="form.description" placeholder="服务器描述" class="rounded-lg" />
          </div>
          <div>
            <Label class="mb-2 block text-sm">自定义请求头 (JSON)</Label>
            <Textarea v-model="form.headers" placeholder='{"Authorization": "Bearer xxx"}' :rows="3" class="rounded-lg font-mono text-xs" />
          </div>
          <div class="flex items-center gap-3">
            <Checkbox v-model="form.enabled" id="mcp-enabled" />
            <Label for="mcp-enabled" class="cursor-pointer text-sm">启用此服务器</Label>
          </div>
        </div>

        <!-- 测试连接 -->
        <div class="flex items-center gap-3 rounded-lg border p-3">
          <Button variant="outline" size="sm" class="gap-1.5 rounded-lg" @click="handleTest" :disabled="testing || !form.url">
            <Loader2 v-if="testing" class="size-3.5 animate-spin" />
            <Wifi v-else class="size-3.5" />
            测试连接
          </Button>
          <div v-if="testResult === 'success'" class="flex items-center gap-1.5 text-sm text-green-600 dark:text-green-400">
            <CheckCircle2 class="size-4" /> 连接成功
          </div>
          <div v-if="testResult === 'error'" class="flex items-center gap-1.5 text-sm text-destructive">
            <XCircle class="size-4" /> 连接失败
          </div>
        </div>

        <div class="flex justify-end gap-2 pt-2">
          <Button variant="outline" @click="editDialogOpen = false" class="rounded-lg">取消</Button>
          <Button @click="handleSave" :disabled="!form.name.trim() || !form.url.trim()" class="rounded-lg">保存</Button>
        </div>
      </div>
    </Dialog>
  </div>
</template>
