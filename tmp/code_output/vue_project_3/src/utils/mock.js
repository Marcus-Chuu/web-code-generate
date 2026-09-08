import { reactive } from 'vue'

export const stats = reactive({
  mrr: { label: '本月新增 MRR', value: 186420, unit: '元', rate: 12.6, up: true },
  active: { label: '活跃用户 (MAU)', value: 28465, unit: '人', rate: 8.2, up: true },
  pay: { label: '付费转化率', value: 3.86, unit: '%', rate: 0.64, up: true },
  churn: { label: '客户流失率', value: 1.28, unit: '%', rate: 0.18, up: false }
})

export const trendData = [
  { date: '5/1', pv: 1284, uv: 420 }, { date: '5/2', pv: 1503, uv: 486 },
  { date: '5/3', pv: 1390, uv: 441 }, { date: '5/4', pv: 1756, uv: 529 },
  { date: '5/5', pv: 1688, uv: 512 }, { date: '5/6', pv: 2034, uv: 618 },
  { date: '5/7', pv: 1926, uv: 571 }, { date: '5/8', pv: 2285, uv: 674 },
  { date: '5/9', pv: 2148, uv: 641 }, { date: '5/10', pv: 2607, uv: 763 },
  { date: '5/11', pv: 2482, uv: 712 }, { date: '5/12', pv: 2891, uv: 839 },
  { date: '5/13', pv: 2746, uv: 796 }, { date: '5/14', pv: 3204, uv: 932 }
]

export const channels = [
  { name: '自然搜索', value: 1248, color: '#4f6bf6' },
  { name: '直接访问', value: 986, color: '#7c5cf0' },
  { name: '内容营销', value: 742, color: '#18a561' },
  { name: '社交媒体', value: 513, color: '#f2a93b' },
  { name: '付费广告', value: 356, color: '#e04a5f' },
  { name: '其他渠道', value: 212, color: '#98a2b8' }
]

export const funnelSteps = [
  { name: '访问落地页', value: 4057, rate: 100 },
  { name: '注册账号', value: 3182, rate: 78.4 },
  { name: '创建首个项目', value: 2106, rate: 51.9 },
  { name: '邀请团队成员', value: 1374, rate: 33.9 },
  { name: '升级付费套餐', value: 646, rate: 15.9 }
]

export const hotPages = [
  { name: '/dashboard', visits: 8421, change: 12.4, up: true },
  { name: '/features/integrations', visits: 6890, change: 8.7, up: true },
  { name: '/pricing', visits: 5732, change: 5.1, up: true },
  { name: '/docs/quickstart', visits: 4108, change: 3.2, down: true },
  { name: '/blog/2024-saas-report', visits: 3296, change: 15.8, up: true }
]

export const cityData = [
  { name: '北京', value: 18.2, color: '#4f6bf6' },
  { name: '上海', value: 16.4, color: '#7c5cf0' },
  { name: '深圳', value: 11.8, color: '#18a561' },
  { name: '杭州', value: 9.6, color: '#f2a93b' },
  { name: '广州', value: 7.3, color: '#e04a5f' },
  { name: '成都', value: 5.1, color: '#5fc4e8' }
]

export const visitors = [
  { name: '灵犀智能', type: '企业版', mrr: '¥ 12,800', active: true },
  { name: '云帆科技', type: '专业版', mrr: '¥ 6,400', active: true },
  { name: '海豚互动', type: '专业版', mrr: '¥ 3,200', active: false },
  { name: '逐浪文化', type: '团队版', mrr: '¥ 960', active: false }
]
