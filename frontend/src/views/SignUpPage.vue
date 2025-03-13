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
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const isSubmitting = ref(false)
const formError = ref('')

// Form validation
const isValidEmail = (email: string) => {
  const re = /\S+@\S+\.\S+/
  return re.test(email)
}

const validateForm = () => {
  formError.value = ''
  
  if (!username.value || !email.value || !password.value || !confirmPassword.value) {
    formError.value = 'All fields are required'
    return false
  }
  
  if (username.value.length < 3) {
    formError.value = 'Username must be at least 3 characters'
    return false
  }
  
  if (!isValidEmail(email.value)) {
    formError.value = 'Please enter a valid email address'
    return false
  }
  
  if (password.value.length < 6) {
    formError.value = 'Password must be at least 6 characters'
    return false
  }
  
  if (password.value !== confirmPassword.value) {
    formError.value = 'Passwords do not match'
    return false
  }
  
  return true
}

const handleSubmit = async () => {
  if (!validateForm()) return
  
  try {
    isSubmitting.value = true
    await authStore.register({
      username: username.value,
      email: email.value,
      password: password.value
    })
    router.push('/signin')
  } catch (error) {
    formError.value = authStore.error || 'Registration failed. Please try again.'
  } finally {
    isSubmitting.value = false
  }
}

const navigateToSignIn = () => {
  router.push('/signin')
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
        <CardTitle class="text-2xl font-bold text-center">Sign Up</CardTitle>
        <CardDescription class="text-center">
          Create a new account to get started
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
            <Label for="email">Email</Label>
            <Input 
              id="email" 
              v-model="email" 
              type="email" 
              placeholder="Enter your email"
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
                placeholder="Create a password"
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
          
          <div class="space-y-2">
            <Label for="confirmPassword">Confirm Password</Label>
            <div class="relative">
              <Input 
                id="confirmPassword" 
                v-model="confirmPassword" 
                :type="showConfirmPassword ? 'text' : 'password'" 
                placeholder="Confirm your password"
                :disabled="isSubmitting" 
              />
              <button 
                type="button" 
                @click="showConfirmPassword = !showConfirmPassword" 
                class="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500"
              >
                <Eye v-if="!showConfirmPassword" class="h-5 w-5" />
                <EyeOff v-else class="h-5 w-5" />
              </button>
            </div>
          </div>
          
          <div v-if="formError" class="flex items-center p-3 text-sm text-red-600 bg-red-50 rounded-md">
            <AlertCircle class="h-4 w-4 mr-2" />
            {{ formError }}
          </div>
          
          <Button type="submit" class="w-full font-medium" size="lg" :disabled="isSubmitting">
            {{ isSubmitting ? 'Creating Account...' : 'Sign Up' }}
          </Button>
        </form>
      </CardContent>
      <CardFooter class="flex justify-center">
        <p class="text-sm text-center text-gray-600">
          Already have an account?
          <a @click="navigateToSignIn" class="text-blue-600 hover:text-blue-800 cursor-pointer font-medium">
            Sign In
          </a>
        </p>
      </CardFooter>
    </Card>
  </div>
</template>