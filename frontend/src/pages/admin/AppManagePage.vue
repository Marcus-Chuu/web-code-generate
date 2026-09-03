<script setup lang="ts">
import { h, onMounted, reactive, ref } from 'vue'
import { Button, message, Modal, Space, Tag } from 'ant-design-vue'
import { apiErrorMessage } from '@/api/client'
import { appService } from '@/api/services'
import { codeTypeLabel, formatDate } from '@/utils/format'
import type { AppVO } from '@/types/domain'

const loading = ref(false),
  apps = ref<AppVO[]>([]),
  total = ref(0),
  open = ref(false),
  saving = ref(false)
const query = reactive({
  pageNum: 1,
  pageSize: 10,
  appName: '',
  userId: undefined as number | undefined,
})
const form = reactive({ id: '' as string | number, appName: '', cover: '', priority: 0 })
const load = async () => {
  loading.value = true
  try {
    const page = await appService.listAdmin(query)
    apps.value = page.records || []
    total.value = Number(page.totalRow || 0)
  } catch (e) {
    message.error(apiErrorMessage(e, '应用列表加载失败'))
  } finally {
    loading.value = false
  }
}
const edit = (r: AppVO) => {
  Object.assign(form, {
    id: r.id,
    appName: r.appName || '',
    cover: r.cover || '',
    priority: r.priority || 0,
  })
  open.value = true
}
const save = async () => {
  saving.value = true
  try {
    await appService.updateAdmin(form)
    message.success('应用已更新')
    open.value = false
    await load()
  } catch (e) {
    message.error(apiErrorMessage(e, '更新失败'))
  } finally {
    saving.value = false
  }
}
const remove = (r: AppVO) =>
  Modal.confirm({
    title: `删除应用「${r.appName || '未命名'}」？`,
    okType: 'danger',
    okText: '删除',
    cancelText: '取消',
    async onOk() {
      try {
        await appService.removeAdmin(r.id)
        message.success('已删除')
        await load()
      } catch (e) {
        message.error(apiErrorMessage(e, '删除失败'))
        throw e
      }
    },
  })
const columns = [
  {
    title: '应用',
    key: 'app',
    customRender: ({ record: r }: { record: AppVO }) =>
      h('div', {}, [
        h('b', {}, r.appName || '未命名应用'),
        h('div', { class: 'cell-sub' }, String(r.id)),
      ]),
  },
  {
    title: '类型',
    dataIndex: 'codeGenType',
    width: 120,
    customRender: ({ text }: { text: AppVO['codeGenType'] }) =>
      h(Tag, {}, () => codeTypeLabel(text)),
  },
  { title: '创建者', dataIndex: 'userId', width: 160 },
  { title: '优先级', dataIndex: 'priority', width: 90 },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
    customRender: ({ text }: { text: string }) => formatDate(text),
  },
  {
    title: '操作',
    key: 'action',
    width: 150,
    customRender: ({ record }: { record: AppVO }) =>
      h(Space, {}, () => [
        h(Button, { type: 'link', size: 'small', onClick: () => edit(record) }, () => '编辑'),
        h(
          Button,
          { type: 'link', size: 'small', danger: true, onClick: () => remove(record) },
          () => '删除',
        ),
      ]),
  },
]
const tableChange = (page: { current?: number; pageSize?: number }) => {
  query.pageNum = page.current || 1
  query.pageSize = page.pageSize || 10
  void load()
}
const search = () => { query.pageNum = 1; void load() }
onMounted(load)
</script>
<template>
  <main class="page-shell page-section table-page">
    <div class="section-heading">
      <div>
        <span class="eyebrow">Administration</span>
        <h1 class="page-title">应用管理</h1>
        <p class="page-description">调整精选优先级、封面与应用信息。</p>
      </div>
    </div>
    <div class="filters surface">
      <a-input v-model:value="query.appName" allow-clear placeholder="应用名称" /><a-input-number
        v-model:value="query.userId"
        placeholder="用户 ID"
        style="width: 180px"
      /><a-button
        type="primary"
        @click="search"
        >查询</a-button
      >
    </div>
    <a-table
      :columns="columns"
      :data-source="apps"
      :loading="loading"
      row-key="id"
      :pagination="{
        current: query.pageNum,
        pageSize: query.pageSize,
        total,
        showSizeChanger: true,
      }"
      @change="tableChange"
    /><a-modal v-model:open="open" title="编辑应用" :confirm-loading="saving" @ok="save"
      ><a-form layout="vertical"
        ><a-form-item label="应用名称"><a-input v-model:value="form.appName" /></a-form-item
        ><a-form-item label="封面地址"><a-input v-model:value="form.cover" /></a-form-item
        ><a-form-item label="精选优先级"
          ><a-input-number v-model:value="form.priority" :min="0" style="width: 100%" /><small
            >大于 0 的应用会进入首页精选，数值越大越靠前。</small
          ></a-form-item
        ></a-form
      ></a-modal
    >
  </main>
</template>
<style scoped>
.filters {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px;
}
.filters .ant-input {
  max-width: 260px;
}
:deep(.cell-sub) {
  margin-top: 3px;
  color: #9499a4;
  font-size: 11px;
}
@media (max-width: 600px) {
  .filters {
    flex-direction: column;
  }
  .filters .ant-input {
    max-width: none;
  }
}
</style>
