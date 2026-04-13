<script setup>
import { computed } from 'vue'
import { cn } from '@/lib/utils'

const props = defineProps({
  class: { type: String, default: '' },
  modelValue: { type: String, default: '' },
  placeholder: { type: String, default: '' },
  rows: { type: Number, default: 3 },
  disabled: Boolean
})

const emit = defineEmits(['update:modelValue'])

const classes = computed(() =>
  cn(
    'flex min-h-[60px] w-full rounded-md border border-input bg-transparent px-3 py-2 text-sm shadow-sm placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50',
    props.class
  )
)

function onInput(event) {
  emit('update:modelValue', event.target.value)
}
</script>

<template>
  <textarea :class="classes" :value="modelValue" :placeholder="placeholder" :rows="rows" :disabled="disabled" @input="onInput" />
</template>
