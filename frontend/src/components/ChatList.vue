<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useChatRoomStore, type ChatRoom } from '@/stores/chatRoom'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Users, AlertCircle, Plus } from 'lucide-vue-next'

const props = defineProps<{
  title?: string
  description?: string
}>()

const router = useRouter()
const chatRoomStore = useChatRoomStore()

// Default values
const cardTitle = computed(() => props.title || 'Your Chat Rooms')
const cardDescription = computed(() => props.description || 'Select a room to start chatting')

// Navigate to create chat page
const navigateToCreateChat = () => {
  router.push({ name: 'createChat' })
}

// Open a chat room
const openChatRoom = (roomId: string) => {
  router.push({ name: 'chatRoom', params: { id: roomId } })
}

// Format date for display
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}
</script>

<template>
  <Card>
    <CardHeader>
      <div class="flex justify-between items-center">
        <div>
          <CardTitle class="text-xl">{{ cardTitle }}</CardTitle>
          <CardDescription>
            {{ cardDescription }}
          </CardDescription>
        </div>
        
        <Button 
          @click="navigateToCreateChat" 
          size="sm"
          class="flex items-center gap-1"
        >
          <Plus class="h-4 w-4" />
          New Chat
        </Button>
      </div>
    </CardHeader>
    
    <CardContent>
      <div v-if="chatRoomStore.loading" class="py-4 text-center text-gray-500">
        <div class="w-8 h-8 border-2 border-primary border-t-transparent rounded-full animate-spin mx-auto"></div>
        <div class="mt-2">Loading chat rooms...</div>
      </div>
      
      <div v-else-if="chatRoomStore.error" class="flex items-center p-3 text-sm text-red-600 bg-red-50 rounded-md">
        <AlertCircle class="h-4 w-4 mr-2" />
        {{ chatRoomStore.error }}
      </div>
      
      <div v-else-if="chatRoomStore.chatRooms.length === 0" class="py-4 text-center text-gray-500">
        No chat rooms found. Create a new one to get started!
      </div>
      
      <div v-else class="space-y-3">
        <div 
          v-for="room in chatRoomStore.chatRooms" 
          :key="room.id" 
          class="p-4 border rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 cursor-pointer transition-colors"
          @click="openChatRoom(room.id)"
        >
          <div class="flex justify-between items-center">
            <div>
              <div class="flex items-center gap-2">
                <h3 class="font-medium text-gray-900 dark:text-white">{{ room.name }}</h3>
                <span v-if="room.isPrivate" class="bg-indigo-100 text-indigo-800 dark:bg-indigo-900 dark:text-indigo-300 text-xs px-2 py-1 rounded-full">
                  Private
                </span>
              </div>
              <p class="text-sm text-gray-500 flex items-center mt-1">
                <Users class="h-4 w-4 mr-1" /> 
                Created by {{ room.createdBy.username }}
              </p>
            </div>
            <div class="text-right">
              <div class="text-sm text-gray-500">
                {{ formatDate(room.createdAt) }}
              </div>
              <div v-if="room.memberCount" class="mt-1 text-xs text-gray-500">
                {{ room.memberCount }} members
              </div>
            </div>
          </div>
        </div>
      </div>
    </CardContent>
  </Card>
</template>