<script setup>
import {computed} from 'vue'
import {useRoute} from 'vue-router'

const route = useRoute()
const isAuthPage = computed(() => {
  return ['login', 'register'].includes(route.name)
})
</script>

<template>
  <div class="app-container" :class="{ 'auth-page-container': isAuthPage }">
    <router-view />
  </div>
</template>

<style>
.app-container {
  width: 100%;
  min-height: 100vh;
  background: var(--el-bg-color);
}

/* 登录和注册页面的背景 */
.app-container.auth-page-container {
  background: linear-gradient(135deg, #0f0f2d 0%, #1a1a3e 50%, #0d0d2b 100%);
  position: relative;
  overflow: hidden;
  perspective: 1000px;
}
</style>
