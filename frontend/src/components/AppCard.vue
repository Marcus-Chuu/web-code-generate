<script setup lang="ts">
import { ArrowRightOutlined, CodeOutlined, GlobalOutlined } from '@ant-design/icons-vue'
import { DEFAULT_AVATAR } from '@/config/avatars'
import { codeTypeLabel } from '@/utils/format'
import type { AppVO } from '@/types/domain'

defineProps<{ app: AppVO; showAuthor?: boolean }>()
defineEmits<{ open: [app: AppVO]; preview: [app: AppVO] }>()
</script>

<template>
  <article class="app-card">
    <button class="cover" type="button" @click="$emit('open', app)">
      <img v-if="app.cover" :src="app.cover" :alt="app.appName || '应用封面'" />
      <span v-else class="cover-art"><CodeOutlined /><i></i></span>
      <span class="open-hint">继续创作 <ArrowRightOutlined /></span>
    </button>
    <div class="card-body">
      <div class="card-title-row">
        <div>
          <h3>{{ app.appName || '未命名应用' }}</h3>
          <span class="type-tag">{{ codeTypeLabel(app.codeGenType) }}</span>
        </div>
        <a-button v-if="app.id && app.codeGenType" shape="circle" title="预览作品" @click="$emit('preview', app)"><GlobalOutlined /></a-button>
      </div>
      <p>{{ app.initPrompt || '一段新的产品灵感正在这里生长。' }}</p>
      <div v-if="showAuthor" class="author">
        <a-avatar :size="28" :src="app.user?.userAvatar || DEFAULT_AVATAR" />
        <span>{{ app.user?.userName || app.user?.userAccount || '灵构创作者' }}</span>
      </div>
    </div>
  </article>
</template>

<style scoped>
.app-card { overflow: hidden; background: white; border: 1px solid #e7e7e4; border-radius: 18px; transition: transform .22s ease, box-shadow .22s ease; }
.app-card:hover { transform: translateY(-4px); box-shadow: 0 20px 48px rgba(42,45,66,.11); }
.cover { position: relative; display: block; width: 100%; height: 184px; padding: 0; overflow: hidden; border: 0; background: #e7e9f6; cursor: pointer; }
.cover img { width: 100%; height: 100%; object-fit: cover; transition: transform .35s ease; }
.app-card:hover .cover img { transform: scale(1.025); }
.cover-art { position: absolute; inset: 0; display: grid; place-items: center; color: white; font-size: 42px; background: radial-gradient(circle at 24% 20%, #9c94ff 0 18%, transparent 19%), linear-gradient(145deg,#1b2233,#5e55df); }
.cover-art i { position: absolute; right: 18%; bottom: 14%; width: 80px; height: 80px; border: 1px solid rgba(255,255,255,.26); border-radius: 24px; transform: rotate(18deg); }
.open-hint { position: absolute; right: 14px; bottom: 14px; display: flex; align-items: center; gap: 6px; padding: 8px 12px; color: #172033; font-size: 12px; font-weight: 700; background: rgba(255,255,255,.9); border-radius: 100px; backdrop-filter: blur(8px); }
.card-body { padding: 18px; }
.card-title-row { display: flex; align-items: flex-start; justify-content: space-between; gap: 10px; }
h3 { margin: 0 0 8px; font-size: 17px; letter-spacing: -.02em; }
.type-tag { padding: 4px 7px; color: #6157ca; font-size: 10px; font-weight: 750; background: #f0efff; border-radius: 6px; }
p { height: 44px; margin: 14px 0 12px; overflow: hidden; color: #747b8b; font-size: 13px; line-height: 1.7; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.author { display: flex; align-items: center; gap: 8px; padding-top: 12px; color: #586073; font-size: 12px; border-top: 1px solid #f0f0ed; }
</style>
