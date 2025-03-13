<script setup lang="ts">
import { onMounted } from 'vue'
import { useAuthStore } from './stores/auth'

const authStore = useAuthStore()

onMounted(() => {
  // Initialize user data if a token exists
  authStore.initUser()
})
</script>

<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 flex flex-col">
    <main class="flex-1 flex flex-col">
      <router-view />
    </main>
  </div>
</template>