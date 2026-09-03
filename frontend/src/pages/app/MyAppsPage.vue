<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { DeleteOutlined, EditOutlined, PlusOutlined, RocketOutlined } from '@ant-design/icons-vue'
import AppCard from '@/components/AppCard.vue'
import { apiErrorMessage } from '@/api/client'
import { appService } from '@/api/services'
import { resolvePreviewUrl, withCacheBuster } from '@/utils/preview'
import type { AppVO } from '@/types/domain'

const router = useRouter()
const loading = ref(false)
const apps = ref<AppVO[]>([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 8, appName: '' })

const load = async () => {
  loading.value = true
  try {
    const page = await appService.listMine(query)
    apps.value = page.records || []
    total.value = Number(page.totalRow || 0)
  } catch (error) { message.error(apiErrorMessage(error, '应用列表加载失败')) }
  finally { loading.value = false }
}
const preview = (app: AppVO) => {
  const url = resolvePreviewUrl(app.id, app.codeGenType)
  if (!url) return message.info('应用还没有可预览的代码')
  window.open(withCacheBuster(url), '_blank', 'noopener')
}
const deploy = async (app: AppVO) => {
  try {
    const url = await appService.deploy(app.id)
    app.deployKey = new URL(url, window.location.href).pathname.split('/').filter(Boolean).pop()
    message.success('部署成功')
  } catch (error) { message.error(apiErrorMessage(error, '部署失败')) }
}
const remove = (app: AppVO) => Modal.confirm({
  title: `删除「${app.appName || '未命名应用'}」？`, content: '删除后无法恢复。', okText: '删除', okType: 'danger', cancelText: '取消',
  async onOk() { try { await appService.remove(app.id); message.success('已删除'); await load() } catch (error) { message.error(apiErrorMessage(error, '删除失败')); throw error } },
})

onMounted(load)
</script>

<template>
  <main class="page-shell page-section">
    <div class="section-heading">
      <div><span class="eyebrow">My workspace</span><h1 class="page-title">我的应用</h1><p class="page-description">继续对话、管理或发布你的作品。</p></div>
      <a-button type="primary" size="large" @click="router.push('/')"><PlusOutlined /> 创建新应用</a-button>
    </div>
    <div class="toolbar surface"><a-input-search v-model:value="query.appName" allow-clear placeholder="搜索应用名称" style="max-width:360px" @search="query.pageNum=1;load()" /><span>共 {{ total }} 个应用</span></div>
    <a-spin :spinning="loading">
      <div v-if="apps.length" class="app-grid">
        <div v-for="app in apps" :key="String(app.id)" class="app-wrap">
          <AppCard :app="app" @open="router.push(`/app/chat/${$event.id}`)" @preview="preview" />
          <div class="quick-actions">
            <a-button type="text" size="small" @click="router.push(`/app/edit/${app.id}`)"><EditOutlined /> 编辑</a-button>
            <a-button type="text" size="small" @click="deploy(app)"><RocketOutlined /> 部署</a-button>
            <a-button type="text" danger size="small" @click="remove(app)"><DeleteOutlined /> 删除</a-button>
          </div>
        </div>
      </div>
      <div v-else class="surface empty-state">还没有应用，去描述你的第一个想法吧。</div>
    </a-spin>
    <a-pagination v-if="total > query.pageSize" v-model:current="query.pageNum" v-model:page-size="query.pageSize" :total="total" class="pagination" @change="load" />
  </main>
</template>

<style scoped>
.toolbar{display:flex;align-items:center;justify-content:space-between;gap:16px;margin-bottom:24px;padding:14px 16px}.toolbar>span{color:#858b98;font-size:12px}.app-grid{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:20px}.app-wrap{position:relative}.quick-actions{display:flex;justify-content:flex-end;gap:2px;margin-top:8px}.pagination{display:flex;justify-content:center;margin-top:36px}@media(max-width:1000px){.app-grid{grid-template-columns:repeat(2,1fr)}}@media(max-width:620px){.app-grid{grid-template-columns:1fr}.toolbar{align-items:stretch;flex-direction:column}}
</style>
