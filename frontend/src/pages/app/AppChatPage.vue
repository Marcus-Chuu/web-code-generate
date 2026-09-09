<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  ArrowLeftOutlined,
  CloudUploadOutlined,
  DownloadOutlined,
  EditOutlined,
  ExpandOutlined,
  HighlightOutlined,
  ReloadOutlined,
  SendOutlined,
} from '@ant-design/icons-vue'
import MarkdownRenderer from '@/components/MarkdownRenderer.vue'
import { DEFAULT_AVATAR } from '@/config/avatars'
import { apiErrorMessage } from '@/api/client'
import { appService, chatService, formatWorkflowContent } from '@/api/services'
import { useLoginUserStore } from '@/stores/loginUser'
import { codeTypeLabel } from '@/utils/format'
import { resolvePreviewUrl, withCacheBuster } from '@/utils/preview'
import { useVisualEditor, describeElement } from '@/composables/useVisualEditor'
import type { AppVO, ChatHistory } from '@/types/domain'

interface ChatMessage {
  id: string
  type: 'user' | 'ai'
  content: string
  pending?: boolean
  error?: boolean
}
const route = useRoute()
const router = useRouter()
const userStore = useLoginUserStore()
const appId = computed(() => String(route.params.id))
const app = ref<AppVO | null>(null)
const messages = ref<ChatMessage[]>([])
const input = ref('')
const loading = ref(true)
const generating = ref(false)
const agentMode = ref(false)
const deploying = ref(false)
const downloading = ref(false)
const loadingHistory = ref(false)
const hasMore = ref(false)
const oldestTime = ref<string>()
const messageList = ref<HTMLElement>()
const previewUrl = ref('')
const previewFrame = ref<HTMLIFrameElement | null>(null)
const previewLoading = ref(false)
const previewError = ref('')
const mobilePanel = ref<'chat' | 'preview'>('chat')
const { editing, selectedElement, frameSrc, toggleEdit, exitEdit, clearSelection } = useVisualEditor(previewFrame, previewUrl)
const selectedSummary = computed(() => (selectedElement.value ? describeElement(selectedElement.value) : ''))
let stopStream: (() => void) | undefined
let typeTimer: number | undefined
let queuedText = ''
let serverDone = false
let activeAiId = ''

const isOwner = computed(
  () => String(app.value?.userId || '') === String(userStore.loginUser?.id || ''),
)
const scrollBottom = async () => {
  await nextTick()
  if (messageList.value) messageList.value.scrollTop = messageList.value.scrollHeight
}
const mapHistory = (item: ChatHistory): ChatMessage => ({
  id: String(item.id),
  type: item.messageType,
  content: item.messageType === 'ai' ? formatWorkflowContent(item.message) : item.message,
})

const setPreviewFromApp = (cacheBust = false) => {
  if (!app.value?.id || !app.value.codeGenType) return
  const url = resolvePreviewUrl(app.value.id, app.value.codeGenType)
  previewError.value = ''
  previewLoading.value = true
  previewUrl.value = cacheBust ? withCacheBuster(url) : url
}
const refreshPreview = () => {
  setPreviewFromApp(true)
}
const openPreviewWindow = () => {
  if (previewUrl.value) window.open(previewUrl.value, '_blank', 'noopener')
}
const deploy = async () => {
  deploying.value = true
  previewError.value = ''
  try {
    const backendUrl = await appService.deploy(appId.value)
    if (app.value)
      app.value.deployKey = new URL(backendUrl, window.location.href).pathname
        .split('/')
        .filter(Boolean)
        .pop()
    setPreviewFromApp(true)
    message.success('部署完成')
  } catch (error) {
    message.error(apiErrorMessage(error, '部署失败'))
  } finally {
    deploying.value = false
  }
}
const download = async () => {
  downloading.value = true
  try {
    await appService.download(appId.value)
    message.success('代码压缩包已开始下载')
  } catch (e) {
    message.error(apiErrorMessage(e, '下载失败'))
  } finally {
    downloading.value = false
  }
}

const finishTypingIfReady = async () => {
  if (!serverDone || queuedText) return
  if (typeTimer) {
    window.clearInterval(typeTimer)
    typeTimer = undefined
  }
  const target = messages.value.find((item) => item.id === activeAiId)
  if (target) target.pending = false
  generating.value = false
  stopStream = undefined
  setPreviewFromApp(true)
}
const startTypewriter = () => {
  if (typeTimer) return
  typeTimer = window.setInterval(() => {
    if (!queuedText) {
      void finishTypingIfReady()
      return
    }
    const target = messages.value.find((item) => item.id === activeAiId)
    if (!target) return
    const count = Math.max(1, Math.min(8, Math.ceil(queuedText.length / 70)))
    target.content += queuedText.slice(0, count)
    queuedText = queuedText.slice(count)
    void scrollBottom()
  }, 18)
}
const send = async (initialMessage?: string) => {
  let content = (initialMessage ?? input.value).trim()
  // 可视化编辑：将选中的元素信息拼接到提示词
  if (selectedElement.value) {
    const hint = `[选中的页面元素] ${describeElement(selectedElement.value)}`
    content = content ? `${content}\n\n${hint}` : hint
  }
  if (!content || generating.value || !isOwner.value) return
  input.value = ''
  // 发送后清除选中并退出编辑模式
  clearSelection()
  exitEdit()
  generating.value = true
  serverDone = false
  queuedText = ''
  messages.value.push({ id: `u-${Date.now()}`, type: 'user', content })
  activeAiId = `a-${Date.now()}`
  messages.value.push({ id: activeAiId, type: 'ai', content: '', pending: true })
  await scrollBottom()
  startTypewriter()
  const useAgent = agentMode.value
  stopStream = chatService.stream(appId.value, content, useAgent, {
    onChunk(chunk) {
      queuedText += chunk
    },
    onDone() {
      serverDone = true
      void finishTypingIfReady()
    },
    onError(error) {
      queuedText = ''
      serverDone = false
      generating.value = false
      if (typeTimer) {
        window.clearInterval(typeTimer)
        typeTimer = undefined
      }
      const target = messages.value.find((item) => item.id === activeAiId)
      if (target) {
        target.pending = false
        target.error = true
        if (!target.content) target.content = `生成失败：${error.message}`
      }
      message.error(error.message)
    },
  })
}
const keydown = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    void send()
  }
}

const loadHistory = async (older = false) => {
  if (older) loadingHistory.value = true
  const previousHeight = messageList.value?.scrollHeight || 0
  try {
    const page = await chatService.listByApp(appId.value, 30, older ? oldestTime.value : undefined)
    const rows = [...(page.records || [])].sort(
      (a, b) => new Date(a.createTime || 0).getTime() - new Date(b.createTime || 0).getTime(),
    )
    if (older) messages.value.unshift(...rows.map(mapHistory))
    else messages.value = rows.map(mapHistory)
    oldestTime.value = rows[0]?.createTime || oldestTime.value
    hasMore.value = rows.length === 30
    await nextTick()
    if (older && messageList.value)
      messageList.value.scrollTop = messageList.value.scrollHeight - previousHeight
    else await scrollBottom()
  } catch (error) {
    message.error(apiErrorMessage(error, '对话记录加载失败'))
  } finally {
    loadingHistory.value = false
  }
}

onMounted(async () => {
  try {
    app.value = await appService.get(appId.value)
    await loadHistory()
    if (messages.value.some((item) => item.type === 'ai')) setPreviewFromApp()
    if (route.query.start === '1' && messages.value.length === 0 && app.value.initPrompt) {
      agentMode.value = route.query.agent === '1'
      await router.replace({ query: {} })
      void send(app.value.initPrompt)
    }
  } catch (error) {
    message.error(apiErrorMessage(error, '应用加载失败'))
    await router.replace('/apps')
  } finally {
    loading.value = false
  }
})
onBeforeUnmount(() => {
  stopStream?.()
  if (typeTimer) window.clearInterval(typeTimer)
})
</script>

<template>
  <main class="studio" :class="`show-${mobilePanel}`">
    <div class="studio-bar">
      <div class="title-area">
        <a-button type="text" shape="circle" @click="router.push('/apps')"
          ><ArrowLeftOutlined
        /></a-button>
        <div>
          <b>{{ app?.appName || 'AI 创作台' }}</b
          ><span
            >{{ codeTypeLabel(app?.codeGenType) }} · {{ generating ? '生成中' : '已同步' }}</span
          >
        </div>
      </div>
      <div class="mobile-switch">
        <button :class="{ active: mobilePanel === 'chat' }" @click="mobilePanel = 'chat'">
          对话</button
        ><button :class="{ active: mobilePanel === 'preview' }" @click="mobilePanel = 'preview'">
          预览
        </button>
      </div>
      <a-space class="bar-actions"
        ><a-button :disabled="!isOwner" @click="router.push(`/app/edit/${appId}`)"
          ><EditOutlined /> 编辑</a-button
        ><a-button :loading="downloading" :disabled="!isOwner" @click="download"
          ><DownloadOutlined /> 下载</a-button
        ><a-button type="primary" :loading="deploying" :disabled="!isOwner" @click="deploy"
          ><CloudUploadOutlined /> 部署</a-button
        ></a-space
      >
    </div>

    <div class="studio-loading">
      <a-spin :spinning="loading">
        <div class="studio-body">
          <section class="chat-panel">
            <div ref="messageList" class="message-list">
              <div v-if="hasMore" class="load-more">
                <a-button
                  type="link"
                  size="small"
                  :loading="loadingHistory"
                  @click="loadHistory(true)"
                  >加载更早记录</a-button
                >
              </div>
              <div v-if="!messages.length && !loading" class="chat-welcome">
                <span>✦</span>
                <h2>和 AI 一起完成你的页面</h2>
                <p>描述布局、颜色、交互或内容，生成结果会实时显示在右侧。</p>
              </div>
              <article
                v-for="item in messages"
                :key="item.id"
                class="message-row"
                :class="item.type"
              >
                <a-avatar
                  :size="32"
                  :src="
                    item.type === 'user'
                      ? userStore.loginUser?.userAvatar || DEFAULT_AVATAR
                      : undefined
                  "
                  class="avatar"
                  >{{ item.type === 'ai' ? 'AI' : '' }}</a-avatar
                >
                <div class="bubble" :class="{ error: item.error }">
                  <MarkdownRenderer
                    v-if="item.type === 'ai' && item.content"
                    :content="item.content"
                  /><span v-else>{{ item.content }}</span
                  ><span v-if="item.pending" class="typing-cursor"></span>
                </div>
              </article>
            </div>
            <div class="composer-wrap">
              <a-alert
                v-if="!isOwner && app"
                type="warning"
                show-icon
                message="只有应用创建者可以继续对话"
              />
              <div class="composer" :class="{ disabled: !isOwner }">
                <a-alert
                  v-if="editing && selectedElement"
                  type="warning"
                  show-icon
                  closable
                  message="已选中页面元素"
                  :description="selectedSummary"
                  @close="clearSelection"
                />
                <a-textarea
                  v-model:value="input"
                  :disabled="!isOwner || generating"
                  :bordered="false"
                  :auto-size="{ minRows: 2, maxRows: 6 }"
                  maxlength="1000"
                  placeholder="告诉 AI 下一步怎么改…（Enter 发送，Shift + Enter 换行）"
                  @keydown="keydown"
                />
                <div class="composer-footer">
                  <div class="composer-options">
                    <label class="agent-mode" title="使用 LangGraph4j 多步骤规划、生成和质量检查">
                      <a-switch
                        v-model:checked="agentMode"
                        size="small"
                        :disabled="generating || !isOwner"
                      />
                      <span>工作流模式</span>
                    </label>
                    <span class="input-count">{{ input.length }}/1000</span>
                  </div>
                  <a-space :size="8">
                    <a-button
                      shape="circle"
                      :type="editing ? 'primary' : 'default'"
                      :disabled="!isOwner || generating"
                      :title="editing ? '退出选取模式' : '选取页面元素'"
                      @click="toggleEdit"
                      ><HighlightOutlined
                    /></a-button>
                    <a-button
                      type="primary"
                      shape="circle"
                      :loading="generating"
                      :disabled="!input.trim() || !isOwner"
                      @click="send()"
                      ><SendOutlined
                    /></a-button>
                  </a-space>
                </div>
              </div>
            </div>
          </section>

          <section class="preview-panel">
            <div class="preview-toolbar">
              <div class="preview-dots"><i></i><i></i><i></i></div>
              <div class="address">{{ previewUrl || '生成完成后将在这里显示实时页面' }}</div>
              <a-button type="text" shape="circle" :disabled="!previewUrl" @click="refreshPreview"
                ><ReloadOutlined /></a-button
              ><a-button
                type="text"
                shape="circle"
                :disabled="!previewUrl"
                @click="openPreviewWindow"
                ><ExpandOutlined
              /></a-button>
            </div>
            <div class="preview-canvas">
              <iframe
                v-if="previewUrl"
                ref="previewFrame"
                :src="frameSrc"
                title="生成应用预览"
                sandbox="allow-scripts allow-forms allow-modals allow-popups allow-same-origin"
                @load="previewLoading = false"
              />
              <div v-else class="preview-empty">
                <div class="preview-icon">⌘</div>
                <h2>等待第一版页面</h2>
                <p>AI 完成生成后会自动刷新预览，确认效果后可点击右上角部署。</p>
                <a-button v-if="messages.length && !generating" :loading="deploying" @click="deploy"
                  >部署应用</a-button
                >
              </div>
              <div v-if="previewLoading" class="preview-mask">
                <a-spin /><span>正在载入最新页面…</span>
              </div>
              <a-result
                v-if="previewError"
                status="error"
                title="预览暂时不可用"
                :sub-title="previewError"
                class="preview-error"
                ><template #extra
                  ><a-button @click="refreshPreview">重新加载</a-button></template
                ></a-result
              >
            </div>
          </section>
        </div>
      </a-spin>
    </div>
  </main>
</template>

<style scoped>
.studio {
  width: 100%;
  height: calc(100vh - 68px);
  min-height: 620px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #eef2e8;
}
.studio-bar {
  height: 64px;
  flex: 0 0 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: white;
  border-bottom: 1px solid #e2e8dc;
}
.title-area {
  display: flex;
  align-items: center;
  gap: 8px;
}
.title-area > div {
  display: grid;
  gap: 3px;
}
.title-area b {
  font-size: 14px;
}
.title-area span {
  color: #8a909e;
  font-size: 10px;
}
.studio-loading {
  flex: 1 1 0;
  height: 0;
  min-height: 0;
  overflow: hidden;
}
.studio-loading :deep(.ant-spin-nested-loading),
.studio-loading :deep(.ant-spin-container) {
  height: 100%;
  min-height: 0;
  overflow: hidden;
}
.studio-body {
  height: 100%;
  min-height: 0;
  display: grid;
  grid-template-columns: minmax(380px, 42%) 1fr;
  grid-template-rows: minmax(0, 1fr);
  gap: 1px;
  overflow: hidden;
  background: #dddeda;
}
.chat-panel,
.preview-panel {
  height: 100%;
  min-width: 0;
  min-height: 0;
  overflow: hidden;
  background: #f9f9f7;
}
.chat-panel {
  display: flex;
  flex-direction: column;
}
.message-list {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 28px 24px;
}
.load-more {
  text-align: center;
  margin-bottom: 18px;
}
.chat-welcome {
  max-width: 360px;
  margin: 15vh auto 0;
  text-align: center;
  color: #777f90;
}
.chat-welcome > span {
  display: grid;
  width: 48px;
  height: 48px;
  margin: 0 auto 15px;
  place-items: center;
  color: white;
  font-size: 22px;
  background: #52765b;
  border-radius: 14px;
}
.chat-welcome h2 {
  margin: 0 0 8px;
  color: #272e3e;
  font-size: 19px;
}
.chat-welcome p {
  font-size: 13px;
  line-height: 1.7;
}
.message-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 22px;
}
.message-row.user {
  flex-direction: row-reverse;
}
.message-row.ai .avatar {
  color: white;
  background: #1d2332;
}
.bubble {
  max-width: 82%;
  padding: 11px 14px;
  color: #333b4d;
  font-size: 13px;
  line-height: 1.7;
  background: white;
  border: 1px solid #e7e7e4;
  border-radius: 5px 16px 16px 16px;
  box-shadow: 0 6px 20px rgba(43, 47, 65, 0.04);
  word-break: break-word;
}
.user .bubble {
  color: white;
  background: #52765b;
  border-color: #52765b;
  border-radius: 16px 5px 16px 16px;
}
.bubble.error {
  color: #ba3131;
  background: #fff0f0;
  border-color: #ffd3d3;
}
.typing-cursor {
  display: inline-block;
  width: 6px;
  height: 14px;
  margin-left: 3px;
  vertical-align: -2px;
  background: #52765b;
  animation: blink 0.8s infinite;
}
.composer-wrap {
  padding: 12px 18px 18px;
  background: linear-gradient(transparent, #f9f9f7 16%);
}
.composer-wrap :deep(.ant-alert) {
  margin-bottom: 8px;
}
.composer {
  padding: 10px 12px 8px;
  background: white;
  border: 1px solid #d9e2d0;
  border-radius: 16px;
  box-shadow: 0 12px 32px rgba(38, 42, 60, 0.1);
}
.composer:focus-within {
  border-color: #91ad82;
  box-shadow: 0 12px 35px rgba(101, 88, 245, 0.15);
}
.composer.disabled {
  opacity: 0.65;
}
.composer-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 4px 2px 0;
}
.composer-options {
  display: flex;
  align-items: center;
  gap: 14px;
}
.agent-mode {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #5f6676;
  font-size: 11px;
  cursor: pointer;
}
.agent-mode :deep(.ant-switch-checked) {
  background: #52765b;
}
.input-count {
  color: #a0a4ae;
  font-size: 10px;
}
.preview-panel {
  display: flex;
  flex-direction: column;
  background: #edf1e7;
}
.preview-toolbar {
  height: 48px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 12px;
  background: #fbfbfa;
  border-bottom: 1px solid #dcdcd8;
}
.preview-dots {
  display: flex;
  gap: 5px;
}
.preview-dots i {
  width: 8px;
  height: 8px;
  background: #d1d1d3;
  border-radius: 50%;
}
.preview-dots i:first-child {
  background: #ff8c85;
}
.preview-dots i:nth-child(2) {
  background: #f2cb67;
}
.preview-dots i:nth-child(3) {
  background: #69d391;
}
.address {
  flex: 1;
  overflow: hidden;
  padding: 6px 12px;
  color: #8b909b;
  font-size: 10px;
  text-align: center;
  text-overflow: ellipsis;
  white-space: nowrap;
  background: #efefed;
  border-radius: 7px;
}
.preview-canvas {
  position: relative;
  flex: 1;
  min-height: 0;
  padding: 14px;
}
.preview-canvas iframe {
  width: 100%;
  height: 100%;
  background: white;
  border: 0;
  border-radius: 8px;
  box-shadow: 0 10px 30px rgba(37, 41, 57, 0.1);
}
.preview-empty {
  position: absolute;
  inset: 0;
  display: grid;
  place-content: center;
  justify-items: center;
  padding: 30px;
  text-align: center;
  color: #7e8593;
}
.preview-icon {
  display: grid;
  width: 64px;
  height: 64px;
  place-items: center;
  color: #52765b;
  font-size: 28px;
  background: #e7efe0;
  border-radius: 20px;
}
.preview-empty h2 {
  margin: 18px 0 6px;
  color: #333a4b;
  font-size: 20px;
}
.preview-empty p {
  max-width: 340px;
  font-size: 12px;
  line-height: 1.7;
}
.preview-mask {
  position: absolute;
  inset: 14px;
  display: grid;
  place-content: center;
  gap: 12px;
  text-align: center;
  color: #6d7483;
  font-size: 12px;
  background: rgba(255, 255, 255, 0.88);
  border-radius: 8px;
}
.preview-error {
  position: absolute;
  inset: 14px;
  background: white;
  border-radius: 8px;
}
.mobile-switch {
  display: none;
}
.bar-actions {
  display: flex;
}
@keyframes blink {
  50% {
    opacity: 0;
  }
}
@media (max-width: 800px) {
  .studio-body {
    display: block;
  }
  .chat-panel,
  .preview-panel {
    height: 100%;
  }
  .show-chat .preview-panel,
  .show-preview .chat-panel {
    display: none;
  }
  .mobile-switch {
    display: flex;
    padding: 3px;
    background: #efefed;
    border-radius: 8px;
  }
  .mobile-switch button {
    padding: 5px 12px;
    color: #777d89;
    background: none;
    border: 0;
    border-radius: 6px;
  }
  .mobile-switch button.active {
    color: #242b3a;
    background: white;
    box-shadow: 0 2px 7px rgba(0, 0, 0, 0.08);
  }
  .bar-actions .ant-btn:not(:last-child) {
    display: none;
  }
  .title-area span {
    display: none;
  }
  .message-list {
    padding: 22px 14px;
  }
}
</style>
