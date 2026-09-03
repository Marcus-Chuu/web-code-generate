<script setup>
import { computed } from 'vue'
import { useCartStore } from '@/store/cart'
import { useRouter } from 'vue-router'

const cart = useCartStore()
const router = useRouter()

const count = computed(() => cart.totalCount)
const price = computed(() => cart.totalPrice)
const discount = computed(() => Math.round(price.value * 0.02))

function checkout() {
  if (cart.isEmpty) return
  window.alert(
    `订单提交成功！\n共 ${count.value} 件商品，实付 ¥${(price.value - discount.value).toFixed(2)}`
  )
  cart.clearCart()
}
</script>

<template>
  <div class="page container">
    <div class="page-head">
      <h1 class="section-title">购物车</h1>
      <p class="section-sub">共 {{ count }} 件商品，认真核对后再提交哦</p>
    </div>

    <div v-if="cart.isEmpty" class="empty-tip">
      <p class="empty-icon">🛒</p>
      <p>购物车空空如也，快去挑选心仪的好物吧～</p>
      <router-link to="/products" class="btn-primary">去逛逛</router-link>
    </div>

    <div v-else class="cart-layout">
      <div class="cart-list">
        <div v-for="item in cart.cartItems" :key="item.id" class="cart-item">
          <img :src="item.image" :alt="item.name" />
          <div class="cart-item-info">
            <h3>{{ item.name }}</h3>
            <p>单价：¥{{ item.price }}</p>
          </div>
          <div class="qty">
            <button @click="cart.updateQuantity(item.id, item.quantity - 1)">−</button>
            <span>{{ item.quantity }}</span>
            <button @click="cart.updateQuantity(item.id, item.quantity + 1)">+</button>
          </div>
          <div class="subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
          <button class="remove-btn" @click="cart.removeFromCart(item.id)">移除</button>
        </div>
      </div>

      <aside class="summary">
        <h3>订单摘要</h3>
        <div class="summary-row">
          <span>商品件数</span>
          <span>{{ count }} 件</span>
        </div>
        <div class="summary-row">
          <span>商品小计</span>
          <span>¥{{ price }}</span>
        </div>
        <div class="summary-row">
          <span>会员折扣</span>
          <span class="discount">- ¥{{ discount }}</span>
        </div>
        <div class="summary-total">
          <span>应付总额</span>
          <span class="total-price">¥{{ (price - discount).toFixed(2) }}</span>
        </div>
        <button class="checkout-btn" @click="checkout">提交订单</button>
        <router-link to="/products" class="continue-link">← 继续购物</router-link>
      </aside>
    </div>
  </div>
</template>

<style scoped>
.page {
  padding-top: 40px;
}
.page-head {
  margin-bottom: 26px;
}
.cart-layout {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 24px;
  align-items: start;
}
.cart-list {
  background: var(--card-bg);
  border-radius: 14px;
  box-shadow: var(--shadow);
  padding: 8px 24px;
}
.cart-item {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 18px 0;
  border-bottom: 1px solid var(--border);
}
.cart-item:last-child {
  border-bottom: none;
}
.cart-item img {
  width: 88px;
  height: 88px;
  border-radius: 12px;
  object-fit: cover;
}
.cart-item-info {
  flex: 1;
  min-width: 0;
}
.cart-item-info h3 {
  font-size: 15px;
  margin-bottom: 8px;
}
.cart-item-info p {
  color: var(--text-light);
  font-size: 13px;
}
.qty {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f5f4f0;
  border-radius: 20px;
  padding: 5px 8px;
}
.qty button {
  width: 26px;
  height: 26px;
  border: none;
  background: #fff;
  border-radius: 50%;
  cursor: pointer;
  font-size: 15px;
}
.qty span {
  min-width: 20px;
  text-align: center;
  font-size: 14px;
}
.subtotal {
  width: 90px;
  text-align: right;
  color: #d42a2a;
  font-weight: 700;
  font-size: 16px;
}
.remove-btn {
  border: none;
  background: none;
  color: var(--text-light);
  font-size: 13px;
  cursor: pointer;
  width: 44px;
}
.remove-btn:hover {
  color: var(--danger);
}

.summary {
  background: var(--card-bg);
  border-radius: 14px;
  box-shadow: var(--shadow);
  padding: 26px;
  position: sticky;
  top: 84px;
}
.summary h3 {
  font-size: 17px;
  margin-bottom: 18px;
}
.summary-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: var(--text-light);
  padding: 8px 0;
}
.discount {
  color: #2f9e44;
}
.summary-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px dashed var(--border);
  margin-top: 14px;
  padding-top: 16px;
  font-size: 15px;
  font-weight: 600;
}
.total-price {
  color: #d42a2a;
  font-size: 26px;
  font-weight: 800;
}
.checkout-btn {
  width: 100%;
  margin-top: 20px;
  padding: 14px;
  border: none;
  border-radius: 30px;
  background: linear-gradient(135deg, var(--accent-light), var(--accent));
  color: #16181f;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.checkout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(201, 162, 74, 0.35);
}
.continue-link {
  display: block;
  text-align: center;
  margin-top: 14px;
  font-size: 13px;
  color: var(--text-light);
  text-decoration: none;
}
.continue-link:hover {
  color: var(--accent);
}

@media (max-width: 900px) {
  .cart-layout {
    grid-template-columns: 1fr;
  }
  .summary {
    position: static;
  }
}
@media (max-width: 640px) {
  .cart-item {
    flex-wrap: wrap;
  }
  .subtotal {
    text-align: left;
    margin-left: auto;
  }
}
</style>
