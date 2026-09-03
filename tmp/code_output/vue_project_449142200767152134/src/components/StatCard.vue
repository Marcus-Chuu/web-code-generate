<script setup>
defineProps({
  title: String,
  value: String,
  delta: String,
  trend: String,
  desc: String,
  icon: String,
  color: String
})
</script>

<template>
  <div class="stat-card card">
    <div class="stat-top">
      <div class="stat-icon" :class="'is-' + color">{{ icon }}</div>
      <span class="stat-delta" :class="trend === 'up' ? 'delta-up' : 'delta-down'">
        {{ delta }}
        <span class="delta-arrow">{{ trend === 'up' ? '▲' : '▼' }}</span>
      </span>
    </div>
    <p class="stat-title">{{ title }}</p>
    <p class="stat-value">{{ value }}</p>
    <p class="stat-desc">{{ desc }}</p>
  </div>
</template>

<style scoped>
.stat-card {
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(15, 23, 42, 0.08);
}

.stat-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.is-blue { background: var(--primary-light); }
.is-green { background: var(--success-light); }
.is-orange { background: var(--warning-light); }
.is-purple { background: #f3effe; }

.stat-delta {
  font-size: 13px;
  font-weight: 600;
  padding: 3px 8px;
  border-radius: 999px;
}

.delta-up {
  color: #16a34a;
  background: var(--success-light);
}

.delta-down {
  color: #dc2626;
  background: var(--danger-light);
}

.delta-arrow {
  font-size: 10px;
}

.stat-title {
  margin-top: 14px;
  font-size: 13px;
  color: var(--text-sub);
}

.stat-value {
  margin-top: 4px;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.stat-desc {
  margin-top: 4px;
  font-size: 12px;
  color: #94a3b8;
}
</style>
