import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'landing',
      component: () => import('@/views/LandingPage.vue')
    },
    {
      path: '/signup',
      name: 'signup',
      component: () => import('@/views/SignUpPage.vue')
    },
    {
      path: '/signin',
      name: 'signin',
      component: () => import('@/views/SignInPage.vue')
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/DashboardPage.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // Check if the route requires authentication
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    // If not authenticated, redirect to login page
    next({ name: 'signin' })
  } else {
    // Otherwise proceed to the route
    next()
  }
})

export default router