<script setup>
import { computed } from 'vue'
import { cn } from '@/lib/utils'

const props = defineProps({
  class: { type: String, default: '' },
  type: { type: String, default: 'text' },
  modelValue: { type: [String, Number], default: '' },
  placeholder: { type: String, default: '' },
  disabled: Boolean
})

const emit = defineEmits(['update:modelValue'])

const classes = computed(() =>
  cn(
    'flex h-9 w-full rounded-lg border border-input bg-transparent px-3 py-1 text-sm shadow-sm transition-colors file:border-0 file:bg-transparent file:text-sm file:font-medium file:text-foreground placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring/30 disabled:cursor-not-allowed disabled:opacity-50',
    props.class
  )
)

function onInput(event) {
  emit('update:modelValue', event.target.value)
}
</script>

<template>
  <input :type="type" :class="classes" :value="modelValue" :placeholder="placeholder" :disabled="disabled" @input="onInput" />
</template>
