<script setup>
import { ref } from 'vue'
import TrendChart from '@/components/TrendChart.vue'
import DonutChart from '@/components/DonutChart.vue'
import { reportList, metricTrend, incomeData } from '@/utils/mock'

const blocks = ref([
  { id: 1, title: '访问趋势报表', desc: '来源：站点流量埋点', meta: '更新于 10 分钟前', color: '#4f6bf6', type: 'line' },
  { id: 2, title: '收入构成报表', desc: '来源：交易订单数据', meta: '更新于 1 小时前', color: '#22c1a3', type: 'donut' },
  { id: 3, title: '渠道转化日报', desc: '来源：广告投放系统', meta: '更新于 今天 09:00', color: '#f5a623', type: 'table' },
  { id: 4, title: '留存分析周报', desc: '来源：用户行为数据', meta: '更新于 昨天 18:30', color: '#7c5cf0', type: 'line' }
])
const metricTabs = ['访问量', '转化率', '客单价']
const activeMetric = ref('访问量')
const openId = ref(1)
const toggled = ref(false)
</script>

<template>
  <div class="page">
    <div class="page-head">
      <div>
        <h2>自定义报表</h2>
        <p>灵活组合指标维度，创建专属数据看板</p>
      </div>
      <div class="actions">
        <button class="btn ghost">模板市场</button>
        <button class="btn primary">+ 新建报表</button>
      </div>
    </div>

    <!-- 指标速览 -->
    <section class="cards">
      <div v-for="m in reportList" :key="m.title" class="card metric">
        <span class="m-icon" :style="{ background: m.bg, color: m.color }"><i :class="'icon ' + m.icon"></i></span>
        <div>
          <b>{{ m.title }}</b>
          <p>{{ m.unit }}</p>
        </div>
        <div class="right">
          <span class="m-num">{{ m.value }}</span>
          <span class="m-diff" :class="m.up ? 'up' : 'down'">{{ m.diff }}</span>
        </div>
      </div>
    </section>

    <!-- 报表卡片 -->
    <section class="report-grid">
      <div v-for="b in blocks" :key="b.id" class="card report" :class="{ wide: b.id === 1 }">
        <div class="rep-head">
          <span class="rep-dot" :style="{ background: b.color }"></span>
          <div class="rep-title">
            <b>{{ b.title }}</b>
            <p>{{ b.desc }} · {{ b.meta }}</p>
          </div>
          <button class="more">⋯</button>
        </div>

        <div class="rep-body" v-if="b.type === 'line' && b.id === 1">
          <TrendChart :data="metricTrend" />
        </div>
        <div class="rep-body donut" v-else-if="b.type === 'donut' && b.id === 2">
          <DonutChart :data="incomeData" />
        </div>
        <div class="rep-body table" v-else>
          <table>
            <thead>
              <tr><th>渠道</th><th>访客</th><th>转化</th><th>金额</th><th>趋势</th></tr>
            </thead>
            <tbody>
              <tr v-for="r in reportList" :key="r.title">
                <td>{{ r.title }}</td>
                <td>8,{{ 1200 + r.value * 7 }}</td>
                <td>¥{{ (r.value * 86).toLocaleString() }}</td>
                <td><b>{{ r.unit }}</b></td>
                <td><span class="trend-up">↑ 12%</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </section>

    <!-- 报表管理 -->
    <section class="card manage">
      <div class="card-head">
        <div>
          <h3>报表列表</h3>
          <p>管理已创建的报表与定时任务</p>
        </div>
        <button class="more">管理</button>
      </div>
      <div class="m-list">
        <div v-for="(r, i) in blocks" :key="r.id" class="m-row">
          <span class="m-no">0{{ i + 1 }}</span>
          <div class="m-info">
            <b>{{ r.title }}</b>
            <span>最近运行：{{ r.meta }}</span>
          </div>
          <div class="m-chips">
            <span class="chip">每日推送</span>
            <span class="chip dim">邮件</span>
          </div>
          <button class="m-btn">编辑</button>
          <button class="m-btn danger">删除</button>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.page { display: flex; flex-direction: column; gap: 20px; }
.page-head { display: flex; justify-content: space-between; align-items: center; gap: 12px; }
.page-head h2 { font-size: 22px; }
.page-head p { color: var(--muted); font-size: 13px; margin-top: 4px; }
.actions { display: flex; gap: 10px; }
.btn { display: inline-flex; gap: 6px; align-items: center; padding: 10px 17px; border-radius: 12px; font-size: 14px; font-weight: 600; cursor: pointer; }
.btn.ghost { background: #fff; border: 1px solid var(--line); color: var(--text); }
.btn.ghost:hover { border-color: #cfd6ff; color: var(--primary); }
.btn.primary { background: var(--primary); color: #fff; box-shadow: 0 6px 14px rgba(79, 107, 246, 0.3); }
.btn.primary:hover { background: #3e58e8; }

.cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 18px; }
.metric { display: flex; align-items: center; gap: 13px; }
.m-icon { width: 46px; height: 46px; border-radius: 13px; display: grid; place-items: center; font-size: 21px; flex: none; }
.metric b { display: block; font-size: 14px; }
.metric p { font-size: 12px; color: #9aa3b5; margin-top: 2px; }
.right { margin-left: auto; text-align: right; }
.m-num { display: block; font-weight: 800; font-size: 19px; }
.m-diff { font-size: 11px; font-weight: 700; }
.m-diff.up { color: #0fa97a; }
.m-diff.down { color: #e04a5f; }

.report-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 18px; }
.report.wide { grid-column: span 2; }
.rep-head { display: flex; align-items: center; gap: 11px; margin-bottom: 13px; }
.rep-dot { width: 10px; height: 10px; border-radius: 4px; flex: none; }
.rep-title b { font-size: 15px; }
.rep-title p { font-size: 12px; color: #9aa3b5; margin-top: 2px; }
.more { margin-left: auto; color: var(--muted); font-size: 18px; padding: 0 4px; }
.rep-body table { width: 100%; border-collapse: collapse; }
.rep-body th { text-align: left; font-size: 12px; color: #9aa3b5; padding: 7px 6px; font-weight: 600; }
.rep-body td { font-size: 13px; padding: 8px 6px; border-top: 1px solid #f0f2f7; }
.trend-up { color: #0fa97a; font-weight: 700; }

.manage { overflow: hidden; }
.card-head { display: flex; justify-content: space-between; margin-bottom: 14px; }
.card-head h3 { font-size: 16px; }
.card-head p { font-size: 12px; color: #9aa3b5; margin-top: 3px; }
.more { font-size: 13px; color: var(--primary); font-weight: 600; }
.m-list { display: flex; flex-direction: column; }
.m-row { display: flex; align-items: center; gap: 14px; padding: 14px 4px; border-bottom: 1px solid #f0f2f7; }
.m-row:last-child { border-bottom: none; }
.m-no { width: 30px; height: 30px; flex: none; border-radius: 9px; background: #f0f2f8; display: grid; place-items: center; font-size: 13px; font-weight: 700; color: #7d8698; }
.m-info { flex: 1; min-width: 0; }
.m-info b { display: block; font-size: 14px; }
.m-info span { font-size: 12px; color: #a3acbd; }
.m-chips { display: flex; gap: 6px; }
.chip { font-size: 11px; padding: 3px 9px; border-radius: 20px; background: rgba(79, 107, 246, 0.1); color: var(--primary); font-weight: 600; }
.chip.dim { background: #f0f2f8; color: var(--muted); }
.m-btn { font-size: 13px; color: var(--primary); font-weight: 600; padding: 4px 8px; }
.m-btn:hover { text-decoration: underline; }
.m-btn.danger { color: #e04a5f; }

@media (max-width: 1200px) { .cards { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 900px) { .report-grid { grid-template-columns: 1fr; } .report.wide { grid-column: span 1; } }
@media (max-width: 640px) {
  .page-head { flex-direction: column; align-items: flex-start; }
  .cards { grid-template-columns: 1fr; }
  .m-chips { display: none; }
}
</style>
