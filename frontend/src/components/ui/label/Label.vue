<script setup lang="ts">
import { cn } from '@/lib/utils'

const props = defineProps<{
  htmlFor?: string
  class?: string
}>()
</script>

<template>
  <label
    :for="htmlFor"
    :class="cn(
      'text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70',
      props.class
    )"
  >
    <slot />
  </label>
</template>