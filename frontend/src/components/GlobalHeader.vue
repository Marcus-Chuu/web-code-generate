<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { MenuOutlined, LogoutOutlined, SettingOutlined } from '@ant-design/icons-vue'
import { DEFAULT_AVATAR } from '@/config/avatars'
import { useLoginUserStore } from '@/stores/loginUser'

const route = useRoute()
const router = useRouter()
const userStore = useLoginUserStore()
const mobileOpen = ref(false)

const menus = computed(() => [
  { key: '/', label: '发现' },
  ...(userStore.isLoggedIn ? [{ key: '/apps', label: '我的应用' }] : []),
  ...(userStore.isAdmin
    ? [
        { key: '/admin/users', label: '用户' },
        { key: '/admin/apps', label: '应用' },
        { key: '/admin/chats', label: '对话' },
      ]
    : []),
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
        <img class="brand-mark" src="/favicon.svg?v=2" alt="" width="33" height="33" />
        <span>灵构 <b>AI</b></span
        ><span class="brand-divider"></span><span class="brand-caption">灵感 · 即刻发生</span>
      </RouterLink>

      <nav class="desktop-nav" aria-label="主导航">
        <button
          v-for="item in menus"
          :key="item.key"
          :class="{ active: activeKey === item.key }"
          @click="navigate(item.key)"
        >
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
                <a-menu-item key="apps" @click="navigate('/apps')"
                  ><SettingOutlined /> 我的应用</a-menu-item
                >
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
        <a-button
          class="mobile-trigger"
          type="text"
          aria-label="打开菜单"
          @click="mobileOpen = true"
          ><MenuOutlined
        /></a-button>
      </div>
    </div>
  </a-layout-header>

  <a-drawer v-model:open="mobileOpen" title="导航" placement="right" :width="280">
    <div class="mobile-nav">
      <button
        v-for="item in menus"
        :key="item.key"
        :class="{ active: activeKey === item.key }"
        @click="navigate(item.key)"
      >
        {{ item.label }}
      </button>
    </div>
  </a-drawer>
</template>

<style scoped>
.site-header {
  position: sticky;
  top: 0;
  z-index: 50;
  height: 68px;
  padding: 0;
  line-height: normal;
  background: rgba(250, 251, 247, 0.92);
  border-bottom: 1px solid #e7ebe2;
  backdrop-filter: blur(18px);
}
.header-inner {
  width: min(1280px, calc(100% - 64px));
  height: 100%;
  margin: auto;
  display: flex;
  align-items: center;
  gap: 36px;
}
.brand {
  display: flex;
  align-items: center;
  gap: 9px;
  color: #314937;
  font-size: 19px;
  font-weight: 700;
  letter-spacing: -0.03em;
  white-space: nowrap;
}
.brand b {
  color: #7e9573;
  font-size: 15px;
  font-weight: 500;
}
.brand-mark {
  width: 33px;
  height: 33px;
  display: block;
  flex-shrink: 0;
  transform: rotate(-5deg);
}
.brand-divider {
  width: 1px;
  height: 17px;
  margin: 0 5px 0 10px;
  background: #dde3d5;
}
.brand .brand-caption {
  font-size: 10px;
  font-weight: 400;
  color: #98a18f;
  letter-spacing: 0.08em;
}
.desktop-nav {
  display: flex;
  align-items: center;
  gap: 6px;
}
.desktop-nav button,
.mobile-nav button {
  border: 0;
  background: none;
  color: #7c8577;
  cursor: pointer;
  transition:
    background 0.2s,
    color 0.2s;
}
.desktop-nav button {
  position: relative;
  padding: 8px 15px;
  border-radius: 7px;
  font-size: 13px;
  font-weight: 500;
}
.desktop-nav button.active {
  color: #48643f;
  background: #edf2e7;
}
.desktop-nav button:hover {
  color: #48643f;
  background: #f0f3eb;
}
.header-actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 8px;
}
.header-actions :deep(.ant-btn) {
  font-size: 12px;
}
.user-chip {
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 5px 9px 5px 5px;
  border: 1px solid #e7ebe1;
  border-radius: 30px;
  background: #fff;
  color: #5a6a51;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
}
.user-chip :deep(.ant-avatar) {
  width: 28px !important;
  height: 28px !important;
}
.mobile-trigger {
  display: none;
}
.mobile-nav {
  display: grid;
  gap: 8px;
}
.mobile-nav button {
  padding: 13px 16px;
  border-radius: 10px;
  text-align: left;
  font-weight: 500;
}
.mobile-nav button.active {
  color: #52765b;
  background: #edf2e7;
}
@media (max-width: 1100px) {
  .brand-divider,
  .brand-caption {
    display: none;
  }
  .header-inner {
    gap: 24px;
  }
}
@media (max-width: 760px) {
  .header-inner {
    width: calc(100% - 32px);
    gap: 12px;
  }
  .desktop-nav {
    display: none;
  }
  .user-chip > span:last-child {
    display: none;
  }
  .user-chip {
    padding: 3px;
  }
  .mobile-trigger {
    display: inline-flex;
  }
  .header-actions {
    gap: 3px;
  }
  .brand {
    font-size: 17px;
  }
}
</style>
