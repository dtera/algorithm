<script setup>
import { computed } from 'vue'
import { cn } from '@/lib/utils'

const props = defineProps({
  class: { type: String, default: '' },
  modelValue: { type: Boolean, default: false },
  disabled: Boolean,
  id: String
})

const emit = defineEmits(['update:modelValue'])

const classes = computed(() =>
  cn(
    'aspect-square h-4 w-4 rounded-sm border border-primary shadow focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50',
    props.modelValue ? 'bg-primary text-primary-foreground' : 'bg-background',
    props.class
  )
)
</script>

<template>
  <input
    type="checkbox"
    :class="classes"
    :checked="modelValue"
    :disabled="disabled"
    :id="id"
    @change="emit('update:modelValue', $event.target.checked)"
  />
</template>
