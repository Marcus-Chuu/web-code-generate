<script setup>
import { RouterLink } from 'vue-router'
import TrendChart from '@/components/TrendChart.vue'
import DonutChart from '@/components/DonutChart.vue'
import RankList from '@/components/RankList.vue'
import Funnel from '@/components/Funnel.vue'
import {
  cards, trendData, platform, channel,
  realtime, funnelSteps, heatData, timeline, alerts
} from '@/utils/mock'

const cardIcons = ['icon-eye', 'icon-user', 'icon-cart', 'icon-trend']
const channelColors = ['#4f6bf6', '#7c5cf0', '#22c1a3', '#f5a623', '#e04a5f', '#5ab0f6']
const alertsStyle = ['warn', 'info', 'danger']
</script>

<template>
  <div class="page">
    <!-- 问候横幅 -->
    <section class="banner">
      <div>
        <h1>早上好，王雨桐 👋</h1>
        <p>欢迎回到数据概览，过去 7 天访问量同比提升 <em>+18.6%</em>，转化效果持续向好。</p>
      </div>
      <div class="banner-actions">
        <button class="btn ghost" @click="$router.push('/reports')">
          <i class="icon icon-cal"></i>导出周报
        </button>
        <button class="btn primary" @click="$router.push('/users')">
          <i class="icon icon-plus"></i>新建分析
        </button>
      </div>
    </section>

    <!-- 指标卡片 -->
    <section class="cards">
      <div v-for="(card, i) in cards" :key="card.title" class="card stat">
        <div class="stat-head">
          <div class="stat-icon" :style="{ background: card.bg, color: card.color }">
            <i :class="'icon ' + cardIcons[i]"></i>
          </div>
          <span class="tag" :class="card.up ? 'up' : 'down'">{{ card.diff }}</span>
        </div>
        <b class="stat-num">{{ card.value }}</b>
        <div class="stat-title">
          {{ card.title }}
          <span class="vs">较昨日</span>
        </div>
        <div class="spark" :style="{ '--c': card.color }">
          <svg viewBox="0 0 96 26" preserveAspectRatio="none">
            <path :d="card.spark" fill="none" stroke="currentColor" stroke-width="2" />
          </svg>
        </div>
      </div>
    </section>

    <!-- 趋势 + 渠道 -->
    <section class="row">
      <div class="card grow">
        <div class="card-head">
          <div>
            <h3>访问趋势</h3>
            <p>近 30 天访问量与访客数走势</p>
          </div>
          <div class="seg">
            <button class="on">浏览量</button>
            <button>访客数</button>
            <button>订单量</button>
          </div>
        </div>
        <TrendChart :data="trendData" />
      </div>
      <div class="card donut-card">
        <div class="card-head">
          <div>
            <h3>用户构成</h3>
            <p>新增用户渠道分布</p>
          </div>
          <RouterLink to="/users" class="more">分析</RouterLink>
        </div>
        <DonutChart :data="platform" />
      </div>
    </section>

    <!-- 实时 + 渠道排名 -->
    <section class="row">
      <div class="card">
        <div class="card-head">
          <div>
            <h3>实时访客</h3>
            <p>当前在线人数动态</p>
          </div>
          <span class="live"><i></i>实时</span>
        </div>
        <ul class="realtime-list">
          <li v-for="(p, i) in realtime" :key="p.name">
            <span class="pos">{{ i + 1 }}</span>
            <div class="rt-main">
              <b>{{ p.name }}</b>
              <div class="rt-bar"><i :style="{ width: (p.value / realtime[0].value) * 100 + '%' }"></i></div>
            </div>
            <span class="rt-num">{{ p.value }}</span>
          </li>
        </ul>
      </div>
      <div class="card grow">
        <div class="card-head">
          <div>
            <h3>渠道来源 TOP 6</h3>
            <p>各渠道带来访客排行</p>
          </div>
          <button class="more">本月</button>
        </div>
        <RankList :data="channel" />
      </div>
    </section>

    <!-- 漏斗 -->
    <section class="card funnel-card">
      <div class="card-head">
        <div>
          <h3>转化漏斗</h3>
          <p>核心业务路径转化情况</p>
        </div>
        <RouterLink to="/reports" class="more">查看报表</RouterLink>
      </div>
      <Funnel :data="funnelSteps" />
    </section>

    <!-- 热力地图 + 实时动态 -->
    <section class="row">
      <div class="card grow">
        <div class="card-head">
          <div>
            <h3>区域热度</h3>
            <p>全国各省访问量分布</p>
          </div>
        </div>
        <div class="heat">
          <div v-for="(h, i) in heatData" :key="h.prov" class="heat-row" :style="{ '--r': h.ratio }">
            <span class="prov">{{ h.prov }}</span>
            <span class="hd"><i :style="{ width: h.ratio * 100 + '%', background: 'hsl(' + (225 - h.ratio * 45) + ', 85%, ' + (62 - h.ratio * 24) + '%)' }"></i></span>
            <span class="val">{{ h.value.toLocaleString() }}</span>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="card-head">
          <div>
            <h3>实时动态</h3>
            <p>系统关键行为事件</p>
          </div>
        </div>
        <ul class="timeline">
          <li v-for="(t, i) in timeline" :key="i" class="tl-item">
            <span class="tl-dot" :style="{ background: '#4f6bf6' }"></span>
            <div class="tl-main">
              <p>{{ t.text }}</p>
              <span>{{ t.time }}</span>
            </div>
          </li>
        </ul>
      </div>
    </section>

    <!-- 智能告警 -->
    <section class="card">
      <div class="card-head">
        <div>
          <h3>智能告警</h3>
          <p>需关注的数据异常提醒</p>
        </div>
        <RouterLink to="/alerts" class="more">全部告警</RouterLink>
      </div>
      <div class="alerts">
        <div v-for="(a, i) in alerts" :key="a.title" class="alert" :class="alertsStyle[i % 3]">
          <i :class="'icon ' + (i === 0 ? 'icon-alert' : i === 1 ? 'icon-info' : 'icon-warn')"></i>
          <div class="alert-main">
            <b>{{ a.title }}</b>
            <p>{{ a.desc }}</p>
          </div>
          <span class="alert-time">{{ a.time }}</span>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.page { display: flex; flex-direction: column; gap: 20px; }

.banner {
  display: flex; align-items: center; justify-content: space-between; gap: 16px;
  padding: 26px 30px; border-radius: 20px; color: #fff;
  background: linear-gradient(120deg, #2e3fdc 0%, #5a4ae0 55%, #7c5cf0 100%);
  box-shadow: 0 14px 32px rgba(79, 107, 246, 0.28);
}
.banner h1 { font-size: 24px; }
.banner p { margin-top: 8px; font-size: 14px; opacity: 0.88; }
.banner em { font-style: normal; font-weight: 700; background: rgba(255, 255, 255, 0.2); padding: 1px 8px; border-radius: 6px; }
.banner-actions { display: flex; gap: 10px; flex: none; }
.btn { display: inline-flex; align-items: center; gap: 7px; padding: 11px 18px; border-radius: 12px; font-size: 14px; font-weight: 600; cursor: pointer; }
.btn.ghost { background: rgba(255, 255, 255, 0.14); color: #fff; border: 1px solid rgba(255, 255, 255, 0.3); }
.btn.ghost:hover { background: rgba(255, 255, 255, 0.24); }
.btn.primary { background: #fff; color: #3f4fdf; }
.btn.primary:hover { background: #f0f2ff; }

.cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 18px; }
.stat { position: relative; overflow: hidden; }
.stat-head { display: flex; align-items: center; justify-content: space-between; }
.stat-icon { width: 44px; height: 44px; border-radius: 13px; display: grid; place-items: center; font-size: 20px; }
.tag { font-size: 12px; font-weight: 700; padding: 4px 9px; border-radius: 20px; }
.tag.up { color: #0fa97a; background: rgba(34, 193, 163, 0.12); }
.tag.down { color: #e04a5f; background: rgba(224, 74, 95, 0.1); }
.stat-num { display: block; font-size: 32px; font-weight: 800; margin: 14px 0 2px; letter-spacing: -0.5px; }
.stat-title { color: var(--muted); font-size: 14px; }
.stat-title .vs { font-size: 12px; color: #b3bac9; margin-left: 4px; }
.spark { position: absolute; right: 16px; bottom: 14px; width: 90px; height: 28px; color: var(--c); opacity: 0.75; }
.spark svg { width: 100%; height: 100%; }

.row { display: grid; grid-template-columns: 1fr 360px; gap: 18px; align-items: start; }
.row > .card { min-width: 0; }
.row.reverse { grid-template-columns: 360px 1fr; }
.grow { min-width: 0; }
.donut-card .card-head { margin-bottom: 8px; }

.card-head { display: flex; align-items: flex-start; justify-content: space-between; gap: 10px; margin-bottom: 14px; }
.card-head h3 { font-size: 16px; }
.card-head p { font-size: 12px; color: #9aa3b5; margin-top: 3px; }
.more { font-size: 13px; color: var(--primary); font-weight: 600; flex: none; }
.more:hover { text-decoration: underline; }
.seg { display: flex; background: #f0f2f8; padding: 3px; border-radius: 10px; flex: none; }
.seg button { padding: 5px 12px; border-radius: 8px; font-size: 12px; color: var(--muted); }
.seg button.on { background: #fff; color: var(--primary); font-weight: 700; box-shadow: 0 2px 6px rgba(16, 22, 43, 0.08); }

.live { display: inline-flex; align-items: center; gap: 6px; font-size: 12px; font-weight: 700; color: #0fa97a; background: rgba(34, 193, 163, 0.1); padding: 4px 10px; border-radius: 20px; }
.live i { width: 7px; height: 7px; border-radius: 50%; background: #0fa97a; animation: blink 1.4s infinite; }
@keyframes blink { 0%, 100% { opacity: 1; } 50% { opacity: 0.25; } }

.realtime-list { display: flex; flex-direction: column; gap: 13px; }
.realtime-list li { display: flex; align-items: center; gap: 12px; }
.pos { width: 24px; height: 24px; flex: none; border-radius: 8px; background: #f0f2f8; color: #8a93a7; display: grid; place-items: center; font-size: 12px; font-weight: 700; }
.realtime-list li:nth-child(1) .pos { background: linear-gradient(135deg, #4f6bf6, #7c5cf0); color: #fff; }
.rt-main { flex: 1; min-width: 0; }
.rt-main b { display: block; font-size: 13px; margin-bottom: 5px; }
.rt-bar { height: 7px; background: #eef1f6; border-radius: 10px; overflow: hidden; }
.rt-bar i { display: block; height: 100%; border-radius: 10px; background: linear-gradient(90deg, #4f6bf6, #7c5cf0); }
.rt-num { font-weight: 700; font-size: 14px; width: 34px; text-align: right; }

.heat { display: flex; flex-direction: column; gap: 11px; }
.heat-row { display: flex; align-items: center; gap: 12px; }
.prov { width: 38px; font-size: 13px; color: var(--muted); flex: none; text-align: right; }
.hd { flex: 1; height: 22px; background: #eef1f6; border-radius: 8px; overflow: hidden; }
.hd i { display: block; height: 100%; border-radius: 8px; }
.val { width: 70px; text-align: right; font-size: 13px; font-weight: 600; }

.timeline { display: flex; flex-direction: column; }
.tl-item { position: relative; display: flex; gap: 13px; padding-bottom: 22px; }
.tl-item::before { content: ''; position: absolute; left: 5px; top: 14px; bottom: 2px; width: 2px; background: #eef1f6; }
.tl-item:last-child { padding-bottom: 0; }
.tl-item:last-child::before { display: none; }
.tl-dot { position: relative; z-index: 1; width: 12px; height: 12px; border-radius: 50%; margin-top: 3px; box-shadow: 0 0 0 3px #fff, 0 0 0 4px var(--line); flex: none; }
.tl-main p { font-size: 13px; color: #434f66; }
.tl-main span { font-size: 12px; color: #a3acbd; }
.tl-main b { color: #242f45; }

.alerts { display: flex; flex-direction: column; gap: 12px; }
.alert { display: flex; align-items: flex-start; gap: 13px; padding: 14px 16px; border-radius: 14px; background: #fafbfe; border: 1px solid #eef1f6; }
.alert i { font-size: 18px; margin-top: 1px; }
.alert.warn i { color: #f5a623; }
.alert.info i { color: #4f6bf6; }
.alert.danger i { color: #e04a5f; }
.alert-main { flex: 1; }
.alert-main b { font-size: 14px; }
.alert-main p { font-size: 13px; color: var(--muted); margin-top: 3px; }
.alert-time { font-size: 12px; color: #a3acbd; flex: none; }

.funnel-card :deep(.funnel) { max-width: 760px; margin: 10px auto 0; }

@media (max-width: 1200px) {
  .cards { grid-template-columns: repeat(2, 1fr); }
  .row { grid-template-columns: 1fr; }
}
@media (max-width: 640px) {
  .banner { flex-direction: column; align-items: flex-start; padding: 22px; }
  .banner h1 { font-size: 20px; }
  .cards { grid-template-columns: 1fr; }
  .stat-num { font-size: 27px; }
  .banner-actions .btn { padding: 10px 14px; font-size: 13px; }
  .seg { display: none; }
}
</style>
