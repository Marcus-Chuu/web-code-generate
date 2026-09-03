<script setup>
import { ref } from 'vue'
import { memberList, noticeLogs, opLogs } from '@/data/mock'

// 成员与邀请
const members = ref(memberList.map((m) => ({ ...m, checked: false })))
const allChecked = ref(false)
const inviteEmail = ref('')
const inviteRole = ref('数据分析师')
const inviteTip = ref('')

const roleOptions = ['超级管理员', '管理员', '数据分析师', '财务专员', '只读成员']

function toggleAll() {
  allChecked.value = !allChecked.value
  members.value.forEach((m) => (m.checked = allChecked.value))
}

function onMemberCheck() {
  allChecked.value = members.value.every((m) => m.checked)
}

function sendInvite() {
  if (!inviteEmail.value || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(inviteEmail.value)) {
    inviteTip.value = '请输入有效的邮箱地址'
    return
  }
  members.value.push({
    name: inviteEmail.value.split('@')[0],
    role: inviteRole.value,
    email: inviteEmail.value,
    status: '待接受',
    avatar: '邀'
  })
  inviteEmail.value = ''
  inviteTip.value = '邀请已发送，等待对方接受'
  setTimeout(() => (inviteTip.value = ''), 3500)
}

// 通知设置
const notif = ref({
  email: true,
  sms: false,
  webhook: true,
  orderAlert: true,
  revenueAlert: true,
  errorAlert: true,
  weekly: true,
  threshold: '70'
})

// 安全设置
const security = ref({
  mfa: true,
  ipWhitelist: false,
  sessionDays: 30,
  apiKey: 'yxk_9f8a7b6c5d4e3f2a1b0c'
})

function copyKey() {
  navigator.clipboard && navigator.clipboard.writeText(security.value.apiKey)
  alert('API Key 已复制到剪贴板')
}

function regenerateKey() {
  const chars = 'abcdef0123456789'
  let key = 'yxk_'
  for (let i = 0; i < 20; i++) key += chars[Math.floor(Math.random() * chars.length)]
  security.value.apiKey = key
}

// 数据管理
const dataMgr = ref({ autoBackup: true, backupTime: '02:00', retentionDays: 90 })
const saveTip = ref('')

function saveAll() {
  saveTip.value = '设置已保存 ✅'
  setTimeout(() => (saveTip.value = ''), 3000)
}
</script>

<template>
  <div class="settings">
    <div class="page-header">
      <div>
        <h1>系统设置</h1>
        <p>管理工作区成员、通知、安全与数据偏好</p>
      </div>
      <button class="btn btn-primary" @click="saveAll">保存全部设置</button>
    </div>

    <p v-if="saveTip" class="save-tip">{{ saveTip }}</p>

    <!-- 成员管理 -->
    <section class="card">
      <div class="card-header">
        <div>
          <h3 class="card-title">成员与角色</h3>
          <p class="card-sub">共 {{ members.length }} 位成员 · 角色权限继承工作区配置</p>
        </div>
      </div>

      <div class="invite-box">
        <input v-model="inviteEmail" class="input invite-input" placeholder="输入成员邮箱，例如 colleague@company.com" />
        <select v-model="inviteRole" class="select invite-select">
          <option v-for="r in roleOptions" :key="r" :value="r">{{ r }}</option>
        </select>
        <button class="btn btn-primary" @click="sendInvite">发送邀请</button>
      </div>
      <p v-if="inviteTip" class="invite-tip">{{ inviteTip }}</p>

      <div class="table-wrap">
        <table class="data-table member-table">
          <thead>
            <tr>
              <th class="check-col">
                <input type="checkbox" :checked="allChecked" @change="toggleAll" />
              </th>
              <th>成员</th>
              <th>角色</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(m, i) in members" :key="i">
              <td><input type="checkbox" v-model="m.checked" @change="onMemberCheck" /></td>
              <td>
                <div class="member-cell">
                  <span class="member-avatar" :style="{ background: i % 2 ? '#4f6ef7' : '#7c5cfc' }">{{ m.avatar }}</span>
                  <div>
                    <p class="member-name">{{ m.name }}</p>
                    <p class="member-email">{{ m.email }}</p>
                  </div>
                </div>
              </td>
              <td>
                <select class="select role-select" v-model="m.role" :disabled="m.role === '超级管理员'">
                  <option v-for="r in roleOptions" :key="r" :value="r">{{ r }}</option>
                </select>
              </td>
              <td>
                <span class="badge" :class="m.status === '启用' ? 'badge-success' : m.status === '停用' ? 'badge-danger' : 'badge-warning'">
                  <span class="status-dot" :class="m.status === '启用' ? 'dot-green' : m.status === '停用' ? 'dot-gray' : 'dot-orange'"></span>{{ m.status }}
                </span>
              </td>
              <td>
                <button v-if="m.status === '启用' && m.role !== '超级管理员'" class="btn btn-text btn-sm">停用</button>
                <button v-else-if="m.status === '停用'" class="btn btn-text btn-sm">启用</button>
                <button v-else class="btn btn-text btn-sm" disabled style="opacity:0.4">—</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <!-- 通知设置 -->
    <section class="card">
      <div class="card-header">
        <div>
          <h3 class="card-title">通知设置</h3>
          <p class="card-sub">选择接收平台消息的方式与关注事件</p>
        </div>
      </div>
      <div class="setting-grid">
        <div class="setting-block">
          <p class="block-title">接收渠道</p>
          <div class="toggle-row" v-for="(val, key) in { email: '邮件通知', sms: '短信通知', webhook: 'Webhook 推送' }" :key="key">
            <span>{{ val }}</span>
            <label class="switch"><input type="checkbox" v-model="notif[key]" /><span class="slider"></span></label>
          </div>
        </div>
        <div class="setting-block">
          <p class="block-title">关注事件</p>
          <div class="toggle-row" v-for="(val, key) in { orderAlert: '新订单 / 退款', revenueAlert: '营收异常波动', errorAlert: '系统错误告警', weekly: '每周数据简报' }" :key="key">
            <span>{{ val }}</span>
            <label class="switch"><input type="checkbox" v-model="notif[key]" /><span class="slider"></span></label>
          </div>
        </div>
      </div>
      <div class="threshold-row">
        <span>异常告警阈值（营收环比波动 %）</span>
        <input v-model="notif.threshold" class="input threshold-input" type="number" min="1" max="100" />
      </div>
    </section>

    <!-- 安全设置 -->
    <section class="card">
      <div class="card-header">
        <div>
          <h3 class="card-title">安全设置</h3>
          <p class="card-sub">账号安全与访问控制</p>
        </div>
      </div>
      <div class="setting-grid">
        <div class="setting-block">
          <p class="block-title">访问安全</p>
          <div class="toggle-row">
            <span>两步验证（MFA）</span>
            <label class="switch"><input type="checkbox" v-model="security.mfa" /><span class="slider"></span></label>
          </div>
          <div class="toggle-row">
            <span>仅允许企业 IP 访问</span>
            <label class="switch"><input type="checkbox" v-model="security.ipWhitelist" /><span class="slider"></span></label>
          </div>
          <div class="toggle-row">
            <span>会话有效期</span>
            <select class="select session-select" v-model="security.sessionDays">
              <option :value="7">7 天</option>
              <option :value="30">30 天</option>
              <option :value="90">90 天</option>
            </select>
          </div>
        </div>
        <div class="setting-block">
          <p class="block-title">API 密钥</p>
          <div class="api-row">
            <code class="api-key">{{ security.apiKey }}</code>
            <button class="btn btn-ghost btn-sm" @click="copyKey">复制</button>
            <button class="btn btn-ghost btn-sm" @click="regenerateKey">重新生成</button>
          </div>
          <p class="block-hint">密钥用于服务端数据接口调用，请妥善保管，切勿泄露到公开仓库。</p>
        </div>
      </div>
    </section>

    <!-- 数据管理 -->
    <section class="card">
      <div class="card-header">
        <div>
          <h3 class="card-title">数据管理</h3>
          <p class="card-sub">数据备份、留存与导出策略</p>
        </div>
      </div>
      <div class="setting-grid">
        <div class="setting-block">
          <p class="block-title">备份策略</p>
          <div class="toggle-row">
            <span>每日自动备份</span>
            <label class="switch"><input type="checkbox" v-model="dataMgr.autoBackup" /><span class="slider"></span></label>
          </div>
          <div class="toggle-row">
            <span>备份时间</span>
            <input v-model="dataMgr.backupTime" class="input time-input" type="time" :disabled="!dataMgr.autoBackup" />
          </div>
          <div class="toggle-row">
            <span>原始数据留存</span>
            <select class="select session-select" v-model="dataMgr.retentionDays">
              <option :value="30">30 天</option>
              <option :value="90">90 天</option>
              <option :value="180">180 天</option>
              <option :value="365">365 天</option>
            </select>
          </div>
        </div>
        <div class="setting-block">
          <p class="block-title">数据导出</p>
          <div class="data-export">
            <p class="block-hint">可导出工作区内全部报表与埋点原始数据，导出文件将发送至管理员邮箱。</p>
            <button class="btn btn-ghost btn-sm">导出全部数据（ZIP）</button>
            <button class="btn btn-ghost btn-sm" style="margin-left:8px">导出配置（JSON）</button>
          </div>
          <p class="block-title" style="margin-top:18px">最近通知记录</p>
          <ul class="mini-log">
            <li v-for="(log, i) in noticeLogs.slice(0, 3)" :key="i">
              <span class="badge" :class="'badge-' + log.statusType">{{ log.title }}</span>
              <span class="log-meta">{{ log.time }} · {{ log.channel }}</span>
            </li>
          </ul>
        </div>
      </div>
    </section>

    <!-- 操作日志 -->
    <section class="card">
      <div class="card-header">
        <div>
          <h3 class="card-title">操作日志</h3>
          <p class="card-sub">工作区近期关键操作记录</p>
        </div>
        <button class="btn btn-text">更多日志 →</button>
      </div>
      <ul class="op-list">
        <li v-for="(log, i) in opLogs" :key="i" class="op-item">
          <div class="op-main">
            <b>{{ log.user }}</b>
            <span>{{ log.action }}</span>
          </div>
          <div class="op-meta">
            <span>{{ log.time }}</span>
            <span class="op-ip">{{ log.ip }}</span>
          </div>
        </li>
      </ul>
    </section>
  </div>
</template>

<style scoped>
.settings {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.save-tip {
  background: var(--success-light);
  color: #16a34a;
  border-radius: 8px;
  padding: 10px 14px;
  font-size: 13px;
}

/* 邀请 */
.invite-box {
  display: flex;
  gap: 10px;
  margin-bottom: 6px;
}

.invite-input {
  flex: 1;
}

.invite-select {
  width: 160px;
}

.invite-tip {
  font-size: 12px;
  color: var(--primary);
  margin-bottom: 10px;
}

/* 成员表格 */
.check-col {
  width: 40px;
}

.member-table td {
  vertical-align: middle;
}

.member-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.member-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}

.member-name {
  font-weight: 600;
  font-size: 13px;
}

.member-email {
  font-size: 12px;
  color: var(--text-sub);
}

.role-select {
  width: 140px;
  padding: 5px 10px;
  font-size: 13px;
}

/* 设置网格 */
.setting-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
}

.setting-block {
  display: flex;
  flex-direction: column;
}

.block-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-main);
  margin-bottom: 12px;
}

.block-hint {
  font-size: 12px;
  color: #94a3b8;
  line-height: 1.8;
}

.toggle-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px dashed #f1f5f9;
  font-size: 14px;
}

.toggle-row:last-child {
  border-bottom: none;
}

.session-select {
  width: 120px;
  padding: 5px 10px;
}

.time-input {
  width: 120px;
  padding: 5px 10px;
}

/* API 密钥 */
.api-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.api-key {
  background: #f8fafc;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 6px 10px;
  font-size: 13px;
  color: var(--primary);
  word-break: break-all;
}

/* 数据导出 */
.data-export {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-start;
}

.mini-log {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.mini-log li {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
}

.log-meta {
  color: #94a3b8;
}

/* 操作日志 */
.op-list {
  display: flex;
  flex-direction: column;
}

.op-item {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  padding: 11px 0;
  border-bottom: 1px solid #f1f5f9;
}

.op-item:last-child {
  border-bottom: none;
}

.op-main {
  display: flex;
  gap: 8px;
  align-items: baseline;
  font-size: 13px;
  flex-wrap: wrap;
}

.op-main b {
  font-weight: 600;
  color: var(--text-main);
}

.op-main span {
  color: var(--text-sub);
}

.op-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  font-size: 12px;
  color: #94a3b8;
  flex-shrink: 0;
}

.op-ip {
  font-size: 11px;
}

@media (max-width: 900px) {
  .setting-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  .invite-box {
    flex-wrap: wrap;
  }
  .invite-select {
    width: 100%;
  }
}

@media (max-width: 640px) {
  .invite-box {
    flex-direction: column;
  }
}
</style>
