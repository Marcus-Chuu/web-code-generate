<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  ArrowRightOutlined,
  BulbOutlined,
  CodeOutlined,
  RocketOutlined,
  CheckOutlined,
  ThunderboltOutlined,
  PlusOutlined,
} from '@ant-design/icons-vue'
import AppCard from '@/components/AppCard.vue'
import { appService } from '@/api/services'
import { apiErrorMessage } from '@/api/client'
import { resolvePreviewUrl, withCacheBuster } from '@/utils/preview'
import { useLoginUserStore } from '@/stores/loginUser'
import type { AppVO } from '@/types/domain'

const router = useRouter()
const route = useRoute()
const userStore = useLoginUserStore()
const prompt = ref('')
const agentMode = ref(false)
const creating = ref(false)
const loading = ref(false)
const featured = ref<AppVO[]>([])
const examples = [
  '做一个极简风个人作品集，突出项目经历',
  '生成一个咖啡品牌落地页，暖色调并带商品卡片',
  '设计一个 SaaS 数据分析后台首页',
]

const createApp = async () => {
  const value = prompt.value.trim()
  if (!value) return message.warning('先描述一下你想创建的页面')
  if (!userStore.isLoggedIn) {
    await router.push({
      path: '/user/login',
      query: { redirect: '/', prompt: value, agent: agentMode.value ? '1' : '0' },
    })
    return
  }
  creating.value = true
  try {
    const id = await appService.create(value)
    await router.push({
      path: `/app/chat/${id}`,
      query: { start: '1', agent: agentMode.value ? '1' : '0' },
    })
  } catch (error) {
    message.error(apiErrorMessage(error, '创建应用失败'))
  } finally {
    creating.value = false
  }
}

const openPreview = (app: AppVO) => {
  const url = resolvePreviewUrl(app.id, app.codeGenType)
  if (!url) return message.info('应用还没有可预览的代码')
  window.open(withCacheBuster(url), '_blank', 'noopener')
}

onMounted(async () => {
  if (typeof route.query.prompt === 'string') {
    prompt.value = route.query.prompt
    agentMode.value = route.query.agent === '1'
    await router.replace({ query: {} })
  }
  loading.value = true
  try {
    const page = await appService.listFeatured({
      pageNum: 1,
      pageSize: 8,
      sortField: 'priority',
      sortOrder: 'descend',
    })
    featured.value = page.records || []
  } catch (error) {
    message.error(apiErrorMessage(error, '精选应用加载失败'))
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <main class="home-page">
    <section class="hero">
      <div class="page-shell hero-inner">
        <div class="hero-copy">
          <span class="hero-badge"><span></span> 让好想法，自由生长 <ArrowRightOutlined /></span>
          <h1>
            你的下一件作品，<br />
            <span>从<em>一个想法</em>开始。</span>
          </h1>
          <p>
            把灵感交给 AI，让创作变得简单。<br class="mobile-break" />
            描述、生成、发布，让想法跃然眼前。
          </p>
          <div class="idea-input surface">
            <div class="composer-heading">
              <ThunderboltOutlined /><span>今天，你想创造什么？</span
              ><span class="composer-label">灵感画布</span>
            </div>
            <textarea
              v-model="prompt"
              aria-label="描述你想创建的页面"
              maxlength="1000"
              placeholder="例如：为一家独立咖啡店设计一个温暖、有故事感的品牌官网……"
              @keydown.ctrl.enter.prevent="createApp"
            ></textarea>
            <div class="input-actions">
              <div class="generation-options">
                <label title="开启后，AI 将分步骤规划、生成并检查页面质量">
                  <a-switch v-model:checked="agentMode" size="small" />
                  <span>工作流模式</span>
                </label>
                <span class="shortcut"><kbd>Ctrl</kbd> + <kbd>Enter</kbd></span>
              </div>
              <a-button type="primary" size="large" :loading="creating" @click="createApp"
                >开始创作 <ArrowRightOutlined
              /></a-button>
            </div>
          </div>
          <div class="examples">
            <span>找点灵感</span
            ><button
              v-for="(item, index) in examples"
              :key="item"
              :title="item"
              @click="prompt = item"
            >
              <PlusOutlined />{{ ['个人作品集', '咖啡品牌官网', '数据分析后台'][index] }}
            </button>
          </div>
        </div>
        <div class="inspiration-card" aria-hidden="true">
          <div class="mini-browser"><i></i><i></i><i></i><span>little moments</span></div>
          <div class="mini-poster">
            <span>THE SLOW STUDIO</span><strong>Less, but<br /><em>better.</em></strong>
            <div class="leaf-art"><i></i><i></i><i></i></div>
            <span class="poster-caption">留一点空间，给生活。</span>
          </div>
          <div class="mini-caption"><span class="tiny-dot"></span> 一点灵感，无限可能</div>
        </div>
        <div class="build-card" aria-hidden="true">
          <div class="build-heading"><CodeOutlined /><span>想法正在成形</span><i></i></div>
          <div class="code-lines">
            <span>&lt;<b>your-idea</b>&gt;</span><span>&nbsp; creativity: <em>∞</em>;</span
            ><span>&nbsp; possibilities: <em>endless</em>;</span
            ><span>&lt;/<b>your-idea</b>&gt;</span>
          </div>
          <div class="build-preview">
            <div></div>
            <div></div>
            <div></div>
          </div>
          <div class="build-status">
            <span><CheckOutlined /> 让灵感成为现实</span><span>100%</span>
          </div>
        </div>
      </div>
    </section>

    <section class="steps page-shell">
      <div class="step">
        <span class="step-icon"><BulbOutlined /></span>
        <div>
          <b>描述你的想法</b>
          <p>一句话，勾勒心中的理想页面</p>
        </div>
        <span class="step-number">01</span>
      </div>
      <div class="step">
        <span class="step-icon"><CodeOutlined /></span>
        <div>
          <b>看着灵感成形</b>
          <p>实时生成代码，随时对话调整</p>
        </div>
        <span class="step-number">02</span>
      </div>
      <div class="step">
        <span class="step-icon"><RocketOutlined /></span>
        <div>
          <b>把作品分享给世界</b>
          <p>一键部署上线，完整源码随心用</p>
        </div>
        <span class="step-number">03</span>
      </div>
    </section>

    <section class="page-shell page-section showcase">
      <div class="section-heading">
        <div>
          <span class="eyebrow">MADE WITH 灵构</span>
          <h2>好想法，值得被看见<span class="heading-dot">.</span></h2>
          <p class="page-description">探索社区精选作品，遇见你的下一个灵感。</p>
        </div>
        <span class="collection-note"><span></span> 创意在这里持续生长</span>
      </div>
      <a-skeleton v-if="loading" active :paragraph="{ rows: 8 }" />
      <div v-else-if="featured.length" class="app-grid">
        <AppCard
          v-for="app in featured"
          :key="String(app.id)"
          :app="app"
          show-author
          @open="router.push(`/app/chat/${$event.id}`)"
          @preview="openPreview"
        />
      </div>
      <div v-else class="surface empty-state">
        <BulbOutlined class="empty-icon" />
        <h3>下一个好作品，也许就是你的</h3>
        <p>这里将展示社区精选作品，从上方输入你的第一个想法吧。</p>
      </div>
    </section>
  </main>
</template>

<style scoped>
.home-page {
  overflow: hidden;
}
.hero {
  position: relative;
  padding: 66px 0 42px;
  background: radial-gradient(ellipse at 50% 42%, #eaf2e780, transparent 65%);
}
.hero-inner {
  position: relative;
}
.hero-copy {
  position: relative;
  z-index: 2;
  max-width: 744px;
  margin: auto;
  text-align: center;
}
.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 7px 13px;
  border: 1px solid #dce6dc;
  border-radius: 30px;
  background: #f0f5ed;
  color: #53715e;
  font-size: 12px;
  letter-spacing: 0.08em;
}
.hero-badge > span:first-child {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #749b77;
}
.hero-badge > .anticon {
  font-size: 11px;
}
.hero-copy h1 {
  margin: 24px 0 20px;
  color: #263d32;
  font-size: clamp(38px, 4.3vw, 60px);
  font-weight: 650;
  line-height: 1.35;
  letter-spacing: -0.045em;
}
.hero-copy h1 em {
  position: relative;
  color: #65856a;
  font-style: normal;
}
.hero-copy h1 em::after {
  content: '';
  position: absolute;
  bottom: 1px;
  left: 2px;
  right: 2px;
  height: 7px;
  border-radius: 50%;
  border-top: 2px solid #9eb891;
  transform: rotate(-3deg);
}
.hero-copy > p {
  margin: 0;
  color: #7a8279;
  font-size: 15px;
  line-height: 1.9;
}
.mobile-break {
  display: none;
}
.idea-input {
  margin-top: 30px;
  padding: 21px 22px 15px;
  text-align: left;
  border: 1px solid #d8e2d5;
  border-radius: 20px;
  background: #fff;
  box-shadow:
    0 10px 36px #435b3910,
    0 0 0 5px #edf1e980;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
}
.idea-input:focus-within {
  border-color: #789578;
  box-shadow:
    0 10px 36px #435b3910,
    0 0 0 5px #dfeadd80;
}
.composer-heading {
  display: flex;
  align-items: center;
  gap: 9px;
  color: #354d3f;
  font-size: 14px;
  font-weight: 600;
}
.composer-heading > .anticon {
  color: #6b8c61;
  font-size: 17px;
}
.composer-label {
  margin-left: auto;
  color: #9ba396;
  font-size: 10px;
  font-weight: 400;
  letter-spacing: 0.12em;
}
textarea {
  display: block;
  width: 100%;
  min-height: 101px;
  padding: 17px 0;
  resize: vertical;
  color: #35483b;
  font-size: 14px;
  line-height: 1.8;
  background: transparent;
  border: 0;
  outline: 0;
}
textarea::placeholder {
  color: #9ba196;
}
.input-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding-top: 13px;
  border-top: 1px solid #eff1eb;
}
.generation-options {
  display: flex;
  align-items: center;
  gap: 18px;
  color: #9aa093;
  font-size: 11px;
}
.generation-options label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #747d70;
  font-size: 12px;
  cursor: pointer;
}
.shortcut kbd {
  font: inherit;
}
.input-actions :deep(.ant-btn-primary) {
  height: 40px;
  padding: 0 19px;
  font-size: 13px;
  box-shadow: 0 3px 8px #36583d14;
}
.examples {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 20px;
  color: #92988b;
  font-size: 11px;
}
.examples > span {
  margin-right: 3px;
}
.examples button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 7px 11px;
  color: #727c6d;
  background: #ffffff80;
  border: 1px solid #e4e8de;
  border-radius: 8px;
  cursor: pointer;
  transition:
    background 0.2s,
    border-color 0.2s;
}
.examples button:hover {
  color: var(--accent);
  border-color: #a8bba2;
  background: #edf3e9;
}
.inspiration-card,
.build-card {
  position: absolute;
  z-index: 1;
  width: 178px;
  overflow: hidden;
  border: 1px solid #e1e6dc;
  border-radius: 12px;
  background: #fffdf8;
  box-shadow: 0 14px 35px #3f503c09;
  pointer-events: none;
}
.inspiration-card {
  top: 153px;
  left: -2px;
  transform: rotate(-8deg);
}
.mini-browser {
  display: flex;
  align-items: center;
  gap: 3px;
  height: 25px;
  padding: 0 9px;
  border-bottom: 1px solid #eeece3;
}
.mini-browser > i {
  width: 4px;
  height: 4px;
  background: #d6d7cc;
  border-radius: 50%;
}
.mini-browser > span {
  margin-left: auto;
  font-size: 6px;
  color: #919584;
}
.mini-poster {
  position: relative;
  height: 190px;
  padding: 20px 17px;
  overflow: hidden;
  background: #f2eedf;
}
.mini-poster > span {
  font-size: 6px;
  color: #8a8a71;
  letter-spacing: 0.13em;
}
.mini-poster strong {
  position: relative;
  z-index: 1;
  display: block;
  margin-top: 12px;
  color: #46553b;
  font:
    30px/1.02 Georgia,
    serif;
  letter-spacing: -0.06em;
}
.mini-poster strong em {
  font-weight: normal;
}
.mini-poster .poster-caption {
  position: absolute;
  bottom: 14px;
  font-size: 7px;
}
.leaf-art {
  position: absolute;
  right: 12px;
  bottom: 22px;
  width: 55px;
  height: 84px;
  border-left: 1px solid #6f7953;
  transform: rotate(25deg);
}
.leaf-art i {
  position: absolute;
  left: 0;
  width: 29px;
  height: 54px;
  background: #8c9c72;
  border-radius: 0 100% 0 100%;
  transform-origin: left bottom;
}
.leaf-art i:nth-child(2) {
  top: 20px;
  left: -29px;
  background: #b1bb92;
  transform: scaleX(-1) rotate(5deg);
}
.leaf-art i:nth-child(3) {
  top: -13px;
  width: 20px;
  height: 36px;
  background: #697e53;
}
.mini-caption {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px;
  color: #8d9586;
  font-size: 8px;
}
.tiny-dot {
  width: 5px;
  height: 5px;
  background: #8fa881;
  border-radius: 50%;
}
.build-card {
  top: 202px;
  right: -7px;
  width: 186px;
  padding: 13px;
  transform: rotate(7deg);
  background: #fff;
}
.build-heading {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 9px;
  color: #67745f;
}
.build-heading > i {
  width: 5px;
  height: 5px;
  margin-left: auto;
  background: #b0c69d;
  border-radius: 50%;
}
.code-lines {
  display: grid;
  gap: 7px;
  padding: 18px 0;
  font:
    8px/1.5 Consolas,
    monospace;
  color: #969d8c;
}
.code-lines b {
  color: #66876b;
  font-weight: 400;
}
.code-lines em {
  color: #b29c66;
  font-style: normal;
}
.build-preview {
  display: flex;
  gap: 5px;
  height: 53px;
  padding: 7px;
  background: #f7f8f3;
  border-radius: 5px;
}
.build-preview > div {
  flex: 1;
  background: #dbe5d2;
  border-radius: 3px;
}
.build-preview > div:nth-child(2) {
  background: #eee6d2;
}
.build-preview > div:nth-child(3) {
  background: #e1e5d9;
}
.build-status {
  display: flex;
  justify-content: space-between;
  margin-top: 13px;
  color: #7d9471;
  font-size: 7px;
}
.steps {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  padding: 28px 0;
  border-top: 1px solid #e4e8de;
  border-bottom: 1px solid #e4e8de;
}
.step {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 0 30px;
}
.step + .step {
  border-left: 1px solid #e4e8de;
}
.step:first-child {
  padding-left: 0;
}
.step:last-child {
  padding-right: 0;
}
.step-icon {
  flex-shrink: 0;
  display: grid;
  place-items: center;
  width: 43px;
  height: 43px;
  background: #edf1e6;
  border: 1px solid #e5eadc;
  border-radius: 13px;
  color: #738660;
  font-size: 20px;
}
.step:nth-child(2) .step-icon {
  background: #f4efe3;
  color: #a28c57;
  border-color: #eee8d9;
}
.step:nth-child(3) .step-icon {
  background: #eaf0ed;
  color: #698779;
  border-color: #e0e9e2;
}
.step b {
  color: #475543;
  font-size: 13px;
  font-weight: 600;
}
.step p {
  margin: 5px 0 0;
  color: #7a8472;
  font-size: 12px;
  line-height: 1.6;
}
.step-number {
  margin-left: auto;
  color: #c0c8b8;
  font:
    12px Georgia,
    serif;
}
.showcase {
  padding-top: 50px;
}
.showcase .section-heading {
  margin-bottom: 27px;
}
.showcase h2 {
  margin: 10px 0 8px;
  font-size: 27px;
  font-weight: 600;
  color: #344b3c;
}
.heading-dot {
  color: #91aa7c;
  margin-left: 3px;
}
.showcase .page-description {
  font-size: 12px;
}
.collection-note {
  display: flex;
  align-items: center;
  gap: 7px;
  padding-bottom: 5px;
  color: #909888;
  font-size: 11px;
}
.collection-note > span {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: #9caf8e;
}
.app-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 22px;
}
.empty-icon {
  color: #829575;
  font-size: 28px;
}
.empty-state h3 {
  margin: 16px 0 8px;
  color: #56684c;
  font-size: 17px;
}
.empty-state p {
  margin: 0;
  font-size: 13px;
}
@media (max-width: 1200px) {
  .inspiration-card,
  .build-card {
    display: none;
  }
}
@media (max-width: 1000px) {
  .app-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .step {
    padding: 0 18px;
    gap: 11px;
  }
  .step-number {
    display: none;
  }
}
@media (max-width: 680px) {
  .hero {
    padding: 40px 0 30px;
  }
  .hero-copy h1 {
    font-size: clamp(32px, 8.7vw, 48px);
    margin-top: 22px;
    line-height: 1.4;
  }
  .hero-copy > p {
    font-size: 12px;
  }
  .mobile-break {
    display: block;
  }
  .hero-badge {
    font-size: 10px;
  }
  .idea-input {
    margin-top: 25px;
    padding: 18px 16px 13px;
    border-radius: 16px;
  }
  .composer-heading {
    font-size: 13px;
  }
  .composer-label {
    font-size: 9px;
  }
  textarea {
    min-height: 116px;
    font-size: 13px;
  }
  .generation-options > .shortcut {
    display: none;
  }
  .input-actions {
    gap: 8px;
  }
  .input-actions :deep(.ant-btn-primary) {
    padding: 0 13px;
  }
  .examples {
    gap: 6px;
    font-size: 10px;
  }
  .examples > span {
    width: 100%;
    margin: 0 0 3px;
  }
  .examples button {
    padding: 7px 9px;
  }
  .steps {
    grid-template-columns: 1fr;
    gap: 21px;
    padding: 25px 12px;
  }
  .step,
  .step:first-child,
  .step:last-child {
    padding: 0;
  }
  .step + .step {
    border: 0;
  }
  .step-number {
    display: block;
  }
  .step b {
    font-size: 13px;
  }
  .step p {
    font-size: 11px;
  }
  .step-icon {
    width: 39px;
    height: 39px;
    font-size: 18px;
  }
  .showcase {
    padding-top: 34px;
  }
  .showcase h2 {
    font-size: 23px;
  }
  .collection-note {
    display: none;
  }
  .app-grid {
    grid-template-columns: 1fr;
    gap: 18px;
  }
}
</style>
