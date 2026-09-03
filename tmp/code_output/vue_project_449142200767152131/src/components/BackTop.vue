<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const visible = ref(false)

function onScroll() {
  visible.value = window.scrollY > 400
}

function toTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => window.addEventListener('scroll', onScroll))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<template>
  <transition name="fade">
    <button v-if="visible" class="back-top" title="回到顶部" @click="toTop">↑</button>
  </transition>
</template>

<style scoped>
.back-top {
  position: fixed;
  right: 28px;
  bottom: 40px;
  width: 46px;
  height: 46px;
  border: none;
  border-radius: 50%;
  background: var(--primary);
  color: var(--accent-light);
  font-size: 20px;
  cursor: pointer;
  box-shadow: 0 8px 22px rgba(22, 24, 31, 0.3);
  transition: transform 0.2s, background 0.2s;
  z-index: 150;
}
.back-top:hover {
  transform: translateY(-4px);
  background: #262a36;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
@media (max-width: 768px) {
  .back-top {
    right: 16px;
    bottom: 24px;
    width: 40px;
    height: 40px;
    font-size: 17px;
  }
}
</style>
