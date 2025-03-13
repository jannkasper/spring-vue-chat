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
    return apiClient.get('/api/chats')
  },
  
  createChatRoom(data: { name: string, isPrivate: boolean }) {
    return apiClient.post('/api/chats', data)
  },
  
  getChatRoomById(id: string) {
    return apiClient.get(`/api/chats/${id}`)
  },
  
  joinChatRoom(chatRoomId: string, userId: string) {
    return apiClient.post(`/api/chats/${chatRoomId}/join`, { userId })
  },
  
  removeMemberFromChatRoom(chatRoomId: string, userId: string) {
    return apiClient.delete(`/api/chats/${chatRoomId}/members/${userId}`)
  },
  
  // Message APIs
  getChatMessages(chatRoomId: string) {
    return apiClient.get(`/api/chats/${chatRoomId}/messages`)
  },
  
  getChatMessagesPaginated(chatRoomId: string, page = 0, size = 20) {
    return apiClient.get(`/api/chats/${chatRoomId}/messages/paginated?page=${page}&size=${size}`)
  },
  
  sendMessage(chatRoomId: string, data: { 
    senderId: string, 
    message: string,
    fileUrl?: string,
    encrypted?: boolean 
  }) {
    return apiClient.post(`/api/chats/${chatRoomId}/messages`, data)
  },
  
  searchMessages(chatRoomId: string, query: string, page = 0, size = 20) {
    return apiClient.get(`/api/chats/${chatRoomId}/messages/search?query=${query}&page=${page}&size=${size}`)
  }
}

export default apiClient