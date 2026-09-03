<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'

const route = useRoute()
const immersive = computed(() => Boolean(route.meta.immersive))
</script>

<template>
  <a-layout class="basic-layout" :class="{ immersive }">
    <GlobalHeader />
    <a-layout-content class="main-content">
      <RouterView v-slot="{ Component }">
        <Transition name="page" mode="out-in">
          <component :is="Component" />
        </Transition>
      </RouterView>
    </a-layout-content>
    <GlobalFooter v-if="!immersive" />
  </a-layout>
</template>

<style scoped>
.basic-layout { min-height: 100vh; background: #f6f6f2; }
.main-content { flex: 1; width: 100%; }
.immersive .main-content { display: flex; min-height: 0; }
.page-enter-active,.page-leave-active { transition: opacity .18s ease, transform .18s ease; }
.page-enter-from { opacity: 0; transform: translateY(5px); }
.page-leave-to { opacity: 0; }
</style>
