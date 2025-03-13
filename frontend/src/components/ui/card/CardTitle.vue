<script setup lang="ts">
import { cn } from '@/lib/utils'

const props = defineProps<{
  class?: string
  tag?: string
}>()

const tag = props.tag || 'h3'
</script>

<template>
  <component 
    :is="tag"
    :class="cn(
      'text-2xl font-semibold leading-none tracking-tight',
      props.class
    )"
  >
    <slot />
  </component>
</template>