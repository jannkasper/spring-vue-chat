import axios from 'axios'
import router from '@/router'

// Create axios instance
const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
apiClient.interceptors.response.use(
  (response) => {
    return response
  },
  async (error) => {
    // Handle 401 Unauthorized error
    if (error.response && error.response.status === 401) {
      // Clear token and redirect to login
      localStorage.removeItem('token')
      router.push('/signin')
    }
    
    // Handle 403 Forbidden error
    if (error.response && error.response.status === 403) {
      router.push('/403')
    }
    
    // Handle 500 Internal Server error
    if (error.response && error.response.status >= 500) {
      console.error('Server error:', error)
    }
    
    return Promise.reject(error)
  }
)

export default apiClient