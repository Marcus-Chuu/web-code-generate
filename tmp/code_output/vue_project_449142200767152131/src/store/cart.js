import { ref, computed } from 'vue'

// 全局购物车状态（单例模块）
const cartItems = ref([])

// 从本地存储恢复
try {
  const saved = JSON.parse(localStorage.getItem('zx-cart') || '[]')
  if (Array.isArray(saved)) cartItems.value = saved
} catch (e) {
  cartItems.value = []
}

function persist() {
  localStorage.setItem('zx-cart', JSON.stringify(cartItems.value))
}

export function useCartStore() {
  const totalCount = computed(() =>
    cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
  )
  const totalPrice = computed(() =>
    cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  )
  const isEmpty = computed(() => cartItems.value.length === 0)

  function findItem(id) {
    return cartItems.value.find(item => item.id === id)
  }

  function addToCart(product, quantity = 1) {
    const existing = findItem(product.id)
    if (existing) {
      existing.quantity += quantity
    } else {
      cartItems.value.push({
        id: product.id,
        name: product.name,
        price: product.price,
        image: product.image,
        category: product.category,
        quantity
      })
    }
    persist()
  }

  function removeFromCart(id) {
    cartItems.value = cartItems.value.filter(item => item.id !== id)
    persist()
  }

  function updateQuantity(id, quantity) {
    const item = findItem(id)
    if (!item) return
    item.quantity = Math.max(1, quantity)
    persist()
  }

  function clearCart() {
    cartItems.value = []
    persist()
  }

  return {
    cartItems,
    totalCount,
    totalPrice,
    isEmpty,
    addToCart,
    removeFromCart,
    updateQuantity,
    clearCart
  }
}
