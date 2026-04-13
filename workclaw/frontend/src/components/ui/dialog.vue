<script setup>
import { computed } from 'vue'
import { cn } from '@/lib/utils'
import { X } from 'lucide-vue-next'

const props = defineProps({
  open: { type: Boolean, default: false },
  class: { type: String, default: '' }
})

const emit = defineEmits(['update:open'])

function onOverlayClick() {
  emit('update:open', false)
}
</script>

<template>
  <Teleport to="body">
    <Transition name="dialog">
      <div v-if="open" class="fixed inset-0 z-50 flex items-center justify-center">
        <!-- 遮罩 -->
        <div class="fixed inset-0 bg-black/80 data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0" @click="onOverlayClick" />
        <!-- 内容 -->
        <div
          :class="cn(
            'relative z-50 grid w-full max-w-lg gap-4 border bg-background p-6 shadow-lg sm:rounded-xl duration-200',
            props.class
          )"
        >
          <slot />
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.dialog-enter-active { transition: opacity 0.15s ease; }
.dialog-leave-active { transition: opacity 0.1s ease; }
.dialog-enter-from, .dialog-leave-to { opacity: 0; }
.dialog-enter-from > div:last-child { transform: scale(0.95); }
.dialog-leave-to > div:last-child { transform: scale(0.95); }
</style>
