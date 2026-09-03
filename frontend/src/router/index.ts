import { createRouter, createWebHistory } from 'vue-router'
import { useLoginUserStore } from '@/stores/loginUser'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    requiresAuth?: boolean
    requiresAdmin?: boolean
    guestOnly?: boolean
    immersive?: boolean
  }
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  scrollBehavior: () => ({ top: 0 }),
  routes: [
    { path: '/', component: () => import('@/pages/HomePage.vue'), meta: { title: '首页' } },
    { path: '/user/login', component: () => import('@/pages/user/UserLoginPage.vue'), meta: { title: '登录', guestOnly: true } },
    { path: '/user/register', component: () => import('@/pages/user/UserRegisterPage.vue'), meta: { title: '注册', guestOnly: true } },
    { path: '/apps', component: () => import('@/pages/app/MyAppsPage.vue'), meta: { title: '我的应用', requiresAuth: true } },
    { path: '/app/chat/:id', component: () => import('@/pages/app/AppChatPage.vue'), meta: { title: 'AI 创作台', requiresAuth: true, immersive: true } },
    { path: '/app/edit/:id', component: () => import('@/pages/app/AppEditPage.vue'), meta: { title: '编辑应用', requiresAuth: true } },
    { path: '/admin/users', component: () => import('@/pages/admin/UserManagePage.vue'), meta: { title: '用户管理', requiresAdmin: true } },
    { path: '/admin/apps', component: () => import('@/pages/admin/AppManagePage.vue'), meta: { title: '应用管理', requiresAdmin: true } },
    { path: '/admin/chats', component: () => import('@/pages/admin/ChatManagePage.vue'), meta: { title: '对话管理', requiresAdmin: true } },
    { path: '/:pathMatch(.*)*', component: () => import('@/pages/NotFoundPage.vue'), meta: { title: '页面不存在' } },
  ],
})

router.beforeEach((to) => {
  const store = useLoginUserStore()
  document.title = `${to.meta.title || '灵构 AI'} · 灵构 AI`
  if ((to.meta.requiresAuth || to.meta.requiresAdmin) && !store.isLoggedIn) {
    return { path: '/user/login', query: { redirect: to.fullPath } }
  }
  if (to.meta.requiresAdmin && !store.isAdmin) return '/'
  if (to.meta.guestOnly && store.isLoggedIn) return '/'
})

export default router
