<script setup>
const guarantees = [
  {
    icon: '🛡️',
    title: '正品保障',
    desc: '所有商品均来自品牌官方或授权供应商直供，每一件商品均可溯源，假一赔十。',
    points: ['品牌授权书公示', '全程防伪溯源', '假一赔十承诺']
  },
  {
    icon: '🚚',
    title: '极速物流',
    desc: '全国主流城市 48 小时内送达，200+ 城市支持次日达，冷链商品专线配送。',
    points: ['48 小时极速达', '次日达覆盖 200+ 城', '全程物流实时追踪']
  },
  {
    icon: '↩️',
    title: '七天无理由退换',
    desc: '自签收之日起 7 天内，商品完好、配件齐全即可享受无理由退换服务。',
    points: ['7 天无理由退换', '退货免运费', '极速审核退款']
  },
  {
    icon: '💬',
    title: '专属客服',
    desc: '一对一的专属服务顾问，7×24 小时在线，售前咨询、售后处理一站搞定。',
    points: ['7×24 小时在线', '专属服务顾问', '售后 1 小时响应']
  }
]

const processes = [
  { step: '01', title: '严选供应商', desc: '实地考察工厂与品牌方，审核资质与生产能力' },
  { step: '02', title: '多重质检', desc: '入库前 36 项检测，抽检合格率必须达到 100%' },
  { step: '03', title: '专业仓储', desc: '恒温恒湿仓库，分区管理，杜绝商品受潮变质' },
  { step: '04', title: '贴心配送', desc: '专业包装减震防撞，快递全程保险护航' },
  { step: '05', title: '无忧售后', desc: '售后问题 1 小时响应，7 天无理由退换' }
]

const faqs = [
  { q: '如何确保买到的是正品？', a: '我们与品牌方、授权经销商直接签约合作，所有商品均提供正规渠道的授权证明，支持扫码验真与全程溯源，若鉴定为非正品，我们将十倍赔偿。' },
  { q: '退换货的流程是怎样的？', a: '自签收之日起 7 天内，您可在订单详情页发起退换申请，商品保持完好即可。审核通过后我们为您安排上门取件，运费由平台承担，退款将在验收后 24 小时内原路退回。' },
  { q: '配送时间大概是多久？', a: '大部分现货商品将在付款后 24 小时内发出，全国主流城市 48 小时内送达，部分偏远地区预计 3-5 天。您可以随时在订单页面查看实时物流轨迹。' },
  { q: '会员有什么专属权益？', a: '注册即享会员价与积分累积，积分可兑换优惠券与臻选好礼；升级高级会员还可享受专属客服、生日礼包、新品优先购等权益。' }
]
</script>

<template>
  <div class="quality-page">
    <!-- 页头横幅 -->
    <section class="quality-hero">
      <div class="container hero-inner">
        <h1>品质保障</h1>
        <p>每一件好物，都经过层层把关 · 用心守护您的每一次购物</p>
      </div>
    </section>

    <!-- 四大保障 -->
    <section class="container section">
      <div class="guarantee-grid">
        <div v-for="g in guarantees" :key="g.title" class="guarantee-card">
          <span class="g-icon">{{ g.icon }}</span>
          <h3>{{ g.title }}</h3>
          <p class="g-desc">{{ g.desc }}</p>
          <ul>
            <li v-for="point in g.points" :key="point">✓ {{ point }}</li>
          </ul>
        </div>
      </div>
    </section>

    <!-- 品控流程 -->
    <section class="process-section">
      <div class="container">
        <div class="section-head">
          <h2>臻选品控流程</h2>
          <p>从源头到您手中，每一步都严格把关</p>
        </div>
        <div class="process-list">
          <div v-for="p in processes" :key="p.step" class="process-item">
            <span class="process-step">{{ p.step }}</span>
            <h4>{{ p.title }}</h4>
            <p>{{ p.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 常见问题 -->
    <section class="container section faq-section">
      <div class="section-head">
        <h2>常见问题</h2>
        <p>关于品质与售后，您可能想知道的都在这里</p>
      </div>
      <div class="faq-list">
        <details v-for="f in faqs" :key="f.q" class="faq-item">
          <summary>{{ f.q }}</summary>
          <p>{{ f.a }}</p>
        </details>
      </div>
    </section>

    <!-- 底部行动 -->
    <section class="container section">
      <div class="cta-box">
        <h3>品质好物，即刻拥有</h3>
        <p>全场精选商品，享受正品保障与无忧售后</p>
        <router-link to="/products" class="btn-primary">前往臻选好物 →</router-link>
      </div>
    </section>
  </div>
</template>

<style scoped>
.quality-hero {
  background: linear-gradient(135deg, #1b1e27, #2c3140);
  color: #fff;
  text-align: center;
  padding: 72px 24px;
}
.hero-inner h1 {
  font-size: 36px;
  letter-spacing: 2px;
}
.hero-inner p {
  color: #aab0c0;
  margin-top: 14px;
  font-size: 16px;
}
.section {
  padding-top: 56px;
}
.section-head {
  text-align: center;
  margin-bottom: 36px;
}
.section-head h2 {
  font-size: 26px;
}
.section-head p {
  color: var(--text-light);
  margin-top: 8px;
}

/* 四大保障 */
.guarantee-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 22px;
}
.guarantee-card {
  background: var(--card-bg);
  border-radius: 16px;
  box-shadow: var(--shadow);
  padding: 30px 24px;
  transition: transform 0.25s, box-shadow 0.25s;
}
.guarantee-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 36px rgba(22, 24, 31, 0.14);
}
.g-icon {
  font-size: 40px;
  display: block;
  margin-bottom: 16px;
}
.guarantee-card h3 {
  font-size: 17px;
  margin-bottom: 10px;
}
.g-desc {
  font-size: 13px;
  color: var(--text-light);
  line-height: 1.7;
  margin-bottom: 14px;
}
.guarantee-card ul {
  list-style: none;
}
.guarantee-card li {
  font-size: 13px;
  color: #2f9e44;
  padding: 3px 0;
}

/* 品控流程 */
.process-section {
  background: var(--card-bg);
  margin-top: 60px;
  padding: 56px 0;
}
.process-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}
.process-item {
  text-align: center;
  padding: 24px 16px;
  border: 1px solid var(--border);
  border-radius: 14px;
  transition: border-color 0.2s, transform 0.2s;
}
.process-item:hover {
  border-color: var(--accent);
  transform: translateY(-4px);
}
.process-step {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--accent-light), var(--accent));
  color: #16181f;
  font-size: 15px;
  font-weight: 800;
  margin-bottom: 14px;
}
.process-item h4 {
  font-size: 15px;
  margin-bottom: 8px;
}
.process-item p {
  font-size: 12px;
  color: var(--text-light);
  line-height: 1.6;
}

/* 常见问题 */
.faq-list {
  max-width: 860px;
  margin: 0 auto;
}
.faq-item {
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 18px 24px;
  margin-bottom: 14px;
  cursor: pointer;
}
.faq-item summary {
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  outline: none;
}
.faq-item p {
  font-size: 14px;
  color: var(--text-light);
  line-height: 1.8;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed var(--border);
}

/* 行动号召 */
.cta-box {
  text-align: center;
  background: linear-gradient(135deg, #1b1e27, #2c3140);
  color: #fff;
  border-radius: 20px;
  padding: 56px 24px;
}
.cta-box h3 {
  font-size: 26px;
  margin-bottom: 10px;
}
.cta-box p {
  color: #aab0c0;
  margin-bottom: 24px;
}

@media (max-width: 1024px) {
  .guarantee-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .process-list {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 640px) {
  .hero-inner h1 {
    font-size: 28px;
  }
  .guarantee-grid,
  .process-list {
    grid-template-columns: 1fr;
  }
}
</style>
