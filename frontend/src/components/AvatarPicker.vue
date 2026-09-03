<script setup lang="ts">
import { CheckOutlined } from '@ant-design/icons-vue'
import { AVATAR_OPTIONS } from '@/config/avatars'

defineProps<{ modelValue: string }>()
defineEmits<{ 'update:modelValue': [value: string] }>()
</script>

<template>
  <div class="avatar-picker" role="radiogroup" aria-label="选择头像">
    <button
      v-for="avatar in AVATAR_OPTIONS"
      :key="avatar.src"
      type="button"
      class="avatar-choice"
      :class="{ active: modelValue === avatar.src }"
      role="radio"
      :aria-checked="modelValue === avatar.src"
      :aria-label="avatar.label"
      @click="$emit('update:modelValue', avatar.src)"
    >
      <img :src="avatar.src" :alt="avatar.label" />
      <span v-if="modelValue === avatar.src"><CheckOutlined /></span>
    </button>
  </div>
</template>

<style scoped>
.avatar-picker { display: flex; flex-wrap: wrap; gap: 14px; }
.avatar-choice { position: relative; width: 70px; height: 70px; padding: 3px; cursor: pointer; background: #eef0f5; border: 2px solid transparent; border-radius: 22px; transition: .22s ease; }
.avatar-choice:hover { border-color: #8e84ff; transform: translateY(-2px); }
.avatar-choice.active { border-color: var(--accent); box-shadow: 0 9px 24px rgba(102, 89, 255, .22); }
.avatar-choice img { width: 100%; height: 100%; object-fit: cover; border-radius: 17px; }
.avatar-choice span { position: absolute; right: -5px; bottom: -5px; display: grid; width: 24px; height: 24px; color: #fff; font-size: 12px; background: var(--accent); border: 2px solid #fff; border-radius: 50%; place-items: center; }
</style>
