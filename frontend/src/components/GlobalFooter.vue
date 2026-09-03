<template>
  <a-layout-footer class="site-footer">
    <div class="footer-inner">
      <span class="footer-brand">灵构 AI</span>
      <span>AI 代码自动生成 by Marcuschu</span>
      <span>把想法编织成可访问的页面</span>
    </div>
  </a-layout-footer>
</template>

<style scoped>
.site-footer { padding: 0 20px; color: #777e8c; background: #171c28; }
.footer-inner { width: min(1180px, 100%); min-height: 82px; margin: auto; display: flex; align-items: center; justify-content: space-between; gap: 20px; font-size: 13px; }
.footer-brand { color: white; font-weight: 800; }
@media (max-width: 640px) { .footer-inner { padding: 24px 0; flex-direction: column; justify-content: center; gap: 8px; } }
</style>
