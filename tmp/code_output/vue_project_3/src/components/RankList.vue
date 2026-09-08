<script setup>
import { computed } from 'vue'

const props = defineProps({ data: { type: Array, required: true } })
const max = computed(() => Math.max(...props.data.map(d => d.value)))
const total = computed(() => props.data.reduce((s, d) => s + d.value, 0))
</script>

<template>
  <div class="rank-list">
    <div v-for="(item, i) in props.data" :key="item.name" class="rank-row">
      <span class="no" :class="{ top: i < 3 }">{{ i + 1 }}</span>
      <div class="bar-wrap">
        <div class="bar-line">
          <i :style="{ width: (item.value / max) * 100 + '%', background: item.color }"></i>
        </div>
        <div class="bar-meta">
          <b>{{ item.name }}</b>
          <span>{{ item.value.toLocaleString() }}</span>
        </div>
      </div>
      <span class="pct">{{ ((item.value / total) * 100).toFixed(1) }}%</span>
    </div>
  </div>
</template>

<style scoped>
.rank-list { display: flex; flex-direction: column; gap: 13px; }
.rank-row { display: flex; align-items: center; gap: 10px; }
.no {
  width: 22px; height: 22px; border-radius: 7px; flex: none;
  display: grid; place-items: center;
  font-size: 12px; font-weight: 600; color: #7d8698; background: #f1f3f8;
}
.no.top { color: #fff; background: linear-gradient(135deg, #4f6bf6, #7c5cf0); }
.bar-wrap { flex: 1; min-width: 0; }
.bar-line { height: 9px; background: #eef1f6; border-radius: 20px; overflow: hidden; }
.bar-line i { display: block; height: 100%; border-radius: 20px; }
.bar-meta { display: flex; justify-content: space-between; margin-top: 5px; font-size: 12px; }
.bar-meta span { color: var(--muted); }
.pct { width: 42px; text-align: right; font-size: 12px; color: #a3acbd; }
</style>
