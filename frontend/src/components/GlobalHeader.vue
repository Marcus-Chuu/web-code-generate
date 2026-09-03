<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { AppstoreOutlined, MenuOutlined, LogoutOutlined, SettingOutlined } from '@ant-design/icons-vue'
import { DEFAULT_AVATAR } from '@/config/avatars'
import { useLoginUserStore } from '@/stores/loginUser'

const route = useRoute()
const router = useRouter()
const userStore = useLoginUserStore()
const mobileOpen = ref(false)

const menus = computed(() => [
  { key: '/', label: '发现' },
  ...(userStore.isLoggedIn ? [{ key: '/apps', label: '我的应用' }] : []),
  ...(userStore.isAdmin ? [
    { key: '/admin/users', label: '用户' },
    { key: '/admin/apps', label: '应用' },
    { key: '/admin/chats', label: '对话' },
  ] : []),
])
const activeKey = computed(() => {
  const match = menus.value.find((item) => item.key !== '/' && route.path.startsWith(item.key))
  return match?.key || '/'
})

const navigate = (key: string) => {
  mobileOpen.value = false
  void router.push(key)
}
const logout = async () => {
  try {
    await userStore.logout()
    message.success('已安全退出')
    await router.push('/')
  } catch {
    message.warning('本地登录状态已清除')
    await router.push('/')
  }
}
</script>

<template>
  <a-layout-header class="site-header">
    <div class="header-inner">
      <RouterLink to="/" class="brand" aria-label="灵构 AI 首页">
        <span class="brand-mark"><AppstoreOutlined /></span>
        <span>灵构 <b>AI</b></span>
      </RouterLink>

      <nav class="desktop-nav" aria-label="主导航">
        <button v-for="item in menus" :key="item.key" :class="{ active: activeKey === item.key }" @click="navigate(item.key)">
          {{ item.label }}
        </button>
      </nav>

      <div class="header-actions">
        <template v-if="userStore.isLoggedIn">
          <a-dropdown placement="bottomRight">
            <button class="user-chip">
              <a-avatar :size="34" :src="userStore.loginUser?.userAvatar || DEFAULT_AVATAR" />
              <span>{{ userStore.displayName }}</span>
            </button>
            <template #overlay>
              <a-menu>
                <a-menu-item key="apps" @click="navigate('/apps')"><SettingOutlined /> 我的应用</a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout" @click="logout"><LogoutOutlined /> 退出登录</a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </template>
        <template v-else>
          <a-button type="text" @click="navigate('/user/login')">登录</a-button>
          <a-button type="primary" @click="navigate('/user/register')">免费开始</a-button>
        </template>
        <a-button class="mobile-trigger" type="text" aria-label="打开菜单" @click="mobileOpen = true"><MenuOutlined /></a-button>
      </div>
    </div>
  </a-layout-header>

  <a-drawer v-model:open="mobileOpen" title="导航" placement="right" :width="280">
    <div class="mobile-nav">
      <button v-for="item in menus" :key="item.key" :class="{ active: activeKey === item.key }" @click="navigate(item.key)">{{ item.label }}</button>
    </div>
  </a-drawer>
</template>

<style scoped>
.site-header { position: sticky; top: 0; z-index: 50; height: 68px; padding: 0; line-height: normal; background: rgba(246,246,242,.86); border-bottom: 1px solid rgba(22,29,44,.08); backdrop-filter: blur(18px); }
.header-inner { width: min(1220px, calc(100% - 40px)); height: 100%; margin: auto; display: flex; align-items: center; gap: 34px; }
.brand { display: flex; align-items: center; gap: 10px; color: #151b29; font-size: 18px; font-weight: 800; letter-spacing: -.03em; white-space: nowrap; }
.brand b { color: #6558f5; }
.brand-mark { width: 34px; height: 34px; display: grid; place-items: center; color: white; background: #181d2b; border-radius: 10px; box-shadow: inset -8px -8px 18px rgba(101,88,245,.55); }
.desktop-nav { display: flex; align-self: stretch; gap: 4px; }
.desktop-nav button,.mobile-nav button { border: 0; background: none; color: #686f7f; cursor: pointer; }
.desktop-nav button { position: relative; padding: 0 15px; font-weight: 600; }
.desktop-nav button.active { color: #191f2d; }
.desktop-nav button.active::after { content: ''; position: absolute; right: 15px; bottom: 0; left: 15px; height: 3px; background: #6558f5; border-radius: 4px 4px 0 0; }
.header-actions { margin-left: auto; display: flex; align-items: center; gap: 6px; }
.user-chip { display: flex; align-items: center; gap: 9px; border: 0; background: transparent; color: #283044; font-weight: 650; cursor: pointer; }
.mobile-trigger { display: none; }
.mobile-nav { display: grid; gap: 8px; }
.mobile-nav button { padding: 13px 16px; border-radius: 10px; text-align: left; font-weight: 650; }
.mobile-nav button.active { color: #5144df; background: #f0efff; }
@media (max-width: 760px) {
  .header-inner { width: calc(100% - 24px); }
  .desktop-nav { display: none; }
  .user-chip span { display: none; }
  .mobile-trigger { display: inline-flex; }
}
</style>
