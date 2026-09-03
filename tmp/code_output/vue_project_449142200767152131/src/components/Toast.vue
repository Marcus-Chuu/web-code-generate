<script setup>
import { useToast } from '@/store/toast'

const { toasts } = useToast()
</script>

<template>
  <div class="toast-wrap">
    <transition-group name="toast">
      <div v-for="t in toasts" :key="t.id" class="toast">{{ t.message }}</div>
    </transition-group>
  </div>
</template>

<style scoped>
.toast-wrap {
  position: fixed;
  top: 84px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 300;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  pointer-events: none;
}
.toast {
  background: rgba(22, 24, 31, 0.92);
  color: #fff;
  font-size: 14px;
  padding: 10px 22px;
  border-radius: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.25);
  white-space: nowrap;
}
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}
.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateY(-14px);
}
</style>
