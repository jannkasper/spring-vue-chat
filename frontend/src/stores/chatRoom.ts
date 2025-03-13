import { defineStore } from 'pinia'
import { ref } from 'vue'
import { chatRoomApi } from '@/services/api'

export interface ChatRoom {
  id: number
  name: string
  createdAt: string
  updatedAt: string
  createdBy: {
    id: number
    username: string
  }
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

  const createChatRoom = async (name: string) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await chatRoomApi.createChatRoom(name)
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

  return {
    chatRooms,
    loading,
    error,
    fetchChatRooms,
    createChatRoom
  }
})