import apiClient from '@/utils/http-interceptor'

// Auth API calls
export const authApi = {
  login(credentials: { username: string; password: string }) {
    return apiClient.post('/api/auth/signin', credentials)
  },
  
  register(userData: { 
    username: string; 
    email: string; 
    password: string;
    firstName?: string;
    lastName?: string;
  }) {
    return apiClient.post('/api/auth/signup', userData)
  },
  
  getCurrentUser() {
    return apiClient.get('/api/auth/me')
  }
}

// Chat Rooms API calls
export const chatRoomApi = {
  getAllChatRooms() {
    return apiClient.get('/api/chatrooms')
  },
  
  createChatRoom(name: string) {
    return apiClient.post('/api/chatrooms', { name })
  },
  
  getChatRoomById(id: number) {
    return apiClient.get(`/api/chatrooms/${id}`)
  },
  
  deleteChatRoom(id: number) {
    return apiClient.delete(`/api/chatrooms/${id}`)
  }
}

export default apiClient