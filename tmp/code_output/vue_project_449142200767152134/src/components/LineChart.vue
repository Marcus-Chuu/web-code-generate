<script setup>
import { computed } from 'vue'

const props = defineProps({
  data: { type: Array, required: true },
  xKey: { type: String, default: 'month' },
  series: { type: Array, required: true },
  height: { type: Number, default: 280 }
})

const W = 720
const H = 300
const PAD = { top: 20, right: 20, bottom: 34, left: 44 }

const maxValue = computed(() => {
  let max = 0
  props.data.forEach((d) => {
    props.series.forEach((s) => {
      if (d[s.key] > max) max = d[s.key]
    })
  })
  return Math.ceil((max * 1.15) / 10) * 10
})

const innerW = computed(() => W - PAD.left - PAD.right)
const innerH = computed(() => H - PAD.top - PAD.bottom)

const xPos = computed(() => {
  const step = innerW.value / Math.max(props.data.length - 1, 1)
  return props.data.map((_, i) => PAD.left + i * step)
})

const yPos = computed(() => (v) => PAD.top + innerH.value - (v / maxValue.value) * innerH.value)

const gridLines = computed(() => {
  const lines = []
  for (let i = 0; i <= 4; i++) {
    const val = (maxValue.value / 4) * i
    lines.push({ y: yPos.value(val), label: val })
  }
  return lines
})

function points(key) {
  return xPos.value.map((x, i) => `${x},${yPos.value(props.data[i][key])}`).join(' ')
}

function areaPoints(key) {
  const last = xPos.value[xPos.value.length - 1]
  return points(key) + ` ${last},${PAD.top + innerH.value} ${PAD.left},${PAD.top + innerH.value}`
}

function lastLabel(key) {
  const d = props.data[props.data.length - 1]
  const x = xPos.value[xPos.value.length - 1] + 8
  const y = yPos.value(d[key]) - 8
  return { x, y, text: d[key] }
}
</script>

<template>
  <svg :viewBox="`0 0 ${W} ${H}`" class="line-chart" :style="{ height: height + 'px' }" preserveAspectRatio="none">
    <defs>
      <linearGradient id="gradA" x1="0" y1="0" x2="0" y2="1">
        <stop offset="0%" stop-color="#4f6ef7" stop-opacity="0.18" />
        <stop offset="100%" stop-color="#4f6ef7" stop-opacity="0" />
      </linearGradient>
      <linearGradient id="gradB" x1="0" y1="0" x2="0" y2="1">
        <stop offset="0%" stop-color="#22c55e" stop-opacity="0.15" />
        <stop offset="100%" stop-color="#22c55e" stop-opacity="0" />
      </linearGradient>
    </defs>

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

    <polygon v-if="series[0]" :points="areaPoints(series[0].key)" fill="url(#gradA)" />
    <polygon v-if="series[1]" :points="areaPoints(series[1].key)" fill="url(#gradB)" />

    <polyline
      v-for="(s, si) in series"
      :key="s.key"
      :points="points(s.key)"
      fill="none"
      :stroke="s.color"
      stroke-width="2.5"
      stroke-linejoin="round"
      stroke-linecap="round"
    />

    <circle
      v-for="(s, si) in series"
      :key="'dot' + s.key"
      v-show="si === 0"
      :cx="xPos[xPos.length - 1]"
      :cy="yPos(data[data.length - 1][s.key])"
      r="4.5"
      :fill="s.color"
      stroke="#fff"
      stroke-width="2"
    />

    <g v-for="(s, si) in series" :key="'last' + s.key">
      <text
        v-if="si === 0"
        :x="lastLabel(s.key).x"
        :y="lastLabel(s.key).y"
        font-size="12"
        font-weight="600"
        :fill="s.color"
      >
        {{ lastLabel(s.key).text }}
      </text>
    </g>

    <text
      v-for="(label, i) in data.map((d) => d[xKey])"
      :key="i"
      :x="xPos[i]"
      :y="H - 10"
      text-anchor="middle"
      font-size="11"
      fill="#94a3b8"
    >
      {{ label }}
    </text>
  </svg>

  <div class="chart-legend">
    <span v-for="s in series" :key="s.key" class="legend-item">
      <i :style="{ background: s.color }"></i>{{ s.label }}
    </span>
  </div>
</template>

<style scoped>
.line-chart {
  width: 100%;
  display: block;
}

.chart-legend {
  display: flex;
  gap: 18px;
  justify-content: center;
  margin-top: 10px;
}

.legend-item {
  font-size: 12px;
  color: var(--text-sub);
  display: flex;
  align-items: center;
  gap: 6px;
}

.legend-item i {
  width: 10px;
  height: 10px;
  border-radius: 3px;
  display: inline-block;
}
</style>
