import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { userService } from '@/api/services'
import type { LoginUserVO, UserLoginRequest, UserRegisterRequest } from '@/types/domain'

const STORAGE_KEY = 'web-weave-login-user'

const restoreUser = (): LoginUserVO | null => {
  try {
    const value = localStorage.getItem(STORAGE_KEY)
    return value ? JSON.parse(value) as LoginUserVO : null
  } catch {
    return null
  }
}

/**
 * 登录用户信息
 */
export const useLoginUserStore = defineStore('loginUser', () => {
  const loginUser = ref<LoginUserVO | null>(restoreUser())
  const isLoggedIn = computed(() => Boolean(loginUser.value?.id))
  const isAdmin = computed(() => loginUser.value?.userRole === 'admin')
  const displayName = computed(() => loginUser.value?.userName || loginUser.value?.userAccount || '未登录')

  function setLoginUser(user: LoginUserVO | null) {
    loginUser.value = user
    if (user) localStorage.setItem(STORAGE_KEY, JSON.stringify(user))
    else localStorage.removeItem(STORAGE_KEY)
  }

  async function login(data: UserLoginRequest) {
    const user = await userService.login(data)
    setLoginUser(user)
    return user
  }

  async function register(data: UserRegisterRequest) {
    return userService.register(data)
  }

  async function logout() {
    try { await userService.logout() } finally { setLoginUser(null) }
  }

  if (typeof window !== 'undefined') {
    window.addEventListener('app:unauthorized', () => setLoginUser(null))
  }

  return { loginUser, isLoggedIn, isAdmin, displayName, setLoginUser, login, register, logout }
})
