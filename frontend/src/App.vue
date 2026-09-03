<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'

const theme = {
  token: {
    colorPrimary: '#6558f5',
    colorInfo: '#6558f5',
    colorSuccess: '#12a67d',
    colorText: '#172033',
    colorTextSecondary: '#677086',
    colorBgLayout: '#f6f6f2',
    borderRadius: 10,
    borderRadiusLG: 16,
    fontFamily: "Inter, 'PingFang SC', 'Microsoft YaHei', sans-serif",
  },
}
</script>

<template>
  <a-config-provider :theme="theme">
    <BasicLayout />
  </a-config-provider>
</template>
