<script setup lang="ts">
import { ArrowRightOutlined, GlobalOutlined } from '@ant-design/icons-vue'
import { DEFAULT_AVATAR } from '@/config/avatars'
import { codeTypeLabel } from '@/utils/format'
import type { AppVO } from '@/types/domain'

defineProps<{ app: AppVO; showAuthor?: boolean }>()
defineEmits<{ open: [app: AppVO]; preview: [app: AppVO] }>()
</script>

<template>
  <article class="app-card" :class="`app-card--${app.codeGenType || 'default'}`">
    <button
      class="cover"
      type="button"
      :aria-label="`继续创作：${app.appName || '未命名应用'}`"
      @click="$emit('open', app)"
    >
      <img v-if="app.cover" :src="app.cover" :alt="app.appName || '应用封面'" />
      <span v-else class="cover-art" aria-hidden="true">
        <span class="cover-grid"></span>
        <span class="preview-window"
          ><span class="preview-chrome"><i></i><i></i><i></i></span
          ><span class="preview-content"
            ><span class="preview-sidebar"><i></i><i></i><i></i></span
            ><span class="preview-main"
              ><span class="preview-kicker">YOUR NEXT BIG IDEA</span
              ><strong>{{ app.appName || 'Made with imagination.' }}</strong
              ><span class="preview-line"></span
              ><span class="preview-blocks"><i></i><i></i><i></i></span></span></span
        ></span>
        <span class="cover-index">灵感，正在生长 ↗</span>
      </span>
      <span class="open-hint">继续创作 <ArrowRightOutlined /></span>
    </button>
    <div class="card-body">
      <div class="card-title-row">
        <div>
          <h3>{{ app.appName || '未命名应用' }}</h3>
          <span class="type-tag">{{ codeTypeLabel(app.codeGenType) }}</span>
        </div>
        <a-button
          v-if="app.id && app.codeGenType"
          shape="circle"
          title="预览作品"
          @click="$emit('preview', app)"
          ><GlobalOutlined
        /></a-button>
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
.app-card {
  --cover-bg: #edf0e2;
  --cover-accent: #9eae85;
  overflow: hidden;
  background: white;
  border: 1px solid #e4e8dd;
  border-radius: 14px;
  transition:
    transform 0.22s ease,
    box-shadow 0.22s ease;
}
.app-card--html {
  --cover-bg: #f3eadd;
  --cover-accent: #c9ac86;
}
.app-card--multi_file {
  --cover-bg: #e4eeea;
  --cover-accent: #9fbdb2;
}
.app-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px #35462b0d;
  border-color: #cad6bf;
}
.cover {
  position: relative;
  display: block;
  width: 100%;
  height: 185px;
  padding: 0;
  overflow: hidden;
  border: 0;
  background: var(--cover-bg);
  cursor: pointer;
  text-align: left;
}
.cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.35s ease;
}
.app-card:hover .cover img {
  transform: scale(1.025);
}
.cover-art {
  position: absolute;
  inset: 0;
  background: var(--cover-bg);
}
.cover-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(#ffffff45 1px, transparent 1px),
    linear-gradient(90deg, #ffffff45 1px, transparent 1px);
  background-size: 22px 22px;
}
.preview-window {
  position: absolute;
  left: 24px;
  right: 24px;
  top: 24px;
  bottom: 31px;
  overflow: hidden;
  background: #ffffffec;
  border: 1px solid #ffffff;
  border-radius: 6px;
  box-shadow: 0 5px 15px #4c593010;
  transform: rotate(-3deg);
}
.app-card--html .preview-window {
  transform: rotate(3deg);
}
.preview-chrome {
  display: flex;
  align-items: center;
  gap: 3px;
  height: 17px;
  padding: 0 7px;
  border-bottom: 1px solid #edf0e9;
}
.preview-chrome i {
  width: 3px;
  height: 3px;
  border-radius: 50%;
  background: #d0d7c6;
}
.preview-content {
  display: flex;
  height: calc(100% - 17px);
}
.preview-sidebar {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 30px;
  padding: 12px 8px;
  border-right: 1px solid #edf0e9;
}
.preview-sidebar i {
  height: 3px;
  background: #dce4d2;
  border-radius: 2px;
}
.preview-main {
  min-width: 0;
  flex: 1;
  padding: 12px;
}
.preview-kicker {
  display: block;
  color: #a1ab97;
  font-size: 4px;
  letter-spacing: 0.1em;
}
.preview-main strong {
  display: block;
  overflow: hidden;
  margin-top: 5px;
  white-space: nowrap;
  text-overflow: ellipsis;
  color: #647756;
  font-size: 9px;
  font-weight: 500;
}
.preview-line {
  display: block;
  width: 68%;
  height: 3px;
  margin-top: 7px;
  background: #edf0e7;
}
.preview-blocks {
  display: flex;
  gap: 5px;
  margin-top: 10px;
}
.preview-blocks i {
  flex: 1;
  height: 27px;
  background: var(--cover-accent);
  opacity: 0.45;
  border-radius: 3px;
}
.preview-blocks i:nth-child(2) {
  opacity: 0.65;
}
.preview-blocks i:nth-child(3) {
  opacity: 0.25;
}
.cover-index {
  position: absolute;
  bottom: 13px;
  left: 17px;
  color: #8a987d;
  font-size: 8px;
  letter-spacing: 0.1em;
}
.open-hint {
  position: absolute;
  right: 12px;
  bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 9px;
  color: #57694d;
  font-size: 10px;
  background: #ffffffeb;
  border: 1px solid #ffffff;
  border-radius: 6px;
  backdrop-filter: blur(8px);
}
.card-body {
  padding: 17px;
}
.card-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
}
.card-title-row > div {
  min-width: 0;
}
.card-title-row :deep(.ant-btn) {
  flex-shrink: 0;
  width: 27px;
  min-width: 27px;
  height: 27px;
  color: #8b977d;
  border-color: #e7ecdf;
  font-size: 12px;
}
h3 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin: 0 0 9px;
  color: #405239;
  font-size: 14px;
  font-weight: 600;
  line-height: 1.5;
}
.type-tag {
  display: inline-block;
  padding: 3px 6px;
  color: #72835f;
  font-size: 10px;
  line-height: 1.3;
  background: #f1f4eb;
  border-radius: 4px;
}
p {
  height: 42px;
  margin: 11px 0 14px;
  overflow: hidden;
  color: #7b8674;
  font-size: 12px;
  line-height: 1.75;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
.author {
  display: flex;
  align-items: center;
  gap: 7px;
  padding-top: 12px;
  color: #748167;
  font-size: 11px;
  border-top: 1px solid #eef1e8;
}
.author :deep(.ant-avatar) {
  width: 22px !important;
  height: 22px !important;
}
.author > span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
@media (max-width: 680px) {
  .cover {
    height: 210px;
  }
  .preview-window {
    left: 38px;
    right: 38px;
    top: 28px;
    bottom: 36px;
  }
  .preview-main {
    padding: 15px;
  }
  .preview-main strong {
    font-size: 12px;
  }
  .preview-blocks i {
    height: 38px;
  }
  h3 {
    font-size: 15px;
  }
  p {
    font-size: 12px;
    height: 42px;
  }
}
</style>
