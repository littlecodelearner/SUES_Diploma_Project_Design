<script setup>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '@/store/user'
import {getUserFullInfo, updateUserInfo} from '@/api/user'
import {ElMessage} from 'element-plus'
import {Calendar, Message, Phone, User} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

// 表单数据
const formData = ref({
  userId: localStorage.getItem('userId'),
  fullName: '',
  gender: '',
  birthDate: '',
  height: '',
  weight: '',
  email: '',
  phoneNumber: ''
})

// 用户原始数据（用于显示和比较变更）
const originalData = ref(null)

// 表单引用
const formRef = ref(null)

// 表单验证规则
const rules = {
  fullName: [
    { max: 50, message: '姓名不能超过50个字符', trigger: 'blur' }
  ],
  gender: [
    { pattern: /^(男|女)$/, message: '性别只能是"男"或"女"', trigger: 'change' }
  ],
  height: [
    { pattern: /^\d*\.?\d*$/, message: '身高必须是数字', trigger: 'blur' },
    { validator: (rule, value, callback) => {
      if (value && (value < 30 || value > 300)) {
        callback(new Error('身高必须在30-300cm之间'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ],
  weight: [
    { pattern: /^\d*\.?\d*$/, message: '体重必须是数字', trigger: 'blur' },
    { validator: (rule, value, callback) => {
      if (value && (value < 2 || value > 500)) {
        callback(new Error('体重必须在2-500kg之间'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' },
    { max: 100, message: '邮箱不能超过100个字符', trigger: 'blur' }
  ],
  phoneNumber: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    // 过滤掉空值
    const submitData = {
      userId: parseInt(formData.value.userId)
    }
    Object.keys(formData.value).forEach(key => {
      if (key !== 'userId' && formData.value[key]) {
        submitData[key] = formData.value[key]
      }
    })

    const response = await updateUserInfo(submitData)
    console.log('更新响应:', response)

    // 通用响应处理
    if (response && typeof response === 'object') {
      // 业务成功
      if (response.code === 200) {
        ElMessage({
          type: 'success',
          message: '✅ 个人信息更新成功',
          duration: 2000
        })
        // 更新 store 中的用户信息
        userStore.setUserProfile(submitData)
        // 刷新数据
        await fetchUserInfo()
        // 返回仪表盘
        router.push('/dashboard')
      } 
      // 邮箱已被注册
      else if (response.code === 1003) {
        ElMessage({
          type: 'warning',
          message: '⚠️ 该邮箱已被其他用户注册，请使用其他邮箱',
          duration: 3000
        })
        // 聚焦到邮箱输入框
        setTimeout(() => {
          const emailInput = document.querySelector('input[type="email"], input[name="email"]')
          if (emailInput) {
            emailInput.focus()
          }
        }, 500)
      } 
      // 请求格式错误
      else if (response.code === 400) {
        ElMessage({
          type: 'error',
          message: '请求格式错误，请检查输入信息',
          duration: 3000
        })
      } 
      // 更新失败
      else if (response.code === 1017) {
        ElMessage({
          type: 'error',
          message: '⚠️ 更新用户信息失败，请稍后重试',
          duration: 3000
        })
      } 
      // 其他业务错误
      else {
        ElMessage({
          type: 'error',
          message: `更新个人信息失败：${response.message || '未知错误'}`,
          duration: 3000
        })
      }
    } else {
      // 响应不符合预期格式
      ElMessage({
        type: 'error',
        message: '服务器响应格式错误，请联系管理员',
        duration: 3000
      })
    }
  } catch (error) {
    console.error('更新个人信息失败:', error)
    
    // 处理业务错误 - 在catch中也检查是否有业务错误码
    if (error.response?.data && typeof error.response.data === 'object') {
      const errorData = error.response.data
      
      if (errorData.code === 1003) {
        ElMessage({
          type: 'warning',
          message: '⚠️ 该邮箱已被其他用户注册，请使用其他邮箱',
          duration: 3000
        })
        // 聚焦到邮箱输入框
        setTimeout(() => {
          const emailInput = document.querySelector('input[type="email"], input[name="email"]')
          if (emailInput) {
            emailInput.focus()
          }
        }, 500)
        loading.value = false
        return
      }
      
      if (errorData.message) {
        ElMessage({
          type: 'error',
          message: errorData.message,
          duration: 3000
        })
        loading.value = false
        return
      }
    }
    
    // 处理HTTP错误
    if (error.response?.status === 400) {
      ElMessage({
        type: 'error',
        message: '请求参数错误，请检查填写的信息',
        duration: 3000
      })
    } else {
      ElMessage({
        type: 'error',
        message: '网络连接异常，请检查网络后重试',
        duration: 3000
      })
    }
  } finally {
    loading.value = false
  }
}

// 处理取消
const handleCancel = () => {
  router.push('/dashboard')
}

// 日期选择器配置
const datePickerOptions = {
  disabledDate(time) {
    return time.getTime() > Date.now()
  }
}

// 获取用户信息
const fetchUserInfo = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    loading.value = true
    const response = await getUserFullInfo(userId)
    
    if (response.code === 200) {
      // 保存原始数据用于显示
      originalData.value = { ...response.data }
      
      // 更新表单数据
      Object.keys(formData.value).forEach(key => {
        if (response.data[key] !== undefined) {
          formData.value[key] = response.data[key]
        }
      })
      // 更新 store 中的用户信息
      userStore.setUserProfile(response.data)
    } else if (response.code === 1000) {
      ElMessage({
        type: 'error',
        message: '⚠️ 用户账号不存在',
        duration: 2000
      })
      router.push('/login')
    } else {
      ElMessage({
        type: 'error',
        message: `获取用户信息失败：${response.message || '未知错误'}`,
        duration: 2000
      })
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage({
      type: 'error',
      message: '网络连接异常，请检查网络后重试',
      duration: 2000
    })
  } finally {
    loading.value = false
  }
}

// 格式化日期显示
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  if (isNaN(date.getTime())) return dateString
  return date.toLocaleDateString('zh-CN')
}

// 初始化表单数据
onMounted(async () => {
  await fetchUserInfo()
})
</script>

<template>
  <div class="profile-container">
    <el-card class="profile-card" v-loading="loading" :body-style="{ padding: '0' }">
      <div class="profile-header">
        <div class="profile-header-content">
          <h2 class="title">编辑个人信息</h2>
          <p class="subtitle">您可以随时更新您的个人资料以保持信息的准确性</p>
        </div>
      </div>

      <div class="profile-content">
        <!-- 当前信息概览 -->
        <transition name="fade">
          <div v-if="originalData" class="profile-summary">
            <div class="summary-header">
              <h3>当前信息</h3>
              <el-divider />
            </div>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">姓名</span>
                <span class="info-value">{{ originalData.fullName || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">性别</span>
                <span class="info-value">{{ originalData.gender || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">出生日期</span>
                <span class="info-value">{{ formatDate(originalData.birthDate) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">身高</span>
                <span class="info-value">{{ originalData.height ? `${originalData.height} cm` : '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">体重</span>
                <span class="info-value">{{ originalData.weight ? `${originalData.weight} kg` : '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">邮箱</span>
                <span class="info-value">{{ originalData.email || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">手机号码</span>
                <span class="info-value">{{ originalData.phoneNumber || '-' }}</span>
              </div>
            </div>
          </div>
        </transition>
        
        <div class="form-section">
          <div class="form-header">
            <h3>更新信息</h3>
            <el-divider />
          </div>
          <el-form
            ref="formRef"
            :model="formData"
            :rules="rules"
            label-position="top"
            class="profile-form"
          >
            <el-row :gutter="20">
              <el-col :xs="24" :sm="12">
                <el-form-item label="姓名" prop="fullName">
                  <el-input
                    v-model="formData.fullName"
                    placeholder="请输入您的姓名"
                    :prefix-icon="User"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="formData.gender" class="gender-radio">
                    <el-radio label="男">男</el-radio>
                    <el-radio label="女">女</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :xs="24" :sm="12">
                <el-form-item label="出生日期" prop="birthDate">
                  <el-date-picker
                    v-model="formData.birthDate"
                    type="date"
                    placeholder="请选择出生日期"
                    :disabled-date="datePickerOptions.disabledDate"
                    value-format="YYYY-MM-DD"
                    style="width: 100%"
                    :prefix-icon="Calendar"
                  />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input
                    v-model="formData.email"
                    placeholder="请输入邮箱地址"
                    :prefix-icon="Message"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :xs="24" :sm="12">
                <el-form-item label="身高 (cm)" prop="height">
                  <el-input-number
                    v-model="formData.height"
                    :min="30"
                    :max="300"
                    :precision="1"
                    :step="0.1"
                    placeholder="请输入身高(cm)"
                    style="width: 100%"
                    controls-position="right"
                  >
                    <template #suffix>cm</template>
                  </el-input-number>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="体重 (kg)" prop="weight">
                  <el-input-number
                    v-model="formData.weight"
                    :min="2"
                    :max="500"
                    :precision="1"
                    :step="0.1"
                    placeholder="请输入体重(kg)"
                    style="width: 100%"
                    controls-position="right"
                  >
                    <template #suffix>kg</template>
                  </el-input-number>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :xs="24" :sm="12">
                <el-form-item label="手机号码" prop="phoneNumber">
                  <el-input
                    v-model="formData.phoneNumber"
                    placeholder="请输入手机号码"
                    :prefix-icon="Phone"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <div class="form-actions">
              <el-button 
                type="primary" 
                @click="handleSubmit" 
                :loading="loading"
                round
                class="submit-btn"
              >
                保存修改
              </el-button>
              <el-button 
                @click="handleCancel" 
                round
                class="cancel-btn"
              >
                取消
              </el-button>
            </div>
          </el-form>
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.profile-container {
  max-width: 900px;
  margin: 30px auto;
  padding: 0 20px;
}

.profile-card {
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
}

.profile-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.profile-header {
  padding: 30px;
  background: linear-gradient(135deg, var(--el-color-primary-light-5), var(--el-color-primary));
  color: white;
  text-align: center;
}

.profile-header-content {
  max-width: 600px;
  margin: 0 auto;
}

.title {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 8px;
}

.subtitle {
  margin: 0;
  font-size: 16px;
  font-weight: 400;
  opacity: 0.9;
}

.profile-content {
  padding: 30px;
  background-color: white;
}

.summary-header h3,
.form-header h3 {
  font-size: 20px;
  color: var(--el-color-primary);
  margin: 0 0 8px 0;
  font-weight: 600;
}

.profile-summary {
  margin-bottom: 30px;
  padding-bottom: 10px;
  border-radius: 8px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
  margin-top: 20px;
}

.info-item {
  padding: 15px;
  border-radius: 8px;
  background-color: var(--el-color-primary-light-9);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  transition: all 0.2s ease;
}

.info-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.info-label {
  font-weight: 600;
  color: var(--el-text-color-secondary);
  font-size: 14px;
  margin-bottom: 6px;
}

.info-value {
  color: var(--el-text-color-primary);
  font-size: 16px;
  font-weight: 500;
}

.form-section {
  margin-top: 20px;
}

.profile-form {
  margin-top: 20px;
}

.gender-radio {
  display: flex;
  height: 40px;
  align-items: center;
}

.form-actions {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  gap: 16px;
}

.submit-btn {
  padding-left: 25px;
  padding-right: 25px;
  font-weight: 500;
}

.cancel-btn {
  padding-left: 25px;
  padding-right: 25px;
}

/* 添加淡入淡出过渡效果 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .profile-container {
    padding: 10px;
    margin: 15px auto;
  }
  
  .profile-header {
    padding: 20px;
  }
  
  .profile-content {
    padding: 20px;
  }
  
  .title {
    font-size: 24px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .form-actions {
    flex-direction: column;
    width: 100%;
  }
  
  .submit-btn,
  .cancel-btn {
    width: 100%;
    margin: 5px 0;
  }
}
</style>
 
            type="date"
 