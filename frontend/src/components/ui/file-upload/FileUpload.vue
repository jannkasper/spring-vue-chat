<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { Button } from '@/components/ui/button'
import { X, Paperclip, File, Image, FileText, Film } from 'lucide-vue-next'

const props = defineProps<{
  modelValue?: File | null
  accept?: string
  maxSize?: number // in bytes
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', file: File | null): void
  (e: 'error', message: string): void
}>()

const fileInput = ref<HTMLInputElement | null>(null)
const dragActive = ref(false)
const error = ref('')

// Handle file selection
const handleFileSelect = (event: Event) => {
  const input = event.target as HTMLInputElement
  if (input.files && input.files.length > 0) {
    validateAndSetFile(input.files[0])
  }
}

// Handle file drop
const handleDrop = (event: DragEvent) => {
  event.preventDefault()
  event.stopPropagation()
  dragActive.value = false
  
  if (event.dataTransfer?.files && event.dataTransfer.files.length > 0) {
    validateAndSetFile(event.dataTransfer.files[0])
  }
}

// Validate file and set it as the model value
const validateAndSetFile = (file: File) => {
  error.value = ''
  
  // Check file type if accept is provided
  if (props.accept && !isAcceptedType(file)) {
    error.value = `File type not allowed. Accepted types: ${props.accept}`
    emit('error', error.value)
    return
  }
  
  // Check file size if maxSize is provided
  if (props.maxSize && file.size > props.maxSize) {
    const maxSizeMB = props.maxSize / (1024 * 1024)
    error.value = `File is too large. Maximum size: ${maxSizeMB.toFixed(1)} MB`
    emit('error', error.value)
    return
  }
  
  // Set the file as the model value
  emit('update:modelValue', file)
}

// Check if the file type is accepted
const isAcceptedType = (file: File) => {
  if (!props.accept) return true
  
  const acceptedTypes = props.accept.split(',').map(type => type.trim())
  
  return acceptedTypes.some(type => {
    // Check for wildcard mime types (e.g., "image/*")
    if (type.endsWith('/*')) {
      const category = type.split('/')[0]
      return file.type.startsWith(`${category}/`)
    }
    
    // Check for exact mime types
    if (type.includes('/')) {
      return file.type === type
    }
    
    // Check for file extensions (e.g., ".pdf")
    if (type.startsWith('.')) {
      return file.name.toLowerCase().endsWith(type.toLowerCase())
    }
    
    return false
  })
}

// Open file dialog when the component is clicked
const openFileDialog = () => {
  if (fileInput.value && !props.disabled) {
    fileInput.value.click()
  }
}

// Clear selected file
const clearFile = () => {
  emit('update:modelValue', null)
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

// File type icon based on mime type
const fileIcon = computed(() => {
  if (!props.modelValue) return Paperclip
  
  const mimeType = props.modelValue.type
  
  if (mimeType.startsWith('image/')) return Image
  if (mimeType.startsWith('video/')) return Film
  if (mimeType.startsWith('text/')) return FileText
  
  return File
})

// Format file size
const formattedFileSize = computed(() => {
  if (!props.modelValue) return ''
  
  const bytes = props.modelValue.size
  
  if (bytes < 1024) return `${bytes} bytes`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
})

// Watch for changes in the modelValue prop
watch(() => props.modelValue, (newValue) => {
  if (newValue === null && fileInput.value) {
    fileInput.value.value = ''
  }
})
</script>

<template>
  <div class="space-y-2">
    <!-- File input (hidden) -->
    <input 
      ref="fileInput"
      type="file"
      class="hidden"
      :accept="accept"
      @change="handleFileSelect"
      :disabled="disabled"
    />
    
    <!-- Drop zone -->
    <div 
      class="border-2 border-dashed rounded-lg p-4 transition-colors"
      :class="[
        disabled ? 'opacity-50 cursor-not-allowed bg-gray-100 dark:bg-gray-800 border-gray-300 dark:border-gray-700' :
        dragActive ? 'border-blue-500 bg-blue-50 dark:bg-blue-900/20' : 
        'border-gray-300 dark:border-gray-700 hover:border-blue-400 dark:hover:border-blue-600'
      ]"
      @click="openFileDialog"
      @dragenter.prevent="dragActive = !disabled"
      @dragleave.prevent="dragActive = false"
      @dragover.prevent
      @drop="handleDrop"
    >
      <!-- File preview -->
      <div v-if="modelValue" class="flex items-start gap-3">
        <div class="flex-shrink-0 w-10 h-10 bg-blue-100 dark:bg-blue-900 rounded flex items-center justify-center">
          <component :is="fileIcon" class="h-5 w-5 text-blue-600 dark:text-blue-400" />
        </div>
        
        <div class="flex-1 min-w-0">
          <div class="font-medium text-gray-900 dark:text-white truncate">
            {{ modelValue.name }}
          </div>
          <div class="text-xs text-gray-500 dark:text-gray-400 mt-1">
            {{ formattedFileSize }} • {{ modelValue.type || 'Unknown type' }}
          </div>
        </div>
        
        <Button 
          v-if="!disabled"
          type="button"
          variant="ghost" 
          size="icon" 
          class="h-8 w-8 flex-shrink-0"
          @click.stop="clearFile"
        >
          <X class="h-4 w-4" />
          <span class="sr-only">Remove file</span>
        </Button>
      </div>
      
      <!-- Empty state -->
      <div v-else class="flex flex-col items-center justify-center py-4">
        <Paperclip class="h-8 w-8 text-gray-400 dark:text-gray-600 mb-2" />
        <div class="text-sm font-medium text-gray-700 dark:text-gray-300">
          Click to upload or drag and drop
        </div>
        <div class="text-xs text-gray-500 dark:text-gray-400 mt-1">
          {{ accept ? `Accepted formats: ${accept}` : 'All file types accepted' }}
        </div>
      </div>
    </div>
    
    <!-- Error message -->
    <div v-if="error" class="text-sm text-red-600 dark:text-red-400">
      {{ error }}
    </div>
  </div>
</template>