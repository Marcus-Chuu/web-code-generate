<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { ArrowLeftOutlined, LockOutlined, UserOutlined } from '@ant-design/icons-vue'
import { apiErrorMessage } from '@/api/client'
import { useLoginUserStore } from '@/stores/loginUser'

const route = useRoute()
const router = useRouter()
const userStore = useLoginUserStore()
const loading = ref(false)
const form = reactive({ userAccount: '', userPassword: '' })

const submit = async () => {
  loading.value = true
  try {
    await userStore.login(form)
    message.success('欢迎回来')
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/'
    const prompt = typeof route.query.prompt === 'string' ? route.query.prompt : ''
    const agent = route.query.agent === '1' ? '1' : '0'
    await router.push(
      prompt && redirect === '/' ? { path: '/', query: { prompt, agent } } : redirect,
    )
  } catch (error) {
    message.error(apiErrorMessage(error, '登录失败'))
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="auth-page">
    <RouterLink to="/" class="back"><ArrowLeftOutlined /> 返回首页</RouterLink>
    <section class="auth-card surface">
      <div class="auth-intro">
        <span class="eyebrow">Welcome back</span>
        <h1>继续编织<br />你的数字灵感</h1>
        <p>登录后继续对话、部署并下载你创建的每一个应用。</p>
        <div class="visual"><span>&lt;idea&gt;</span><i></i><b>build()</b></div>
      </div>
      <div class="auth-form">
        <h2>登录灵构 AI</h2>
        <p>还没有账户？<RouterLink to="/user/register">立即注册</RouterLink></p>
        <a-form layout="vertical" :model="form" @finish="submit">
          <a-form-item
            label="账号"
            name="userAccount"
            :rules="[
              { required: true, message: '请输入账号' },
              { min: 4, message: '至少 4 位' },
            ]"
          >
            <a-input
              v-model:value="form.userAccount"
              size="large"
              autocomplete="username"
              placeholder="请输入账号"
              ><template #prefix><UserOutlined /></template
            ></a-input>
          </a-form-item>
          <a-form-item
            label="密码"
            name="userPassword"
            :rules="[
              { required: true, message: '请输入密码' },
              { min: 8, message: '至少 8 位' },
            ]"
          >
            <a-input-password
              v-model:value="form.userPassword"
              size="large"
              autocomplete="current-password"
              placeholder="请输入密码"
              ><template #prefix><LockOutlined /></template
            ></a-input-password>
          </a-form-item>
          <a-button type="primary" size="large" html-type="submit" block :loading="loading"
            >登录并继续</a-button
          >
        </a-form>
      </div>
    </section>
  </main>
</template>

<style scoped>
.auth-page {
  position: relative;
  min-height: calc(100vh - 150px);
  display: grid;
  place-items: center;
  padding: 64px 20px;
  background: radial-gradient(circle at 15% 10%, #eaf0e2 0, transparent 33%), #fafbf7;
}
.back {
  position: absolute;
  top: 28px;
  left: max(24px, calc((100% - 1040px) / 2));
  color: #646c7d;
  font-size: 13px;
}
.auth-card {
  width: min(100%, 980px);
  display: grid;
  grid-template-columns: 0.9fr 1.1fr;
  overflow: hidden;
}
.auth-intro {
  position: relative;
  min-height: 530px;
  padding: 56px 48px;
  overflow: hidden;
  color: white;
  background: #344c3b;
}
.auth-intro .eyebrow {
  color: #c5d4aa;
}
.auth-intro h1 {
  margin: 18px 0;
  font-size: 42px;
  line-height: 1.12;
  letter-spacing: -0.04em;
}
.auth-intro p {
  color: #becbb6;
  line-height: 1.8;
}
.visual {
  position: absolute;
  right: -40px;
  bottom: -30px;
  left: 40px;
  height: 230px;
  color: #c5d4aa;
  font-family: monospace;
  background: linear-gradient(145deg, #425b43, #384e3a);
  border: 1px solid #5c7350;
  border-radius: 24px 0 0;
  transform: rotate(-4deg);
}
.visual span {
  position: absolute;
  top: 40px;
  left: 40px;
  font-size: 22px;
}
.visual i {
  position: absolute;
  top: 86px;
  left: 42px;
  width: 65%;
  height: 10px;
  background: #81936c;
  border-radius: 5px;
  box-shadow:
    0 28px #5e7555,
    0 56px #5e7555;
}
.visual b {
  position: absolute;
  right: 30px;
  bottom: 25px;
  color: #79dbc0;
}
.auth-form {
  padding: 66px 64px;
}
.auth-form h2 {
  margin: 0 0 8px;
  font-size: 28px;
}
.auth-form > p {
  margin: 0 0 38px;
  color: #7c8392;
}
.auth-form a {
  color: #52765b;
  font-weight: 700;
}
@media (max-width: 760px) {
  .auth-card {
    grid-template-columns: 1fr;
  }
  .auth-intro {
    display: none;
  }
  .auth-form {
    padding: 44px 28px;
  }
}
</style>
