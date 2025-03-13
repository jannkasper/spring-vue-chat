<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { Eye, EyeOff, AlertCircle, ArrowLeft } from 'lucide-vue-next'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const showPassword = ref(false)
const isSubmitting = ref(false)
const formError = ref('')

const handleSubmit = async () => {
  if (!username.value || !password.value) {
    formError.value = 'Username and password are required'
    return
  }
  
  try {
    isSubmitting.value = true
    await authStore.login({
      username: username.value,
      password: password.value
    })
    // The router navigation happens in the login function
  } catch (error) {
    formError.value = authStore.error || 'Login failed. Please check your credentials.'
  } finally {
    isSubmitting.value = false
  }
}

const navigateToSignUp = () => {
  router.push('/signup')
}
</script>

<template>
  <div class="flex-1 container mx-auto flex flex-col items-center justify-center px-4 py-10 relative">
    <Button 
      variant="ghost" 
      class="absolute left-4 top-4 flex items-center gap-1 md:left-8 md:top-8" 
      @click="router.push('/')"
    >
      <ArrowLeft class="h-4 w-4" /> Return
    </Button>
    
    <Card class="w-full max-w-md mx-auto shadow-lg">
      <CardHeader>
        <CardTitle class="text-2xl font-bold text-center">Sign In</CardTitle>
        <CardDescription class="text-center">
          Enter your credentials to access your account
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form @submit.prevent="handleSubmit" class="space-y-4">
          <div class="space-y-2">
            <Label for="username">Username</Label>
            <Input 
              id="username" 
              v-model="username" 
              type="text" 
              placeholder="Enter your username"
              :disabled="isSubmitting" 
            />
          </div>
          
          <div class="space-y-2">
            <Label for="password">Password</Label>
            <div class="relative">
              <Input 
                id="password" 
                v-model="password" 
                :type="showPassword ? 'text' : 'password'" 
                placeholder="Enter your password"
                :disabled="isSubmitting" 
              />
              <button 
                type="button" 
                @click="showPassword = !showPassword" 
                class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500"
              >
                <Eye v-if="!showPassword" class="h-5 w-5" />
                <EyeOff v-else class="h-5 w-5" />
              </button>
            </div>
          </div>
          
          <div v-if="formError" class="flex items-center p-3 text-sm text-red-600 bg-red-50 rounded-md">
            <AlertCircle class="h-4 w-4 mr-2" />
            {{ formError }}
          </div>
          
          <Button type="submit" class="w-full font-medium" size="lg" :disabled="isSubmitting">
            {{ isSubmitting ? 'Signing In...' : 'Sign In' }}
          </Button>
        </form>
      </CardContent>
      <CardFooter class="flex justify-center">
        <p class="text-sm text-center text-gray-600">
          Don't have an account?
          <a @click="navigateToSignUp" class="text-blue-600 hover:text-blue-800 cursor-pointer font-medium">
            Sign Up
          </a>
        </p>
      </CardFooter>
    </Card>
  </div>
</template>