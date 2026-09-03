import { ref } from 'vue'

// 全局轻提示状态（单例模块）
const toasts = ref([])
let seed = 0

export function useToast() {
  function show(message, duration = 2000) {
    const id = ++seed
    toasts.value.push({ id, message })
    setTimeout(() => {
      toasts.value = toasts.value.filter(t => t.id !== id)
    }, duration)
  }
  return { toasts, show }
}
