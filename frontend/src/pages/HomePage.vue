<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { ArrowRightOutlined, BulbOutlined, CodeOutlined, RocketOutlined } from '@ant-design/icons-vue'
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
const examples = ['做一个极简风个人作品集，突出项目经历', '生成一个咖啡品牌落地页，暖色调并带商品卡片', '设计一个 SaaS 数据分析后台首页']

const createApp = async () => {
  const value = prompt.value.trim()
  if (!value) return message.warning('先描述一下你想创建的页面')
  if (!userStore.isLoggedIn) {
    await router.push({ path: '/user/login', query: { redirect: '/', prompt: value, agent: agentMode.value ? '1' : '0' } })
    return
  }
  creating.value = true
  try {
    const id = await appService.create(value)
    await router.push({ path: `/app/chat/${id}`, query: { start: '1', agent: agentMode.value ? '1' : '0' } })
  } catch (error) { message.error(apiErrorMessage(error, '创建应用失败')) }
  finally { creating.value = false }
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
    const page = await appService.listFeatured({ pageNum: 1, pageSize: 8, sortField: 'priority', sortOrder: 'descend' })
    featured.value = page.records || []
  } catch (error) { message.error(apiErrorMessage(error, '精选应用加载失败')) }
  finally { loading.value = false }
})
</script>

<template>
  <main>
    <section class="hero">
      <div class="hero-orb orb-one"></div><div class="hero-orb orb-two"></div>
      <div class="page-shell hero-inner">
        <div class="hero-copy">
          <span class="eyebrow">AI Website Builder</span>
          <h1>
            <span>说出灵感，</span>
            <span class="title-second-line"><em>编织</em>成真实页面。</span>
          </h1>
          <p>无需从空白画布开始。用自然语言描述产品，AI 会实时生成代码、展示预览，并随你的反馈持续迭代。</p>
          <div class="idea-input surface">
            <textarea v-model="prompt" maxlength="1000" placeholder="例如：为一家独立咖啡店设计一个有故事感的品牌官网……" @keydown.ctrl.enter.prevent="createApp"></textarea>
            <div class="input-actions">
              <div class="generation-options">
                <label title="使用 LangGraph4j 多步骤规划、生成和质量检查">
                  <a-switch v-model:checked="agentMode" size="small" />
                  <span>工作流模式</span>
                </label>
                <span>Ctrl + Enter 发送</span>
              </div>
              <a-button type="primary" size="large" :loading="creating" @click="createApp">开始生成 <ArrowRightOutlined /></a-button>
            </div>
          </div>
          <div class="examples">
            <span>试试：</span><button v-for="item in examples" :key="item" @click="prompt = item">{{ item }}</button>
          </div>
        </div>
        <div class="hero-visual" aria-hidden="true">
          <div class="browser-card">
            <div class="browser-top"><i></i><i></i><i></i><span>your-idea.app</span></div>
            <div class="mock-nav"></div><div class="mock-title"></div><div class="mock-text"></div>
            <div class="mock-grid"><i></i><i></i><i></i></div>
          </div>
          <span class="float-label label-ai">AI 正在构建 ···</span>
          <span class="float-label label-code">&lt;/&gt; Vue</span>
        </div>
      </div>
    </section>

    <section class="steps page-shell">
      <div><BulbOutlined /><b>描述想法</b><span>一句话定义页面目标与风格</span></div>
      <i></i><div><CodeOutlined /><b>实时生成</b><span>像打字一样看见代码构建过程</span></div>
      <i></i><div><RocketOutlined /><b>发布使用</b><span>一键部署并下载完整源代码</span></div>
    </section>

    <section class="page-shell page-section showcase">
      <div class="section-heading"><div><span class="eyebrow">Community Picks</span><h2>本周精选灵感</h2></div><p class="page-description">看看其他创作者最近完成了什么</p></div>
      <a-skeleton v-if="loading" active :paragraph="{ rows: 8 }" />
      <div v-else-if="featured.length" class="app-grid">
        <AppCard v-for="app in featured" :key="String(app.id)" :app="app" show-author @open="router.push(`/app/chat/${$event.id}`)" @preview="openPreview" />
      </div>
      <div v-else class="surface empty-state">还没有精选应用，成为第一个创作者吧。</div>
    </section>
  </main>
</template>

<style scoped>
.hero { position: relative; overflow: hidden; padding: 84px 0 76px; background: #f6f6f2; }
.hero-inner { position: relative; z-index: 1; display: grid; grid-template-columns: minmax(0,1.08fr) minmax(360px,.92fr); align-items: center; gap: 64px; }
.hero-copy h1 { display: grid; gap: 10px; margin: 18px 0 32px; font-size: clamp(46px,5.25vw,70px); line-height: 1.1; letter-spacing: -.055em; }
.hero-copy h1 span { display: block; width: fit-content; white-space: nowrap; }
.hero-copy h1 .title-second-line { position: relative; padding-bottom: 4px; }
.hero-copy h1 .title-second-line::after { content: ''; position: absolute; right: 1%; bottom: -3px; left: 0; height: 7px; background: linear-gradient(90deg,rgba(101,88,245,.2),rgba(101,88,245,0)); border-radius: 10px; }
.hero-copy h1 em { position: relative; z-index: 1; color: #6558f5; font-style: normal; }
.hero-copy > p { max-width: 600px; margin: 0; color: #687083; font-size: 17px; line-height: 1.9; }
.idea-input { margin-top: 30px; padding: 10px; }
textarea { display: block; width: 100%; min-height: 100px; padding: 14px; resize: none; color: #263047; font-size: 15px; line-height: 1.7; background: transparent; border: 0; outline: 0; }
.input-actions { display: flex; align-items: center; justify-content: space-between; gap: 12px; padding: 6px 6px 4px 14px; border-top: 1px solid #ededeb; }
.generation-options { display: flex; align-items: center; gap: 16px; color: #9a9fac; font-size: 11px; }
.generation-options label { display: flex; align-items: center; gap: 7px; color: #555d6f; cursor: pointer; }
.generation-options :deep(.ant-switch-checked) { background: #6558f5; }
.examples { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 14px; color: #858b98; font-size: 11px; }
.examples button { max-width: 200px; overflow: hidden; padding: 4px 8px; color: #686f7e; text-overflow: ellipsis; white-space: nowrap; background: #ecece7; border: 0; border-radius: 6px; cursor: pointer; }
.hero-visual { position: relative; min-height: 440px; }
.browser-card { position: absolute; inset: 26px 0 30px 24px; padding: 58px 34px 34px; background: white; border: 1px solid rgba(23,29,43,.1); border-radius: 22px; box-shadow: 0 38px 80px rgba(38,42,65,.18); transform: rotate(2deg); }
.browser-top { position: absolute; top: 0; left: 0; right: 0; height: 42px; display: flex; align-items: center; gap: 6px; padding: 0 14px; background: #eeeef0; border-radius: 22px 22px 0 0; }
.browser-top i { width: 7px; height: 7px; background: #bbbcc3; border-radius: 50%; }.browser-top span { margin: auto; padding: 5px 54px; color: #8e919b; font-size: 9px; background: white; border-radius: 20px; }
.mock-nav { width: 100%; height: 14px; margin-bottom: 70px; background: linear-gradient(90deg,#23283a 0 20%,transparent 20% 67%,#dfe0e5 67% 100%); }
.mock-title { width: 70%; height: 32px; background: #242a3c; border-radius: 5px; }.mock-text { width: 85%; height: 12px; margin-top: 17px; background: #d8d9df; border-radius: 4px; }
.mock-grid { display: grid; grid-template-columns: repeat(3,1fr); gap: 12px; margin-top: 58px; }.mock-grid i { height: 105px; background: linear-gradient(145deg,#edecff,#cbd5ff); border-radius: 10px; }.mock-grid i:nth-child(2){background:linear-gradient(145deg,#d8f8ec,#b5e4d4)}.mock-grid i:nth-child(3){background:linear-gradient(145deg,#ffeacb,#f5c795)}
.float-label { position: absolute; padding: 10px 14px; color: white; font-size: 11px; font-weight: 700; background: #1c2231; border-radius: 10px; box-shadow: 0 12px 30px rgba(31,36,54,.22); }.label-ai { right: -15px; bottom: 54px; }.label-code { top: 4px; left: 0; color: #4d43d3; background: #eeecff; }
.hero-orb { position: absolute; border-radius: 50%; filter: blur(1px); }.orb-one { width: 420px; height: 420px; right: -170px; top: -220px; background: #e6e1ff; }.orb-two { width: 250px; height: 250px; left: -120px; bottom: -150px; background: #d7f5e9; }
.steps { position: relative; z-index: 2; display: grid; grid-template-columns: 1fr 50px 1fr 50px 1fr; gap: 22px; margin-top: -20px; padding: 24px 34px; background: #181e2b; border-radius: 18px; box-shadow: 0 18px 50px rgba(24,30,43,.18); }
.steps div { display: grid; grid-template-columns: 32px 1fr; align-items: center; color: #887cff; }.steps b { color: white; font-size: 14px; }.steps span { grid-column: 2; margin-top: 4px; color: #969dab; font-size: 11px; }.steps > i { align-self: center; height: 1px; background: #343a48; }
.showcase { padding-top: 74px; }.app-grid { display: grid; grid-template-columns: repeat(4,minmax(0,1fr)); gap: 20px; }
@media(max-width:1000px){.hero-inner{grid-template-columns:1fr}.hero-visual{display:none}.app-grid{grid-template-columns:repeat(2,1fr)}}
@media(max-width:680px){.hero{padding:52px 0}.hero-copy h1{gap:8px;margin:16px 0 26px;font-size:clamp(37px,11.5vw,48px);line-height:1.12;letter-spacing:-.05em}.hero-copy>p{font-size:15px;line-height:1.8}.steps{grid-template-columns:1fr}.steps>i{display:none}.app-grid{grid-template-columns:1fr}.generation-options>span{display:none}}
</style>
