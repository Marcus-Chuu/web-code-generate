<script setup>
import { computed } from 'vue'

const props = defineProps({
  data: { type: Array, required: true },
  size: { type: Number, default: 170 },
  centerLabel: { type: String, default: '合计' }
})

const R = 70
const CX = 100
const CY = 100
const STROKE = 20
const CIRC = 2 * Math.PI * R

const total = computed(() => props.data.reduce((sum, d) => sum + d.value, 0))

// 预计算每段弧的偏移
const segments = computed(() => {
  let acc = 0
  return props.data.map((d) => {
    const frac = d.value / total.value
    const seg = { ...d, frac, offset: acc }
    acc += frac
    return seg
  })
})

function dashArray(frac) {
  return `${frac * CIRC} ${CIRC}`
}

function dashOffset(offset) {
  return -offset * CIRC
}
</script>

<template>
  <div class="donut-wrap">
    <svg :viewBox="`0 0 200 200`" :style="{ width: size + 'px', height: size + 'px' }">
      <circle :cx="CX" :cy="CY" :r="R" fill="none" stroke="#f1f5f9" :stroke-width="STROKE" />
      <circle
        v-for="(seg, i) in segments"
        :key="i"
        :cx="CX"
        :cy="CY"
        :r="R"
        fill="none"
        :stroke="seg.color"
        :stroke-width="STROKE"
        stroke-linecap="butt"
        :stroke-dasharray="dashArray(seg.frac)"
        :stroke-dashoffset="dashOffset(seg.offset)"
        transform="rotate(-90 100 100)"
      />
      <text :x="CX" :y="CY - 6" text-anchor="middle" font-size="12" fill="#94a3b8">{{ centerLabel }}</text>
      <text :x="CX" :y="CY + 18" text-anchor="middle" font-size="20" font-weight="700" fill="#1e293b">
        {{ total }}
      </text>
    </svg>

    <ul class="donut-legend">
      <li v-for="(seg, i) in data" :key="i" class="donut-item">
        <span class="dot" :style="{ background: seg.color }"></span>
        <span class="name">{{ seg.name }}</span>
        <span class="val">{{ seg.value }}%</span>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.donut-wrap {
  display: flex;
  align-items: center;
  gap: 24px;
}

.donut-legend {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.donut-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}

.name {
  color: var(--text-sub);
  flex: 1;
}

.val {
  font-weight: 600;
  color: var(--text-main);
}

@media (max-width: 480px) {
  .donut-wrap {
    flex-direction: column;
  }
  .donut-legend {
    width: 100%;
  }
}
</style>
