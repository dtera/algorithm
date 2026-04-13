<script setup>
import { computed } from 'vue'
import { cn } from '@/lib/utils'

const props = defineProps({
  class: { type: String, default: '' },
  modelValue: { type: String, default: '' },
  disabled: Boolean
})

const emit = defineEmits(['update:modelValue'])

const classes = computed(() =>
  cn(
    'flex h-9 w-full items-center justify-between whitespace-nowrap rounded-md border border-input bg-transparent px-3 text-sm shadow-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-1 focus:ring-ring disabled:cursor-not-allowed disabled:opacity-50 [&>span]:line-clamp-1',
    props.class
  )
)
</script>

<template>
  <select
    :class="classes"
    :value="modelValue"
    :disabled="disabled"
    @change="emit('update:modelValue', $event.target.value)"
  >
    <slot />
  </select>
</template>
