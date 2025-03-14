import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import router from '@/router'
import { authApi } from '@/services/api'

interface User {
  id: number
  username: string
  email: string
}

interface LoginCredentials {
  username: string
  password: string
}

interface RegisterCredentials {
  username: string
  email: string
  password: string
  firstName?: string
  lastName?: string
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<User | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => !!token.value)

  // Initialize user from token if it exists
  const initUser = async () => {
    if (token.value) {
      try {
        const response = await authApi.getCurrentUser()
        user.value = response.data
      } catch (err) {
        console.error('Failed to fetch user data:', err)
        logout()
      }
    }
  }

  const login = async (credentials: LoginCredentials) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await authApi.login(credentials)
      token.value = response.data.token
      if (token.value) {
        localStorage.setItem('token', token.value)
      }
      
      // Get user details
      user.value = {
        id: response.data.id,
        username: response.data.username,
        email: response.data.email
      }
      
      router.push('/dashboard')
    } catch (err: any) {
      console.error('Login failed:', err)
      error.value = err.response?.data?.message || 'Failed to login. Please try again.'
    } finally {
      loading.value = false
    }
  }

  const register = async (credentials: RegisterCredentials) => {
    loading.value = true
    error.value = null
    
    try {
      const response = await authApi.register(credentials)
      router.push('/signin')
      return response.data
    } catch (err: any) {
      console.error('Registration failed:', err)
      error.value = err.response?.data?.message || 'Failed to register. Please try again.'
      throw error.value
    } finally {
      loading.value = false
    }
  }

  const logout = () => {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    router.push('/signin')
  }

  return {
    token,
    user,
    loading,
    error,
    isAuthenticated,
    login,
    register, 
    logout,
    initUser
  }
})