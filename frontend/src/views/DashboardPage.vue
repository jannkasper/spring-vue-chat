<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useChatRoomStore } from '@/stores/chatRoom'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { LogOut, MessageCircle, Plus } from 'lucide-vue-next'
import ChatList from '@/components/ChatList.vue'
import PublicChatList from '@/components/PublicChatList.vue'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'

const router = useRouter()

const authStore = useAuthStore()
const chatRoomStore = useChatRoomStore()

const newRoomName = ref('')
const isCreatingRoom = ref(false)
const createRoomError = ref('')
const activeTab = ref('my-chats')

onMounted(async () => {
  await chatRoomStore.fetchChatRooms()
})

const navigateToCreateChat = () => {
  router.push({ name: 'createChat' })
}

const openChatRoom = (roomId: string) => {
  router.push({ name: 'chatRoom', params: { id: roomId } })
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
            <CardTitle class="text-xl">Chat Rooms</CardTitle>
            <CardDescription>
              Create or join chat rooms
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div class="space-y-4">
              <Button 
                @click="navigateToCreateChat" 
                class="w-full flex justify-center items-center gap-2"
              >
                <Plus class="h-4 w-4" />
                Create New Chat Room
              </Button>
              
              <p class="text-sm text-gray-600 dark:text-gray-300">
                Click the button above to create a new chat room and invite others to join.
              </p>
            </div>
          </CardContent>
        </Card>
        
        <!-- Chat Rooms List with Tabs -->
        <div class="md:col-span-2">
          <Tabs v-model="activeTab" default-value="my-chats" class="w-full">
            <TabsList class="grid w-full grid-cols-2">
              <TabsTrigger value="my-chats">My Chat Rooms</TabsTrigger>
              <TabsTrigger value="public-chats">Public Chat Rooms</TabsTrigger>
            </TabsList>
            
            <TabsContent value="my-chats">
              <ChatList title="Your Chat Rooms" description="Select a room to start chatting" />
            </TabsContent>
            
            <TabsContent value="public-chats">
              <PublicChatList title="Public Chat Rooms" description="Discover and join public chat rooms" />
            </TabsContent>
          </Tabs>
        </div>
      </div>
    </main>
  </div>
</template>