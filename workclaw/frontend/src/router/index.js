import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/chat'
  },
  {
    path: '/chat',
    name: 'chat',
    component: () => import('@/views/ChatView.vue')
  },
  {
    path: '/skills',
    name: 'skills',
    component: () => import('@/views/SkillConfigView.vue')
  },
  {
    path: '/mcp',
    name: 'mcp',
    component: () => import('@/views/McpConfigView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
