<script setup>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '@/store/user'
import {createHealthProfile, getHealthProfile} from '@/api/health'
import {ElMessage} from 'element-plus'
import HealthProfileForm from '@/components/health/HealthProfileForm.vue'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

// 检查用户是否已有健康档案
const checkExistingProfile = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    loading.value = true
    const response = await getHealthProfile(userId)
    
    if (response.code === 200) {
      // 用户已有健康档案，跳转回仪表盘
      ElMessage({
        type: 'warning',
        message: '⚠️ 您已创建过健康档案，正在返回仪表盘...',
        duration: 2000
      })
      router.push('/dashboard')
    }
  } catch (error) {
    // 404 错误说明用户没有健康档案，可以继续创建
    if (error.response?.status !== 404) {
      ElMessage.error('系统异常，请稍后重试')
      router.push('/dashboard')
    }
  } finally {
    loading.value = false
  }
}

// 处理表单提交
const handleSubmit = async (formData) => {
  const userId = localStorage.getItem('userId')
  if (!userId) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    loading.value = true
    const response = await createHealthProfile({
      userId: parseInt(userId),
      ...formData
    })

    if (response.code === 200) {
      ElMessage({
        type: 'success',
        message: '✅ 健康档案创建成功',
        duration: 2000
      })
      // 更新 store 中的健康档案数据
      userStore.setHealthProfile({
        userId: parseInt(userId),
        ...formData
      })
      // 返回仪表盘
      router.push('/dashboard')
    } else if (response.code === 1006) {
      ElMessage({
        type: 'warning',
        message: '⚠️ 您已创建过健康档案',
        duration: 2000
      })
      router.push('/dashboard')
    } else {
      ElMessage.error('创建健康档案失败，请稍后重试')
    }
  } catch (error) {
    console.error('创建健康档案失败:', error)
    ElMessage.error('创建健康档案失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 处理取消
const handleCancel = () => {
  router.push('/dashboard')
}

// 组件挂载时检查是否已有健康档案
onMounted(() => {
  checkExistingProfile()
})
</script>

<template>
  <div class="create-profile-container">
    <el-card class="create-profile-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <h2 class="title">创建健康档案</h2>
          <p class="subtitle">请填写您的健康信息，帮助我们为您提供更好的服务</p>
        </div>
      </template>

      <health-profile-form
        :loading="loading"
        @submit="handleSubmit"
        @cancel="handleCancel"
      />
    </el-card>
  </div>
</template>

<style scoped>
.create-profile-container {
  max-width: 1000px;
  margin: 20px auto;
  padding: 0 20px;
}

.create-profile-card {
  margin-bottom: 20px;
}

.card-header {
  text-align: center;
  padding: 10px 0;
}

.title {
  margin: 0;
  font-size: 24px;
  color: var(--el-color-primary);
}

.subtitle {
  margin: 10px 0 0;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}
</style> 