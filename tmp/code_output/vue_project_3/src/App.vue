<script setup>
import { ref } from 'vue'
import SideNav from '@/components/SideNav.vue'
import TopBar from '@/components/TopBar.vue'

const sidebarOpen = ref(false)
const toggleSidebar = () => (sidebarOpen.value = !sidebarOpen.value)
const closeSidebar = () => (sidebarOpen.value = false)
</script>

<template>
  <div class="layout">
    <SideNav :open="sidebarOpen" @close="closeSidebar" />
    <div v-if="sidebarOpen" class="mask" @click="closeSidebar"></div>
    <div class="layout-main">
      <TopBar @toggle="toggleSidebar" />
      <main>
        <router-view />
      </main>
    </div>
  </div>
</template>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
html, body { height: 100%; }
body {
  font-family: 'PingFang SC', 'Microsoft YaHei', 'Helvetica Neue', Arial, sans-serif;
  background: #f4f6fb;
  color: #2c3a58;
  -webkit-font-smoothing: antialiased;
}
#app { height: 100%; }
a { color: inherit; }
</style>

<style scoped>
.layout { display: flex; min-height: 100vh; }
.layout-main { flex: 1; min-width: 0; display: flex; flex-direction: column; }
main { flex: 1; padding: 20px; }
.mask {
  display: none;
  position: fixed;
  inset: 0;
  z-index: 100;
  background: rgba(15, 23, 42, 0.45);
}
@media (max-width: 1023px) {
  main { padding: 14px; }
  .mask { display: block; }
}
</style>
