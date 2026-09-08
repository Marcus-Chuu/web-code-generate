<script setup>
import { useRoute } from 'vue-router'

defineProps({ open: { type: Boolean, default: false } })
const emit = defineEmits(['close'])
const route = useRoute()

const groups = [
  {
    label: '数据工作台',
    items: [
      { name: '数据概览', to: '/dashboard', icon: 'grid' },
      { name: '深度分析', to: '/analysis', icon: 'chart' },
      { name: '用户洞察', to: '/users', icon: 'user' }
    ]
  },
  {
    label: '增长与运营',
    items: [
      { name: '营销增长', to: '/marketing', icon: 'rocket' },
      { name: '实时监控', to: '/realtime', icon: 'pulse', badge: 'LIVE' }
    ]
  }
]

const icons = {
  grid: '<path d="M3 3h7v7H3zM14 3h7v7h-7zM3 14h7v7H3zM14 14h7v7h-7z"/>',
  chart: '<path d="M4 19V9m6 10V5m6 14v-7m6 7V3"/>',
  user: '<circle cx="12" cy="8" r="4"/><path d="M4 21c0-4 3.6-6 8-6s8 2 8 6"/>',
  rocket: '<path d="M12 2s6 4 6 10c0 4-2 6-6 8-4-2-6-4-6-8 0-6 6-10 6-10z"/><circle cx="12" cy="11" r="2.5"/>',
  pulse: '<path d="M2 12h4l3-7 4 14 3-7h6"/>'
}

const isActive = (to) => route.path === to
</script>

<template>
  <aside class="sidebar" :class="{ open }">
    <div class="brand">
      <div class="brand-logo">数</div>
      <div>
        <div class="brand-text">数迹 · DataFlow</div>
        <div class="brand-sub">SaaS 数据分析平台</div>
      </div>
    </div>

    <nav>
      <template v-for="g in groups" :key="g.label">
        <div class="nav-label">{{ g.label }}</div>
        <router-link
          v-for="item in g.items"
          :key="item.name"
          :to="item.to"
          class="nav-item"
          :class="{ 'router-link-active': isActive(item.to) }"
          @click="emit('close')"
        >
          <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" v-html="icons[item.icon]"></svg>
          <span>{{ item.name }}</span>
          <span v-if="item.badge" class="nav-badge">{{ item.badge }}</span>
        </router-link>
      </template>

      <div class="nav-label">管理</div>
      <router-link to="/settings" class="nav-item" :class="{ 'router-link-active': isActive('/settings') }" @click="emit('close')">
        <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3.2"/><path d="M19.4 15a1.6 1.6 0 0 0 .32 1.77l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.6 1.6 0 0 0-1.77-.32 1.6 1.6 0 0 0-.97 1.47V21a2 2 0 1 1-4 0v-.09a1.6 1.6 0 0 0-1.05-1.47 1.6 1.6 0 0 0-1.77.32l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06a1.6 1.6 0 0 0 .32-1.77 1.6 1.6 0 0 0-1.47-.97H3a2 2 0 1 1 0-4h.09a1.6 1.6 0 0 0 1.47-1.05 1.6 1.6 0 0 0-.32-1.77l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06a1.6 1.6 0 0 0 1.77.32h.08a1.6 1.6 0 0 0 .97-1.47V3a2 2 0 1 1 4 0v.09a1.6 1.6 0 0 0 .97 1.47 1.6 1.6 0 0 0 1.77-.32l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06a1.6 1.6 0 0 0-.32 1.77v.08a1.6 1.6 0 0 0 1.47.97H21a2 2 0 1 1 0 4h-.09a1.6 1.6 0 0 0-1.47.97z"/></svg>
        <span>系统设置</span>
      </router-link>
    </nav>

    <div class="sidebar-foot">
      <span>企业旗舰版</span>
      <span class="plan-chip">还剩 23 天</span>
    </div>
  </aside>
</template>

<style scoped>
.sidebar {
  width: 240px;
  flex-shrink: 0;
  background: #101a33;
  color: #c6d2ec;
  display: flex;
  flex-direction: column;
  height: 100vh;
  position: sticky;
  top: 0;
  overflow-y: auto;
}

.brand {
  display: none;
  align-items: center;
  gap: 10px;
  padding: 16px 18px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.07);
}
.brand-logo {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: linear-gradient(135deg, #5b8cff, #7c5cff);
  color: #fff;
  font-weight: 800;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 14px rgba(91, 140, 255, 0.35);
}
.brand-text { color: #fff; font-size: 15px; font-weight: 700; }
.brand-sub { font-size: 11px; color: #8fa1c7; margin-top: 1px; }

nav { flex: 1; padding: 12px 12px 8px; }
.nav-label {
  display: none;
  font-size: 11px;
  letter-spacing: 1.5px;
  color: #64789f;
  padding: 10px 8px 6px;
  font-weight: 600;
}
.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 11px;
  border-radius: 8px;
  color: #aebddd;
  text-decoration: none;
  font-size: 13.5px;
  margin: 1px 0;
  transition: background 0.15s, color 0.15s;
}
.nav-item:hover { background: rgba(255, 255, 255, 0.06); color: #fff; }
.nav-item.router-link-active { background: linear-gradient(90deg, #2f4a92, #3d5cad); color: #fff; }
.nav-badge {
  margin-left: auto;
  font-size: 9.5px;
  background: #e5484d;
  color: #fff;
  border-radius: 3px;
  padding: 2px 4px;
  letter-spacing: 0.5px;
  font-weight: 700;
}

.sidebar-foot {
  display: none;
  align-items: center;
  justify-content: space-between;
  padding: 12px 18px 16px;
  font-size: 12.5px;
  color: #7c8db0;
  border-top: 1px solid rgba(255, 255, 255, 0.07);
}
.plan-chip {
  font-size: 11px;
  background: rgba(91, 140, 255, 0.18);
  color: #7ea6ff;
  padding: 3px 8px;
  border-radius: 4px;
}

@media (min-width: 1024px) {
  .brand { display: flex; }
  .nav-label { display: block; }
  .sidebar-foot { display: flex; }
}

@media (max-width: 1023px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 110;
    transform: translateX(-100%);
    transition: transform 0.26s ease;
    box-shadow: none;
  }
  .sidebar.open {
    transform: translateX(0);
    box-shadow: 0 0 0 100vmax rgba(15, 23, 42, 0.45);
  }
  .brand { display: flex; }
  .nav-label { display: block; }
  .sidebar-foot { display: flex; }
}
</style>
