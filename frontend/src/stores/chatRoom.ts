import { defineStore } from 'pinia'
import { ref } from 'vue'
import { chatRoomApi } from '@/services/api'

export interface ChatRoom {
  id: string
  name: string
  isPrivate: boolean
  createdAt: string
  updatedAt: string
  memberCount?: number
  createdBy: {
    id: string
    username: string
  }
}

export interface Message {
  id: string
  chatRoomId: string
  sender: {
    id: string
    username: string
  }
  message: string
  fileUrl?: string
  createdAt: string
  encrypted: boolean
}

export const useChatRoomStore = defineStore('chatRoom', () => {
  const chatRooms = ref<ChatRoom[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  const fetchChatRooms = async () => {
    loading.value = true
    error.value = null
    
    try {
      const response = await chatRoomApi.getAllChatRooms()
      chatRooms.value = response.data
    } catch (err: any) {
      console.error('Failed to fetch chat rooms:', err)
      error.value = err.response?.data?.message || 'Failed to load chat rooms.'
    } finally {
      loading.value = false
    }
  }

  const createChatRoom = async (data: { name: string, isPrivate: boolean }) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await chatRoomApi.createChatRoom(data)
      chatRooms.value.push(response.data)
      return response.data
    } catch (err: any) {
      console.error('Failed to create chat room:', err)
      error.value = err.response?.data?.message || 'Failed to create chat room.'
      throw error.value
    } finally {
      loading.value = false
    }
  }
  
  // For messages in a specific chat room
  const currentChatRoom = ref<ChatRoom | null>(null)
  const messages = ref<Message[]>([])
  const messagesLoading = ref(false)
  const messagesError = ref<string | null>(null)
  
  const getChatRoom = async (chatRoomId: string) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await chatRoomApi.getChatRoomById(chatRoomId)
      currentChatRoom.value = response.data
      return response.data
    } catch (err: any) {
      console.error('Failed to fetch chat room:', err)
      error.value = err.response?.data?.message || 'Failed to load chat room.'
      throw error.value
    } finally {
      loading.value = false
    }
  }
  
  const fetchMessages = async (chatRoomId: string) => {
    messagesLoading.value = true
    messagesError.value = null
    
    try {
      const response = await chatRoomApi.getChatMessages(chatRoomId)
      messages.value = response.data
      return response.data
    } catch (err: any) {
      console.error('Failed to fetch messages:', err)
      messagesError.value = err.response?.data?.message || 'Failed to load messages.'
      throw messagesError.value
    } finally {
      messagesLoading.value = false
    }
  }
  
  const sendMessage = async (chatRoomId: string, data: { 
    senderId: string, 
    message: string,
    fileUrl?: string,
    encrypted?: boolean 
  }) => {
    try {
      const response = await chatRoomApi.sendMessage(chatRoomId, data)
      // Add new message to the list
      messages.value.push(response.data)
      return response.data
    } catch (err: any) {
      console.error('Failed to send message:', err)
      messagesError.value = err.response?.data?.message || 'Failed to send message.'
      throw messagesError.value
    }
  }
  
  // Method to add a message received via WebSocket
  const addMessage = (message: Message) => {
    messages.value.push(message)
  }

  return {
    // Chat rooms
    chatRooms,
    loading,
    error,
    fetchChatRooms,
    createChatRoom,
    getChatRoom,
    currentChatRoom,
    
    // Messages
    messages,
    messagesLoading,
    messagesError,
    fetchMessages,
    sendMessage,
    addMessage
  }
})