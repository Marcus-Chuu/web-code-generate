<template>
  <div class="nf">
    <div class="code">404</div>
    <h2>页面走丢了</h2>
    <p>您访问的页面不存在或已被移除，请检查链接地址是否正确。</p>
    <RouterLink to="/" class="back">返回数据概览</RouterLink>
  </div>
</template>

<script setup>
import { RouterLink } from 'vue-router'
</script>

<style scoped>
.nf { text-align: center; padding: 90px 20px; }
.code { font-size: 110px; font-weight: 800; line-height: 1; background: linear-gradient(135deg, #4f6bf6, #7c5cf0); -webkit-background-clip: text; background-clip: text; color: transparent; }
h2 { margin-top: 10px; font-size: 24px; }
p { color: var(--muted); margin: 12px 0 26px; }
.back { display: inline-block; padding: 11px 26px; background: var(--primary); color: #fff; border-radius: 12px; font-weight: 600; }
.back:hover { background: #3e58e8; }
</style>
