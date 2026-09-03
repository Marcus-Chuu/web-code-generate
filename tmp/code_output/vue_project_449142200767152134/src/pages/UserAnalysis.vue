<script setup>
import { ref } from 'vue'
import LineChart from '@/components/LineChart.vue'
import BarChart from '@/components/BarChart.vue'
import DonutChart from '@/components/DonutChart.vue'
import { userTrend, userTags, deviceData } from '@/data/mock'

const retentionWeeks = ref(['首周', '第2周', '第3周', '第4周', '第5周', '第6周'])
const retentionRates = ref([62, 45, 36, 29, 24, 19])

const tags = ref(userTags)
const devices = ref(deviceData)

const userSeries = [
  { key: 'newUsers', label: '新增用户', color: '#22c55e' },
  { key: 'active', label: '活跃用户', color: '#4f6ef7' }
]

const cohortRows = [
  { cohort: '2024-02', users: 420, w1: 62, w2: 45, w3: 38, w4: 31, w5: 26 },
  { cohort: '2024-03', users: 680, w1: 65, w2: 48, w3: 39, w4: 33, w5: 27 },
  { cohort: '2024-04', users: 560, w1: 60, w2: 44, w3: 35, w4: 28, w5: 23 },
  { cohort: '2024-05', users: 930, w1: 66, w2: 49, w3: 40, w4: 33, w5: 28 },
  { cohort: '2024-06', users: 1180, w1: 63, w2: 47, w3: 38, w4: 30, w5: 24 },
  { cohort: '2024-07', users: 1430, w1: 68, w2: 51, w3: 41, w4: 33, w5: 27 }
]

function cellClass(v) {
  if (v >= 60) return 'cell-high'
  if (v >= 45) return 'cell-mid'
  if (v >= 35) return 'cell-low'
  return 'cell-min'
}

const barHeight = (v) => (v / 1430) * 100 + '%'
</script>

<template>
  <div class="user-analysis">
    <div class="page-header">
      <div>
        <h1>用户分析</h1>
        <p>洞察用户增长、留存表现与画像特征</p>
      </div>
      <div class="cohort-switch">
        <button class="btn btn-ghost btn-sm">导出用户报表</button>
        <button class="btn btn-primary btn-sm">新建用户分群</button>
      </div>
    </div>

    <!-- 用户趋势 -->
    <section class="card">
      <div class="card-header">
        <div>
          <h3 class="card-title">用户增长趋势</h3>
          <p class="card-sub">近 6 个月新增用户与月活跃用户对比</p>
        </div>
        <div class="legend-hint">
          <span class="legend-dot" style="background:#4f6ef7"></span>月活跃
          <span class="legend-dot" style="background:#22c55e;margin-left:10px"></span>新增
        </div>
      </div>
      <LineChart :data="userTrend" x-key="month" :series="userSeries" :height="300" />
    </section>

    <!-- 用户画像三卡片 -->
    <section class="profile-grid">
      <div class="card profile-card">
        <div class="card-header">
          <h3 class="card-title">用户画像</h3>
        </div>
        <DonutChart :data="tags" :size="150" center-label="总用户" />
      </div>

      <div class="card profile-card">
        <div class="card-header">
          <h3 class="card-title">终端设备</h3>
        </div>
        <DonutChart :data="devices" :size="150" center-label="终端数" />
      </div>

      <div class="card profile-card">
        <div class="card-header">
          <h3 class="card-title">近 30 日活跃度</h3>
        </div>
        <div class="activity-summary">
          <div class="activity-row">
            <span>日活跃用户</span>
            <b>9,216</b>
            <em class="up">▲ 12.4%</em>
          </div>
          <div class="activity-row">
            <span>周活跃用户</span>
            <b>11,084</b>
            <em class="up">▲ 8.1%</em>
          </div>
          <div class="activity-row">
            <span>月活跃用户</span>
            <b>12,846</b>
            <em class="up">▲ 9.6%</em>
          </div>
          <div class="activity-row">
            <span>平均使用时长</span>
            <b>18.6 分钟</b>
            <em class="up">▲ 2.3 分钟</em>
          </div>
          <div class="activity-row">
            <span>次日留存率</span>
            <b>68.2%</b>
            <em class="down">▼ 1.4%</em>
          </div>
        </div>
      </div>
    </section>

    <!-- 新增用户柱状图 + 留存柱状图 -->
    <section class="section-grid two-col">
      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">每月新增用户</h3>
            <p class="card-sub">2 月 - 7 月新注册用户数（人）</p>
          </div>
        </div>
        <BarChart :data="userTrend" x-key="month" y-key="newUsers" color="#22c55e" />
      </div>

      <div class="card">
        <div class="card-header">
          <div>
            <h3 class="card-title">新用户留存率</h3>
            <p class="card-sub">2024-07 新用户各周留存表现</p>
          </div>
        </div>
        <div class="retention-bars">
          <div v-for="(label, i) in retentionWeeks" :key="i" class="retention-item">
            <span class="retention-val">{{ retentionRates[i] }}%</span>
            <div class="bar retention-bar">
              <i :style="{ width: retentionRates[i] * 1.5 + '%', background: '#4f6ef7' }"></i>
            </div>
            <span class="retention-label">{{ label }}</span>
          </div>
        </div>
        <p class="retention-tip">* 首周留存 62%，处于行业优秀水平（基准 50%）</p>
      </div>
    </section>

    <!-- 留存明细表 -->
    <section class="card">
      <div class="card-header">
        <div>
          <h3 class="card-title">新用户留存明细</h3>
          <p class="card-sub">按月新增用户的周留存率（%）</p>
        </div>
        <span class="badge badge-primary">留存分析</span>
      </div>
      <div class="table-wrap">
        <table class="data-table cohort-table">
          <thead>
            <tr>
              <th>新增月份</th>
              <th>新增用户</th>
              <th v-for="w in ['首周', '第2周', '第3周', '第4周', '第5周']" :key="w">{{ w }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, i) in cohortRows" :key="i">
              <td class="cohort-name">{{ row.cohort }}</td>
              <td>{{ row.users }}</td>
              <td v-for="key in ['w1', 'w2', 'w3', 'w4', 'w5']" :key="key">
                <span class="retention-cell" :class="cellClass(row[key])">{{ row[key] }}%</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>

<style scoped>
.user-analysis {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.cohort-switch {
  display: flex;
  gap: 8px;
}

.legend-hint {
  font-size: 12px;
  color: var(--text-sub);
}

.legend-dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 3px;
  margin-right: 4px;
}

/* 用户画像 */
.profile-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 20px;
}

.profile-card {
  display: flex;
  flex-direction: column;
}

.profile-card :deep(.donut-wrap) {
  justify-content: center;
  flex: 1;
}

.activity-summary {
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex: 1;
}

.activity-row {
  display: flex;
  align-items: center;
  padding: 9px 0;
  border-bottom: 1px dashed #f1f5f9;
  font-size: 13px;
}

.activity-row:last-child {
  border-bottom: none;
}

.activity-row span {
  flex: 1;
  color: var(--text-sub);
}

.activity-row b {
  font-weight: 600;
  font-size: 14px;
}

.activity-row em {
  font-style: normal;
  font-size: 12px;
  margin-left: 12px;
  width: 84px;
  text-align: right;
}

em.up { color: #16a34a; }
em.down { color: #dc2626; }

/* 留存柱 */
.retention-bars {
  display: flex;
  align-items: flex-end;
  gap: 20px;
  height: 210px;
  padding: 0 8px;
  border-bottom: 1px solid var(--border);
}

.retention-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
  height: 100%;
}

.retention-val {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-main);
}

.retention-item .bar {
  width: 100%;
  background: #f1f5f9;
}

.retention-item .bar i {
  background: linear-gradient(90deg, #4f6ef7, #8aa2ff);
}

.retention-label {
  font-size: 12px;
  color: var(--text-sub);
  margin-bottom: 8px;
}

.retention-tip {
  margin-top: 14px;
  font-size: 12px;
  color: #94a3b8;
}

/* 留存表格 */
.cohort-name {
  font-weight: 600;
}

.retention-cell {
  display: inline-block;
  min-width: 56px;
  text-align: center;
  padding: 4px 10px;
  border-radius: 6px;
  font-weight: 500;
}

.cell-high { background: #16a34a; color: #fff; }
.cell-mid { background: #86efac; color: #14532d; }
.cell-low { background: #fef3c7; color: #92400e; }
.cell-min { background: #f1f5f9; color: var(--text-sub); }

@media (max-width: 1100px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .cohort-switch {
    width: 100%;
  }
  .retention-bars {
    gap: 8px;
  }
}
</style>
