<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useChatRoomStore } from '@/stores/chatRoom'
import { useAuthStore } from '@/stores/auth'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { ArrowLeft, AlertCircle, Lock, Globe } from 'lucide-vue-next'

const router = useRouter()
const chatRoomStore = useChatRoomStore()
const authStore = useAuthStore()

const chatName = ref('')
const isPrivate = ref(false)
const isSubmitting = ref(false)
const error = ref('')

const handleSubmit = async () => {
  // Validate input
  if (!chatName.value.trim()) {
    error.value = 'Chat room name is required'
    return
  }
  
  if (chatName.value.length < 3) {
    error.value = 'Chat room name must be at least 3 characters'
    return
  }
  
  try {
    isSubmitting.value = true
    error.value = ''
    
    // Create the chat room
    const chatRoom = await chatRoomStore.createChatRoom({
      name: chatName.value,
      isPrivate: isPrivate.value
    })
    
    // Redirect to the new chat room
    router.push({ name: 'chatRoom', params: { id: chatRoom.id } })
  } catch (err) {
    error.value = chatRoomStore.error || 'Failed to create chat room'
  } finally {
    isSubmitting.value = false
  }
}

const navigateBack = () => {
  router.push('/dashboard')
}
</script>

<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 flex flex-col">
    <!-- Header -->
    <header class="bg-white dark:bg-gray-800 shadow-sm">
      <div class="container mx-auto px-4 py-4 flex justify-between items-center">
        <div class="flex items-center gap-2">
          <Button variant="ghost" size="sm" @click="navigateBack" class="flex items-center gap-1">
            <ArrowLeft class="h-4 w-4" />
            Back
          </Button>
          <h1 class="text-xl font-bold text-gray-900 dark:text-white">Create New Chat Room</h1>
        </div>
      </div>
    </header>
    
    <!-- Main Content -->
    <main class="flex-1 container mx-auto px-4 py-8 flex items-center justify-center">
      <Card class="w-full max-w-lg">
        <CardHeader>
          <CardTitle>Create a Chat Room</CardTitle>
          <CardDescription>
            Create a new chat room and invite others to join the conversation.
          </CardDescription>
        </CardHeader>
        
        <CardContent>
          <form @submit.prevent="handleSubmit" class="space-y-6">
            <div class="space-y-2">
              <Label for="chatName">Chat Room Name</Label>
              <Input 
                id="chatName" 
                v-model="chatName"
                placeholder="Enter a name for your chat room"
                :disabled="isSubmitting"
              />
              <p class="text-sm text-gray-500">
                Choose a descriptive name that tells people what your chat is about.
              </p>
            </div>
            
            <div class="space-y-2">
              <Label>Chat Room Type</Label>
              <div class="grid grid-cols-2 gap-4">
                <div 
                  class="border rounded-lg p-4 cursor-pointer text-center transition-colors"
                  :class="!isPrivate ? 'border-blue-500 bg-blue-50 dark:bg-blue-900/20' : 'hover:border-gray-400'"
                  @click="isPrivate = false"
                >
                  <Globe class="h-6 w-6 mx-auto mb-2" :class="!isPrivate ? 'text-blue-500' : 'text-gray-400'" />
                  <h3 class="font-medium" :class="!isPrivate ? 'text-blue-700 dark:text-blue-300' : ''">
                    Public
                  </h3>
                  <p class="text-xs text-gray-500 mt-1">
                    Anyone can find and join
                  </p>
                </div>
                
                <div 
                  class="border rounded-lg p-4 cursor-pointer text-center transition-colors"
                  :class="isPrivate ? 'border-blue-500 bg-blue-50 dark:bg-blue-900/20' : 'hover:border-gray-400'"
                  @click="isPrivate = true"
                >
                  <Lock class="h-6 w-6 mx-auto mb-2" :class="isPrivate ? 'text-blue-500' : 'text-gray-400'" />
                  <h3 class="font-medium" :class="isPrivate ? 'text-blue-700 dark:text-blue-300' : ''">
                    Private
                  </h3>
                  <p class="text-xs text-gray-500 mt-1">
                    Only invited users can join
                  </p>
                </div>
              </div>
            </div>
            
            <div v-if="error" class="flex items-center p-3 text-sm text-red-600 bg-red-50 dark:bg-red-900/20 dark:text-red-400 rounded-md">
              <AlertCircle class="h-4 w-4 mr-2 flex-shrink-0" />
              {{ error }}
            </div>
          </form>
        </CardContent>
        
        <CardFooter class="flex justify-end gap-3">
          <Button variant="outline" @click="navigateBack" :disabled="isSubmitting">
            Cancel
          </Button>
          <Button type="submit" @click="handleSubmit" :disabled="isSubmitting">
            {{ isSubmitting ? 'Creating...' : 'Create Chat Room' }}
          </Button>
        </CardFooter>
      </Card>
    </main>
  </div>
</template>