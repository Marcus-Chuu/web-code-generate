import { createRouter, createWebHashHistory } from 'vue-router'
import Dashboard from '@/pages/Dashboard.vue'
import Monitor from '@/pages/Monitor.vue'
import UserAnalysis from '@/pages/UserAnalysis.vue'
import Revenue from '@/pages/Revenue.vue'
import Settings from '@/pages/Settings.vue'

const routes = [
  { path: '/', name: 'dashboard', component: Dashboard },
  { path: '/monitor', name: 'monitor', component: Monitor },
  { path: '/users', name: 'users', component: UserAnalysis },
  { path: '/revenue', name: 'revenue', component: Revenue },
  { path: '/settings', name: 'settings', component: Settings }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
