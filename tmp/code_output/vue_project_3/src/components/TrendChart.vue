<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

const props = defineProps({
  data: { type: Array, required: true },
  color: { type: String, default: '#4f6bf6' },
  gridColor: { type: String, default: '#eceff5' }
})

const box = ref(null)
const W = 720
const H = 252
const pad = { l: 42, r: 12, t: 16, b: 28 }
const pvMax = Math.max(...props.data.map(d => d.pv))
const uvMax = Math.max(...props.data.map(d => d.uv))
const yMax = Math.ceil(Math.max(pvMax, uvMax) / 400) * 400

const yTicks = [0, 0.25, 0.5, 0.75, 1].map(t => Math.round(yMax * t))
const yPos = v => pad.t + (H - pad.t - pad.b) * (1 - v / yMax)
const xPos = i => pad.l + (W - pad.l - pad.r) * (i / (props.data.length - 1))

const buildPath = (key) => {
  return props.data.map((d, i) => {
    const x = xPos(i)
    const y = yPos(d[key])
    return `${i === 0 ? 'M' : 'L'} ${x.toFixed(1)} ${y.toFixed(1)}`
  }).join(' ')
}

const area = computedArea()
const linePv = buildPath('pv')
const lineUv = buildPath('uv')

function computedArea() {
  const path = buildPath('pv')
  const last = props.data[props.data.length - 1]
  return `${path} L ${xPos(props.data.length - 1)} ${H - pad.b} L ${pad.l} ${H - pad.b} Z`
}

const tooltip = ref(null)
const hovered = ref(null)

function onMove(e) {
  const rect = box.value.getBoundingClientRect()
  const ratio = (e.clientX - rect.left) / rect.width
  const idx = Math.round(ratio * (props.data.length - 1))
  const clamped = Math.max(0, Math.min(props.data.length - 1, idx))
  const d = props.data[clamped]
  const px = xPos(clamped)
  const py = yPos(d.pv)
  tooltip.value = {
    show: true,
    x: (px / W * rect.width),
    y: (py / H * rect.height),
    text: `${d.date} · 浏览量 ${d.pv.toLocaleString()} · 访客 ${d.uv.toLocaleString()}`
  }
  hovered.value = clamped
}
function onLeave() { tooltip.value = null; hovered.value = null }

const resize = () => {
  if (box.value && tooltip.value) tooltip.value = null
}
onMounted(() => window.addEventListener('resize', resize))
onBeforeUnmount(() => window.removeEventListener('resize', resize))
</script>

<template>
  <div class="linechart" ref="box" @mousemove="onMove" @mouseleave="onLeave">
    <svg :viewBox="`0 0 ${W} ${H}`" preserveAspectRatio="none" class="chart-svg">
      <g v-for="t in yTicks" :key="t">
        <line :x1="pad.l" :x2="W - pad.r" :y1="yPos(t)" :y2="yPos(t)" :stroke="gridColor" stroke-width="1" stroke-dasharray="3 4" />
        <text :x="pad.l - 8" :y="yPos(t) + 4" text-anchor="end" class="axis-text" v-if="t !== 0 || true">{{ t >= 1000 ? (t / 1000) + 'k' : t }}</text>
      </g>
      <path :d="area" :fill="props.color" opacity="0.08" />
      <path :d="lineUv" fill="none" stroke="#7c5cf0" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" opacity="0.55" />
      <path :d="linePv" fill="none" :stroke="props.color" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
      <g v-for="(d, i) in props.data" :key="d.date">
        <circle v-if="hovered === i" :cx="xPos(i)" :cy="yPos(d.pv)" :fill="props.color" r="5" stroke="#fff" stroke-width="2" />
      </g>
    </svg>
    <div v-if="tooltip && tooltip.show" class="tip" :style="{ left: tooltip.x + 'px', top: tooltip.y + 'px' }">
      {{ tooltip.text }}
    </div>
  </div>
</template>

<style scoped>
.linechart { position: relative; width: 100%; height: 252px; }
.chart-svg { width: 100%; height: 100%; overflow: visible; }
.axis-text { font-size: 11px; fill: #a3acbd; }
.tip {
  position: absolute; transform: translate(-50%, -110%);
  background: #10162b; color: #fff; font-size: 12px;
  padding: 7px 11px; border-radius: 8px; white-space: nowrap; pointer-events: none;
  box-shadow: 0 6px 20px rgba(16, 22, 43, 0.2); z-index: 5;
}
</style>
