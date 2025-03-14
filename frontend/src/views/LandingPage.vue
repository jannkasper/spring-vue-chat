<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { 
  MessageCircle, 
  Shield, 
  Users, 
  Clock, 
  Lock, 
  CheckCircle2,
  Settings, 
  Zap, 
  ChevronRight, 
  Globe, 
  BarChart, 
  UserPlus, 
  Star, 
  Menu,
  X,
  Github,
  Twitter,
  Linkedin,
  MousePointerClick,
  ArrowRight
} from 'lucide-vue-next'

const router = useRouter()
const authStore = useAuthStore()
const mobileMenuOpen = ref(false)

// Smooth scroll functionality
const scrollToSection = (sectionId: string) => {
  const element = document.getElementById(sectionId)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
  mobileMenuOpen.value = false
}

const navigateToSignUp = () => {
  router.push('/signup')
}

const navigateToSignIn = () => {
  router.push('/signin')
}

const navigateToDashboard = () => {
  router.push('/dashboard')
}

// FAQ Items
const faqItems = ref([
  {
    question: "How secure is Spring Vue Chat?",
    answer: "We use industry-standard JWT authentication and encrypted communications to ensure your conversations remain private and secure."
  },
  {
    question: "Can I create private chat rooms?",
    answer: "Yes, you can create both public and private chat rooms. Private rooms are accessible only by invitation."
  },
  {
    question: "Is there a limit to the number of chat rooms I can create?",
    answer: "Free users can create up to 5 chat rooms. Premium users have unlimited chat room creation capabilities."
  },
  {
    question: "Can I use Spring Vue Chat on mobile devices?",
    answer: "Absolutely! Our platform is fully responsive and works seamlessly across desktop, tablet, and mobile devices."
  },
  {
    question: "How do I invite others to my chat room?",
    answer: "After creating a room, you can generate an invitation link or directly invite users by their username or email."
  },
  {
    question: "Is there message history and search functionality?",
    answer: "Yes, all messages are stored securely, and premium users can access advanced search capabilities to find any message in their history."
  }
])

// Testimonials data
const testimonials = ref([
  {
    name: "Sarah Johnson",
    role: "Project Manager",
    avatar: "https://randomuser.me/api/portraits/women/45.jpg",
    content: "Spring Vue Chat has transformed our team communication. The interface is intuitive, and the real-time messaging is incredibly reliable.",
    rating: 5
  },
  {
    name: "David Chen",
    role: "Software Developer",
    avatar: "https://randomuser.me/api/portraits/men/32.jpg",
    content: "As a developer, I appreciate the security and performance of this platform. The API is also well-documented and easy to integrate with.",
    rating: 5
  },
  {
    name: "Emma Rodriguez",
    role: "Marketing Director",
    avatar: "https://randomuser.me/api/portraits/women/29.jpg",
    content: "We've tried several chat platforms, but Spring Vue Chat offers the best balance of features, ease of use, and value for money.",
    rating: 4
  }
])

// Pricing plans
const pricingPlans = ref([
  {
    name: "Free",
    price: "$0",
    period: "forever",
    description: "Perfect for individuals and small teams",
    features: [
      "Up to 5 chat rooms",
      "Real-time messaging",
      "Basic security features",
      "7-day message history",
      "Email support"
    ],
    cta: "Get Started",
    variant: "outline"
  },
  {
    name: "Pro",
    price: "$12",
    period: "per user/month",
    description: "For growing teams that need more features",
    features: [
      "Unlimited chat rooms",
      "Advanced security",
      "File sharing up to 1GB",
      "90-day message history",
      "Priority email support",
      "Custom branding"
    ],
    cta: "Try Free for 14 Days",
    variant: "default",
    popular: true
  },
  {
    name: "Enterprise",
    price: "Custom",
    period: "contact for pricing",
    description: "Advanced features for large organizations",
    features: [
      "Unlimited everything",
      "Advanced admin controls",
      "Enterprise-grade security",
      "Unlimited message history",
      "24/7 phone & email support",
      "Dedicated account manager",
      "Custom integrations"
    ],
    cta: "Contact Sales",
    variant: "secondary"
  }
])

// Comparison table
const comparisonFeatures = ref([
  {
    feature: "Real-time messaging",
    ours: true,
    competitor1: true,
    competitor2: true
  },
  {
    feature: "Unlimited chat rooms",
    ours: true,
    competitor1: false,
    competitor2: true
  },
  {
    feature: "End-to-end encryption",
    ours: true,
    competitor1: false,
    competitor2: false
  },
  {
    feature: "File sharing",
    ours: true,
    competitor1: true,
    competitor2: true
  },
  {
    feature: "Message search",
    ours: true,
    competitor1: true,
    competitor2: false
  },
  {
    feature: "Custom branding",
    ours: true,
    competitor1: false,
    competitor2: false
  },
  {
    feature: "API access",
    ours: true,
    competitor1: false,
    competitor2: true
  },
  {
    feature: "Self-hosting option",
    ours: true,
    competitor1: false,
    competitor2: false
  }
])

onMounted(() => {
  // Handle smooth scrolling for anchor links
  const handleAnchorClick = (e: Event) => {
    const target = e.target as HTMLElement
    if (target.tagName === 'A') {
      const href = target.getAttribute('href')
      if (href && href.startsWith('#')) {
        e.preventDefault()
        const sectionId = href.substring(1)
        scrollToSection(sectionId)
      }
    }
  }
  
  document.addEventListener('click', handleAnchorClick)
  
  return () => {
    document.removeEventListener('click', handleAnchorClick)
  }
})
</script>

<template>
  <!-- Header/Navigation -->
  <header class="bg-white dark:bg-gray-900 sticky top-0 z-50 border-b border-gray-200 dark:border-gray-800">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between h-16 items-center">
        <!-- Logo -->
        <div class="flex items-center">
          <a href="#" class="flex items-center text-xl font-bold text-primary">
            <MessageCircle class="h-6 w-6 mr-2 text-primary" />
            <span>Spring Vue Chat</span>
          </a>
        </div>
        
        <!-- Desktop navigation -->
        <nav class="hidden md:flex space-x-8 items-center">
          <a @click="scrollToSection('features')" class="text-gray-600 dark:text-gray-300 hover:text-primary dark:hover:text-primary transition-colors cursor-pointer">Features</a>
          <a @click="scrollToSection('how-it-works')" class="text-gray-600 dark:text-gray-300 hover:text-primary dark:hover:text-primary transition-colors cursor-pointer">How It Works</a>
          <a @click="scrollToSection('pricing')" class="text-gray-600 dark:text-gray-300 hover:text-primary dark:hover:text-primary transition-colors cursor-pointer">Pricing</a>
          <a @click="scrollToSection('faq')" class="text-gray-600 dark:text-gray-300 hover:text-primary dark:hover:text-primary transition-colors cursor-pointer">FAQ</a>
          
          <template v-if="!authStore.isAuthenticated">
            <Button @click="navigateToSignIn" variant="ghost">Sign In</Button>
            <Button @click="navigateToSignUp" variant="default">Sign Up</Button>
          </template>
          <template v-else>
            <Button @click="navigateToDashboard" variant="default">Dashboard</Button>
          </template>
        </nav>
        
        <!-- Mobile menu button -->
        <div class="md:hidden">
          <Button variant="ghost" size="icon" @click="mobileMenuOpen = !mobileMenuOpen">
            <Menu v-if="!mobileMenuOpen" class="h-6 w-6" />
            <X v-else class="h-6 w-6" />
          </Button>
        </div>
      </div>
    </div>
    
    <!-- Mobile menu -->
    <div v-if="mobileMenuOpen" class="md:hidden">
      <div class="pt-2 pb-4 space-y-2 px-4 border-t border-gray-200 dark:border-gray-800">
        <a @click="scrollToSection('features')" class="block py-2 text-gray-600 dark:text-gray-300 hover:text-primary dark:hover:text-primary transition-colors cursor-pointer">Features</a>
        <a @click="scrollToSection('how-it-works')" class="block py-2 text-gray-600 dark:text-gray-300 hover:text-primary dark:hover:text-primary transition-colors cursor-pointer">How It Works</a>
        <a @click="scrollToSection('pricing')" class="block py-2 text-gray-600 dark:text-gray-300 hover:text-primary dark:hover:text-primary transition-colors cursor-pointer">Pricing</a>
        <a @click="scrollToSection('faq')" class="block py-2 text-gray-600 dark:text-gray-300 hover:text-primary dark:hover:text-primary transition-colors cursor-pointer">FAQ</a>
        
        <div class="pt-4 space-y-2 border-t border-gray-200 dark:border-gray-800">
          <template v-if="!authStore.isAuthenticated">
            <Button @click="navigateToSignIn" variant="ghost" class="w-full justify-start">Sign In</Button>
            <Button @click="navigateToSignUp" variant="default" class="w-full justify-start">Sign Up</Button>
          </template>
          <template v-else>
            <Button @click="navigateToDashboard" variant="default" class="w-full justify-start">Dashboard</Button>
          </template>
        </div>
      </div>
    </div>
  </header>

  <!-- Hero Section -->
  <section class="py-16 md:py-24 overflow-hidden bg-gradient-to-b from-slate-50 to-white dark:from-gray-950 dark:to-gray-900">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex flex-col lg:flex-row items-center">
        <div class="w-full lg:w-1/2 lg:pr-16 mb-10 lg:mb-0">
          <h1 class="text-4xl sm:text-5xl md:text-6xl font-bold leading-tight text-gray-900 dark:text-white mb-6">
            Communicate Better with <span class="bg-clip-text text-transparent bg-gradient-to-r from-blue-500 to-teal-400">Spring Vue Chat</span>
          </h1>
          <p class="text-xl text-gray-600 dark:text-gray-300 mb-8 max-w-2xl">
            A modern, secure, and feature-rich chat platform that helps teams collaborate effectively with real-time messaging.
          </p>
          <div class="flex flex-col sm:flex-row gap-4">
            <Button @click="navigateToSignUp" size="lg" variant="default" class="font-medium">
              Get Started Free
              <ArrowRight class="ml-2 h-5 w-5" />
            </Button>
            <Button @click="scrollToSection('how-it-works')" size="lg" variant="outline" class="font-medium">
              How it Works
            </Button>
          </div>
          
          <div class="flex items-center mt-8 text-sm text-gray-500 dark:text-gray-400">
            <CheckCircle2 class="h-5 w-5 text-green-500 mr-2" />
            <span>No credit card required</span>
            <CheckCircle2 class="h-5 w-5 text-green-500 mx-2 ml-4" />
            <span>Free plan available</span>
            <CheckCircle2 class="h-5 w-5 text-green-500 mx-2 ml-4" />
            <span>Cancel anytime</span>
          </div>
        </div>
        <div class="w-full lg:w-1/2 relative">
          <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl p-2 md:p-6 transform rotate-1 transition-all hover:rotate-0 duration-300">
            <img 
              src="https://placehold.co/800x500/2563eb/FFFFFF/png?text=Chat+Interface+Preview&font=montserrat" 
              alt="Chat Interface Preview" 
              class="rounded-lg w-full"
            />
            <div class="absolute -bottom-4 -left-4 bg-gradient-to-r from-blue-600 to-indigo-600 text-white px-4 py-2 rounded-lg shadow-lg text-sm font-medium">
              🚀 Seamless Communication
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>

  <!-- Features Section -->
  <section id="features" class="py-16 bg-white dark:bg-gray-900">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center mb-16">
        <h2 class="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white mb-4">Powerful Features For Your Team</h2>
        <p class="text-xl text-gray-600 dark:text-gray-300 max-w-3xl mx-auto">
          Discover all the tools you need to collaborate effectively with your team
        </p>
      </div>
      
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        <!-- Feature 1 -->
        <Card class="border border-gray-200 dark:border-gray-800 hover:shadow-lg transition-shadow">
          <CardHeader>
            <div class="bg-blue-100 dark:bg-blue-900 rounded-full w-12 h-12 flex items-center justify-center mb-4">
              <MessageCircle class="h-6 w-6 text-blue-600 dark:text-blue-400" />
            </div>
            <CardTitle>Real-time Messaging</CardTitle>
          </CardHeader>
          <CardContent>
            <p class="text-gray-600 dark:text-gray-400">
              Instant message delivery ensures your team stays in sync without delays, enhancing productivity and collaboration.
            </p>
          </CardContent>
        </Card>
        
        <!-- Feature 2 -->
        <Card class="border border-gray-200 dark:border-gray-800 hover:shadow-lg transition-shadow">
          <CardHeader>
            <div class="bg-indigo-100 dark:bg-indigo-900 rounded-full w-12 h-12 flex items-center justify-center mb-4">
              <Users class="h-6 w-6 text-indigo-600 dark:text-indigo-400" />
            </div>
            <CardTitle>Multiple Chat Rooms</CardTitle>
          </CardHeader>
          <CardContent>
            <p class="text-gray-600 dark:text-gray-400">
              Create dedicated spaces for different projects, teams, or topics, keeping conversations organized and focused.
            </p>
          </CardContent>
        </Card>
        
        <!-- Feature 3 -->
        <Card class="border border-gray-200 dark:border-gray-800 hover:shadow-lg transition-shadow">
          <CardHeader>
            <div class="bg-teal-100 dark:bg-teal-900 rounded-full w-12 h-12 flex items-center justify-center mb-4">
              <Shield class="h-6 w-6 text-teal-600 dark:text-teal-400" />
            </div>
            <CardTitle>Advanced Security</CardTitle>
          </CardHeader>
          <CardContent>
            <p class="text-gray-600 dark:text-gray-400">
              JWT authentication and encrypted communications ensure your conversations remain confidential and protected.
            </p>
          </CardContent>
        </Card>
        
        <!-- Feature 4 -->
        <Card class="border border-gray-200 dark:border-gray-800 hover:shadow-lg transition-shadow">
          <CardHeader>
            <div class="bg-purple-100 dark:bg-purple-900 rounded-full w-12 h-12 flex items-center justify-center mb-4">
              <Clock class="h-6 w-6 text-purple-600 dark:text-purple-400" />
            </div>
            <CardTitle>Message History</CardTitle>
          </CardHeader>
          <CardContent>
            <p class="text-gray-600 dark:text-gray-400">
              Access past conversations and search through message history to find important information quickly.
            </p>
          </CardContent>
        </Card>
        
        <!-- Feature 5 -->
        <Card class="border border-gray-200 dark:border-gray-800 hover:shadow-lg transition-shadow">
          <CardHeader>
            <div class="bg-pink-100 dark:bg-pink-900 rounded-full w-12 h-12 flex items-center justify-center mb-4">
              <Settings class="h-6 w-6 text-pink-600 dark:text-pink-400" />
            </div>
            <CardTitle>Customizable Interface</CardTitle>
          </CardHeader>
          <CardContent>
            <p class="text-gray-600 dark:text-gray-400">
              Personalize your experience with dark/light mode, custom themes, and layout options to match your preferences.
            </p>
          </CardContent>
        </Card>
        
        <!-- Feature 6 -->
        <Card class="border border-gray-200 dark:border-gray-800 hover:shadow-lg transition-shadow">
          <CardHeader>
            <div class="bg-green-100 dark:bg-green-900 rounded-full w-12 h-12 flex items-center justify-center mb-4">
              <Globe class="h-6 w-6 text-green-600 dark:text-green-400" />
            </div>
            <CardTitle>Cross-Platform Access</CardTitle>
          </CardHeader>
          <CardContent>
            <p class="text-gray-600 dark:text-gray-400">
              Use Spring Vue Chat on any device with a responsive design that works seamlessly on desktop, tablet, and mobile.
            </p>
          </CardContent>
        </Card>
      </div>
    </div>
  </section>

  <!-- How It Works Section -->
  <section id="how-it-works" class="py-16 bg-gray-50 dark:bg-gray-800">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center mb-16">
        <h2 class="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white mb-4">How Spring Vue Chat Works</h2>
        <p class="text-xl text-gray-600 dark:text-gray-300 max-w-3xl mx-auto">
          Get started in minutes with our simple and intuitive platform
        </p>
      </div>
      
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8 relative">
        <!-- Line connecting steps on larger screens -->
        <div class="hidden md:block absolute top-1/4 left-0 right-0 h-0.5 bg-gray-200 dark:bg-gray-700" style="z-index: 1;"></div>
        
        <!-- Step 1 -->
        <div class="bg-white dark:bg-gray-900 rounded-xl p-8 shadow-lg relative z-10">
          <div class="bg-blue-600 text-white w-12 h-12 rounded-full flex items-center justify-center font-bold text-xl mb-6 mx-auto">
            1
          </div>
          <h3 class="text-xl font-bold text-center mb-4 text-gray-900 dark:text-white">Create an Account</h3>
          <p class="text-gray-600 dark:text-gray-400 text-center">
            Sign up for free with your email address and create a secure password to get started.
          </p>
          <div class="flex justify-center mt-6">
            <Button @click="navigateToSignUp" variant="outline" size="sm">
              Sign Up Now
              <ChevronRight class="ml-1 h-4 w-4" />
            </Button>
          </div>
        </div>
        
        <!-- Step 2 -->
        <div class="bg-white dark:bg-gray-900 rounded-xl p-8 shadow-lg relative z-10">
          <div class="bg-blue-600 text-white w-12 h-12 rounded-full flex items-center justify-center font-bold text-xl mb-6 mx-auto">
            2
          </div>
          <h3 class="text-xl font-bold text-center mb-4 text-gray-900 dark:text-white">Create or Join Rooms</h3>
          <p class="text-gray-600 dark:text-gray-400 text-center">
            Start a new chat room for your team or join existing rooms to connect with others.
          </p>
        </div>
        
        <!-- Step 3 -->
        <div class="bg-white dark:bg-gray-900 rounded-xl p-8 shadow-lg relative z-10">
          <div class="bg-blue-600 text-white w-12 h-12 rounded-full flex items-center justify-center font-bold text-xl mb-6 mx-auto">
            3
          </div>
          <h3 class="text-xl font-bold text-center mb-4 text-gray-900 dark:text-white">Start Collaborating</h3>
          <p class="text-gray-600 dark:text-gray-400 text-center">
            Send messages, share files, and collaborate in real-time with your team members.
          </p>
        </div>
      </div>
      
      <!-- Video or Screenshot -->
      <div class="mt-16 bg-white dark:bg-gray-900 p-4 rounded-xl shadow-lg overflow-hidden">
        <div class="aspect-w-16 aspect-h-9 rounded-lg overflow-hidden">
          <img 
            src="https://placehold.co/1200x675/2563eb/FFFFFF/png?text=Platform+Demo+Video&font=montserrat" 
            alt="Platform demonstration" 
            class="w-full h-full object-cover"
          />
          <div class="absolute inset-0 flex items-center justify-center">
            <div class="bg-white dark:bg-gray-800 bg-opacity-90 dark:bg-opacity-90 rounded-full w-16 h-16 flex items-center justify-center cursor-pointer hover:scale-110 transition-transform">
              <div class="w-0 h-0 border-t-[10px] border-t-transparent border-l-[18px] border-l-blue-600 dark:border-l-blue-400 border-b-[10px] border-b-transparent ml-1"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>

  <!-- Testimonials Section -->
  <section class="py-16 bg-white dark:bg-gray-900">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center mb-16">
        <h2 class="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white mb-4">What Our Users Say</h2>
        <p class="text-xl text-gray-600 dark:text-gray-300 max-w-3xl mx-auto">
          Join thousands of satisfied teams already using Spring Vue Chat
        </p>
      </div>
      
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <Card 
          v-for="(testimonial, index) in testimonials" 
          :key="index"
          class="border border-gray-200 dark:border-gray-800 relative overflow-hidden"
        >
          <CardContent class="pt-6">
            <!-- Quote icon -->
            <div class="absolute -top-4 -right-4 text-gray-200 dark:text-gray-800 opacity-50 transform rotate-12">
              <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="currentColor">
                <path d="M9.983 3v7.391c0 5.704-3.731 9.57-8.983 10.609l-.995-2.151c2.432-.917 3.995-3.638 3.995-5.849h-4v-10h9.983zm14.017 0v7.391c0 5.704-3.748 9.571-9 10.609l-.996-2.151c2.433-.917 3.996-3.638 3.996-5.849h-3.983v-10h9.983z" />
              </svg>
            </div>
            
            <div class="flex items-center mb-6">
              <img 
                :src="testimonial.avatar" 
                :alt="testimonial.name" 
                class="w-12 h-12 rounded-full mr-4 border-2 border-blue-100 dark:border-blue-900"
              />
              <div>
                <h4 class="font-bold text-gray-900 dark:text-white">{{ testimonial.name }}</h4>
                <p class="text-sm text-gray-600 dark:text-gray-400">{{ testimonial.role }}</p>
              </div>
            </div>
            
            <p class="text-gray-600 dark:text-gray-400 mb-4">
              "{{ testimonial.content }}"
            </p>
            
            <div class="flex text-yellow-400">
              <Star v-for="i in testimonial.rating" :key="i" class="w-5 h-5 fill-current" />
              <Star v-for="i in 5 - testimonial.rating" :key="i + 5" class="w-5 h-5 fill-current text-gray-300 dark:text-gray-700" />
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  </section>

  <!-- Pricing Section -->
  <section id="pricing" class="py-16 bg-gray-50 dark:bg-gray-800">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center mb-16">
        <h2 class="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white mb-4">Simple, Transparent Pricing</h2>
        <p class="text-xl text-gray-600 dark:text-gray-300 max-w-3xl mx-auto">
          Choose the plan that works best for your team
        </p>
      </div>
      
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <Card 
          v-for="(plan, index) in pricingPlans" 
          :key="index"
          class="border border-gray-200 dark:border-gray-800 relative"
          :class="plan.popular ? 'border-blue-500 dark:border-blue-400 shadow-xl' : ''"
        >
          <div v-if="plan.popular" class="absolute top-0 right-0 bg-blue-500 text-white text-xs font-bold px-3 py-1 transform translate-x-2 -translate-y-2 rotate-12">
            Most Popular
          </div>
          
          <CardHeader>
            <CardTitle>{{ plan.name }}</CardTitle>
            <div class="mt-4">
              <span class="text-4xl font-bold text-gray-900 dark:text-white">{{ plan.price }}</span>
              <span class="text-gray-500 dark:text-gray-400 ml-2">{{ plan.period }}</span>
            </div>
            <CardDescription class="mt-2">{{ plan.description }}</CardDescription>
          </CardHeader>
          
          <CardContent>
            <ul class="space-y-3">
              <li v-for="(feature, i) in plan.features" :key="i" class="flex items-start">
                <CheckCircle2 class="h-5 w-5 text-green-500 mr-2 flex-shrink-0 mt-0.5" />
                <span class="text-gray-700 dark:text-gray-300">{{ feature }}</span>
              </li>
            </ul>
          </CardContent>
          
          <CardFooter>
            <Button :variant="plan.variant as any" class="w-full font-medium">
              {{ plan.cta }}
            </Button>
          </CardFooter>
        </Card>
      </div>
    </div>
  </section>

  <!-- Comparison Table -->
  <section class="py-16 bg-white dark:bg-gray-900">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center mb-16">
        <h2 class="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white mb-4">How We Compare</h2>
        <p class="text-xl text-gray-600 dark:text-gray-300 max-w-3xl mx-auto">
          See why Spring Vue Chat is the right choice for your team
        </p>
      </div>
      
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200 dark:divide-gray-800">
          <thead>
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider w-1/4">Feature</th>
              <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider w-1/4">Spring Vue Chat</th>
              <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider w-1/4">Competitor A</th>
              <th class="px-6 py-3 text-center text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider w-1/4">Competitor B</th>
            </tr>
          </thead>
          <tbody class="bg-white dark:bg-gray-900 divide-y divide-gray-200 dark:divide-gray-800">
            <tr v-for="(item, index) in comparisonFeatures" :key="index" :class="{'bg-gray-50 dark:bg-gray-800': index % 2 === 0}">
              <td class="px-6 py-4 text-sm font-medium text-gray-900 dark:text-white">{{ item.feature }}</td>
              <td class="px-6 py-4 text-center">
                <CheckCircle2 v-if="item.ours" class="h-5 w-5 text-green-500 mx-auto" />
                <X v-else class="h-5 w-5 text-red-500 mx-auto" />
              </td>
              <td class="px-6 py-4 text-center">
                <CheckCircle2 v-if="item.competitor1" class="h-5 w-5 text-green-500 mx-auto" />
                <X v-else class="h-5 w-5 text-red-500 mx-auto" />
              </td>
              <td class="px-6 py-4 text-center">
                <CheckCircle2 v-if="item.competitor2" class="h-5 w-5 text-green-500 mx-auto" />
                <X v-else class="h-5 w-5 text-red-500 mx-auto" />
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </section>

  <!-- FAQ Section -->
  <section id="faq" class="py-16 bg-gray-50 dark:bg-gray-800">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8">
      <div class="text-center mb-16">
        <h2 class="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white mb-4">Frequently Asked Questions</h2>
        <p class="text-xl text-gray-600 dark:text-gray-300 max-w-3xl mx-auto">
          Find answers to the most common questions about our platform
        </p>
      </div>
      
      <div class="max-w-3xl mx-auto">
        <Tabs defaultValue="tab1" class="w-full">
          <TabsList class="grid w-full grid-cols-3 mb-8">
            <TabsTrigger value="tab1">General</TabsTrigger>
            <TabsTrigger value="tab2">Account</TabsTrigger>
            <TabsTrigger value="tab3">Features</TabsTrigger>
          </TabsList>
          
          <TabsContent value="tab1">
            <div class="space-y-4">
              <div v-for="(faq, index) in faqItems.slice(0, 2)" :key="index" class="bg-white dark:bg-gray-900 rounded-lg shadow p-6">
                <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-2">{{ faq.question }}</h3>
                <p class="text-gray-600 dark:text-gray-400">{{ faq.answer }}</p>
              </div>
            </div>
          </TabsContent>
          
          <TabsContent value="tab2">
            <div class="space-y-4">
              <div v-for="(faq, index) in faqItems.slice(2, 4)" :key="index" class="bg-white dark:bg-gray-900 rounded-lg shadow p-6">
                <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-2">{{ faq.question }}</h3>
                <p class="text-gray-600 dark:text-gray-400">{{ faq.answer }}</p>
              </div>
            </div>
          </TabsContent>
          
          <TabsContent value="tab3">
            <div class="space-y-4">
              <div v-for="(faq, index) in faqItems.slice(4, 6)" :key="index" class="bg-white dark:bg-gray-900 rounded-lg shadow p-6">
                <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-2">{{ faq.question }}</h3>
                <p class="text-gray-600 dark:text-gray-400">{{ faq.answer }}</p>
              </div>
            </div>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  </section>

  <!-- CTA Section -->
  <section class="py-16 bg-blue-600 dark:bg-blue-800">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8">
      <div class="max-w-4xl mx-auto text-center">
        <h2 class="text-3xl md:text-4xl font-bold text-white mb-6">Ready to transform your team's communication?</h2>
        <p class="text-xl text-blue-100 mb-8">
          Join thousands of teams already using Spring Vue Chat to collaborate more effectively
        </p>
        
        <div class="flex flex-col sm:flex-row gap-4 justify-center">
          <Button @click="navigateToSignUp" size="lg" variant="default" class="bg-white text-blue-700 hover:bg-gray-100">
            Get Started Free
            <MousePointerClick class="ml-2 h-5 w-5" />
          </Button>
          <Button size="lg" variant="outline" class="border-white text-white hover:bg-blue-700">
            Schedule a Demo
          </Button>
        </div>
        
        <p class="mt-8 text-blue-100 text-sm">
          No credit card required. Free plan available with basic features.
        </p>
      </div>
    </div>
  </section>

  <!-- Footer -->
  <footer class="bg-gray-900 text-gray-400 py-12">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
        <div>
          <div class="flex items-center text-xl font-bold text-white mb-4">
            <MessageCircle class="h-6 w-6 mr-2 text-blue-400" />
            <span>Spring Vue Chat</span>
          </div>
          <p class="mb-4">
            A modern, secure chat platform for teams of all sizes.
          </p>
          <div class="flex space-x-4">
            <a href="#" class="text-gray-400 hover:text-white">
              <Github class="h-5 w-5" />
            </a>
            <a href="#" class="text-gray-400 hover:text-white">
              <Twitter class="h-5 w-5" />
            </a>
            <a href="#" class="text-gray-400 hover:text-white">
              <Linkedin class="h-5 w-5" />
            </a>
          </div>
        </div>
        
        <div>
          <h3 class="text-white text-lg font-semibold mb-4">Product</h3>
          <ul class="space-y-2">
            <li><a href="#features" class="hover:text-white">Features</a></li>
            <li><a href="#pricing" class="hover:text-white">Pricing</a></li>
            <li><a href="#" class="hover:text-white">Security</a></li>
            <li><a href="#" class="hover:text-white">Enterprise</a></li>
            <li><a href="#" class="hover:text-white">Changelog</a></li>
          </ul>
        </div>
        
        <div>
          <h3 class="text-white text-lg font-semibold mb-4">Resources</h3>
          <ul class="space-y-2">
            <li><a href="#" class="hover:text-white">Documentation</a></li>
            <li><a href="#" class="hover:text-white">API Reference</a></li>
            <li><a href="#" class="hover:text-white">Tutorials</a></li>
            <li><a href="#" class="hover:text-white">Blog</a></li>
            <li><a href="#faq" class="hover:text-white">FAQ</a></li>
          </ul>
        </div>
        
        <div>
          <h3 class="text-white text-lg font-semibold mb-4">Company</h3>
          <ul class="space-y-2">
            <li><a href="#" class="hover:text-white">About</a></li>
            <li><a href="#" class="hover:text-white">Careers</a></li>
            <li><a href="#" class="hover:text-white">Contact</a></li>
            <li><a href="#" class="hover:text-white">Privacy Policy</a></li>
            <li><a href="#" class="hover:text-white">Terms of Service</a></li>
          </ul>
        </div>
      </div>
      
      <div class="mt-12 pt-8 border-t border-gray-800 text-center text-sm">
        <p>&copy; {{ new Date().getFullYear() }} Spring Vue Chat. All rights reserved.</p>
      </div>
    </div>
  </footer>
</template>

<style scoped>
/* Add smooth scrolling to the entire page */
html {
  scroll-behavior: smooth;
}

/* Add animation for feature cards */
.card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.card:hover {
  transform: translateY(-5px);
}

/* Custom aspect ratio for video container */
.aspect-w-16 {
  position: relative;
  padding-bottom: 56.25%; /* 16:9 aspect ratio */
}
.aspect-w-16 > * {
  position: absolute;
  height: 100%;
  width: 100%;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
}
</style>