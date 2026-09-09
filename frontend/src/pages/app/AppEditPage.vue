<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { ArrowLeftOutlined } from '@ant-design/icons-vue'
import { apiErrorMessage } from '@/api/client'
import { appService } from '@/api/services'
import { codeTypeLabel, formatDate } from '@/utils/format'
import type { AppVO } from '@/types/domain'

const route = useRoute()
const router = useRouter()
const app = ref<AppVO | null>(null)
const loading = ref(false)
const saving = ref(false)
const form = reactive({ appName: '' })
onMounted(async () => {
  loading.value = true
  try {
    app.value = await appService.get(String(route.params.id))
    form.appName = app.value.appName || ''
  } catch (e) {
    message.error(apiErrorMessage(e, '应用加载失败'))
  } finally {
    loading.value = false
  }
})
const save = async () => {
  if (!app.value) return
  saving.value = true
  try {
    await appService.update({ id: app.value.id, appName: form.appName.trim() })
    message.success('应用信息已保存')
    await router.push('/apps')
  } catch (e) {
    message.error(apiErrorMessage(e, '保存失败'))
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <main class="page-shell page-section edit-page">
    <button class="back" @click="router.back()"><ArrowLeftOutlined /> 返回</button>
    <a-spin :spinning="loading"
      ><section v-if="app" class="surface edit-card">
        <div class="intro">
          <span class="eyebrow">Application settings</span>
          <h1>整理应用信息</h1>
          <p>为作品设置一个清晰的名称。生成方式与初始需求由系统保留，方便之后继续迭代。</p>
          <dl>
            <div>
              <dt>生成类型</dt>
              <dd>{{ codeTypeLabel(app.codeGenType) }}</dd>
            </div>
            <div>
              <dt>创建时间</dt>
              <dd>{{ formatDate(app.createTime) }}</dd>
            </div>
            <div>
              <dt>部署状态</dt>
              <dd>{{ app.deployKey ? '已部署' : '尚未部署' }}</dd>
            </div>
          </dl>
        </div>
        <a-form class="form" layout="vertical" :model="form" @finish="save"
          ><h2>基本信息</h2>
          <a-form-item
            label="应用名称"
            name="appName"
            :rules="[
              { required: true, message: '请输入应用名称' },
              { max: 64, message: '最多 64 个字符' },
            ]"
            ><a-input
              v-model:value="form.appName"
              size="large"
              placeholder="给应用起个名字" /></a-form-item
          ><a-form-item label="初始需求"
            ><a-textarea :value="app.initPrompt" :rows="5" disabled /></a-form-item
          ><a-space
            ><a-button @click="router.back()">取消</a-button
            ><a-button type="primary" html-type="submit" :loading="saving"
              >保存修改</a-button
            ></a-space
          ></a-form
        >
      </section></a-spin
    >
  </main>
</template>

<style scoped>
.edit-page {
  max-width: 1000px;
}
.back {
  margin-bottom: 18px;
  padding: 0;
  color: #656d7e;
  background: none;
  border: 0;
  cursor: pointer;
}
.edit-card {
  display: grid;
  grid-template-columns: 0.85fr 1.15fr;
  overflow: hidden;
}
.intro {
  padding: 48px;
  color: white;
  background: #344c3b;
}
.intro h1 {
  margin: 12px 0;
  font-size: 34px;
}
.intro p {
  color: #becbb6;
  line-height: 1.8;
}
.intro dl {
  margin-top: 44px;
}
.intro dl div {
  display: flex;
  justify-content: space-between;
  padding: 14px 0;
  border-bottom: 1px solid #50634a;
}
.intro dt {
  color: #acbca0;
}
.intro dd {
  margin: 0;
}
.form {
  padding: 48px;
}
.form h2 {
  margin: 0 0 28px;
}
@media (max-width: 720px) {
  .edit-card {
    grid-template-columns: 1fr;
  }
  .intro,
  .form {
    padding: 32px;
  }
}
</style>
