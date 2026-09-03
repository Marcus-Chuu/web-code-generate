<script setup>
import { ref } from 'vue'
import { products } from '@/data/products'
import ProductCard from '@/components/ProductCard.vue'
import { useCartStore } from '@/store/cart'
import { useToast } from '@/store/toast'
import { useRouter } from 'vue-router'

const cart = useCartStore()
const { show } = useToast()
const router = useRouter()

const hotProducts = products.filter(p => p.tag).slice(0, 4)
const freshProducts = products.slice(0, 8)

const services = [
  { icon: '🛡️', title: '正品保障', desc: '严选品牌直供，假一赔十' },
  { icon: '🚚', title: '极速物流', desc: '全国 48 小时送达，次日达覆盖 200+ 城市' },
  { icon: '↩️', title: '七天无理由', desc: '七天无理由退换，购物无忧' },
  { icon: '💬', title: '专属客服', desc: '一对一专属服务，24 小时在线' }
]

const bannerImages = [
  'https://picsum.photos/seed/banner1/1600/640',
  'https://picsum.photos/seed/banner2/1600/640',
  'https://picsum.photos/seed/banner3/1600/640'
]
const bannerTexts = [
  { title: '年度臻选大促', sub: '全场低至 5 折 · 好物不贵', btn: '立即抢购' },
  { title: '数码潮玩上新', sub: '新品首发 · 限量预售', btn: '去逛逛' },
  { title: '品质家居节', sub: '打造理想生活空间', btn: '了解更多' }
]
const currentSlide = ref(0)
let timer = null

function goSlide(i) {
  currentSlide.value = (i + bannerImages.length) % bannerImages.length
}
function nextSlide() {
  goSlide(currentSlide.value + 1)
}
function startAuto() {
  timer = setInterval(nextSlide, 4000)
}
function stopAuto() {
  clearInterval(timer)
}
startAuto()

function addToCart(product) {
  cart.addToCart(product, 1)
  show(`已加入购物车：${product.name}`)
}
function viewAll() {
  router.push('/products')
}
</script>

<template>
  <div>
    <!-- 轮播 -->
    <section class="banner" @mouseenter="stopAuto" @mouseleave="startAuto">
      <div class="banner-track" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
        <div
          v-for="(img, i) in bannerImages"
          :key="i"
          class="banner-slide"
          :style="{ backgroundImage: `url(${img})` }"
        >
          <div class="banner-overlay"></div>
          <div class="container banner-content">
            <h2>{{ bannerTexts[i].title }}</h2>
            <p>{{ bannerTexts[i].sub }}</p>
            <button class="btn-primary banner-btn" @click="viewAll">
              {{ bannerTexts[i].btn }} →
            </button>
          </div>
        </div>
      </div>
      <button class="banner-arrow left" @click="goSlide(currentSlide - 1)">‹</button>
      <button class="banner-arrow right" @click="goSlide(currentSlide + 1)">›</button>
      <div class="banner-dots">
        <span
          v-for="(_, i) in bannerImages"
          :key="i"
          class="dot"
          :class="{ active: currentSlide === i }"
          @click="goSlide(i)"
        ></span>
      </div>
    </section>

    <!-- 服务保障 -->
    <section id="services" class="container services">
      <div v-for="s in services" :key="s.title" class="service-item">
        <span class="service-icon">{{ s.icon }}</span>
        <div>
          <h4>{{ s.title }}</h4>
          <p>{{ s.desc }}</p>
        </div>
      </div>
    </section>

    <!-- 热销商品 -->
    <section class="container section">
      <div class="section-head">
        <div>
          <h2 class="section-title">🔥 本周热销</h2>
          <p class="section-sub">口碑爆款，备受青睐</p>
        </div>
        <button class="text-btn" @click="viewAll">查看全部 →</button>
      </div>
      <div class="product-grid">
        <ProductCard
          v-for="p in hotProducts"
          :key="p.id"
          :product="p"
          @add-to-cart="addToCart"
        />
      </div>
    </section>

    <!-- 臻选好物 -->
    <section id="products" class="container section">
      <div class="section-head">
        <div>
          <h2 class="section-title">✨ 臻选好物</h2>
          <p class="section-sub">精挑细选，只为品质生活</p>
        </div>
        <button class="text-btn" @click="viewAll">查看全部 →</button>
      </div>
      <div class="product-grid">
        <ProductCard
          v-for="p in freshProducts"
          :key="p.id"
          :product="p"
          @add-to-cart="addToCart"
        />
      </div>
    </section>

    <!-- 品牌故事 -->
    <section class="brand-story">
      <div class="container story-inner">
        <div class="story-text">
          <h2>关于臻选精品</h2>
          <p>
            我们是一群对生活品质有着极致追求的人。从源头严选全球好物，
            与数百家品牌与产地直供建立合作，剔除中间环节，让每一件商品都物超所值。
          </p>
          <p>
            臻选的每一件商品，都经过资深买手团队的反复比对与真实体验，
            从材质、工艺到售后体验层层把关，只为把真正值得的东西带到你面前。
          </p>
          <button class="btn-primary" @click="viewAll">开始臻选之旅</button>
        </div>
        <img
          src="https://picsum.photos/seed/story/640/480"
          alt="品牌故事"
          class="story-img"
        />
      </div>
    </section>
  </div>
</template>

<style scoped>
/* 轮播 */
.banner {
  position: relative;
  overflow: hidden;
  height: 400px;
}
.banner-track {
  display: flex;
  height: 100%;
  transition: transform 0.5s ease;
}
.banner-slide {
  min-width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
}
.banner-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(90deg, rgba(10, 12, 18, 0.72) 0%, rgba(10, 12, 18, 0.3) 60%, rgba(10, 12, 18, 0.1) 100%);
}
.banner-content {
  position: relative;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #fff;
}
.banner-content h2 {
  font-size: 44px;
  font-weight: 800;
  letter-spacing: 2px;
  margin-bottom: 14px;
  text-shadow: 0 3px 14px rgba(0, 0, 0, 0.3);
}
.banner-content p {
  font-size: 18px;
  color: #e8e6df;
  margin-bottom: 28px;
}
.banner-dots {
  position: absolute;
  bottom: 22px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  z-index: 5;
}
.banner-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 42px;
  height: 42px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 50%;
  background: rgba(22, 24, 31, 0.35);
  color: #fff;
  font-size: 22px;
  cursor: pointer;
  z-index: 6;
  transition: background 0.2s;
}
.banner-arrow:hover {
  background: rgba(22, 24, 31, 0.65);
}
.banner-arrow.left {
  left: 24px;
}
.banner-arrow.right {
  right: 24px;
}
.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.45);
  cursor: pointer;
  transition: all 0.3s;
}
.dot.active {
  background: var(--accent-light);
  width: 26px;
  border-radius: 6px;
}

/* 服务 */
.services {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 22px;
  margin-top: -36px;
  position: relative;
  z-index: 4;
}
.service-item {
  display: flex;
  gap: 14px;
  align-items: flex-start;
  background: var(--card-bg);
  border-radius: 14px;
  padding: 22px 20px;
  box-shadow: var(--shadow);
}
.service-icon {
  font-size: 28px;
}
.service-item h4 {
  font-size: 15px;
  margin-bottom: 6px;
}
.service-item p {
  font-size: 12px;
  color: var(--text-light);
  line-height: 1.6;
}

/* 通用区块 */
.section {
  padding-top: 56px;
}
.section-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 24px;
}
.text-btn {
  border: none;
  background: none;
  color: var(--accent);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  padding-bottom: 6px;
}
.text-btn:hover {
  text-decoration: underline;
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 22px;
}

/* 品牌故事 */
.brand-story {
  margin-top: 70px;
  background: var(--primary);
  color: #fff;
}
.story-inner {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 48px;
  align-items: center;
  padding-top: 64px;
  padding-bottom: 64px;
}
.story-text h2 {
  font-size: 30px;
  margin-bottom: 20px;
}
.story-text p {
  color: #b8bdc9;
  font-size: 15px;
  line-height: 1.9;
  margin-bottom: 16px;
}
.story-img {
  width: 100%;
  border-radius: 18px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.4);
}

@media (max-width: 1024px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .services {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 768px) {
  .banner {
    height: 300px;
  }
  .banner-content h2 {
    font-size: 30px;
  }
  .banner-content p {
    font-size: 15px;
  }
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .services {
    grid-template-columns: 1fr;
    margin-top: -20px;
  }
  .story-inner {
    grid-template-columns: 1fr;
    gap: 28px;
  }
}
@media (max-width: 480px) {
  .product-grid {
    grid-template-columns: 1fr 1fr;
    gap: 12px;
  }
}
</style>
