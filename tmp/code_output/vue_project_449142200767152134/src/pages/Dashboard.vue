<script setup>
import { ref } from 'vue'
import StatCard from '@/components/StatCard.vue'
import LineChart from '@/components/LineChart.vue'
import BarChart from '@/components/BarChart.vue'
import DonutChart from '@/components/DonutChart.vue'
import {
  revenueTrend,
  orderTrend,
  trafficSources,
  regionData,
  metrics,
  activities,
  recentOrders,
  productRank
} from '@/data/mock'

const revenueSeries = [
  { key: 'revenue', label: '营收', color: '#4f6ef7' },
  { key: 'profit', label: '利润', color: '#22c55e' }
]

const orderSeries = [
  { key: 'orders', label: '订单量', color: '#f59e0b' }
]

const statusTypeMap = {
  success: 'badge-success',
  warning: 'badge-warning',
  danger: 'badge-danger'
}

const statusTextMap = {
  success: '已支付',
  warning: '待支付',
  danger: '已退款'
}

const banner = ref(true)
</script>

<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <div v-if="banner" class="welcome-banner">
      <div class="welcome-text">
        <h1>早上好，林晓峰 👋</h1>
        <p>欢迎回到云析数据，今天共有 <b>8</b> 项待处理任务，<b>3</b> 个订阅即将到期。</p>
      </div>
      <div class="banner-right">
        <span class="banner-date">2024年7月19日 · 星期五</span>
        <button class="close-btn" @click="banner = false">✕</button>
      </div>
    </div>

    <!-- 核心指标 -->
    <section class="metrics-grid">
      <StatCard
        v-for="(m, i) in metrics"
        :key="i"
        :title="m.title"
        :value="m.value"
        :delta="m.delta"
        :trend="m.trend"
        :desc="m.desc"
        :icon="m.icon"
        :color="m.color"
      />
    </section>

    <!-- 营收趋势 -->
    <section class="panel-grid two-col">
      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">营收趋势</h3>
            <p class="card-sub">近 12 个月营收与利润对比（万元）</p>
          </div>
          <button class="ghost-btn">查看报表 →</button>
        </div>
        <LineChart :data="revenueTrend" x-key="month" :series="revenueSeries" :height="280" />
      </div>

      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">流量来源</h3>
            <p class="card-sub">渠道访问占比统计</p>
          </div>
          <button class="ghost-btn">详情 →</button>
        </div>
        <DonutChart :data="trafficSources" :size="170" center-label="总访问量" />
        <div class="source-list">
          <div class="source-row" v-for="(s, i) in trafficSources" :key="i">
            <span class="source-name">{{ s.name }}</span>
            <div class="source-bar"><i :style="{ width: s.value * 2 + '%', background: s.color }"></i></div>
            <span class="source-val">{{ s.value }}%</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 订单量 + 区域分布 -->
    <section class="panel-grid two-col">
      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">订单量趋势</h3>
            <p class="card-sub">近 7 日订单统计（单）</p>
          </div>
          <div class="mini-tags">
            <span class="mini-tag active">全部</span>
            <span class="mini-tag">已完成</span>
          </div>
        </div>
        <BarChart :data="orderTrend" x-key="day" y-key="orders" color="#f59e0b" />
      </div>

      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">区域订单分布</h3>
            <p class="card-sub">全国各区域订单占比</p>
          </div>
          <button class="ghost-btn">地图视图 →</button>
        </div>
        <DonutChart :data="regionData" :size="150" center-label="总订单" />
        <div class="region-grid">
          <div class="region-item" v-for="(r, i) in regionData" :key="i">
            <span class="dot" :style="{ background: r.color }"></span>
            <span>{{ r.name }}</span>
            <b>{{ r.value }}%</b>
          </div>
        </div>
      </div>
    </section>

    <!-- 运营动态 + 产品排行 -->
    <section class="panel-grid two-col">
      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">运营动态</h3>
            <p class="card-sub">平台实时运营事件</p>
          </div>
          <span class="live-dot">● 实时</span>
        </div>
        <ul class="activity-list">
          <li v-for="(a, i) in activities" :key="i" class="activity-item">
            <span class="activity-icon" :style="{ background: a.color + '1a' }">{{ a.icon }}</span>
            <div class="activity-body">
              <p>{{ a.text }}</p>
              <span class="activity-time">{{ a.time }}</span>
            </div>
          </li>
        </ul>
      </div>

      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">产品销量排行</h3>
            <p class="card-sub">按销售额降序 TOP 5</p>
          </div>
          <button class="ghost-btn">更多 →</button>
        </div>
        <ul class="rank-list">
          <li v-for="(p, i) in productRank" :key="i" class="rank-item">
            <span class="rank-no" :class="{ 'rank-top': i < 3 }">{{ i + 1 }}</span>
            <div class="rank-main">
              <div class="rank-topline">
                <span class="rank-name">{{ p.name }}</span>
                <span class="rank-sales">{{ p.sales }} 单</span>
              </div>
              <div class="rank-bar"><i :style="{ width: p.ratio + '%' }"></i></div>
              <div class="rank-revenue">营收 {{ p.revenue }}</div>
            </div>
          </li>
        </ul>
      </div>
    </section>

    <!-- 最新订单 -->
    <section class="card">
      <div class="card-header">
        <div>
          <h3 class="card-title">最新订单</h3>
          <p class="card-sub">今日最新成交订单（前 6 条）</p>
        </div>
        <button class="primary-btn">查看全部订单</button>
      </div>
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>订单编号</th>
              <th>客户名称</th>
              <th>订阅产品</th>
              <th>订单金额</th>
              <th>支付状态</th>
              <th>下单时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(o, i) in recentOrders" :key="i">
              <td class="order-id">{{ o.id }}</td>
              <td>{{ o.customer }}</td>
              <td>{{ o.product }}</td>
              <td class="amount">¥{{ o.amount.toLocaleString() }}</td>
              <td><span class="badge" :class="statusTypeMap[o.statusType]">{{ statusTextMap[o.statusType] }}</span></td>
              <td class="time">{{ o.time }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(120deg, #4f6ef7, #7c5cfc);
  border-radius: 14px;
  padding: 22px 26px;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-text h1 {
  font-size: 20px;
  font-weight: 700;
}

.welcome-text p {
  margin-top: 6px;
  font-size: 13px;
  opacity: 0.9;
}

.welcome-text b {
  font-weight: 700;
}

.banner-right {
  display: flex;
  align-items: center;
  gap: 14px;
}

.banner-date {
  font-size: 12px;
  opacity: 0.9;
}

.close-btn {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  font-size: 12px;
  line-height: 1;
}

/* 指标网格 */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

/* 面板网格 */
.panel-grid {
  display: grid;
  gap: 20px;
}

.two-col {
  grid-template-columns: 1.4fr 1fr;
}

/* 通用按钮 */
.ghost-btn {
  border: none;
  background: transparent;
  color: var(--primary);
  font-size: 13px;
  font-weight: 500;
}

.ghost-btn:hover {
  color: #3b5bdb;
}

.primary-btn {
  border: none;
  background: var(--primary);
  color: #fff;
  font-size: 13px;
  font-weight: 500;
  padding: 8px 16px;
  border-radius: 8px;
}

.primary-btn:hover {
  background: #3b5bdb;
}

/* 流量来源 */
.source-list {
  margin-top: 18px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.source-row {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
}

.source-name {
  width: 64px;
  color: var(--text-sub);
  flex-shrink: 0;
}

.source-bar {
  flex: 1;
  height: 6px;
  border-radius: 999px;
  background: #f1f5f9;
  overflow: hidden;
}

.source-bar i {
  display: block;
  height: 100%;
  border-radius: 999px;
}

.source-val {
  width: 40px;
  text-align: right;
  font-weight: 600;
}

/* 迷你标签 */
.mini-tags {
  display: flex;
  gap: 6px;
}

.mini-tag {
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 999px;
  color: var(--text-sub);
  background: var(--bg-page);
}

.mini-tag.active {
  background: var(--primary-light);
  color: var(--primary);
  font-weight: 500;
}

/* 区域分布 */
.region-grid {
  margin-top: 18px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.region-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--text-sub);
}

.region-item .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.region-item b {
  margin-left: auto;
  color: var(--text-main);
}

/* 运营动态 */
.live-dot {
  font-size: 12px;
  color: #16a34a;
  font-weight: 600;
}

.activity-list {
  display: flex;
  flex-direction: column;
}

.activity-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f1f5f9;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.activity-body {
  flex: 1;
}

.activity-body p {
  font-size: 13px;
  color: var(--text-main);
}

.activity-time {
  font-size: 12px;
  color: #94a3b8;
}

/* 产品排行 */
.rank-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.rank-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.rank-no {
  width: 22px;
  height: 22px;
  border-radius: 6px;
  background: #f1f5f9;
  color: var(--text-sub);
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.rank-no.rank-top {
  background: var(--primary-light);
  color: var(--primary);
}

.rank-main {
  flex: 1;
}

.rank-topline {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

.rank-name {
  font-weight: 500;
}

.rank-sales {
  color: var(--text-sub);
}

.rank-bar {
  height: 6px;
  border-radius: 999px;
  background: #f1f5f9;
  overflow: hidden;
  margin-top: 6px;
}

.rank-bar i {
  display: block;
  height: 100%;
  border-radius: 999px;
  background: linear-gradient(90deg, #4f6ef7, #7c5cfc);
}

.rank-revenue {
  margin-top: 4px;
  font-size: 12px;
  color: #94a3b8;
}

/* 表格微调 */
.order-id {
  color: var(--primary);
  font-weight: 500;
}

.amount {
  font-weight: 600;
}

.time {
  color: var(--text-sub);
}

/* 响应式 */
@media (max-width: 1100px) {
  .metrics-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .two-col {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .metrics-grid {
    grid-template-columns: 1fr;
  }
  .welcome-banner {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  .region-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
