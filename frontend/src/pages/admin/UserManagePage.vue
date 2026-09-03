<script setup lang="ts">
import { h, onMounted, reactive, ref } from 'vue'
import { Avatar, Button, message, Modal, Space, Tag } from 'ant-design-vue'
import { apiErrorMessage } from '@/api/client'
import { userService } from '@/api/services'
import { DEFAULT_AVATAR } from '@/config/avatars'
import { formatDate } from '@/utils/format'
import type { UserRole, UserVO } from '@/types/domain'

const loading = ref(false),
  users = ref<UserVO[]>([]),
  total = ref(0),
  editorOpen = ref(false),
  saving = ref(false)
const query = reactive({ pageNum: 1, pageSize: 10, userAccount: '', userName: '' })
const form = reactive<{
  id: string | number
  userName: string
  userAvatar: string
  userProfile: string
  userRole: UserRole
}>({ id: '', userName: '', userAvatar: '', userProfile: '', userRole: 'user' })
const load = async () => {
  loading.value = true
  try {
    const page = await userService.list(query)
    users.value = page.records || []
    total.value = Number(page.totalRow || 0)
  } catch (e) {
    message.error(apiErrorMessage(e, '用户列表加载失败'))
  } finally {
    loading.value = false
  }
}
const edit = (record: UserVO) => {
  Object.assign(form, {
    id: record.id,
    userName: record.userName || '',
    userAvatar: record.userAvatar || '',
    userProfile: record.userProfile || '',
    userRole: record.userRole,
  })
  editorOpen.value = true
}
const save = async () => {
  saving.value = true
  try {
    await userService.update(form)
    message.success('用户已更新')
    editorOpen.value = false
    await load()
  } catch (e) {
    message.error(apiErrorMessage(e, '更新失败'))
  } finally {
    saving.value = false
  }
}
const remove = (record: UserVO) =>
  Modal.confirm({
    title: `删除用户 ${record.userAccount}？`,
    content: '该操作无法恢复。',
    okType: 'danger',
    okText: '删除',
    cancelText: '取消',
    async onOk() {
      try {
        await userService.remove(record.id)
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
    title: '用户',
    key: 'user',
    customRender: ({ record }: { record: UserVO }) =>
      h(Space, {}, () => [
        h(Avatar, { src: record.userAvatar || DEFAULT_AVATAR }),
        h('div', {}, [
          h('b', {}, record.userName || '未命名'),
          h('div', { class: 'cell-sub' }, record.userAccount),
        ]),
      ]),
  },
  {
    title: '角色',
    dataIndex: 'userRole',
    width: 100,
    customRender: ({ text }: { text: UserRole }) =>
      h(Tag, { color: text === 'admin' ? 'purple' : text === 'ban' ? 'red' : 'default' }, () =>
        text === 'admin' ? '管理员' : text === 'ban' ? '封禁' : '用户',
      ),
  },
  { title: '简介', dataIndex: 'userProfile', ellipsis: true },
  {
    title: '注册时间',
    dataIndex: 'createTime',
    width: 180,
    customRender: ({ text }: { text: string }) => formatDate(text),
  },
  {
    title: '操作',
    key: 'action',
    width: 150,
    customRender: ({ record }: { record: UserVO }) =>
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
        <h1 class="page-title">用户管理</h1>
        <p class="page-description">查看账户、调整角色与用户状态。</p>
      </div>
    </div>
    <div class="filters surface">
      <a-input v-model:value="query.userAccount" allow-clear placeholder="账号" /><a-input
        v-model:value="query.userName"
        allow-clear
        placeholder="昵称"
      /><a-button
        type="primary"
        @click="search"
        >查询</a-button
      >
    </div>
    <a-table
      :columns="columns"
      :data-source="users"
      :loading="loading"
      row-key="id"
      :pagination="{
        current: query.pageNum,
        pageSize: query.pageSize,
        total,
        showSizeChanger: true,
      }"
      @change="tableChange"
    /><a-modal v-model:open="editorOpen" title="编辑用户" :confirm-loading="saving" @ok="save"
      ><a-form layout="vertical"
        ><a-form-item label="昵称"><a-input v-model:value="form.userName" /></a-form-item
        ><a-form-item label="头像地址"><a-input v-model:value="form.userAvatar" /></a-form-item
        ><a-form-item label="个人简介"><a-textarea v-model:value="form.userProfile" /></a-form-item
        ><a-form-item label="角色"
          ><a-select v-model:value="form.userRole"
            ><a-select-option value="user">普通用户</a-select-option
            ><a-select-option value="admin">管理员</a-select-option
            ><a-select-option value="ban">封禁</a-select-option></a-select
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
  max-width: 240px;
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
