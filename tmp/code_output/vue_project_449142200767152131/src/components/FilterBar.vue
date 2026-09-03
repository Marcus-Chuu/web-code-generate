<script setup>
import { categories } from '@/data/products'

defineProps({
  modelValue: {
    type: String,
    default: 'all'
  },
  sortValue: {
    type: String,
    default: 'default'
  }
})

const emit = defineEmits(['update:modelValue', 'update:sortValue'])

const sorts = [
  { value: 'default', name: '综合排序' },
  { value: 'sales', name: '销量优先' },
  { value: 'price-asc', name: '价格从低到高' },
  { value: 'price-desc', name: '价格从高到低' },
  { value: 'rating', name: '好评优先' }
]
</script>

<template>
  <div class="filter-bar">
    <div class="filter-cats">
      <button
        v-for="cat in categories"
        :key="cat.id"
        class="cat-btn"
        :class="{ active: modelValue === cat.id }"
        @click="emit('update:modelValue', cat.id)"
      >
        {{ cat.name }}
      </button>
    </div>
    <div class="filter-sort">
      <select
        class="sort-select"
        :value="sortValue"
        @change="emit('update:sortValue', $event.target.value)"
      >
        <option v-for="s in sorts" :key="s.value" :value="s.value">{{ s.name }}</option>
      </select>
    </div>
  </div>
</template>

<style scoped>
.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  padding: 18px 22px;
  background: var(--card-bg);
  border-radius: 14px;
  box-shadow: var(--shadow);
  margin-bottom: 28px;
}
.filter-cats {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
.cat-btn {
  padding: 8px 18px;
  border: 1px solid var(--border);
  background: var(--card-bg);
  border-radius: 30px;
  font-size: 14px;
  color: var(--text);
  cursor: pointer;
  transition: all 0.2s;
}
.cat-btn:hover {
  border-color: var(--accent);
  color: var(--accent);
}
.cat-btn.active {
  background: var(--primary);
  border-color: var(--primary);
  color: #fff;
}
.sort-select {
  padding: 8px 14px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--card-bg);
  font-size: 14px;
  color: var(--text);
  cursor: pointer;
  outline: none;
}
@media (max-width: 600px) {
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }
  .filter-sort {
    align-self: flex-start;
  }
}
</style>
