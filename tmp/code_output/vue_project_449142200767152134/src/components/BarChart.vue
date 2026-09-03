<script setup>
import { computed } from 'vue'

const props = defineProps({
  data: { type: Array, required: true },
  xKey: { type: String, default: 'day' },
  yKey: { type: String, default: 'orders' },
  color: { type: String, default: '#4f6ef7' },
  suffix: { type: String, default: '单' }
})

const W = 640
const H = 280
const PAD = { top: 24, right: 16, bottom: 34, left: 44 }

const maxValue = computed(() => {
  let max = 0
  props.data.forEach((d) => {
    if (d[props.yKey] > max) max = d[props.yKey]
  })
  return Math.ceil((max * 1.15) / 20) * 20
})

const innerW = computed(() => W - PAD.left - PAD.right)
const innerH = computed(() => H - PAD.top - PAD.bottom)

const band = computed(() => innerW.value / props.data.length)
const barWidth = computed(() => Math.min(band.value * 0.5, 42))

const barX = computed(() => (i) => PAD.left + band.value * i + (band.value - barWidth.value) / 2)
const barY = computed(() => (v) => PAD.top + innerH.value - (v / maxValue.value) * innerH.value)
const barH = computed(() => (v) => (v / maxValue.value) * innerH.value)

const gridLines = computed(() => {
  const lines = []
  for (let i = 0; i <= 4; i++) {
    const val = (maxValue.value / 4) * i
    lines.push({ y: barY.value(val), label: val })
  }
  return lines
})
</script>

<template>
  <svg :viewBox="`0 0 ${W} ${H}`" class="bar-chart" preserveAspectRatio="none">
    <g v-for="(line, i) in gridLines" :key="i">
      <line
        :x1="PAD.left"
        :y1="line.y"
        :x2="W - PAD.right"
        :y2="line.y"
        stroke="#eef2f7"
        stroke-width="1"
        stroke-dasharray="4 4"
      />
      <text :x="PAD.left - 8" :y="line.y + 4" text-anchor="end" font-size="11" fill="#94a3b8">
        {{ line.label }}
      </text>
    </g>

    <template v-for="(d, i) in data" :key="i">
      <rect
        :x="barX(i)"
        :y="barY(d[yKey])"
        :width="barWidth"
        :height="barH(d[yKey])"
        rx="6"
        :fill="color"
        fill-opacity="0.85"
      />
      <text :x="barX(i) + barWidth / 2" :y="barY(d[yKey]) - 6" text-anchor="middle" font-size="11" font-weight="600" :fill="color">
        {{ d[yKey] }}
      </text>
      <text :x="barX(i) + barWidth / 2" :y="H - 10" text-anchor="middle" font-size="11" fill="#94a3b8">
        {{ d[xKey] }}
      </text>
    </template>
  </svg>
</template>

<style scoped>
.bar-chart {
  width: 100%;
  height: 280px;
  display: block;
}
</style>
