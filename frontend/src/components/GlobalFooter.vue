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
.site-footer {
  padding: 0 20px;
  color: #9aa18f;
  background: #f3f5ee;
  border-top: 1px solid #e5eadd;
}
.footer-inner {
  width: min(1180px, 100%);
  min-height: 82px;
  margin: auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  font-size: 11px;
}
.footer-brand {
  color: #52684a;
  font-size: 15px;
  font-weight: 650;
}
@media (max-width: 640px) {
  .footer-inner {
    padding: 24px 0;
    flex-direction: column;
    justify-content: center;
    gap: 9px;
  }
}
</style>
