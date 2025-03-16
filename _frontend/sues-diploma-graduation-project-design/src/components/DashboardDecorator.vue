<template>
  <div class="dashboard-decorator anime-style street-style">
    <div class="chinese-style-overlay"></div>
    <slot></slot>
  </div>
</template>

<script setup>
import '@/styles/dashboard-decoration.css'
</script>

<style scoped>
.dashboard-decorator {
  position: relative;
  min-height: 100vh;
  width: 100%;
}
</style> 