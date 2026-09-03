<script setup>
import { computed, ref } from 'vue'
import AppHeader from '@/components/AppHeader.vue'
import CartDrawer from '@/components/CartDrawer.vue'
import Toast from '@/components/Toast.vue'
import BackTop from '@/components/BackTop.vue'
import { useCartStore } from '@/store/cart'
import { useRouter } from 'vue-router'

const cart = useCartStore()
const router = useRouter()
const cartCount = computed(() => cart.totalCount)

const drawerRef = ref(null)

function onSearch(keyword) {
  const kw = keyword.trim()
  if (!kw) return
  router.push({ path: '/products', query: { q: kw } })
}
</script>

<template>
  <AppHeader :cart-count="cartCount" @search="onSearch" @open-cart="drawerRef?.open()" />
  <main class="app-main">
    <router-view />
  </main>
  <footer class="app-footer">
    <div class="container footer-inner">
      <div class="footer-brand">
        <span class="footer-logo">臻</span>
        <div>
          <h3>臻选精品</h3>
          <p>甄选全球好物，致敬品质生活</p>
        </div>
      </div>
      <div class="footer-links">
        <div>
          <h4>购物指南</h4>
          <a href="#">购物流程</a>
          <a href="#">会员介绍</a>
          <a href="#">常见问题</a>
        </div>
        <div>
          <h4>服务保障</h4>
          <a href="#">正品保证</a>
          <a href="#">售后政策</a>
          <a href="#">隐私保护</a>
        </div>
        <div>
          <h4>关于我们</h4>
          <a href="#">品牌故事</a>
          <a href="#">加入我们</a>
          <a href="#">联系方式</a>
        </div>
      </div>
      <p class="footer-copy">© 2024 臻选精品商城 · 用心甄选每一件好物</p>
    </div>
  </footer>
  <CartDrawer ref="drawerRef" />
  <Toast />
  <BackTop />
</template>

<style scoped>
.app-main {
  min-height: calc(100vh - 180px);
}
.app-footer {
  background: #16181f;
  color: #9aa0b0;
  margin-top: 80px;
}
.footer-inner {
  padding: 48px 24px 24px;
  position: relative;
}
.footer-brand {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 32px;
}
.footer-logo {
  width: 46px;
  height: 46px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f0c36d, #d4a03c);
  color: #16181f;
  font-size: 24px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}
.footer-brand h3 {
  color: #fff;
  font-size: 18px;
}
.footer-brand p {
  font-size: 13px;
  margin-top: 4px;
}
.footer-links {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  padding-bottom: 32px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}
.footer-links h4 {
  color: #e8eaef;
  font-size: 14px;
  margin-bottom: 12px;
}
.footer-links a {
  display: block;
  color: #9aa0b0;
  font-size: 13px;
  line-height: 2.1;
  text-decoration: none;
}
.footer-links a:hover {
  color: #f0c36d;
}
.footer-copy {
  text-align: center;
  font-size: 12px;
  padding-top: 20px;
  color: #6b7180;
}
@media (max-width: 768px) {
  .footer-links {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}
</style>
