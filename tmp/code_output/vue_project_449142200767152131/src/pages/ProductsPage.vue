<script setup>
import { ref, computed, watch } from 'vue'
import { products } from '@/data/products'
import ProductCard from '@/components/ProductCard.vue'
import { useCartStore } from '@/store/cart'
import { useToast } from '@/store/toast'
import { useRoute, useRouter } from 'vue-router'

const cart = useCartStore()
const { show } = useToast()
const route = useRoute()
const router = useRouter()

const categories = ['全部', ...new Set(products.map(p => p.category))]
const activeCate = ref('全部')
const keyword = ref('')
const sortBy = ref('default')

const filtered = computed(() => {
  let list = products
  if (activeCate.value !== '全部') {
    list = list.filter(p => p.category === activeCate.value)
  }
  const kw = keyword.value.trim()
  if (kw) {
    list = list.filter(p => p.name.includes(kw) || p.category.includes(kw))
  }
  if (sortBy.value === 'price-asc') list = [...list].sort((a, b) => a.price - b.price)
  if (sortBy.value === 'price-desc') list = [...list].sort((a, b) => b.price - a.price)
  if (sortBy.value === 'sales') list = [...list].sort((a, b) => b.sales - a.sales)
  return list
})

// 支持从导航栏搜索跳转（URL 参数 q）
watch(
  () => route.query.q,
  val => {
    keyword.value = typeof val === 'string' ? val : ''
    activeCate.value = '全部'
  },
  { immediate: true }
)

function selectCate(c) {
  activeCate.value = c
}

function addToCart(product) {
  cart.addToCart(product, 1)
  show(`已加入购物车：${product.name}`)
}
</script>

<template>
  <div class="page">
    <div class="container">
      <div class="page-head">
        <h2>全部臻选好物</h2>
        <p>共 {{ filtered.length }} 件商品 · 精挑细选，只为品质生活</p>
      </div>

      <!-- 筛选栏 -->
      <div class="toolbar">
        <div class="cate-tabs">
          <button
            v-for="c in categories"
            :key="c"
            class="cate-btn"
            :class="{ active: activeCate === c }"
            @click="selectCate(c)"
          >
            {{ c }}
          </button>
        </div>
        <div class="toolbar-right">
          <input v-model="keyword" type="text" placeholder="输入关键词筛选…" class="kw-input" />
          <select v-model="sortBy" class="sort-select">
            <option value="default">默认排序</option>
            <option value="sales">销量优先</option>
            <option value="price-asc">价格从低到高</option>
            <option value="price-desc">价格从高到低</option>
          </select>
        </div>
      </div>

      <!-- 商品列表 -->
      <div v-if="filtered.length" class="product-grid">
        <ProductCard v-for="p in filtered" :key="p.id" :product="p" @add-to-cart="addToCart" />
      </div>
      <div v-else class="empty-tip">
        <p>😕 没有找到符合条件的商品</p>
        <button class="btn-primary" @click="keyword = ''; activeCate = '全部'">清除筛选</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding: 40px 0 20px;
}
.page-head h2 {
  font-size: 26px;
}
.page-head p {
  color: var(--text-light);
  margin-top: 8px;
}
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
  background: var(--card-bg);
  border-radius: 14px;
  box-shadow: var(--shadow);
  padding: 14px 18px;
  margin: 24px 0 28px;
}
.cate-tabs {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.cate-btn {
  border: 1px solid #e5e5e5;
  background: #fff;
  color: #666;
  border-radius: 20px;
  padding: 6px 16px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}
.cate-btn:hover {
  border-color: var(--accent);
  color: var(--accent);
}
.cate-btn.active {
  background: var(--primary);
  border-color: var(--primary);
  color: var(--accent-light);
}
.toolbar-right {
  display: flex;
  gap: 10px;
  align-items: center;
}
.kw-input {
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  padding: 8px 16px;
  font-size: 13px;
  outline: none;
  width: 190px;
}
.kw-input:focus {
  border-color: var(--accent);
}
.sort-select {
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  padding: 8px 12px;
  font-size: 13px;
  outline: none;
  background: #fff;
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 22px;
}
.empty-tip {
  text-align: center;
  padding: 80px 0;
  color: var(--text-light);
}
.empty-tip p {
  font-size: 16px;
  margin-bottom: 20px;
}
@media (max-width: 1024px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }
  .toolbar-right {
    justify-content: space-between;
  }
  .kw-input {
    width: 100%;
  }
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
