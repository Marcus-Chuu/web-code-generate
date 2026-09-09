<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'

const theme = {
  token: {
    colorPrimary: '#52765b',
    colorInfo: '#52765b',
    colorSuccess: '#12a67d',
    colorText: '#2e4235',
    colorTextSecondary: '#7c8578',
    colorBgLayout: '#fafbf7',
    colorBorder: '#dfe5d9',
    controlHeight: 36,
    borderRadius: 10,
    borderRadiusLG: 16,
    fontFamily:
      "Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif",
  },
}
</script>

<template>
  <a-config-provider :theme="theme">
    <BasicLayout />
  </a-config-provider>
</template>
