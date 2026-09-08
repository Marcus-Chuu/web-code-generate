<script setup>
import { ref } from 'vue'
import TrendChart from '@/components/TrendChart.vue'
import { eventCards, eventTrend, eventList } from '@/utils/mock'

const filters = ['全部', '页面浏览', '按钮点击', '表单提交', '登录注册']
const active = ref('全部')
const evtIcons = ['icon-event', 'icon-user-add', 'icon-cart', 'icon-info']
const typeColor = t => ({ 页面浏览: '#4f6bf6', 按钮点击: '#22c1a3', 表单提交: '#f5a623', 登录注册: '#7c5cf0', 下载: '#e04a5f' }[t] || '#4f6bf6')
</script>

<template>
  <div class="page">
    <div class="page-head">
      <div>
        <h2>事件管理</h2>
        <p>定义并管理产品内的用户行为事件</p>
      </div>
      <div class="actions">
        <button class="btn ghost">导入埋点</button>
        <button class="btn primary">+ 新建事件</button>
      </div>
    </div>

    <section class="cards">
      <div v-for="(c, i) in eventCards" :key="c.title" class="card stat">
        <div class="stat-head">
          <div class="stat-icon" :style="{ background: c.bg, color: c.color }"><i :class="'icon ' + evtIcons[i]"></i></div>
          <span class="tag" :class="c.up ? 'up' : 'down'">{{ c.diff }}</span>
        </div>
        <b class="stat-num">{{ c.value }}</b>
        <div class="stat-title">{{ c.title }}</div>
        <div class="stat-sub">共 {{ c.total }} 个事件</div>
      </div>
    </section>

    <section class="row">
      <div class="card grow">
        <div class="card-head">
          <div>
            <h3>事件触发趋势</h3>
            <p>全站核心事件日触发量</p>
          </div>
        </div>
        <TrendChart :data="eventTrend" />
      </div>
      <div class="card">
        <div class="card-head">
          <div><h3>事件类别占比</h3><p>各类别事件触发分布</p></div>
        </div>
        <ul class="cat-list">
          <li v-for="c in eventCards" :key="c.title">
            <i class="dot" :style="{ background: c.color }"></i>
            <span>{{ c.title }}</span>
            <b>{{ c.total }}</b>
            <em>{{ Math.round(c.total / 260) }}%</em>
          </li>
        </ul>
      </div>
    </section>

    <section class="card table-card">
      <div class="card-head">
        <div><h3>事件列表</h3><p>共 {{ eventList.length }} 个已启用事件</p></div>
        <input class="search" placeholder="搜索事件名称 / ID" />
      </div>
      <div class="filters">
        <button v-for="f in filters" :key="f" class="f" :class="{ on: active === f }" @click="active = f">{{ f }}</button>
      </div>
      <div class="table-scroll">
        <table>
          <thead>
            <tr><th>事件名称</th><th>事件 ID</th><th>类型</th><th>触发量</th><th>触发用户</th><th>状态</th><th>操作</th></tr>
          </thead>
          <tbody>
            <tr v-for="e in eventList" :key="e.id">
              <td><b>{{ e.name }}</b></td>
              <td class="mono">{{ e.id }}</td>
              <td><span class="type-badge" :style="{ background: typeColor(e.type) + '1f', color: typeColor(e.type) }">{{ e.type }}</span></td>
              <td>{{ e.count.toLocaleString() }}</td>
              <td>{{ e.users.toLocaleString() }} 人</td>
              <td><span class="badge" :class="e.status === '启用' ? 'ok' : 'off'">{{ e.status }}</span></td>
              <td><div class="ops"><button class="ope">编辑</button><button class="ope dim">配置</button></div></td>
            </tr>
          </tbody>
        </table>
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
.btn { padding: 10px 17px; border-radius: 12px; font-size: 14px; font-weight: 600; cursor: pointer; }
.btn.ghost { background: #fff; border: 1px solid var(--line); color: var(--text); }
.btn.ghost:hover { border-color: #cfd6ff; color: var(--primary); }
.btn.primary { background: var(--primary); color: #fff; box-shadow: 0 6px 14px rgba(79, 107, 246, 0.3); }
.btn.primary:hover { background: #3e58e8; }

.cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 18px; }
.stat-head { display: flex; justify-content: space-between; align-items: center; }
.stat-icon { width: 44px; height: 44px; border-radius: 13px; display: grid; place-items: center; font-size: 20px; }
.tag { font-size: 12px; font-weight: 700; padding: 4px 9px; border-radius: 20px; }
.tag.up { color: #0fa97a; background: rgba(34, 193, 163, 0.12); }
.tag.down { color: #e04a5f; background: rgba(224, 74, 95, 0.1); }
.stat-num { display: block; font-size: 30px; font-weight: 800; margin: 13px 0 3px; }
.stat-title { font-size: 14px; color: var(--muted); }
.stat-sub { font-size: 11px; color: #a3acbd; margin-top: 4px; }

.row { display: grid; grid-template-columns: 1fr 320px; gap: 18px; align-items: start; }
.card-head { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 14px; }
.card-head h3 { font-size: 16px; }
.card-head p { font-size: 12px; color: #9aa3b5; margin-top: 3px; }
.grow { min-width: 0; }
.cat-list { display: flex; flex-direction: column; gap: 12px; }
.cat-list li { display: flex; align-items: center; gap: 10px; font-size: 13px; }
.cat-list .dot { width: 10px; height: 10px; border-radius: 4px; }
.cat-list span { flex: 1; color: var(--muted); }
.cat-list b { font-weight: 600; }
.cat-list em { font-style: normal; font-size: 12px; color: #a3acbd; width: 34px; text-align: right; }

.table-card { overflow: hidden; }
.filters { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 12px; }
.f { font-size: 12px; padding: 6px 14px; border-radius: 20px; background: #f3f5fa; color: var(--muted); font-weight: 500; }
.f.on { background: var(--primary); color: #fff; }
.search { height: 34px; padding: 0 12px; border: 1px solid var(--line); border-radius: 9px; font-size: 13px; width: 210px; }
.table-scroll { overflow-x: auto; }
table { width: 100%; border-collapse: collapse; min-width: 720px; }
th { text-align: left; font-size: 12px; color: #9aa3b5; font-weight: 600; padding: 10px 12px; background: #f8f9fc; white-space: nowrap; }
th:first-child { border-radius: 10px 0 0 10px; }
th:last-child { border-radius: 0 10px 10px 0; }
td { padding: 12px; font-size: 13px; border-bottom: 1px solid #f0f2f7; color: #434f66; white-space: nowrap; }
tbody tr:last-child td { border-bottom: none; }
tbody tr:hover td { background: #f8f9fd; }
.mono { font-family: ui-monospace, Menlo, Consolas, monospace; color: #7d8698; }
.type-badge { padding: 4px 10px; border-radius: 8px; font-size: 12px; font-weight: 600; }
.badge { font-size: 12px; padding: 4px 10px; border-radius: 20px; font-weight: 600; }
.badge.ok { color: #0fa97a; background: rgba(34, 193, 163, 0.12); }
.badge.off { color: #9aa3b5; background: #eef1f6; }
.ops { display: flex; gap: 8px; }
.ope { font-size: 13px; color: var(--primary); font-weight: 600; }
.ope.dim { color: #7d8698; }
.ope:hover { text-decoration: underline; }

@media (max-width: 1200px) { .cards { grid-template-columns: repeat(2, 1fr); } .row { grid-template-columns: 1fr; } }
@media (max-width: 640px) {
  .page-head { flex-direction: column; align-items: flex-start; }
  .cards { grid-template-columns: 1fr; }
  .search { width: 140px; }
}
</style>
