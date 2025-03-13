import { useChatRoomStore, type Message } from '@/stores/chatRoom'
import { useAuthStore } from '@/stores/auth'

// Define the WebSocket message type received from server
interface WebSocketMessage {
  chatRoomId: string
  senderId: string
  senderUsername: string
  message: string
  fileUrl?: string
  createdAt: string
  type: 'CHAT' | 'JOIN' | 'LEAVE' | 'TYPING'
}

export class WebSocketService {
  private socket: WebSocket | null = null
  private chatRoomId: string | null = null
  private reconnectAttempts = 0
  private maxReconnectAttempts = 5
  private reconnectTimeout: ReturnType<typeof setTimeout> | null = null
  
  connect(chatRoomId: string) {
    // Close any existing connections
    this.close()
    
    // Set the current chat room ID
    this.chatRoomId = chatRoomId
    
    // Get the JWT token for authentication
    const authStore = useAuthStore()
    const token = authStore.token
    
    if (!token) {
      console.error('Cannot connect to WebSocket: No authentication token')
      return
    }
    
    try {
      // Get the base URL (without the port or protocol) - localhost or domain name
      const host = window.location.hostname
      // Create WebSocket connection with SockJS (as configured on the backend)
      // We use the backend port 8080 directly here
      const sockjsUrl = `http://${host}:8080/ws?token=${token}`
      
      // Using native WebSocket as a fallback, but ideally you should use SockJS or a WebSocket client library
      this.socket = new WebSocket(`ws://${host}:8080/ws/chat/${chatRoomId}`)
      
      // Connection opened
      this.socket.addEventListener('open', (event) => {
        console.log('WebSocket connection established')
        this.reconnectAttempts = 0 // Reset reconnect attempts on successful connection
        
        // Send authentication message
        if (this.socket && token) {
          this.socket.send(JSON.stringify({ 
            type: 'AUTH', 
            token,
            chatRoomId: this.chatRoomId
          }))
        }
      })
      
      // Listen for messages
      this.socket.addEventListener('message', (event) => {
        try {
          const data: WebSocketMessage = JSON.parse(event.data)
          this.handleMessage(data)
        } catch (err) {
          console.error('Error parsing WebSocket message:', err)
        }
      })
      
      // Connection closed
      this.socket.addEventListener('close', (event) => {
        console.log('WebSocket connection closed:', event.code, event.reason)
        this.attemptReconnect()
      })
      
      // Error handling
      this.socket.addEventListener('error', (event) => {
        console.error('WebSocket error:', event)
        this.close()
        this.attemptReconnect()
      })
    } catch (err) {
      console.error('Failed to create WebSocket connection:', err)
    }
  }
  
  private handleMessage(data: WebSocketMessage) {
    const chatRoomStore = useChatRoomStore()
    
    // Handle different message types
    switch (data.type) {
      case 'CHAT':
        // Convert the WebSocket message to our Message type
        const message: Message = {
          id: crypto.randomUUID(), // Generate a temp ID if not provided
          chatRoomId: data.chatRoomId,
          sender: {
            id: data.senderId,
            username: data.senderUsername
          },
          message: data.message,
          fileUrl: data.fileUrl,
          createdAt: data.createdAt,
          encrypted: false
        }
        
        // Add the message to the store
        chatRoomStore.addMessage(message)
        break
      
      case 'JOIN':
        // Handle user joining the chat
        console.log(`User ${data.senderUsername} joined the chat`)
        break
      
      case 'LEAVE':
        // Handle user leaving the chat
        console.log(`User ${data.senderUsername} left the chat`)
        break
      
      case 'TYPING':
        // Handle typing indicator
        console.log(`User ${data.senderUsername} is typing...`)
        break
    }
  }
  
  sendMessage(message: string, fileUrl?: string) {
    if (!this.socket || this.socket.readyState !== WebSocket.OPEN) {
      console.error('WebSocket is not connected')
      return
    }
    
    const authStore = useAuthStore()
    const user = authStore.user
    
    if (!user || !this.chatRoomId) {
      console.error('Cannot send message: Missing user or chat room ID')
      return
    }
    
    // Create message object
    const messageObject: WebSocketMessage = {
      chatRoomId: this.chatRoomId,
      senderId: user.id,
      senderUsername: user.username,
      message,
      fileUrl,
      createdAt: new Date().toISOString(),
      type: 'CHAT'
    }
    
    // Send the message
    this.socket.send(JSON.stringify(messageObject))
  }
  
  sendTypingStatus() {
    if (!this.socket || this.socket.readyState !== WebSocket.OPEN) {
      return
    }
    
    const authStore = useAuthStore()
    const user = authStore.user
    
    if (!user || !this.chatRoomId) {
      return
    }
    
    // Create typing indicator object
    const typingObject: WebSocketMessage = {
      chatRoomId: this.chatRoomId,
      senderId: user.id,
      senderUsername: user.username,
      message: '',
      createdAt: new Date().toISOString(),
      type: 'TYPING'
    }
    
    // Send the typing indicator
    this.socket.send(JSON.stringify(typingObject))
  }
  
  private attemptReconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('Maximum reconnect attempts reached')
      return
    }
    
    this.reconnectAttempts++
    
    // Exponential backoff for reconnect
    const delay = Math.min(1000 * Math.pow(2, this.reconnectAttempts), 30000)
    console.log(`Attempting to reconnect in ${delay}ms (attempt ${this.reconnectAttempts})`)
    
    this.reconnectTimeout = setTimeout(() => {
      if (this.chatRoomId) {
        this.connect(this.chatRoomId)
      }
    }, delay)
  }
  
  close() {
    // Clear any reconnect timeout
    if (this.reconnectTimeout) {
      clearTimeout(this.reconnectTimeout)
      this.reconnectTimeout = null
    }
    
    // Close the WebSocket if it exists
    if (this.socket) {
      this.socket.close()
      this.socket = null
    }
    
    this.chatRoomId = null
  }
}

// Create a singleton instance
export const webSocketService = new WebSocketService()