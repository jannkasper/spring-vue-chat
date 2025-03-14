import axios from 'axios'
import router from '@/router'

// Create axios instance
// When baseURL is empty or relative, requests are sent to the current origin
// which allows Nginx to proxy the requests to the backend
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
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