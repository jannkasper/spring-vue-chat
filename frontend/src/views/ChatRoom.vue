<script setup lang="ts">
import { ref, onMounted, computed, onBeforeUnmount, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useChatRoomStore, type Message } from '@/stores/chatRoom'
import { webSocketService } from '@/services/websocket'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { FileUpload } from '@/components/ui/file-upload'
import { ArrowLeft, Send, Paperclip, Users, AlertCircle, User } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const chatRoomStore = useChatRoomStore()

// Get chatRoom ID from route params
const chatRoomId = computed(() => route.params.id as string)

// State variables
const message = ref('')
const isSubmitting = ref(false)
const error = ref('')
const messagesEndRef = ref<HTMLDivElement | null>(null)
const messageContainerRef = ref<HTMLDivElement | null>(null)
const fileInput = ref<HTMLInputElement | null>(null)
const selectedFile = ref<File | null>(null)
const fileUploadStatus = ref<'idle' | 'uploading' | 'success' | 'error'>('idle')
const isNearBottom = ref(true)

// Check for authentication
if (!authStore.isAuthenticated || !authStore.user) {
  router.push('/signin')
}

// Function to check if user is near bottom of chat
const checkIfNearBottom = () => {
  if (messageContainerRef.value) {
    const { scrollTop, scrollHeight, clientHeight } = messageContainerRef.value
    // Consider "near bottom" if within 100px of the bottom
    const scrollPosition = scrollHeight - scrollTop - clientHeight
    isNearBottom.value = scrollPosition < 100
  }
}

// Load chat room and messages
onMounted(async () => {
  try {
    if (!chatRoomId.value) return
    
    // Fetch chat room details
    await chatRoomStore.getChatRoom(chatRoomId.value)
    
    // Fetch messages
    await chatRoomStore.fetchMessages(chatRoomId.value)
    
    // Connect to WebSocket for real-time messages
    webSocketService.connect(chatRoomId.value)
    
    // Scroll to bottom of messages
    scrollToBottom()

    // Set up scroll event listener
    if (messageContainerRef.value) {
      messageContainerRef.value.addEventListener('scroll', checkIfNearBottom)
    }
  } catch (err) {
    console.error('Error loading chat room:', err)
    error.value = 'Failed to load chat room'
  }
})

// Cleanup on component unmount
onBeforeUnmount(() => {
  webSocketService.close()
  // Remove scroll event listener
  if (messageContainerRef.value) {
    messageContainerRef.value.removeEventListener('scroll', checkIfNearBottom)
  }
})

// Format date for display
const formatTime = (dateString: string) => {
  return new Date(dateString).toLocaleTimeString(undefined, {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

// Group messages by date
const groupedMessages = computed(() => {
  const grouped: Record<string, Message[]> = {}
  
  chatRoomStore.messages.forEach(message => {
    const date = new Date(message.createdAt).toDateString()
    if (!grouped[date]) {
      grouped[date] = []
    }
    grouped[date].push(message)
  })
  
  return grouped
})

// Check if the message is from the current user
const isCurrentUser = (senderId: string) => {
  return authStore.user?.id?.toString() === senderId
}

// Scroll to bottom of messages
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesEndRef.value) {
      messagesEndRef.value.scrollIntoView({ behavior: 'smooth' })
    }
  })
}

// Watch for new messages and scroll to bottom if user is near bottom
watch(() => chatRoomStore.messages.length, (newLength: number, oldLength: number) => {
  if (newLength > oldLength && isNearBottom.value) {
    // Only scroll to bottom if we're already near the bottom
    scrollToBottom()
  }
})

// Send message
const sendChatMessage = async () => {
  if (!message.value.trim() && !selectedFile.value) {
    return
  }
  
  if (!authStore.user) {
    error.value = 'You must be logged in to send messages'
    return
  }
  
  try {
    isSubmitting.value = true
    error.value = ''
    
    // TODO: Handle file upload
    let fileUrl = undefined
    if (selectedFile.value) {
      // In a real implementation, you would upload the file to a server here
      // and get back a URL to the uploaded file
      fileUrl = '/path/to/uploaded/file'
    }
    
    // Send message via API
    await chatRoomStore.sendMessage(chatRoomId.value, {
      senderId: authStore.user.id.toString(),
      message: message.value,
      fileUrl,
      encrypted: false
    })
    
    // Also send via WebSocket for real-time
    webSocketService.sendMessage(message.value, fileUrl)
    
    // Clear inputs
    message.value = ''
    selectedFile.value = null
    
    // Scroll to bottom
    scrollToBottom()
  } catch (err) {
    console.error('Error sending message:', err)
    error.value = 'Failed to send message'
  } finally {
    isSubmitting.value = false
  }
}

// Handle file selection
const handleFileSelect = (event: Event) => {
  const input = event.target as HTMLInputElement
  if (input.files && input.files.length > 0) {
    selectedFile.value = input.files[0]
  }
}

// Trigger file input click
const openFileDialog = () => {
  if (fileInput.value) {
    fileInput.value.click()
  }
}

// Remove selected file
const removeSelectedFile = () => {
  selectedFile.value = null
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

// Navigate back to dashboard
const navigateBack = () => {
  router.push('/dashboard')
}
</script>

<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 flex flex-col">
    <!-- Header -->
    <header class="bg-white dark:bg-gray-800 shadow-sm sticky top-0 z-10">
      <div class="container mx-auto px-4 py-4 flex justify-between items-center">
        <div class="flex items-center gap-3">
          <Button variant="ghost" size="sm" @click="navigateBack" class="flex items-center gap-1">
            <ArrowLeft class="h-4 w-4" />
            Back
          </Button>
          
          <h1 class="text-xl font-bold text-gray-900 dark:text-white">
            {{ chatRoomStore.currentChatRoom?.name || 'Chat Room' }}
          </h1>
          
          <!-- Private tag if applicable -->
          <span 
            v-if="chatRoomStore.currentChatRoom?.isPrivate" 
            class="bg-indigo-100 text-indigo-800 dark:bg-indigo-900 dark:text-indigo-300 text-xs px-2 py-1 rounded-full"
          >
            Private
          </span>
        </div>
        
        <div class="flex items-center gap-2">
          <Button variant="ghost" size="sm" class="flex items-center gap-1">
            <Users class="h-4 w-4" />
            {{ chatRoomStore.currentChatRoom?.memberCount || 0 }}
          </Button>
        </div>
      </div>
    </header>
    
    <!-- Main Content - Add pb-20 to create space for the fixed footer -->
    <main class="flex-1 container mx-auto px-4 py-4 flex flex-col overflow-hidden pb-24">
      <!-- Loading State -->
      <div v-if="chatRoomStore.messagesLoading" class="flex-1 flex items-center justify-center">
        <div class="text-center">
          <div class="w-10 h-10 border-2 border-primary border-t-transparent rounded-full animate-spin mx-auto"></div>
          <p class="mt-2 text-gray-600 dark:text-gray-300">Loading messages...</p>
        </div>
      </div>
      
      <!-- Error State -->
      <div v-else-if="chatRoomStore.messagesError" class="flex-1 flex items-center justify-center">
        <div class="flex items-center p-4 bg-red-50 dark:bg-red-900/20 rounded-md text-red-600 dark:text-red-400">
          <AlertCircle class="h-5 w-5 mr-2" />
          {{ chatRoomStore.messagesError }}
        </div>
      </div>
      
      <!-- Empty State -->
      <div v-else-if="chatRoomStore.messages.length === 0" class="flex-1 flex items-center justify-center">
        <div class="text-center">
          <p class="text-gray-600 dark:text-gray-300">No messages yet. Start the conversation!</p>
        </div>
      </div>
      
      <!-- Messages -->
      <div 
        v-else 
        class="flex-1 overflow-y-auto pb-4 space-y-6"
        ref="messageContainerRef"
      >
        <div v-for="(messages, date) in groupedMessages" :key="date">
          <!-- Date Separator -->
          <div class="text-center my-4">
            <span class="px-3 py-1 bg-gray-100 dark:bg-gray-800 text-gray-500 text-xs rounded-full">
              {{ formatDate(messages[0].createdAt) }}
            </span>
          </div>
          
          <!-- Messages for the day -->
          <div class="space-y-4">
            <div 
              v-for="msg in messages" 
              :key="msg.id"
              class="flex gap-3"
              :class="isCurrentUser(msg.sender.id) ? 'justify-end' : 'justify-start'"
            >
              <!-- Avatar for other users -->
              <div v-if="!isCurrentUser(msg.sender.id)" class="flex-shrink-0 w-8 h-8 rounded-full bg-blue-100 dark:bg-blue-800 flex items-center justify-center">
                <User class="h-4 w-4 text-blue-600 dark:text-blue-300" />
              </div>
              
              <!-- Message content -->
              <div 
                class="max-w-[80%] rounded-lg p-3"
                :class="isCurrentUser(msg.sender.id) 
                  ? 'bg-blue-600 text-white' 
                  : 'bg-white dark:bg-gray-800 text-gray-900 dark:text-gray-100'"
              >
                <!-- Sender name (only for others) -->
                <div v-if="!isCurrentUser(msg.sender.id)" class="text-xs font-medium mb-1 text-blue-500 dark:text-blue-400">
                  {{ msg.sender.username }}
                </div>
                
                <!-- Message text -->
                <p class="whitespace-pre-wrap break-words">{{ msg.message }}</p>
                
                <!-- File attachment if any -->
                <div v-if="msg.fileUrl" class="mt-2">
                  <a 
                    :href="msg.fileUrl" 
                    target="_blank" 
                    class="flex items-center gap-1 text-xs underline"
                    :class="isCurrentUser(msg.sender.id) ? 'text-blue-200' : 'text-blue-500'"
                  >
                    <Paperclip class="h-3 w-3" />
                    Attachment
                  </a>
                </div>
                
                <!-- Message time -->
                <div 
                  class="text-xs mt-1 text-right"
                  :class="isCurrentUser(msg.sender.id) ? 'text-blue-200' : 'text-gray-500'"
                >
                  {{ formatTime(msg.createdAt) }}
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Scroll anchor -->
        <div ref="messagesEndRef"></div>
      </div>
    </main>
      
    <!-- Message input - Fixed at bottom -->
    <div class="fixed bottom-0 left-0 right-0 bg-white dark:bg-gray-800 shadow-lg border-t border-gray-200 dark:border-gray-700 z-10">
      <div class="container mx-auto px-4 py-3">
        <!-- Selected file preview -->
        <div v-if="selectedFile" class="mb-2 p-2 bg-blue-50 dark:bg-blue-900/20 rounded flex items-center justify-between">
          <div class="flex items-center gap-2 text-sm">
            <Paperclip class="h-4 w-4 text-blue-500" />
            <span class="text-gray-700 dark:text-gray-300">{{ selectedFile.name }}</span>
          </div>
          <Button variant="ghost" size="sm" @click="removeSelectedFile" class="h-6 w-6 p-0">
            <span class="sr-only">Remove file</span>
            <span aria-hidden="true">×</span>
          </Button>
        </div>
        
        <!-- Error message -->
        <div v-if="error" class="mb-2 text-sm text-red-600 bg-red-50 dark:bg-red-900/20 p-2 rounded">
          <AlertCircle class="h-4 w-4 inline mr-1" />
          {{ error }}
        </div>
        
        <div class="flex items-center gap-2">
          <!-- File upload (hidden) -->
          <input 
            ref="fileInput"
            type="file" 
            class="hidden" 
            @change="handleFileSelect"
          />
          
          <!-- File upload button -->
          <Button 
            type="button" 
            variant="ghost" 
            size="icon"
            @click="openFileDialog"
            :disabled="isSubmitting"
          >
            <Paperclip class="h-5 w-5 text-gray-500" />
            <span class="sr-only">Attach file</span>
          </Button>
          
          <!-- Message input -->
          <Input
            v-model="message"
            placeholder="Type your message..."
            class="flex-1"
            :disabled="isSubmitting"
            @keyup.enter="sendChatMessage"
          />
          
          <!-- Send button -->
          <Button 
            type="button" 
            variant="default" 
            @click="sendChatMessage"
            :disabled="isSubmitting || (!message.trim() && !selectedFile)"
          >
            <Send class="h-4 w-4 mr-1" />
            Send
          </Button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Custom scrollbar for the message area */
.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: transparent;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}

.dark .overflow-y-auto::-webkit-scrollbar-thumb {
  background-color: rgba(255, 255, 255, 0.1);
}
</style>