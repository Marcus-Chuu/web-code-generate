<script setup>
import { ref } from 'vue'
import LineChart from '@/components/LineChart.vue'
import BarChart from '@/components/BarChart.vue'
import { serverMetrics, serviceStatus, apiCalls, errorTrend } from '@/data/mock'

const healthScore = ref(92)
const statusMap = {
  green: { text: '正常', badge: 'badge-success' },
  orange: { text: '维护中', badge: 'badge-warning' },
  red: { text: '异常', badge: 'badge-danger' }
}

const apiSeries = [{ key: 'calls', label: '调用量（次）', color: '#4f6ef7' }]
const errorSeries = [{ key: 'errors', label: '错误数（次）', color: '#ef4444' }]

const alertList = ref([
  { level: 'warning', icon: '⚠️', text: '数据仓库服务处于维护窗口期（预计 30 分钟后恢复）', time: '14:20' },
  { level: 'warning', icon: '📈', text: '磁盘占用率连续 15 分钟高于 70%，建议扩容存储空间', time: '13:55' },
  { level: 'success', icon: '✅', text: 'API 网关昨日晚高峰扩容已完成，全部节点运行正常', time: '11:02' },
  { level: 'success', icon: '✅', text: '订单服务已完成自动故障转移演练，无数据丢失', time: '09:40' }
])
</script>

<template>
  <div class="monitor">
    <div class="page-header">
      <div>
        <h1>实时监控</h1>
        <p>实时掌握系统运行状态、服务健康度与告警信息</p>
      </div>
      <div class="header-actions">
        <span class="refresh-status"><span class="status-dot dot-green"></span>数据每 30 秒自动刷新</span>
        <button class="btn btn-primary" @click="healthScore = 88 + Math.floor(Math.random() * 9)">立即刷新</button>
      </div>
    </div>

    <!-- 运行状态横幅 -->
    <div class="health-banner">
      <div class="health-score">
        <div class="score-ring" :style="{ background: `conic-gradient(#22c55e ${healthScore * 3.6}deg, #e2e8f0 0deg)` }">
          <div class="score-inner">
            <b>{{ healthScore }}</b>
            <span>健康分</span>
          </div>
        </div>
        <div>
          <h3>系统整体运行正常</h3>
          <p>全部 12 台节点在线 · 近 1 小时平均可用率 99.96% · 0 个 P0/P1 级告警</p>
        </div>
      </div>
      <div class="quick-status">
        <div class="quick-item" v-for="(m, i) in serverMetrics" :key="i">
          <span class="label">{{ m.label }}</span>
          <b :style="{ color: m.color }">{{ m.value }}{{ m.unit }}</b>
        </div>
      </div>
    </div>

    <!-- 服务器资源 -->
    <section class="card">
      <div class="card-header">
        <div>
          <h3 class="card-title">服务器资源</h3>
          <p class="card-sub">生产环境集群 · 华东一区（实时采样）</p>
        </div>
      </div>
      <div class="resource-grid">
        <div v-for="(m, i) in serverMetrics" :key="i" class="resource-item">
          <div class="resource-head">
            <span>{{ m.label }}</span>
            <b :style="{ color: m.color }">{{ m.value }}{{ m.unit }}</b>
          </div>
          <div class="bar">
            <i :style="{ width: m.value + '%', background: m.color }"></i>
          </div>
          <p class="resource-text">{{ m.text }}</p>
        </div>
      </div>
    </section>

    <!-- 服务状态与调用量 -->
    <section class="section-grid two-col">
      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">核心服务状态</h3>
            <p class="card-sub">6 个核心服务健康检查结果</p>
          </div>
        </div>
        <ul class="service-list">
          <li v-for="(s, i) in serviceStatus" :key="i" class="service-item">
            <span class="service-icon" :class="'is-' + s.type">{{ i + 1 }}</span>
            <div class="service-info">
              <p class="service-name">{{ s.name }}</p>
              <p class="service-meta">响应 {{ s.response }} · 近 30 天可用率 {{ s.uptime }}</p>
            </div>
            <span class="badge" :class="statusMap[s.type].badge">
              <span class="status-dot" :class="'dot-' + s.type"></span>{{ statusMap[s.type].text }}
            </span>
          </li>
        </ul>
      </div>

      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">API 调用量</h3>
            <p class="card-sub">今日每两小时 API 网关调用量（万次）</p>
          </div>
          <span class="badge badge-primary">今日 1,284 万次</span>
        </div>
        <LineChart :data="apiCalls" x-key="hour" :series="apiSeries" :height="240" />
        <div class="inline-chart">
          <p class="inline-title">错误趋势（近 24h）</p>
          <BarChart :data="errorTrend" x-key="hour" y-key="errors" color="#ef4444" />
        </div>
      </div>
    </section>

    <!-- 告警记录 -->
    <section class="card">
      <div class="card-header">
        <div>
          <h3 class="card-title">实时告警</h3>
          <p class="card-sub">最近系统事件与告警通知</p>
        </div>
        <button class="btn btn-text">全部告警 →</button>
      </div>
      <ul class="alert-list">
        <li v-for="(a, i) in alertList" :key="i" class="alert-item">
          <span class="alert-icon" :class="'is-' + a.level">{{ a.icon }}</span>
          <p class="alert-text">{{ a.text }}</p>
          <span class="alert-time">{{ a.time }}</span>
        </li>
      </ul>
    </section>
  </div>
</template>

<style scoped>
.monitor {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 14px;
}

.refresh-status {
  font-size: 13px;
  color: var(--text-sub);
}

/* 健康横幅 */
.health-banner {
  background: #fff;
  border-radius: 14px;
  box-shadow: var(--card-shadow);
  padding: 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  flex-wrap: wrap;
}

.health-score {
  display: flex;
  align-items: center;
  gap: 18px;
}

.score-ring {
  width: 86px;
  height: 86px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.score-inner {
  width: 70px;
  height: 70px;
  background: #fff;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.score-inner b {
  font-size: 24px;
  color: #16a34a;
  line-height: 1.2;
}

.score-inner span {
  font-size: 11px;
  color: var(--text-sub);
}

.health-score h3 {
  font-size: 17px;
  font-weight: 600;
}

.health-score p {
  margin-top: 4px;
  color: var(--text-sub);
  font-size: 13px;
}

.quick-status {
  display: flex;
  gap: 26px;
  flex-wrap: wrap;
}

.quick-item {
  text-align: center;
}

.quick-item .label {
  display: block;
  font-size: 12px;
  color: var(--text-sub);
}

.quick-item b {
  font-size: 20px;
  font-weight: 700;
}

/* 服务器资源 */
.resource-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.resource-head {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  margin-bottom: 10px;
}

.resource-head span {
  color: var(--text-sub);
}

.resource-text {
  margin-top: 8px;
  font-size: 12px;
  color: #94a3b8;
}

/* 服务列表 */
.service-list {
  display: flex;
  flex-direction: column;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 11px 0;
  border-bottom: 1px solid #f1f5f9;
}

.service-item:last-child {
  border-bottom: none;
}

.service-icon {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}

.is-green { background: var(--success-light); color: #16a34a; }
.is-orange { background: var(--warning-light); color: #d97706; }
.is-red { background: var(--danger-light); color: #dc2626; }

.service-info {
  flex: 1;
}

.service-name {
  font-size: 14px;
  font-weight: 500;
}

.service-meta {
  font-size: 12px;
  color: #94a3b8;
}

/* 内嵌小图 */
.inline-chart {
  margin-top: 22px;
  padding-top: 18px;
  border-top: 1px dashed var(--border);
}

.inline-title {
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 10px;
  color: var(--text-main);
}

/* 告警 */
.alert-list {
  display: flex;
  flex-direction: column;
}

.alert-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 11px 0;
  border-bottom: 1px solid #f1f5f9;
}

.alert-item:last-child {
  border-bottom: none;
}

.alert-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.alert-icon.is-warning { background: var(--warning-light); }
.alert-icon.is-success { background: var(--success-light); }

.alert-text {
  flex: 1;
  font-size: 13px;
}

.alert-time {
  color: #94a3b8;
  font-size: 12px;
}

@media (max-width: 1100px) {
  .resource-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .resource-grid {
    grid-template-columns: 1fr;
  }
  .health-banner {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
