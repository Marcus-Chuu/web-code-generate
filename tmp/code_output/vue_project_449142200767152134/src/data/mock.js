// 全局共享的模拟数据

// 近12个月营收（单位：万元）
export const revenueTrend = [
  { month: '1月', revenue: 32, cost: 18, profit: 14 },
  { month: '2月', revenue: 36, cost: 20, profit: 16 },
  { month: '3月', revenue: 42, cost: 23, profit: 19 },
  { month: '4月', revenue: 39, cost: 22, profit: 17 },
  { month: '5月', revenue: 47, cost: 25, profit: 22 },
  { month: '6月', revenue: 55, cost: 28, profit: 27 },
  { month: '7月', revenue: 61, cost: 31, profit: 30 },
  { month: '8月', revenue: 68, cost: 35, profit: 33 },
  { month: '9月', revenue: 72, cost: 37, profit: 35 },
  { month: '10月', revenue: 79, cost: 40, profit: 39 },
  { month: '11月', revenue: 85, cost: 43, profit: 42 },
  { month: '12月', revenue: 96, cost: 47, profit: 49 }
]

// 近7日订单量（单位：单）
export const orderTrend = [
  { day: '周一', orders: 218, completed: 196 },
  { day: '周二', orders: 264, completed: 238 },
  { day: '周三', orders: 302, completed: 275 },
  { day: '周四', orders: 285, completed: 262 },
  { day: '周五', orders: 348, completed: 320 },
  { day: '周六', orders: 412, completed: 381 },
  { day: '周日', orders: 390, completed: 355 }
]

// 流量来源分布
export const trafficSources = [
  { name: '自然搜索', value: 42, color: '#4f6ef7' },
  { name: '付费广告', value: 26, color: '#22c55e' },
  { name: '社交媒体', value: 18, color: '#f59e0b' },
  { name: '直接访问', value: 14, color: '#a78bfa' }
]

// 各区域订单占比
export const regionData = [
  { name: '华东', value: 35, color: '#4f6ef7' },
  { name: '华南', value: 22, color: '#22c55e' },
  { name: '华北', value: 18, color: '#f59e0b' },
  { name: '西南', value: 13, color: '#f97316' },
  { name: '其他', value: 12, color: '#a78bfa' }
]

// 核心指标
export const metrics = [
  {
    title: '总营收（元）',
    value: '¥2,846,920',
    delta: '+18.6%',
    trend: 'up',
    desc: '较上周期增加 44.6 万',
    icon: '💰',
    color: 'blue'
  },
  {
    title: '活跃用户',
    value: '12,846',
    delta: '+9.2%',
    trend: 'up',
    desc: '较上周期增加 1082 人',
    icon: '👤',
    color: 'green'
  },
  {
    title: '新增订单',
    value: '3,428',
    delta: '-3.4%',
    trend: 'down',
    desc: '较上周期减少 121 单',
    icon: '📦',
    color: 'orange'
  },
  {
    title: '转化率',
    value: '4.82%',
    delta: '+0.6%',
    trend: 'up',
    desc: '较上周期提升 0.6 个百分点',
    icon: '🎯',
    color: 'purple'
  }
]

// 运营动态
export const activities = [
  { icon: '🛒', color: '#4f6ef7', text: '上海云启科技 完成了一笔 ¥86,000 的企业版年度订阅', time: '12 分钟前' },
  { icon: '👥', color: '#22c55e', text: '新增企业客户「杭州数联科技」，团队规模 126 人', time: '46 分钟前' },
  { icon: '💳', color: '#f59e0b', text: '广州云帆电商 的账户余额即将不足，已发送续费提醒', time: '1 小时前' },
  { icon: '🚀', color: '#7c5cfc', text: 'v3.8.2 版本发布：新增数据导出与自定义报表功能', time: '2 小时前' },
  { icon: '📈', color: '#ef4444', text: '「深圳智联制造」出现接口调用异常，工单 #10245 处理中', time: '3 小时前' },
  { icon: '✅', color: '#0ea5e9', text: '本月 24 家续费客户已全部完成自动续费流程', time: '5 小时前' }
]

// 最新订单
export const recentOrders = [
  { id: 'SO-20240719-0088', customer: '上海云启科技', product: '企业版 · 年度', amount: 86000, status: '已支付', statusType: 'success', time: '10:24' },
  { id: 'SO-20240719-0087', customer: '北京星辰互娱', product: '专业版 · 月度', amount: 2980, status: '已支付', statusType: 'success', time: '09:56' },
  { id: 'SO-20240719-0086', customer: '杭州数联科技', product: '旗舰版 · 年度', amount: 128000, status: '待支付', statusType: 'warning', time: '09:31' },
  { id: 'SO-20240719-0085', customer: '广州云帆电商', product: '专业版 · 季度', amount: 8940, status: '已支付', statusType: 'success', time: '09:05' },
  { id: 'SO-20240719-0084', customer: '深圳智联制造', product: '企业版 · 月度', amount: 7200, status: '已退款', statusType: 'danger', time: '08:47' },
  { id: 'SO-20240719-0083', customer: '成都锦程教育', product: '专业版 · 年度', amount: 35800, status: '已支付', statusType: 'success', time: '08:22' }
]

// 产品销量排行
export const productRank = [
  { name: '企业版 · 年度订阅', sales: 186, revenue: '¥1,206万', ratio: 82 },
  { name: '旗舰版 · 年度订阅', sales: 92, revenue: '¥1,042万', ratio: 64 },
  { name: '专业版 · 季度订阅', sales: 358, revenue: '¥320万', ratio: 51 },
  { name: '企业版 · 月度订阅', sales: 296, revenue: '¥213万', ratio: 38 },
  { name: '专业版 · 月度订阅', sales: 1042, revenue: '¥310万', ratio: 27 }
]

// ============ 实时监控 ============
export const serverMetrics = [
  { label: 'CPU 使用率', value: 46, unit: '%', color: '#4f6ef7', text: '运行平稳' },
  { label: '内存使用率', value: 62, unit: '%', color: '#22c55e', text: '运行平稳' },
  { label: '磁盘占用率', value: 71, unit: '%', color: '#f59e0b', text: '建议扩容' },
  { label: '网络带宽', value: 84, unit: '%', color: '#a78bfa', text: '访问高峰' }
]

export const serviceStatus = [
  { name: 'Web 控制台', status: '正常', type: 'green', uptime: '99.99%', response: '82 ms' },
  { name: '订单服务', status: '正常', type: 'green', uptime: '99.97%', response: '106 ms' },
  { name: '消息通知', status: '正常', type: 'green', uptime: '99.95%', response: '64 ms' },
  { name: '文件存储', status: '正常', type: 'green', uptime: '99.99%', response: '128 ms' },
  { name: '数据仓库', status: '维护中', type: 'orange', uptime: '99.82%', response: '310 ms' },
  { name: 'API 网关', status: '正常', type: 'green', uptime: '99.96%', response: '58 ms' }
]

export const apiCalls = [
  { hour: '00:00', calls: 320 },
  { hour: '02:00', calls: 210 },
  { hour: '04:00', calls: 180 },
  { hour: '06:00', calls: 260 },
  { hour: '08:00', calls: 520 },
  { hour: '10:00', calls: 1180 },
  { hour: '12:00', calls: 920 },
  { hour: '14:00', calls: 1420 },
  { hour: '16:00', calls: 1650 },
  { hour: '18:00', calls: 1240 },
  { hour: '20:00', calls: 860 },
  { hour: '22:00', calls: 520 }
]

export const errorTrend = [
  { hour: '00:00', errors: 3 },
  { hour: '04:00', errors: 1 },
  { hour: '08:00', errors: 6 },
  { hour: '12:00', errors: 9 },
  { hour: '16:00', errors: 7 },
  { hour: '20:00', errors: 4 }
]

// ============ 用户分析 ============
export const userTrend = [
  { month: '2月', newUsers: 420, active: 2800 },
  { month: '3月', newUsers: 680, active: 3900 },
  { month: '4月', newUsers: 560, active: 4700 },
  { month: '5月', newUsers: 930, active: 6100 },
  { month: '6月', newUsers: 1180, active: 7800 },
  { month: '7月', newUsers: 1430, active: 9200 }
]

export const userGrowth = [
  { label: '2月', active: 2800 },
  { label: '3月', active: 3900 },
  { label: '4月', active: 4700 },
  { label: '5月', active: 6100 },
  { label: '6月', active: 7800 },
  { label: '7月', active: 9200 }
]

export const userTags = [
  { name: '个人开发者', count: 6840, value: 52, color: '#4f6ef7' },
  { name: '小微企业', count: 4120, value: 31, color: '#22c55e' },
  { name: '成长型企业', count: 1520, value: 12, color: '#f59e0b' },
  { name: '大型集团', count: 640, value: 5, color: '#a78bfa' }
]

export const deviceData = [
  { name: 'Windows', value: 46, color: '#4f6ef7' },
  { name: 'macOS', value: 24, color: '#22c55e' },
  { name: '移动端', value: 22, color: '#f59e0b' },
  { name: 'Linux', value: 8, color: '#a78bfa' }
]

// ============ 营收报表 ============
export const revenueBars = [
  { month: '1月', revenue: 32 },
  { month: '2月', revenue: 36 },
  { month: '3月', revenue: 42 },
  { month: '4月', revenue: 39 },
  { month: '5月', revenue: 47 },
  { month: '6月', revenue: 55 },
  { month: '7月', revenue: 61 },
  { month: '8月', revenue: 68 },
  { month: '9月', revenue: 72 },
  { month: '10月', revenue: 79 },
  { month: '11月', revenue: 85 },
  { month: '12月', revenue: 96 }
]

export const arpuTrend = [
  { month: '1月', arpu: 162 },
  { month: '2月', arpu: 170 },
  { month: '3月', arpu: 168 },
  { month: '4月', arpu: 175 },
  { month: '5月', arpu: 183 },
  { month: '6月', arpu: 190 },
  { month: '7月', arpu: 198 },
  { month: '8月', arpu: 205 },
  { month: '9月', arpu: 212 },
  { month: '10月', arpu: 220 },
  { month: '11月', arpu: 228 },
  { month: '12月', arpu: 236 }
]

export const billingRecords = [
  { id: 'INV-202407-0012', type: '订阅账单', time: '2024-07-01 09:30', amount: 129800, method: '对公转账', status: '已入账', statusType: 'success' },
  { id: 'INV-202407-0011', type: '订阅账单', time: '2024-07-02 14:12', amount: 8600, method: '微信支付', status: '已入账', statusType: 'success' },
  { id: 'INV-202407-0010', type: '增值服务', time: '2024-07-04 10:05', amount: 18000, method: '支付宝', status: '待确认', statusType: 'warning' },
  { id: 'INV-202407-0009', type: '订阅账单', time: '2024-07-06 16:48', amount: 4200, method: '对公转账', status: '已入账', statusType: 'success' },
  { id: 'INV-202407-0008', type: '退费', time: '2024-07-08 11:26', amount: -2980, method: '原路退回', status: '已退款', statusType: 'danger' },
  { id: 'INV-202407-0007', type: '订阅账单', time: '2024-07-10 09:14', amount: 128000, method: '对公转账', status: '已入账', statusType: 'success' },
  { id: 'INV-202407-0006', type: '增值服务', time: '2024-07-12 13:40', amount: 5600, method: '微信支付', status: '待确认', statusType: 'warning' },
  { id: 'INV-202407-0005', type: '订阅账单', time: '2024-07-15 15:22', amount: 2980, method: '支付宝', status: '已入账', statusType: 'success' },
  { id: 'INV-202407-0004', type: '订阅账单', time: '2024-07-17 08:58', amount: 8940, method: '对公转账', status: '已入账', statusType: 'success' },
  { id: 'INV-202407-0003', type: '退费', time: '2024-07-18 17:36', amount: -7200, method: '原路退回', status: '已退款', statusType: 'danger' }
]

export const invoices = [
  { id: 'FP-202407-086', title: '云析数据 企业版年度订阅', amount: 129800, date: '2024-07-12', status: '已开票', statusType: 'success' },
  { id: 'FP-202407-085', title: '云析数据 专业版季度订阅', amount: 8940, date: '2024-07-10', status: '已开票', statusType: 'success' },
  { id: 'FP-202407-084', title: '云析数据 旗舰版年度订阅', amount: 128000, date: '2024-07-08', status: '待审核', statusType: 'warning' },
  { id: 'FP-202407-083', title: '增值服务 · 专属培训', amount: 18000, date: '2024-07-05', status: '已开票', statusType: 'success' },
  { id: 'FP-202407-082', title: '云析数据 专业版月度订阅', amount: 2980, date: '2024-07-03', status: '已驳回', statusType: 'danger' }
]

// ============ 系统设置 ============
export const memberList = [
  { name: '林晓峰', role: '超级管理员', email: 'lin@yunxi-data.com', status: '启用', avatar: '林' },
  { name: '苏婉清', role: '管理员', email: 'su@yunxi-data.com', status: '启用', avatar: '苏' },
  { name: '周子墨', role: '数据分析师', email: 'zhou@yunxi-data.com', status: '启用', avatar: '周' },
  { name: '陈思远', role: '数据分析师', email: 'chen@yunxi-data.com', status: '启用', avatar: '陈' },
  { name: '赵若曦', role: '财务专员', email: 'zhao@yunxi-data.com', status: '停用', avatar: '赵' }
]

export const noticeLogs = [
  { time: '2024-07-19 09:12', channel: '邮件', target: '全部管理员', title: '每日数据简报', status: '发送成功', statusType: 'success' },
  { time: '2024-07-19 08:45', channel: '短信', target: '续费客户 24 人', title: '订阅到期提醒', status: '发送成功', statusType: 'success' },
  { time: '2024-07-18 20:30', channel: 'Webhook', target: '告警接收群', title: '系统异常告警', status: '发送成功', statusType: 'success' },
  { time: '2024-07-18 16:22', channel: '邮件', target: '全体员工', title: '版本更新公告', status: '发送失败', statusType: 'danger' },
  { time: '2024-07-18 10:08', channel: '短信', target: '欠费客户 3 人', title: '欠费提醒', status: '发送成功', statusType: 'success' }
]

export const opLogs = [
  { time: '2024-07-19 09:20', user: '林晓峰', action: '更新了团队成员「赵若曦」的角色权限', ip: '101.88.12.56' },
  { time: '2024-07-19 08:51', user: '苏婉清', action: '修改了营收报表的导出时间范围为近 30 天', ip: '101.88.23.110' },
  { time: '2024-07-18 18:02', user: '系统', action: '自动执行了每日 02:00 的数据全量备份', ip: '内部任务' },
  { time: '2024-07-18 15:44', user: '周子墨', action: '创建了数据看板「华东区域销售分析」', ip: '114.92.31.78' },
  { time: '2024-07-18 11:12', user: '陈思远', action: '导出了用户活跃度分析报表（CSV）', ip: '114.92.31.78' }
]
