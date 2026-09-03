<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const navItems = [
  { label: '数据看板', path: '/', icon: '📊' },
  { label: '实时监控', path: '/monitor', icon: '🛰️' },
  { label: '用户分析', path: '/users', icon: '👥' },
  { label: '营收报表', path: '/revenue', icon: '💰' },
  { label: '系统设置', path: '/settings', icon: '⚙️' }
]

const query = ref('近 7 天')
const periods = ['近 7 天', '近 30 天', '本季度', '今年']

function changePeriod(v) {
  query.value = v
}

function go(path) {
  router.push(path)
}
</script>

<template>
  <div class="app-shell">
    <header class="topbar">
      <div class="topbar-inner">
        <div class="logo">
          <span class="logo-mark">云</span>
          <span class="logo-text">云析数据</span>
          <span class="logo-tag">SaaS</span>
        </div>

        <nav class="nav">
          <a
            v-for="item in navItems"
            :key="item.path"
            href="javascript:;"
            class="nav-item"
            :class="{ active: route.path === item.path }"
            @click="go(item.path)"
          >
            <span class="nav-icon">{{ item.icon }}</span>
            <span>{{ item.label }}</span>
          </a>
        </nav>

        <div class="topbar-right">
          <div class="period-select">
            <select :value="query" @change="changePeriod($event.target.value)">
              <option v-for="p in periods" :key="p" :value="p">{{ p }}</option>
            </select>
          </div>
          <button class="icon-btn" title="通知">🔔</button>
          <div class="user">
            <div class="avatar">林</div>
            <span class="user-name">林晓峰</span>
          </div>
        </div>
      </div>
    </header>

    <main class="main">
      <router-view />
    </main>

    <footer class="footer">
      <span>© 2024 云析数据科技有限公司</span>
      <span class="footer-right">沪ICP备2024011886号 · 服务支持 400-880-9900</span>
    </footer>
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: #fff;
  border-bottom: 1px solid var(--border);
  height: 60px;
}

.topbar-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 20px;
  height: 100%;
  display: flex;
  align-items: center;
  gap: 32px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.logo-mark {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: linear-gradient(135deg, #4f6ef7, #7c5cfc);
  color: #fff;
  font-weight: 700;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-text {
  font-size: 17px;
  font-weight: 700;
  letter-spacing: 1px;
}

.logo-tag {
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 4px;
  background: var(--primary-light);
  color: var(--primary);
  font-weight: 600;
}

.nav {
  display: flex;
  gap: 4px;
  flex: 1;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 7px 14px;
  border-radius: 8px;
  color: var(--text-sub);
  font-weight: 500;
  transition: all 0.2s;
}

.nav-item:hover {
  background: var(--bg-page);
  color: var(--text-main);
}

.nav-item.active {
  background: var(--primary-light);
  color: var(--primary);
}

.nav-icon {
  font-size: 15px;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 14px;
  flex-shrink: 0;
}

.period-select select {
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 6px 10px;
  font-size: 13px;
  background: #fff;
  color: var(--text-main);
  outline: none;
  cursor: pointer;
}

.icon-btn {
  border: none;
  background: transparent;
  font-size: 17px;
}

.user {
  display: flex;
  align-items: center;
  gap: 8px;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f59e0b, #f97316);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-name {
  font-size: 13px;
  color: var(--text-main);
}

.main {
  flex: 1;
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
  padding: 20px;
}

.footer {
  border-top: 1px solid var(--border);
  background: #fff;
  padding: 14px 20px;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-sub);
}

@media (max-width: 900px) {
  .user-name,
  .logo-tag {
    display: none;
  }
  .topbar-inner {
    gap: 16px;
  }
}

@media (max-width: 700px) {
  .nav {
    overflow-x: auto;
  }
  .nav-item span:last-child {
    display: none;
  }
  .topbar-inner {
    padding: 0 12px;
  }
  .main {
    padding: 12px;
  }
  .footer {
    flex-direction: column;
    gap: 4px;
  }
}
</style>
