import { useChatRoomStore, type Message } from '@/stores/chatRoom'
import { useAuthStore } from '@/stores/auth'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

// Define the WebSocket message type received from server
interface WebSocketMessage {
  chatRoomId: string
  senderId: string
  senderUsername: string
  message: string
  fileUrl?: string
  createdAt: string
  type: 'CHAT' | 'JOIN' | 'LEAVE' | 'TYPING' | 'AUTH'
}

export class WebSocketService {
  private stompClient: Client | null = null
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
      
      // Create a STOMP client over SockJS
      this.stompClient = new Client({
        webSocketFactory: () => new SockJS('/ws'),
        connectHeaders: {
          'Authorization': `Bearer ${token}`
        },
        debug: function(str) {
          console.log(str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000
      });
      
      // Connection opened handler
      this.stompClient.onConnect = (frame) => {
        console.log('WebSocket connection established');
        this.reconnectAttempts = 0; // Reset reconnect attempts on successful connection
        
        // Subscribe to the chat room topic
        this.stompClient?.subscribe(`/topic/chat/${chatRoomId}`, (message) => {
          try {
            const data: WebSocketMessage = JSON.parse(message.body);
            this.handleMessage(data);
          } catch (err) {
            console.error('Error parsing WebSocket message:', err);
          }
        });

        // Subscribe to typing notifications
        this.stompClient?.subscribe(`/topic/chat/${chatRoomId}/typing`, (message) => {
          try {
            const data: WebSocketMessage = JSON.parse(message.body);
            this.handleMessage(data);
          } catch (err) {
            console.error('Error parsing WebSocket message:', err);
          }
        });
      };
      
      // Error handling
      this.stompClient.onStompError = (frame) => {
        console.error('STOMP error:', frame.headers['message'], frame.body);
      };
      
      this.stompClient.onWebSocketError = (event) => {
        console.error('WebSocket error:', event);
        this.attemptReconnect();
      };
      
      this.stompClient.onWebSocketClose = (event) => {
        console.log('WebSocket connection closed:', event.code, event.reason);
        this.attemptReconnect();
      };
      
      // Activate the connection
      this.stompClient.activate();
      
    } catch (err) {
      console.error('Failed to create WebSocket connection:', err);
    }
  }
  
  private handleMessage(data: WebSocketMessage) {
    const chatRoomStore = useChatRoomStore()
    const authStore = useAuthStore()
    
    // Handle different message types
    switch (data.type) {
      case 'CHAT':
        // Skip messages from the current user to avoid duplicates
        // Since they're already added when the user sends them locally
        if (authStore.user && data.senderId === authStore.user.id.toString()) {
          console.log('Skipping message from current user as it was already added')
          break
        }
        
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
    if (!this.stompClient || !this.stompClient.connected) {
      console.error('WebSocket is not connected');
      return;
    }
    
    const authStore = useAuthStore();
    const user = authStore.user;
    
    if (!user || !this.chatRoomId) {
      console.error('Cannot send message: Missing user or chat room ID');
      return;
    }
    
    // Create message object
    const messageObject: WebSocketMessage = {
      chatRoomId: String(this.chatRoomId),
      senderId: String(user.id),
      senderUsername: user.username,
      message,
      fileUrl,
      createdAt: new Date().toISOString(),
      type: 'CHAT'
    };
    
    // Send the message
    this.stompClient.publish({
      destination: `/app/chat/${this.chatRoomId}`,
      body: JSON.stringify(messageObject)
    });
  }
  
  sendTypingStatus() {
    if (!this.stompClient || !this.stompClient.connected) {
      return;
    }
    
    const authStore = useAuthStore();
    const user = authStore.user;
    
    if (!user || !this.chatRoomId) {
      return;
    }
    
    // Create typing indicator object
    const typingObject: WebSocketMessage = {
      chatRoomId: String(this.chatRoomId),
      senderId: String(user.id),
      senderUsername: user.username,
      message: '',
      createdAt: new Date().toISOString(),
      type: 'TYPING'
    };
    
    // Send the typing indicator
    this.stompClient.publish({
      destination: `/app/chat/${this.chatRoomId}/typing`,
      body: JSON.stringify(typingObject)
    });
  }
  
  private attemptReconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('Maximum reconnect attempts reached');
      return;
    }
    
    this.reconnectAttempts++;
    
    // Exponential backoff for reconnect
    const delay = Math.min(1000 * Math.pow(2, this.reconnectAttempts), 30000);
    console.log(`Attempting to reconnect in ${delay}ms (attempt ${this.reconnectAttempts})`);
    
    this.reconnectTimeout = setTimeout(() => {
      if (this.chatRoomId) {
        this.connect(this.chatRoomId);
      }
    }, delay);
  }
  
  close() {
    // Clear any reconnect timeout
    if (this.reconnectTimeout) {
      clearTimeout(this.reconnectTimeout);
      this.reconnectTimeout = null;
    }
    
    // Close the WebSocket if it exists
    if (this.stompClient) {
      this.stompClient.deactivate();
      this.stompClient = null;
    }
    
    this.chatRoomId = null;
  }
}

// Create a singleton instance
export const webSocketService = new WebSocketService()