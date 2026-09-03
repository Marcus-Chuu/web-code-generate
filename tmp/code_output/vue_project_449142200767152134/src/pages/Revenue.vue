<script setup>
import { ref } from 'vue'
import LineChart from '@/components/LineChart.vue'
import BarChart from '@/components/BarChart.vue'
import { revenueBars, arpuTrend, billingRecords, invoices } from '@/data/mock'

const statusText = {
  success: '已入账',
  warning: '待确认',
  danger: '已退款'
}

const invoiceStatusText = {
  success: '已开票',
  warning: '待审核',
  danger: '已驳回'
}

const summary = [
  { label: '本月营收', value: '¥96.8 万', delta: '+28.4%', trend: 'up', color: '#4f6ef7' },
  { label: '本月回款', value: '¥88.2 万', delta: '+21.6%', trend: 'up', color: '#22c55e' },
  { label: '年度累计营收', value: '¥686.4 万', delta: '+32.1%', trend: 'up', color: '#f59e0b' },
  { label: '应收账款', value: '¥15.6 万', delta: '-12.3%', trend: 'down', color: '#a78bfa' }
]

const revSeries = [{ key: 'revenue', label: '营收（万元）', color: '#4f6ef7' }]
const arpuSeries = [{ key: 'arpu', label: 'ARPU（元/户）', color: '#f59e0b' }]

const range = ref('近 12 个月')
const exportMsg = ref('')

function exportReport() {
  exportMsg.value = '报表导出任务已创建，稍后将发送至您的邮箱（report@yunxi-data.com）'
  setTimeout(() => (exportMsg.value = ''), 4000)
}
</script>

<template>
  <div class="revenue">
    <div class="page-header">
      <div>
        <h1>营收报表</h1>
        <p>多维度的营收统计、账单流水与发票管理</p>
      </div>
      <div class="header-actions">
        <div class="segmented">
          <span class="seg-item active">{{ range }}</span>
          <span class="seg-item">本季度</span>
          <span class="seg-item">今年</span>
        </div>
        <button class="btn btn-primary" @click="exportReport">导出报表</button>
      </div>
    </div>

    <p v-if="exportMsg" class="export-tip">✅ {{ exportMsg }}</p>

    <!-- 营收概览 -->
    <section class="rev-summary">
      <div class="rev-item" v-for="(s, i) in summary" :key="i">
        <div class="rev-icon" :style="{ background: s.color + '1a', color: s.color }">
          {{ ['💰', '🏦', '📈', '📋'][i] }}
        </div>
        <div class="rev-info">
          <p>{{ s.label }}</p>
          <div class="rev-line">
            <b>{{ s.value }}</b>
            <em :class="s.trend === 'up' ? 'up' : 'down'">
              {{ s.trend === 'up' ? '▲' : '▼' }} {{ s.delta }}
            </em>
          </div>
        </div>
      </div>
    </section>

    <!-- 营收图 -->
    <section class="section-grid two-col">
      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">月度营收</h3>
            <p class="card-sub">近 12 个月实际确认营收（万元）</p>
          </div>
          <span class="badge badge-primary">总计 ¥686 万</span>
        </div>
        <LineChart :data="revenueBars" x-key="month" :series="revSeries" :height="280" />
      </div>

      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">ARPU 趋势</h3>
            <p class="card-sub">平均每用户月度收入（元）</p>
          </div>
        </div>
        <BarChart :data="arpuTrend" x-key="month" y-key="arpu" color="#f59e0b" />
      </div>
    </section>

    <!-- 账单流水 -->
    <section class="card">
      <div class="card-header">
        <div>
          <h3 class="card-title">账单流水</h3>
          <p class="card-sub">2024 年 7 月资金明细（元）</p>
        </div>
        <button class="btn btn-text">查看全部 →</button>
      </div>
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>账单编号</th>
              <th>类型</th>
              <th>交易时间</th>
              <th>金额</th>
              <th>支付方式</th>
              <th>状态</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(b, i) in billingRecords" :key="i">
              <td class="bill-id">{{ b.id }}</td>
              <td>{{ b.type }}</td>
              <td class="time">{{ b.time }}</td>
              <td :class="b.amount < 0 ? 'amount-neg' : 'amount-pos'">
                {{ b.amount < 0 ? '-' : '+' }}¥{{ Math.abs(b.amount).toLocaleString() }}
              </td>
              <td>{{ b.method }}</td>
              <td><span class="badge" :class="'badge-' + b.statusType">{{ statusText[b.statusType] }}</span></td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <!-- 发票管理 -->
    <section class="card">
      <div class="card-header">
        <div>
          <h3 class="card-title">发票管理</h3>
          <p class="card-sub">近期开票申请记录</p>
        </div>
        <button class="btn btn-ghost btn-sm">申请开票</button>
      </div>
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>发票编号</th>
              <th>开票内容</th>
              <th>金额（元）</th>
              <th>申请日期</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(inv, i) in invoices" :key="i">
              <td class="bill-id">{{ inv.id }}</td>
              <td>{{ inv.title }}</td>
              <td class="amount-pos">¥{{ inv.amount.toLocaleString() }}</td>
              <td class="time">{{ inv.date }}</td>
              <td><span class="badge" :class="'badge-' + inv.statusType">{{ invoiceStatusText[inv.statusType] }}</span></td>
              <td>
                <button v-if="inv.statusType === 'success'" class="btn btn-text btn-sm">下载</button>
                <span v-else class="time">—</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>

<style scoped>
.revenue {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.segmented {
  display: flex;
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 8px;
  overflow: hidden;
}

.seg-item {
  padding: 7px 14px;
  font-size: 13px;
  color: var(--text-sub);
  cursor: pointer;
  border-right: 1px solid var(--border);
}

.seg-item:last-child {
  border-right: none;
}

.seg-item.active {
  background: var(--primary-light);
  color: var(--primary);
  font-weight: 500;
}

.export-tip {
  background: var(--success-light);
  color: #16a34a;
  border-radius: 8px;
  padding: 10px 14px;
  font-size: 13px;
}

/* 营收概览 */
.rev-summary {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.rev-item {
  background: #fff;
  border-radius: 12px;
  box-shadow: var(--card-shadow);
  padding: 18px;
  display: flex;
  gap: 14px;
  align-items: center;
}

.rev-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.rev-info p {
  font-size: 12px;
  color: var(--text-sub);
}

.rev-line {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 2px;
}

.rev-line b {
  font-size: 18px;
  font-weight: 700;
}

.rev-line em {
  font-style: normal;
  font-size: 12px;
}

em.up { color: #16a34a; }
em.down { color: #dc2626; }

/* 表格 */
.bill-id {
  color: var(--primary);
  font-weight: 500;
}

.amount-pos {
  font-weight: 600;
  color: #16a34a;
}

.amount-neg {
  font-weight: 600;
  color: #dc2626;
}

.time {
  color: var(--text-sub);
}

@media (max-width: 1100px) {
  .rev-summary {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .rev-summary {
    grid-template-columns: 1fr;
  }
  .header-actions {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
