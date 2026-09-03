<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useCartStore } from '@/store/cart'
import { useToast } from '@/store/toast'

const cart = useCartStore()
const { show } = useToast()

const visible = ref(false)
const FREE_SHIP = 199 // 包邮门槛（元）

const totalPrice = computed(() => cart.totalPrice)
const freeRemain = computed(() => Math.max(0, FREE_SHIP - totalPrice.value))
const freePercent = computed(() => Math.min(100, (totalPrice.value / FREE_SHIP) * 100))

function open() {
  visible.value = true
  document.body.style.overflow = 'hidden'
}
function close() {
  visible.value = false
  document.body.style.overflow = ''
}
function onCheckout() {
  show('订单已提交，感谢您的信任～')
  cart.clearCart()
  close()
}

function onKeydown(e) {
  if (e.key === 'Escape') close()
}

onMounted(() => window.addEventListener('keydown', onKeydown))
onUnmounted(() => {
  window.removeEventListener('keydown', onKeydown)
  document.body.style.overflow = ''
})

defineExpose({ open })
</script>

<template>
  <teleport to="body">
    <transition name="mask">
      <div v-if="visible" class="drawer-mask" @click="close"></div>
    </transition>
    <transition name="slide">
      <aside v-if="visible" class="cart-drawer">
        <div class="drawer-head">
          <h3>🛒 购物车</h3>
          <button class="close-btn" @click="close">✕</button>
        </div>

        <div class="free-ship">
          <template v-if="freeRemain > 0">
            🚚 再买 <b>￥{{ freeRemain.toFixed(0) }}</b> 即可享受包邮
          </template>
          <template v-else>🎉 已满足包邮条件，尽情买买买！</template>
          <div class="free-bar"><span :style="{ width: freePercent + '%' }"></span></div>
        </div>

        <div class="drawer-body">
          <template v-if="!cart.isEmpty">
            <div v-for="item in cart.cartItems" :key="item.id" class="cart-item">
              <img :src="item.image" :alt="item.name" class="item-img" />
              <div class="item-info">
                <p class="item-name">{{ item.name }}</p>
                <p class="item-cate">{{ item.category }}</p>
                <div class="item-bottom">
                  <div class="stepper">
                    <button @click="cart.updateQuantity(item.id, item.quantity - 1)">−</button>
                    <span>{{ item.quantity }}</span>
                    <button @click="cart.updateQuantity(item.id, item.quantity + 1)">＋</button>
                  </div>
                  <span class="item-price">￥{{ (item.price * item.quantity).toFixed(2) }}</span>
                </div>
              </div>
              <button class="remove-btn" @click="cart.removeFromCart(item.id)">🗑</button>
            </div>
          </template>
          <div v-else class="cart-empty">
            <span class="empty-icon">🛒</span>
            <p>购物车还是空的</p>
            <button class="btn-primary" @click="close">去逛逛</button>
          </div>
        </div>

        <div class="drawer-foot">
          <div class="foot-summary">
            <span>共 {{ cart.totalCount }} 件商品</span>
            <span class="foot-total">合计 <b>￥{{ totalPrice.toFixed(2) }}</b></span>
          </div>
          <button class="btn-primary checkout-btn" :disabled="cart.isEmpty" @click="onCheckout">
            去结算
          </button>
        </div>
      </aside>
    </transition>
  </teleport>
</template>

<style scoped>
.drawer-mask {
  position: fixed;
  inset: 0;
  background: rgba(10, 12, 18, 0.55);
  z-index: 200;
}
.cart-drawer {
  position: fixed;
  top: 0;
  right: 0;
  width: 400px;
  max-width: 92vw;
  height: 100%;
  background: #fff;
  z-index: 210;
  display: flex;
  flex-direction: column;
  box-shadow: -10px 0 40px rgba(0, 0, 0, 0.2);
}
.drawer-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 22px;
  border-bottom: 1px solid #eee;
}
.drawer-head h3 {
  font-size: 17px;
}
.close-btn {
  border: none;
  background: none;
  font-size: 16px;
  cursor: pointer;
  color: #888;
  width: 32px;
  height: 32px;
  border-radius: 50%;
}
.close-btn:hover {
  background: #f2f2f2;
}
.free-ship {
  background: #faf6ec;
  border-bottom: 1px solid #f0e8d0;
  font-size: 13px;
  color: #8a6d1f;
  padding: 12px 22px;
}
.free-ship b {
  color: #b8860b;
}
.free-bar {
  height: 5px;
  background: #eee;
  border-radius: 3px;
  margin-top: 8px;
  overflow: hidden;
}
.free-bar span {
  display: block;
  height: 100%;
  background: linear-gradient(90deg, #f0c36d, #d4a03c);
  border-radius: 3px;
  transition: width 0.4s;
}
.drawer-body {
  flex: 1;
  overflow-y: auto;
  padding: 8px 22px;
}
.cart-item {
  display: flex;
  gap: 14px;
  padding: 16px 0;
  border-bottom: 1px solid #f2f2f2;
  position: relative;
}
.item-img {
  width: 74px;
  height: 74px;
  border-radius: 10px;
  object-fit: cover;
  flex-shrink: 0;
}
.item-info {
  flex: 1;
  min-width: 0;
}
.item-name {
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.item-cate {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
.item-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 10px;
}
.stepper {
  display: flex;
  align-items: center;
  gap: 2px;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  padding: 2px;
}
.stepper button {
  width: 26px;
  height: 26px;
  border: none;
  background: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 15px;
  color: #555;
}
.stepper button:hover {
  background: #f2f2f2;
}
.stepper span {
  min-width: 28px;
  text-align: center;
  font-size: 14px;
}
.item-price {
  font-size: 15px;
  font-weight: 700;
  color: var(--accent);
}
.remove-btn {
  position: absolute;
  top: 14px;
  right: 0;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 14px;
  opacity: 0.55;
}
.remove-btn:hover {
  opacity: 1;
}
.cart-empty {
  text-align: center;
  padding: 70px 0;
  color: #999;
}
.empty-icon {
  font-size: 46px;
}
.cart-empty p {
  margin: 14px 0 22px;
  font-size: 15px;
}
.drawer-foot {
  padding: 18px 22px;
  border-top: 1px solid #eee;
  background: #fafafa;
}
.foot-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #888;
  margin-bottom: 12px;
}
.foot-total {
  font-size: 14px;
}
.foot-total b {
  font-size: 20px;
  color: var(--accent);
}
.checkout-btn {
  width: 100%;
  padding: 14px;
  font-size: 16px;
}
.checkout-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.mask-enter-active,
.mask-leave-active {
  transition: opacity 0.3s;
}
.mask-enter-from,
.mask-leave-to {
  opacity: 0;
}
.slide-enter-active,
.slide-leave-active {
  transition: transform 0.3s ease;
}
.slide-enter-from,
.slide-leave-to {
  transform: translateX(100%);
}
</style>
