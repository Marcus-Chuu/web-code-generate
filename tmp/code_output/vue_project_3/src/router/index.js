import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/dashboard' },
  {
    path: '/dashboard',
    name: 'overview',
    component: () => import('@/pages/Dashboard.vue'),
    meta: { title: '数据概览', crumb: '工作台 / 数据概览', desc: '实时掌握产品的核心经营指标' }
  },
  {
    path: '/analysis',
    name: 'analysis',
    component: () => import('@/pages/Placeholder.vue'),
    meta: { title: '深度分析', crumb: '工作台 / 深度分析', desc: '多维交叉分析，洞察业务增长背后的真实规律', tag: '构建中' }
  },
  {
    path: '/users',
    name: 'users',
    component: () => import('@/pages/Placeholder.vue'),
    meta: { title: '用户洞察', crumb: '工作台 / 用户洞察', desc: '沉淀用户画像，识别高价值人群与流失风险', tag: '构建中' }
  },
  {
    path: '/marketing',
    name: 'marketing',
    component: () => import('@/pages/Placeholder.vue'),
    meta: { title: '营销增长', crumb: '工作台 / 营销增长', desc: '一站式活动管理与渠道效果归因', tag: '构建中' }
  },
  {
    path: '/realtime',
    name: 'realtime',
    component: () => import('@/pages/Placeholder.vue'),
    meta: { title: '实时监控', crumb: '工作台 / 实时监控', desc: '秒级数据推送，异常波动第一时间告警', tag: '构建中' }
  },
  {
    path: '/settings',
    name: 'settings',
    component: () => import('@/pages/Placeholder.vue'),
    meta: { title: '系统设置', crumb: '工作台 / 系统设置', desc: '管理成员权限、数据接入与告警策略', tag: '构建中' }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

export default router
