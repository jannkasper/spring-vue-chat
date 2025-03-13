<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useChatRoomStore, type ChatRoom } from '@/stores/chatRoom'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { PlusCircle, LogOut, Users, MessageCircle, AlertCircle } from 'lucide-vue-next'

const authStore = useAuthStore()
const chatRoomStore = useChatRoomStore()

const newRoomName = ref('')
const isCreatingRoom = ref(false)
const createRoomError = ref('')

onMounted(async () => {
  await chatRoomStore.fetchChatRooms()
})

const handleCreateRoom = async () => {
  if (!newRoomName.value.trim()) {
    createRoomError.value = 'Room name cannot be empty'
    return
  }
  
  if (newRoomName.value.length < 3) {
    createRoomError.value = 'Room name must be at least 3 characters'
    return
  }
  
  try {
    isCreatingRoom.value = true
    await chatRoomStore.createChatRoom(newRoomName.value)
    newRoomName.value = ''
    createRoomError.value = ''
  } catch (error) {
    createRoomError.value = chatRoomStore.error || 'Failed to create room'
  } finally {
    isCreatingRoom.value = false
  }
}

const handleLogout = () => {
  authStore.logout()
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}
</script>

<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900">
    <!-- Header -->
    <header class="bg-white dark:bg-gray-800 shadow-sm">
      <div class="container mx-auto px-4 py-4 flex justify-between items-center">
        <h1 class="text-xl font-bold text-gray-900 dark:text-white flex items-center">
          <MessageCircle class="h-6 w-6 mr-2" /> Spring Vue Chat
        </h1>
        
        <div class="flex items-center gap-4">
          <span class="text-sm text-gray-600 dark:text-gray-300">
            Hello, {{ authStore.user?.username || 'User' }}
          </span>
          <Button size="sm" variant="outline" @click="handleLogout">
            <LogOut class="h-4 w-4 mr-2" /> Logout
          </Button>
        </div>
      </div>
    </header>
    
    <!-- Main Content -->
    <main class="container mx-auto px-4 py-8">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <!-- Create New Chat Room -->
        <Card>
          <CardHeader>
            <CardTitle class="text-xl">Create New Chat Room</CardTitle>
            <CardDescription>
              Create a new room to start chatting with others
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div class="space-y-4">
              <div class="flex">
                <Input
                  v-model="newRoomName"
                  placeholder="Enter room name"
                  class="rounded-r-none"
                  :disabled="isCreatingRoom"
                />
                <Button 
                  @click="handleCreateRoom" 
                  class="rounded-l-none" 
                  :disabled="isCreatingRoom"
                >
                  <PlusCircle class="h-4 w-4 mr-2" />
                  {{ isCreatingRoom ? 'Creating...' : 'Create' }}
                </Button>
              </div>
              
              <div v-if="createRoomError" class="flex items-center p-3 text-sm text-red-600 bg-red-50 rounded-md">
                <AlertCircle class="h-4 w-4 mr-2" />
                {{ createRoomError }}
              </div>
            </div>
          </CardContent>
        </Card>
        
        <!-- Chat Rooms List -->
        <Card class="md:col-span-2">
          <CardHeader>
            <CardTitle class="text-xl">Your Chat Rooms</CardTitle>
            <CardDescription>
              Select a room to start chatting
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div v-if="chatRoomStore.loading" class="py-4 text-center text-gray-500">
              Loading chat rooms...
            </div>
            
            <div v-else-if="chatRoomStore.error" class="flex items-center p-3 text-sm text-red-600 bg-red-50 rounded-md">
              <AlertCircle class="h-4 w-4 mr-2" />
              {{ chatRoomStore.error }}
            </div>
            
            <div v-else-if="chatRoomStore.chatRooms.length === 0" class="py-4 text-center text-gray-500">
              No chat rooms found. Create a new one to get started!
            </div>
            
            <div v-else class="space-y-3">
              <div v-for="room in chatRoomStore.chatRooms" :key="room.id" class="p-4 border rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 cursor-pointer transition-colors">
                <div class="flex justify-between items-center">
                  <div>
                    <h3 class="font-medium text-gray-900 dark:text-white">{{ room.name }}</h3>
                    <p class="text-sm text-gray-500 flex items-center mt-1">
                      <Users class="h-4 w-4 mr-1" /> 
                      Created by {{ room.createdBy.username }}
                    </p>
                  </div>
                  <div class="text-sm text-gray-500">
                    {{ formatDate(room.createdAt) }}
                  </div>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </main>
  </div>
</template>