<script setup>
import { useCartStore } from '@/store/cart'
import { useToast } from '@/store/toast'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const cart = useCartStore()
const { show } = useToast()

const tagClass = { 热销: 'tag-hot', 新品: 'tag-new', 限时: 'tag-limit' }

function add() {
  cart.addToCart(props.product, 1)
  show(`已加入购物车：${props.product.name}`)
}
</script>

<template>
  <div class="product-card">
    <div class="img-wrap">
      <img :src="product.image" :alt="product.name" loading="lazy" />
      <span v-if="product.tag" class="tag" :class="tagClass[product.tag]">{{ product.tag }}</span>
      <div class="quick-add" @click="add">＋ 快速加入</div>
    </div>
    <div class="card-body">
      <p class="cate">{{ product.category }}</p>
      <h3 class="name" :title="product.name">{{ product.name }}</h3>
      <div class="rating">★ {{ product.rating }} · 已售 {{ product.sales }}</div>
      <div class="card-foot">
        <span class="price">￥{{ product.price.toFixed(2) }}</span>
        <button class="add-btn" @click="add">加入购物车</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-card {
  background: var(--card-bg);
  border-radius: 14px;
  overflow: hidden;
  box-shadow: var(--shadow);
  transition: transform 0.25s, box-shadow 0.25s;
}
.product-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 14px 34px rgba(22, 24, 31, 0.16);
}
.img-wrap {
  position: relative;
  overflow: hidden;
}
.img-wrap img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  display: block;
  transition: transform 0.4s;
}
.product-card:hover .img-wrap img {
  transform: scale(1.06);
}
.tag {
  position: absolute;
  top: 12px;
  left: 12px;
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  padding: 3px 10px;
  border-radius: 20px;
}
.tag-hot {
  background: #e2543f;
}
.tag-new {
  background: #3f9b5f;
}
.tag-limit {
  background: #b8860b;
}
.quick-add {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(22, 24, 31, 0.85);
  color: #fff;
  text-align: center;
  padding: 10px;
  font-size: 14px;
  cursor: pointer;
  transform: translateY(100%);
  transition: transform 0.3s;
}
.product-card:hover .quick-add {
  transform: translateY(0);
}
.card-body {
  padding: 14px 16px 16px;
}
.cate {
  font-size: 12px;
  color: var(--accent);
  font-weight: 600;
}
.name {
  font-size: 15px;
  margin: 6px 0 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.rating {
  font-size: 12px;
  color: #b98a12;
  margin-bottom: 12px;
}
.card-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.price {
  font-size: 18px;
  font-weight: 800;
  color: var(--accent);
}
.add-btn {
  background: var(--primary);
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 7px 14px;
  font-size: 12px;
  cursor: pointer;
  transition: background 0.2s;
}
.add-btn:hover {
  background: #2b303d;
}
</style>
