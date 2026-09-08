<script setup>
import { ref, computed } from 'vue'
import { alertCards, alertRules } from '@/utils/mock'

const seg = ['全部', '未处理', '已处理']
const active = ref('全部')
const list = ref([
  { id: 'ALT-20240621-001', title: '华东区首页跳出率异常升高', desc: '华东区首页跳出率较过去 7 天均值升高 42%，疑似活动页引导问题，建议核查落地页加载与视觉引导。', metric: '跳出率 68.4%', time: '今天 10:24', level: '高', levelCls: 'high', handled: false },
  { id: 'ALT-20240621-002', title: 'iOS 端支付成功率持续下降', desc: 'iOS 端微信支付成功率连续 3 天低于 85% 阈值，已影响整体交易转化，建议优先检查支付回调链路。', metric: '支付成功率 82.1%', time: '今天 09:12', level: '中', levelCls: 'mid', handled: false },
  { id: 'ALT-20240620-003', title: '「限时秒杀」事件触发量激增', desc: '秒杀活动事件触发量较昨日同时段激增 320%，已触发风控水位提醒，请确认是否存在流量异常。', metric: '触发量 128.6k', time: '昨天 22:05', level: '高', levelCls: 'high', handled: false },
  { id: 'ALT-20240620-004', title: '华北区新用户留存低于基准', desc: '华北区次日留存 31.2%，低于全体基准 38.9%，建议结合渠道质量分析调整投放策略。', metric: '次日留存 31.2%', time: '昨天 16:40', level: '中', levelCls: 'mid', handled: true },
  { id: 'ALT-20240619-005', title: '数据仓库同步延迟 12 分钟', desc: '实时数仓近端同步出现积压，延迟最高达 12 分钟，已自动扩容处理，当前延迟恢复至 2 分钟内。', metric: '同步延迟 12min', time: '06-19 08:30', level: '低', levelCls: 'low', handled: true }
])

const shown = computed(() => {
  if (active.value === '全部') return list.value
  if (active.value === '未处理') return list.value.filter(a => !a.handled)
  return list.value.filter(a => a.handled)
})

function mark(id) {
  const a = list.value.find(x => x.id === id)
  if (a) a.handled = !a.handled
}
</script>

<template>
  <div class="page">
    <div class="page-head">
      <div>
        <h2>智能告警</h2>
        <p>基于异常检测算法的自动化数据监控</p>
      </div>
      <button class="btn primary">+ 新建告警规则</button>
    </div>

    <section class="cards">
      <div v-for="c in alertCards" :key="c.title" class="card stat">
        <div class="stat-head">
          <div class="stat-icon" :style="{ background: c.bg, color: c.color }"><i :class="'icon ' + c.icon"></i></div>
        </div>
        <b class="stat-num" :style="{ color: c.color }">{{ c.value }}</b>
        <div class="stat-title">{{ c.title }}</div>
        <p class="stat-desc">{{ c.desc }}</p>
      </div>
    </section>

    <section class="card">
      <div class="card-head">
        <div><h3>告警记录</h3><p>最近 30 天内产生的告警事件</p></div>
        <div class="seg">
          <button v-for="s in seg" :key="s" :class="{ on: active === s }" @click="active = s">{{ s }}</button>
        </div>
      </div>

      <div class="alerts">
        <div v-for="a in shown" :key="a.id" class="alert" :class="{ done: a.handled }">
          <div class="a-left">
            <span class="lv" :class="a.levelCls">{{ a.level }}</span>
            <i class="dot" :class="a.levelCls"></i>
          </div>
          <div class="a-main">
            <div class="a-top">
              <b>{{ a.title }}</b>
              <span class="badge" :class="a.handled ? 'ok' : 'open'">{{ a.handled ? '已处理' : '未处理' }}</span>
            </div>
            <p>{{ a.desc }}</p>
            <div class="a-meta">
              <span class="metric-tag">{{ a.metric }}</span>
              <span class="a-id">ID: {{ a.id }}</span>
              <span class="a-time">{{ a.time }}</span>
            </div>
          </div>
          <div class="a-ops">
            <button class="ope primary" @click="mark(a.id)">{{ a.handled ? '标记未处理' : '标记已处理' }}</button>
            <button class="ope">查看详情</button>
          </div>
        </div>
        <p v-if="!shown.length" class="empty">暂无告警记录 🎉</p>
      </div>
    </section>

    <section class="card">
      <div class="card-head">
        <div><h3>告警规则</h3><p>自动检测策略配置</p></div>
        <button class="more">+ 添加</button>
      </div>
      <div class="rules">
        <div v-for="r in alertRules" :key="r.name" class="rule">
          <i class="icon" :class="r.icon" :style="{ background: r.bg, color: r.color }"></i>
          <div class="r-main">
            <b>{{ r.name }}</b>
            <p>{{ r.desc }}</p>
          </div>
          <div class="r-side">
            <span class="switch" :class="{ on: r.on }"><i></i></span>
            <span class="r-stat">近 7 日触发 {{ r.count }} 次</span>
          </div>
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
.btn.primary { padding: 10px 17px; border-radius: 12px; background: var(--primary); color: #fff; font-weight: 600; font-size: 14px; cursor: pointer; box-shadow: 0 6px 14px rgba(79, 107, 246, 0.3); }
.btn.primary:hover { background: #3e58e8; }

.cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 18px; }
.stat-icon { width: 44px; height: 44px; border-radius: 13px; display: grid; place-items: center; font-size: 20px; }
.stat-num { display: block; font-size: 32px; font-weight: 800; margin: 12px 0 2px; }
.stat-title { font-size: 15px; font-weight: 600; }
.stat-desc { font-size: 12px; color: #a3acbd; margin-top: 4px; }

.card-head { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 14px; }
.card-head h3 { font-size: 16px; }
.card-head p { font-size: 12px; color: #9aa3b5; margin-top: 3px; }
.seg { display: flex; background: #f0f2f8; padding: 3px; border-radius: 10px; }
.seg button { padding: 6px 14px; border-radius: 8px; font-size: 13px; color: var(--muted); }
.seg button.on { background: #fff; color: var(--primary); font-weight: 700; box-shadow: 0 2px 6px rgba(16, 22, 43, 0.08); }

.alerts { display: flex; flex-direction: column; gap: 13px; }
.alert { display: flex; gap: 14px; align-items: flex-start; padding: 16px; border: 1px solid #eef1f6; border-radius: 15px; background: #fafbfe; transition: all 0.2s; }
.alert.done { opacity: 0.62; background: #f7f8fb; }
.a-left { display: flex; flex-direction: column; align-items: center; gap: 8px; padding-top: 2px; }
.lv { font-size: 11px; font-weight: 700; padding: 3px 8px; border-radius: 6px; }
.lv.high { color: #e04a5f; background: rgba(224, 74, 95, 0.1); }
.lv.mid { color: #f5a623; background: rgba(245, 166, 35, 0.12); }
.lv.low { color: #4f6bf6; background: rgba(79, 107, 246, 0.1); }
.dot { width: 8px; height: 8px; border-radius: 50%; }
.dot.high { background: #e04a5f; }
.dot.mid { background: #f5a623; }
.dot.low { background: #4f6bf6; }
.a-main { flex: 1; min-width: 0; }
.a-top { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; }
.a-top b { font-size: 15px; }
.a-main > p { font-size: 13px; color: var(--muted); margin: 7px 0 9px; line-height: 1.6; }
.a-meta { display: flex; align-items: center; gap: 14px; flex-wrap: wrap; font-size: 12px; color: #a3acbd; }
.metric-tag { background: #eef1f6; padding: 3px 9px; border-radius: 6px; font-weight: 600; color: #434f66; }
.a-ops { display: flex; flex-direction: column; gap: 6px; align-items: flex-end; flex: none; }
.ope { font-size: 13px; color: #7d8698; padding: 6px 10px; border-radius: 8px; }
.ope:hover { background: #eef1f6; }
.ope.primary { color: var(--primary); font-weight: 600; }
.badge { font-size: 11px; padding: 3px 9px; border-radius: 20px; font-weight: 600; }
.badge.open { color: #e04a5f; background: rgba(224, 74, 95, 0.1); }
.badge.ok { color: #0fa97a; background: rgba(34, 193, 163, 0.12); }
.empty { text-align: center; padding: 24px 0; color: #a3acbd; font-size: 14px; }

.rules { display: flex; flex-direction: column; gap: 10px; }
.rule { display: flex; align-items: center; gap: 14px; padding: 13px 6px; border-bottom: 1px solid #f0f2f7; }
.rule:last-child { border-bottom: none; }
.rule > .icon { width: 40px; height: 40px; border-radius: 12px; display: grid; place-items: center; font-size: 18px; flex: none; }
.r-main { flex: 1; min-width: 0; }
.r-main b { font-size: 14px; }
.r-main p { font-size: 12px; color: var(--muted); margin-top: 3px; }
.r-side { display: flex; flex-direction: column; align-items: flex-end; gap: 5px; }
.switch { width: 40px; height: 22px; border-radius: 20px; background: #d8dde7; position: relative; cursor: pointer; transition: background 0.2s; }
.switch.on { background: var(--primary); }
.switch i { position: absolute; top: 2px; left: 2px; width: 18px; height: 18px; border-radius: 50%; background: #fff; transition: left 0.2s; }
.switch.on i { left: 20px; }
.r-stat { font-size: 11px; color: #a3acbd; }

@media (max-width: 1200px) { .cards { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 640px) {
  .page-head { flex-direction: column; align-items: flex-start; }
  .cards { grid-template-columns: 1fr; }
  .a-ops { flex-direction: row; }
}
</style>
