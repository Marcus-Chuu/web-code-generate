<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { ArrowLeftOutlined, LockOutlined, UserOutlined } from '@ant-design/icons-vue'
import AvatarPicker from '@/components/AvatarPicker.vue'
import { DEFAULT_AVATAR } from '@/config/avatars'
import { apiErrorMessage } from '@/api/client'
import { useLoginUserStore } from '@/stores/loginUser'

const router = useRouter()
const userStore = useLoginUserStore()
const loading = ref(false)
const form = reactive({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
  userAvatar: DEFAULT_AVATAR,
})
const validateConfirm = async (_rule: unknown, value: string) => {
  if (!value) return Promise.reject('请再次输入密码')
  if (value !== form.userPassword) return Promise.reject('两次输入的密码不一致')
  return Promise.resolve()
}
const submit = async () => {
  loading.value = true
  try {
    await userStore.register(form)
    message.success('注册成功，请登录')
    await router.push('/user/login')
  } catch (error) {
    message.error(apiErrorMessage(error, '注册失败'))
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="register-page">
    <RouterLink to="/" class="back"><ArrowLeftOutlined /> 返回首页</RouterLink>
    <section class="register-card surface">
      <div class="register-heading">
        <span class="eyebrow">Join the studio</span>
        <h1>创建你的创作者账户</h1>
        <p>选择一个喜欢的形象，从第一句话开始构建。</p>
      </div>
      <a-form class="register-form" layout="vertical" :model="form" @finish="submit">
        <a-form-item label="选择头像" name="userAvatar"
          ><AvatarPicker v-model="form.userAvatar"
        /></a-form-item>
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
            placeholder="4 - 16 位账号"
            ><template #prefix><UserOutlined /></template
          ></a-input>
        </a-form-item>
        <div class="password-grid">
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
              autocomplete="new-password"
              placeholder="至少 8 位"
              ><template #prefix><LockOutlined /></template
            ></a-input-password>
          </a-form-item>
          <a-form-item
            label="确认密码"
            name="checkPassword"
            :rules="[{ validator: validateConfirm }]"
          >
            <a-input-password
              v-model:value="form.checkPassword"
              size="large"
              autocomplete="new-password"
              placeholder="再次输入密码"
            />
          </a-form-item>
        </div>
        <a-button type="primary" size="large" html-type="submit" block :loading="loading"
          >创建账户</a-button
        >
        <p class="login-link">已有账户？<RouterLink to="/user/login">直接登录</RouterLink></p>
      </a-form>
    </section>
  </main>
</template>

<style scoped>
.register-page {
  position: relative;
  min-height: calc(100vh - 150px);
  display: grid;
  place-items: center;
  padding: 70px 20px;
  background:
    radial-gradient(circle at 85% 12%, #ddf6ed 0, transparent 30%),
    radial-gradient(circle at 15% 85%, #f1eddf 0, transparent 30%), #fafbf7;
}
.back {
  position: absolute;
  top: 28px;
  left: max(24px, calc((100% - 760px) / 2));
  color: #646c7d;
  font-size: 13px;
}
.register-card {
  width: min(100%, 760px);
  padding: 48px 58px;
}
.register-heading {
  text-align: center;
  margin-bottom: 34px;
}
.register-heading h1 {
  margin: 10px 0;
  font-size: 34px;
  letter-spacing: -0.04em;
}
.register-heading p {
  color: #777f90;
}
.register-form {
  max-width: 590px;
  margin: auto;
}
.password-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
}
.login-link {
  text-align: center;
  margin: 20px 0 0;
  color: #7b8291;
}
.login-link a {
  color: #52765b;
  font-weight: 700;
}
@media (max-width: 620px) {
  .register-card {
    padding: 38px 24px;
  }
  .password-grid {
    grid-template-columns: 1fr;
  }
  .register-heading h1 {
    font-size: 29px;
  }
}
</style>
