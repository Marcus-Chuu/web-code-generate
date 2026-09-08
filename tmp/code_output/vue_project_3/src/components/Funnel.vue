<script setup>
const props = defineProps({ data: { type: Array, required: true } })
const max = Math.max(...props.data.map(d => d.value))
</script>

<template>
  <div class="funnel">
    <div v-for="(step, i) in props.data" :key="step.name" class="step" :style="{ width: (step.value / max) * 100 + '%' }">
      <div class="step-box">
        <div class="idx">0{{ i + 1 }}</div>
        <div class="step-info">
          <b>{{ step.name }}</b>
          <span>{{ step.value.toLocaleString() }} 人 · 转化率 {{ step.rate }}%</span>
        </div>
        <div class="connector" v-if="i < props.data.length - 1"></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.funnel { display: flex; flex-direction: column; gap: 2px; align-items: center; }
.step { transition: all 0.3s ease; }
.step-box {
  position: relative; display: flex; align-items: center; gap: 12px;
  padding: 14px 16px; border-radius: 12px; color: #fff;
  background: linear-gradient(90deg, rgba(79, 107, 246, 0.92), rgba(124, 92, 240, 0.92));
  min-width: 260px; box-shadow: 0 4px 14px rgba(79, 107, 246, 0.18);
}
.step:nth-child(1) .step-box { background: linear-gradient(90deg, #4f6bf6, #6178f7); }
.step:nth-child(2) .step-box { background: linear-gradient(90deg, #6178f7, #6e7ff2); }
.step:nth-child(3) .step-box { background: linear-gradient(90deg, #6e7ff2, #7c88ee); }
.step:nth-child(4) .step-box { background: linear-gradient(90deg, #7c88ee, #8a91ea); }
.step:nth-child(5) .step-box { background: linear-gradient(90deg, #8a91ea, #989ae5); }
.idx {
  width: 32px; height: 32px; border-radius: 9px; flex: none;
  background: rgba(255, 255, 255, 0.2); display: grid; place-items: center;
  font-weight: 700; font-size: 14px;
}
.step-info b { display: block; font-size: 14px; }
.step-info span { font-size: 12px; opacity: 0.85; }
.connector { position: absolute; bottom: -10px; left: 50%; width: 2px; height: 12px; background: rgba(79, 107, 246, 0.3); }
</style>
