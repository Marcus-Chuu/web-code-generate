<script setup lang="ts">
import { h, onMounted, reactive, ref } from 'vue'
import { Tag, message } from 'ant-design-vue'
import { apiErrorMessage } from '@/api/client'
import { chatService } from '@/api/services'
import { formatDate } from '@/utils/format'
import type { ChatHistory, MessageType } from '@/types/domain'
const loading = ref(false),
  rows = ref<ChatHistory[]>([]),
  total = ref(0)
const query = reactive({
  pageNum: 1,
  pageSize: 10,
  message: '',
  messageType: undefined as MessageType | undefined,
  appId: undefined as number | undefined,
  userId: undefined as number | undefined,
})
const load = async () => {
  loading.value = true
  try {
    const page = await chatService.listAdmin(query)
    rows.value = page.records || []
    total.value = Number(page.totalRow || 0)
  } catch (e) {
    message.error(apiErrorMessage(e, '对话记录加载失败'))
  } finally {
    loading.value = false
  }
}
const columns = [
  {
    title: '类型',
    dataIndex: 'messageType',
    width: 90,
    customRender: ({ text }: { text: MessageType }) =>
      h(Tag, { color: text === 'ai' ? 'purple' : 'blue' }, () => (text === 'ai' ? 'AI' : '用户')),
  },
  { title: '消息内容', dataIndex: 'message', ellipsis: true },
  { title: '应用 ID', dataIndex: 'appId', width: 170 },
  { title: '用户 ID', dataIndex: 'userId', width: 170 },
  {
    title: '发送时间',
    dataIndex: 'createTime',
    width: 180,
    customRender: ({ text }: { text: string }) => formatDate(text),
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
        <h1 class="page-title">对话审计</h1>
        <p class="page-description">按应用、用户和消息类型查看生成对话。</p>
      </div>
    </div>
    <div class="filters surface">
      <a-input v-model:value="query.message" allow-clear placeholder="消息关键词" /><a-select
        v-model:value="query.messageType"
        allow-clear
        placeholder="消息类型"
        style="width: 140px"
        ><a-select-option value="user">用户</a-select-option
        ><a-select-option value="ai">AI</a-select-option></a-select
      ><a-input-number v-model:value="query.appId" placeholder="应用 ID" /><a-input-number
        v-model:value="query.userId"
        placeholder="用户 ID"
      /><a-button
        type="primary"
        @click="search"
        >查询</a-button
      >
    </div>
    <a-table
      :columns="columns"
      :data-source="rows"
      :loading="loading"
      row-key="id"
      :pagination="{
        current: query.pageNum,
        pageSize: query.pageSize,
        total,
        showSizeChanger: true,
      }"
      @change="tableChange"
    />
  </main>
</template>
<style scoped>
.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px;
}
.filters .ant-input {
  max-width: 260px;
}
@media (max-width: 600px) {
  .filters {
    flex-direction: column;
  }
  .filters > * {
    width: 100% !important;
    max-width: none !important;
  }
}
</style>
