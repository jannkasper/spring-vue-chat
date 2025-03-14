<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useChatRoomStore, type ChatRoom } from '@/stores/chatRoom'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Users, AlertCircle, Globe } from 'lucide-vue-next'

const props = defineProps<{
  title?: string
  description?: string
}>()

const router = useRouter()
const chatRoomStore = useChatRoomStore()
const processingRoomId = ref<string | null>(null)
const joinError = ref<string | null>(null)

// Default values
const cardTitle = computed(() => props.title || 'Public Chat Rooms')
const cardDescription = computed(() => props.description || 'Discover and join public chat rooms')

// Fetch public chat rooms
onMounted(() => {
  loadPublicChatRooms()
})

const loadPublicChatRooms = async (page = 0) => {
  await chatRoomStore.fetchPublicChatRooms(page, chatRoomStore.publicChatRoomsSize)
}

// Open a chat room with auto-join
const openChatRoom = async (room: ChatRoom) => {
  try {
    joinError.value = null
    processingRoomId.value = room.id
    
    // Join the public chat room first
    await chatRoomStore.joinPublicChatRoom(room.id)
    
    // Then navigate to the chat room
    router.push({ name: 'chatRoom', params: { id: room.id } })
  } catch (error: any) {
    console.error('Failed to join room:', error)
    joinError.value = typeof error === 'string' ? error : 'Failed to join chat room'
  } finally {
    processingRoomId.value = null
  }
}

// Join a public room
const joinRoom = async (room: ChatRoom) => {
  try {
    // Assuming the store already has the user ID from auth store
    await chatRoomStore.getChatRoom(room.id)
    router.push({ name: 'chatRoom', params: { id: room.id } })
  } catch (error) {
    console.error('Failed to join room:', error)
  }
}

// Format date for display
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
}

// Pagination
const goToPage = (page: number) => {
  loadPublicChatRooms(page)
}

const pageNumbers = computed(() => {
  const totalPages = chatRoomStore.publicChatRoomsTotalPages
  const currentPage = chatRoomStore.publicChatRoomsPage
  
  if (totalPages <= 5) {
    return Array.from({ length: totalPages }, (_, i) => i)
  }
  
  // Show first, last, current, and up to 2 pages before and after current
  const pages: number[] = [0]
  
  // Pages around current
  for (let i = Math.max(1, currentPage - 1); i <= Math.min(totalPages - 2, currentPage + 1); i++) {
    pages.push(i)
  }
  
  // Last page
  if (totalPages > 1) {
    pages.push(totalPages - 1)
  }
  
  // Sort and deduplicate
  return [...new Set(pages)].sort((a, b) => a - b)
})
</script>

<template>
  <Card>
    <CardHeader>
      <div class="flex justify-between items-center">
        <div>
          <CardTitle class="text-xl flex items-center gap-2">
            <Globe class="h-5 w-5" />
            {{ cardTitle }}
          </CardTitle>
          <CardDescription>
            {{ cardDescription }}
          </CardDescription>
        </div>
      </div>
    </CardHeader>
    
    <CardContent>
      <!-- Error message for join operations -->
      <div v-if="joinError" class="mb-4 p-3 bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 rounded-md flex items-center">
        <AlertCircle class="h-4 w-4 mr-2" />
        {{ joinError }}
      </div>
      
      <div v-if="chatRoomStore.publicChatRoomsLoading" class="py-4 text-center text-gray-500">
        <div class="w-8 h-8 border-2 border-primary border-t-transparent rounded-full animate-spin mx-auto"></div>
        <div class="mt-2">Loading public chat rooms...</div>
      </div>
      
      <div v-else-if="chatRoomStore.publicChatRoomsError" class="flex items-center p-3 text-sm text-red-600 bg-red-50 rounded-md">
        <AlertCircle class="h-4 w-4 mr-2" />
        {{ chatRoomStore.publicChatRoomsError }}
      </div>
      
      <div v-else-if="chatRoomStore.publicChatRooms.length === 0" class="py-4 text-center text-gray-500">
        No public chat rooms available.
      </div>
      
      <div v-else class="space-y-3">
        <div 
          v-for="room in chatRoomStore.publicChatRooms" 
          :key="room.id" 
          class="p-4 border rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 cursor-pointer transition-colors relative"
          @click="openChatRoom(room)"
        >
          <!-- Show loading spinner when joining -->
          <div v-if="processingRoomId === room.id" 
               class="absolute inset-0 bg-white/70 dark:bg-black/70 flex items-center justify-center rounded-lg z-10">
            <div class="w-6 h-6 border-2 border-primary border-t-transparent rounded-full animate-spin"></div>
          </div>
          
          <div class="flex justify-between items-center">
            <div>
              <div class="flex items-center gap-2">
                <h3 class="font-medium text-gray-900 dark:text-white">{{ room.name }}</h3>
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
      
      <!-- Pagination -->
      <div v-if="chatRoomStore.publicChatRooms.length > 0" class="flex justify-center mt-4 gap-1">
        <Button 
          size="sm" 
          variant="outline"
          :disabled="chatRoomStore.publicChatRoomsPage === 0"
          @click="goToPage(chatRoomStore.publicChatRoomsPage - 1)"
          class="px-3"
        >
          Prev
        </Button>
        
        <Button 
          v-for="page in pageNumbers" 
          :key="page"
          size="sm" 
          :variant="page === chatRoomStore.publicChatRoomsPage ? 'default' : 'outline'"
          @click="goToPage(page)"
          class="px-3"
        >
          {{ page + 1 }}
        </Button>
        
        <Button 
          size="sm" 
          variant="outline"
          :disabled="chatRoomStore.publicChatRoomsPage >= chatRoomStore.publicChatRoomsTotalPages - 1"
          @click="goToPage(chatRoomStore.publicChatRoomsPage + 1)"
          class="px-3"
        >
          Next
        </Button>
      </div>
    </CardContent>
  </Card>
</template> 