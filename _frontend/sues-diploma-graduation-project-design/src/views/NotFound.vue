<script setup>
import {useRouter} from 'vue-router'

const router = useRouter()

const goBack = () => {
  router.back()
}

const goHome = () => {
  router.push('/')
}
</script>

<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <h1 class="error-code">404</h1>
      <h2 class="error-title">页面未找到</h2>
      <p class="error-message">抱歉，您访问的页面不存在或已被移除</p>

      <div class="action-buttons">
        <el-button type="primary" @click="goBack"> 返回上一页 </el-button>
        <el-button @click="goHome"> 返回首页 </el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 100px);
  padding: 20px;
}

.not-found-content {
  text-align: center;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.05);
}

.error-code {
  font-size: 80px;
  font-weight: 700;
  color: var(--el-color-primary);
  margin: 0 0 20px;
  line-height: 1;
}

.error-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px;
}

.error-message {
  font-size: 16px;
  color: #606266;
  margin: 0 0 32px;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
}

@media screen and (max-width: 480px) {
  .error-code {
    font-size: 60px;
  }

  .error-title {
    font-size: 24px;
  }

  .error-message {
    font-size: 14px;
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style>
