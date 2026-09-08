<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  data: { type: Array, required: true },
  size: { type: Number, default: 168 },
  thickness: { type: Number, default: 22 }
})

const total = computed(() => props.data.reduce((acc, d) => acc + d.value, 0))
const active = ref(null)

const cx = props.size / 2
const cy = props.size / 2
const r = (props.size - props.thickness) / 2
const C = 2 * Math.PI * r

let acc = 0
const segs = props.data.map((d) => {
  const frac = d.value / total.value
  const seg = { ...d, frac, offset: acc * C, len: frac * C }
  acc += frac
  return seg
})

const centerText = computed(() => {
  if (active.value !== null) {
    const s = segs[active.value]
    return { big: s.value + ' 万', small: `${(s.frac * 100).toFixed(1)}%` }
  }
  return { big: total.value + ' 万', small: '总访问量' }
})
</script>

<template>
  <div class="donut-wrap">
    <svg :width="size" :height="size" viewBox="0 0 24 24" style="overflow: visible">
      <circle :cx="cx" :cy="cy" :r="r" fill="none" stroke="#eef1f8" :stroke-width="thickness" />
      <circle
        v-for="(s, i) in segs"
        :key="i"
        :cx="cx" :cy="cy" :r="r"
        fill="none"
        :stroke="s.color"
        :stroke-width="thickness"
        :stroke-dasharray="`${Math.max(s.len - 2.5, 0.5)} ${C - Math.max(s.len - 2.5, 0.5)}`"
        :stroke-dashoffset="-(s.offset + 1.2)"
        stroke-linecap="butt"
        transform="rotate(-90 12 12)"
        @mouseenter="active = i"
        @mouseleave="active = null"
      />
    </svg>
    <div class="donut-center">
      <b>{{ centerText.big }}</b>
      <span>{{ centerText.small }}</span>
    </div>
  </div>
</template>

<style scoped>
.donut-wrap { position: relative; width: fit-content; margin: 0 auto; }
.donut-center {
  position: absolute; inset: 0;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  pointer-events: none;
}
.donut-center b { font-size: 20px; letter-spacing: -0.3px; }
.donut-center span { font-size: 11.5px; color: #93a1c0; margin-top: 2px; }
</style>
