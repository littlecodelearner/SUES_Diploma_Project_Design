<script setup>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '@/store/user'
import {getHealthProfile, updateHealthProfile} from '@/api/health'
import {ElMessage} from 'element-plus'
import HealthProfileForm from '@/components/health/HealthProfileForm.vue'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const healthData = ref(null)

// 获取健康档案数据
const fetchHealthProfile = async () => {
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
      healthData.value = response.data
    } else if (response.code === 1005) {
      ElMessage({
        type: 'warning',
        message: '⚠️ 未找到健康档案',
        duration: 2000
      })
      router.push('/health/create')
    } else {
      ElMessage.error('获取健康档案失败，请稍后重试')
    }
  } catch (error) {
    console.error('获取健康档案失败:', error)
    if (error.response?.status === 404) {
      ElMessage({
        type: 'warning',
        message: '⚠️ 未找到健康档案',
        duration: 2000
      })
      router.push('/health/create')
    } else {
      ElMessage.error('获取健康档案失败，请稍后重试')
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
    const response = await updateHealthProfile({
      userId: parseInt(userId),
      ...formData
    })

    if (response.code === 200) {
      ElMessage({
        type: 'success',
        message: '✅ 健康档案更新成功',
        duration: 2000
      })
      // 更新 store 中的健康档案数据
      userStore.setHealthProfile({
        userId: parseInt(userId),
        ...formData
      })
      // 返回仪表盘
      router.push('/dashboard')
    } else if (response.code === 1005) {
      ElMessage({
        type: 'warning',
        message: '⚠️ 未找到健康档案',
        duration: 2000
      })
      router.push('/health/create')
    } else {
      ElMessage.error('更新健康档案失败，请稍后重试')
    }
  } catch (error) {
    console.error('更新健康档案失败:', error)
    ElMessage.error('更新健康档案失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 处理取消
const handleCancel = () => {
  router.push('/dashboard')
}

// 页面加载时获取健康档案数据
onMounted(() => {
  fetchHealthProfile()
})
</script>

<template>
  <div class="edit-profile-container">
    <el-card class="edit-profile-card">
      <template #header>
        <div class="card-header">
          <h2 class="title">编辑健康档案</h2>
          <p class="subtitle">您可以随时更新您的健康信息</p>
        </div>
      </template>

      <health-profile-form
        v-if="healthData"
        :initial-data="healthData"
        :loading="loading"
        @submit="handleSubmit"
        @cancel="handleCancel"
      />
      
      <div v-else class="loading-placeholder" v-loading="loading">
        <p v-if="!loading">加载健康档案失败，请刷新页面重试</p>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.edit-profile-container {
  max-width: 1000px;
  margin: 20px auto;
  padding: 0 20px;
}

.edit-profile-card {
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

.loading-placeholder {
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--el-text-color-secondary);
}
</style> 