import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { visitors } from '@/utils/mock'

const me = { name: '王雨桐', role: '超级管理员', avatar: 'https://picsum.photos/seed/avatar/admin/80/80' }
const nav = [
  { label: '数据概览', icon: 'icon-dash', path: '/' },
  { label: '用户分析', icon: 'icon-user', path: '/users' },
  { label: '自定义报表', icon: 'icon-report', path: '/reports' },
  { label: '事件管理', icon: 'icon-event', path: '/events' },
  { label: '智能告警', icon: 'icon-alert', path: '/alerts' }
]

const toggle = ref(false)

function useSideNav() {
  const route = useRoute()
  const close = () => { toggle.value = false }
  watch(() => route.fullPath, close)
  return { toggle, close }
}

export { me, nav, visitors, toggle, useSideNav }
