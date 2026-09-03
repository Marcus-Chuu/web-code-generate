<script setup>
import { ref } from 'vue'

defineProps({
  cartCount: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['search', 'open-cart'])

const keyword = ref('')

function submitSearch() {
  emit('search', keyword.value)
  keyword.value = ''
}
</script>

<template>
  <header class="header">
    <div class="container header-inner">
      <router-link to="/" class="brand">
        <span class="brand-logo">臻</span>
        <span class="brand-text">臻选精品</span>
      </router-link>

      <form class="search-box" @submit.prevent="submitSearch">
        <input
          v-model="keyword"
          type="text"
          placeholder="搜索臻选好物…"
          class="search-input"
        />
        <button type="submit" class="search-btn">🔍</button>
      </form>

      <nav class="nav-links">
        <router-link to="/" exact-active-class="active">首页</router-link>
        <router-link to="/quality" active-class="active">品质保障</router-link>
        <router-link to="/products" active-class="active">臻选好物</router-link>
      </nav>

      <div class="header-right">
        <button class="cart-btn" @click="emit('open-cart')">
          <span class="cart-icon">🛒</span>
          <span class="cart-label">购物车</span>
          <span v-if="cartCount > 0" class="cart-badge">{{ cartCount > 99 ? '99+' : cartCount }}</span>
        </button>
      </div>
    </div>
  </header>
</template>

<style scoped>
.header {
  background: var(--primary);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}
.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  height: 64px;
}
.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  flex-shrink: 0;
}
.brand-logo {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, #f0c36d, #d4a03c);
  color: #16181f;
  font-size: 19px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
}
.brand-text {
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 1px;
}
.search-box {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 30px;
  padding: 0 6px 0 16px;
  flex: 1;
  max-width: 360px;
  transition: background 0.2s;
}
.search-box:focus-within {
  background: rgba(255, 255, 255, 0.2);
}
.search-input {
  flex: 1;
  background: none;
  border: none;
  outline: none;
  color: #fff;
  font-size: 14px;
  height: 36px;
}
.search-input::placeholder {
  color: rgba(255, 255, 255, 0.55);
}
.search-btn {
  width: 30px;
  height: 30px;
  border: none;
  border-radius: 50%;
  background: var(--accent);
  cursor: pointer;
  font-size: 13px;
  flex-shrink: 0;
}
.nav-links {
  display: flex;
  gap: 28px;
  flex-shrink: 0;
}
.nav-links a {
  color: #b8bdc9;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.2s;
}
.nav-links a:hover,
.nav-links a.active {
  color: var(--accent-light);
}
.header-right {
  flex-shrink: 0;
}
.cart-btn {
  position: relative;
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 30px;
  padding: 8px 18px;
  color: #fff;
  text-decoration: none;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}
.cart-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}
.cart-badge {
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  border-radius: 9px;
  background: var(--accent);
  color: #16181f;
  font-size: 11px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}
@media (max-width: 900px) {
  .search-box {
    order: 3;
    max-width: 100%;
    flex-basis: 100%;
  }
  .header-inner {
    flex-wrap: wrap;
    height: auto;
    padding: 10px 16px;
    row-gap: 10px;
  }
  .nav-links {
    display: none;
  }
}
@media (max-width: 480px) {
  .cart-label {
    display: none;
  }
}
</style>
