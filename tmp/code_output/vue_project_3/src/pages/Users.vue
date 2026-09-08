<script setup>
import { ref } from 'vue'
import DonutChart from '@/components/DonutChart.vue'
import TrendChart from '@/components/TrendChart.vue'
import { userCards, userTrend, gender, age, devices, userList } from '@/utils/mock'

const activeTab = ref('近 7 天')
const tabs = ['近 7 天', '近 30 天', '本季度']
const userIcons = ['icon-user', 'icon-user-add', 'icon-alert', 'icon-trend']
const statusClass = s => ({ 活跃: 'ok', 沉默: 'mute', 流失: 'loss' }[s])
</script>

<template>
  <div class="page">
    <div class="page-head">
      <div>
        <h2>用户分析</h2>
        <p>洞察用户构成与活跃表现，驱动精细化运营决策</p>
      </div>
      <div class="seg">
        <button v-for="t in tabs" :key="t" :class="{ on: activeTab === t }" @click="activeTab = t">{{ t }}</button>
      </div>
    </div>

    <section class="cards">
      <div v-for="(c, i) in userCards" :key="c.title" class="card stat">
        <div class="stat-head">
          <div class="stat-icon" :style="{ background: c.bg, color: c.color }"><i :class="'icon ' + userIcons[i]"></i></div>
          <span class="tag" :class="c.up ? 'up' : 'down'">{{ c.diff }}</span>
        </div>
        <b class="stat-num">{{ c.value }}</b>
        <div class="stat-title">{{ c.title }}</div>
        <div class="mini-progress"><i :style="{ width: c.progress + '%', background: c.color }"></i></div>
      </div>
    </section>

    <section class="row">
      <div class="card grow">
        <div class="card-head">
          <div>
            <h3>用户活跃趋势</h3>
            <p>日活用户与新增用户变化</p>
          </div>
        </div>
        <TrendChart :data="userTrend" />
      </div>
      <div class="card">
        <div class="card-head">
          <div>
            <h3>年龄分布</h3>
            <p>用户年龄构成占比</p>
          </div>
        </div>
        <DonutChart :data="age" />
      </div>
    </section>

    <section class="row reverse">
      <div class="card">
        <div class="card-head">
          <div>
            <h3>性别比例</h3>
            <p>注册用户性别分布</p>
          </div>
        </div>
        <div class="gender-wrap">
          <div class="gender-bar">
            <i class="male" :style="{ width: gender[0].value + '%' }">{{ gender[0].value }}%</i>
            <i class="female" :style="{ width: gender[1].value + '%' }">{{ gender[1].value }}%</i>
          </div>
          <div class="gender-legend">
            <span><i class="dot male"></i>男性 {{ gender[0].value }}%</span>
            <span><i class="dot female"></i>女性 {{ gender[1].value }}%</span>
          </div>
        </div>
      </div>
      <div class="card grow">
        <div class="card-head">
          <div>
            <h3>设备分布</h3>
            <p>用户访问终端占比</p>
          </div>
        </div>
        <div class="device-grid">
          <div v-for="d in devices" :key="d.name" class="device">
            <i :class="'icon ' + d.icon" :style="{ background: d.bg, color: d.color }"></i>
            <b>{{ d.name }}</b>
            <span>{{ d.percent }}% · {{ d.value.toLocaleString() }} 人</span>
          </div>
        </div>
      </div>
    </section>

    <section class="card table-card">
      <div class="card-head">
        <div>
          <h3>用户明细</h3>
          <p>最近注册用户概览</p>
        </div>
        <div class="toolbar">
          <input class="search" placeholder="搜索姓名 / ID" />
          <button class="more">筛选</button>
        </div>
      </div>
      <div class="table-scroll">
        <table>
          <thead>
            <tr>
              <th>用户</th><th>注册时间</th><th>活跃天数</th><th>消费金额</th><th>状态</th><th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="u in userList" :key="u.id">
              <td>
                <div class="u-cell">
                  <img :src="u.avatar" :alt="u.name" />
                  <div><b>{{ u.name }}</b><span>ID: {{ u.id }}</span></div>
                </div>
              </td>
              <td>{{ u.time }}</td>
              <td>{{ u.days }} 天</td>
              <td><b>¥{{ u.amount.toLocaleString() }}</b></td>
              <td><span class="badge" :class="statusClass(u.status)">{{ u.status }}</span></td>
              <td><button class="ope">详情</button></td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>

<style scoped>
.page { display: flex; flex-direction: column; gap: 20px; }
.page-head { display: flex; align-items: center; justify-content: space-between; gap: 14px; }
.page-head h2 { font-size: 22px; }
.page-head p { color: var(--muted); font-size: 13px; margin-top: 4px; }
.seg { display: flex; background: #fff; border: 1px solid var(--line); padding: 4px; border-radius: 12px; }
.seg button { padding: 7px 16px; border-radius: 9px; font-size: 13px; color: var(--muted); font-weight: 500; }
.seg button.on { background: var(--primary); color: #fff; box-shadow: 0 4px 10px rgba(79, 107, 246, 0.3); }

.cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 18px; }
.stat-head { display: flex; justify-content: space-between; align-items: center; }
.stat-icon { width: 44px; height: 44px; border-radius: 13px; display: grid; place-items: center; font-size: 20px; }
.tag { font-size: 12px; font-weight: 700; padding: 4px 9px; border-radius: 20px; }
.tag.up { color: #0fa97a; background: rgba(34, 193, 163, 0.12); }
.tag.down { color: #e04a5f; background: rgba(224, 74, 95, 0.1); }
.stat-num { display: block; font-size: 30px; font-weight: 800; margin: 13px 0 4px; }
.stat-title { color: var(--muted); font-size: 14px; }
.mini-progress { height: 5px; background: #eef1f6; border-radius: 6px; margin-top: 13px; overflow: hidden; }
.mini-progress i { display: block; height: 100%; border-radius: 6px; }

.row { display: grid; grid-template-columns: 1fr 340px; gap: 18px; align-items: start; }
.row.reverse { grid-template-columns: 340px 1fr; }
.card-head { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 14px; }
.card-head h3 { font-size: 16px; }
.card-head p { font-size: 12px; color: #9aa3b5; margin-top: 3px; }
.grow { min-width: 0; }

.gender-bar { display: flex; height: 18px; border-radius: 20px; overflow: hidden; }
.gender-bar i { display: grid; place-items: center; font-size: 11px; font-weight: 700; color: #fff; font-style: normal; }
.male { background: linear-gradient(90deg, #4f6bf6, #6a80ff); }
.female { background: linear-gradient(90deg, #f06aa0, #ff8fb8); }
.gender-legend { display: flex; gap: 20px; justify-content: center; margin-top: 18px; font-size: 13px; color: var(--muted); }
.gender-legend span { display: inline-flex; align-items: center; gap: 7px; }
.gender-legend .dot { width: 10px; height: 10px; border-radius: 4px; }
.dot.male { background: #4f6bf6; }
.dot.female { background: #f06aa0; }

.device-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px; }
.device { background: #fafbfe; border: 1px solid #eef1f6; border-radius: 14px; padding: 18px 10px; text-align: center; }
.device i { display: grid; place-items: center; width: 46px; height: 46px; border-radius: 14px; font-size: 22px; margin: 0 auto 10px; }
.device b { display: block; font-size: 14px; }
.device span { display: block; font-size: 12px; color: var(--muted); margin-top: 5px; }

.table-card { overflow: hidden; }
.toolbar { display: flex; gap: 8px; }
.search { height: 34px; padding: 0 12px; border: 1px solid var(--line); border-radius: 9px; font-size: 13px; width: 200px; background: #fafbfe; }
.more { font-size: 13px; color: var(--primary); font-weight: 600; }
.table-scroll { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; min-width: 640px; }
th { text-align: left; font-size: 12px; color: #9aa3b5; font-weight: 600; padding: 10px 14px; background: #f8f9fc; white-space: nowrap; }
th:first-child { border-radius: 10px 0 0 10px; }
th:last-child { border-radius: 0 10px 10px 0; }
td { padding: 13px 14px; font-size: 13px; border-bottom: 1px solid #f0f2f7; color: #434f66; white-space: nowrap; }
tbody tr:last-child td { border-bottom: none; }
tbody tr:hover td { background: #f8f9fd; }
.u-cell { display: flex; align-items: center; gap: 11px; }
.u-cell img { width: 36px; height: 36px; border-radius: 10px; object-fit: cover; }
.u-cell b { display: block; font-size: 14px; color: var(--text); }
.u-cell span { font-size: 11px; color: #a3acbd; }
.badge { font-size: 12px; padding: 4px 10px; border-radius: 20px; font-weight: 600; }
.badge.ok { color: #0fa97a; background: rgba(34, 193, 163, 0.12); }
.badge.mute { color: #f5a623; background: rgba(245, 166, 35, 0.13); }
.badge.loss { color: #e04a5f; background: rgba(224, 74, 95, 0.1); }
.ope { font-size: 13px; color: var(--primary); font-weight: 600; }
.ope:hover { text-decoration: underline; }

@media (max-width: 1200px) {
  .cards { grid-template-columns: repeat(2, 1fr); }
  .row, .row.reverse { grid-template-columns: 1fr; }
}
@media (max-width: 640px) {
  .page-head { flex-direction: column; align-items: flex-start; }
  .cards { grid-template-columns: 1fr; }
  .device-grid { grid-template-columns: repeat(3, 1fr); }
  .search { width: 120px; }
}
</style>
