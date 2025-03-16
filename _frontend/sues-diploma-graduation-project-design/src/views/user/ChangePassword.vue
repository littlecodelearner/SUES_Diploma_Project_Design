<script setup>
import {reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '@/store/user'
import {changePassword} from '@/api/user'
import {ElMessage, ElMessageBox} from 'element-plus'
import {CircleCheck, CircleClose, InfoFilled, Key, Loading, Lock} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

// 响应数据展示
const responseData = reactive({
  show: false,
  status: 'pending', // 'pending', 'success', 'error'
  code: null,
  message: '',
  details: null,
  timestamp: null
})

// 表单数据
const formData = ref({
  userId: localStorage.getItem('userId'),
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单引用
const formRef = ref(null)

// 确认密码的验证规则
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== formData.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 新密码与旧密码不能相同
const validateNewPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入新密码'))
  } else if (value === formData.value.oldPassword) {
    callback(new Error('新密码不能与旧密码相同'))
  } else {
    callback()
  }
}

// 表单验证规则
const rules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { validator: validateNewPassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 重置响应数据展示
const resetResponseData = () => {
  responseData.show = false
  responseData.status = 'pending'
  responseData.code = null
  responseData.message = ''
  responseData.details = null
  responseData.timestamp = null
}

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true
    resetResponseData()

    // 确认对话框
    await ElMessageBox.confirm(
      '确定要修改密码吗？修改成功后需要重新登录。',
      '修改确认',
      {
        confirmButtonText: '确定修改',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await changePassword({
      userId: parseInt(formData.value.userId),
      oldPassword: formData.value.oldPassword,
      newPassword: formData.value.newPassword
    })

    // 设置响应数据
    responseData.show = true
    responseData.timestamp = new Date().toLocaleString()
    responseData.code = response.code
    responseData.message = response.message || getMessageByCode(response.code)
    responseData.details = response.data || null

    if (response.code === 200) {
      responseData.status = 'success'
      
      ElMessage({
        type: 'success',
        message: '✅ 密码修改成功，请重新登录',
        duration: 2000
      })
      
      // 设置一个计时器，3秒后自动登出
      setTimeout(() => {
        userStore.clearUserInfo()
        router.push('/login')
      }, 3000)
    } else {
      responseData.status = 'error'
      
      // 根据错误码显示不同消息
      if (response.code === 1000) {
        ElMessage.error('用户账号不存在，请重新登录')
      } else if (response.code === 1004) {
        ElMessage.error('旧密码输入错误，请重试')
        // 清空旧密码字段
        formData.value.oldPassword = ''
      } else if (response.code === 1032) {
        ElMessage.error('修改密码失败，请稍后重试')
      } else {
        ElMessage.error('请求错误，请检查输入并重试')
      }
    }
  } catch (error) {
    if (error === 'cancel') {
      // 用户取消了确认
      return
    }
    
    console.error('修改密码失败:', error)
    
    // 设置错误响应数据
    responseData.show = true
    responseData.status = 'error'
    responseData.timestamp = new Date().toLocaleString()
    
    if (error.response?.data) {
      responseData.code = error.response.data.code
      responseData.message = error.response.data.message || getMessageByCode(error.response.data.code)
      responseData.details = error.response.data.data || null
    } else {
      responseData.code = -1
      responseData.message = '网络连接异常，请检查网络后重试'
    }
    
    ElMessage.error('修改密码失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 根据状态码获取默认消息
const getMessageByCode = (code) => {
  const messages = {
    200: '修改密码成功',
    400: '请求参数错误',
    1000: '用户不存在',
    1004: '旧密码输入错误',
    1032: '修改密码失败'
  }
  return messages[code] || '未知错误'
}

// 处理取消
const handleCancel = () => {
  ElMessageBox.confirm(
    '确定要取消修改密码吗？所有输入的内容将会丢失。',
    '操作确认',
    {
      confirmButtonText: '确定取消',
      cancelButtonText: '继续编辑',
      type: 'info'
    }
  ).then(() => {
    router.push('/dashboard')
  }).catch(() => {})
}

// 清除表单数据
const resetForm = () => {
  formData.value.oldPassword = ''
  formData.value.newPassword = ''
  formData.value.confirmPassword = ''
  resetResponseData()
  formRef.value?.resetFields()
}
</script>

<template>
  <div class="change-password-container">
    <el-card class="change-password-card" :body-style="{ padding: '0' }">
      <!-- 标题区域 -->
      <div class="password-header">
        <div class="header-content">
          <h2 class="title">修改密码</h2>
          <p class="subtitle">更改您的账号密码以保护账户安全</p>
        </div>
      </div>
      
      <div class="password-content">
        <!-- 步骤指南 -->
        <div class="password-guide">
          <div class="guide-step" :class="{ 'active': !responseData.show }">
            <div class="step-circle">1</div>
            <div class="step-desc">
              <h4>验证旧密码</h4>
              <p>输入您当前使用的密码</p>
            </div>
          </div>
          <div class="guide-line"></div>
          <div class="guide-step" :class="{ 'active': !responseData.show }">
            <div class="step-circle">2</div>
            <div class="step-desc">
              <h4>设置新密码</h4>
              <p>创建一个强度高的新密码</p>
            </div>
          </div>
          <div class="guide-line"></div>
          <div class="guide-step" :class="{ 'active': responseData.show && responseData.status === 'success' }">
            <div class="step-circle">3</div>
            <div class="step-desc">
              <h4>完成修改</h4>
              <p>密码修改成功后需重新登录</p>
            </div>
          </div>
        </div>

        <!-- 表单区域 -->
        <div v-if="!responseData.show" class="form-section" v-loading="loading">
          <el-form
            ref="formRef"
            :model="formData"
            :rules="rules"
            label-position="top"
            class="password-form"
          >
            <el-form-item label="旧密码" prop="oldPassword">
              <el-input
                v-model="formData.oldPassword"
                type="password"
                placeholder="请输入当前使用的密码"
                show-password
                :prefix-icon="Key"
              />
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="formData.newPassword"
                type="password"
                placeholder="请输入新密码（密码不能为空）"
                show-password
                :prefix-icon="Lock"
              />
            </el-form-item>

            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input
                v-model="formData.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
                :prefix-icon="Lock"
              />
            </el-form-item>

            <div class="form-actions">
              <el-button 
                type="primary" 
                @click="handleSubmit" 
                :loading="loading"
                round
                :icon="Key"
                class="submit-btn"
              >
                确认修改
              </el-button>
              <el-button 
                @click="handleCancel" 
                round
                class="cancel-btn"
              >
                取消
              </el-button>
              <el-button 
                type="text" 
                @click="resetForm" 
                class="reset-btn"
              >
                重置表单
              </el-button>
            </div>
          </el-form>
        </div>

        <!-- 响应结果展示卡片 -->
        <div v-if="responseData.show" class="response-card">
          <div class="response-status" :class="responseData.status">
            <div class="status-icon">
              <el-icon v-if="responseData.status === 'success'"><CircleCheck /></el-icon>
              <el-icon v-else-if="responseData.status === 'error'"><CircleClose /></el-icon>
              <el-icon v-else><Loading /></el-icon>
            </div>
            <div class="status-text">
              <h3>{{ responseData.status === 'success' ? '密码修改成功' : '密码修改失败' }}</h3>
            </div>
          </div>
          
          <div class="response-details">
            <h4>响应详情</h4>
            <el-divider />
            
            <div class="detail-item">
              <span class="detail-label">状态码：</span>
              <span class="detail-value" :class="{ 'success': responseData.code === 200, 'error': responseData.code !== 200 }">
                {{ responseData.code }}
              </span>
            </div>
            
            <div class="detail-item">
              <span class="detail-label">状态信息：</span>
              <span class="detail-value">{{ responseData.message }}</span>
            </div>
            
            <div class="detail-item" v-if="responseData.details">
              <span class="detail-label">详细数据：</span>
              <span class="detail-value">{{ JSON.stringify(responseData.details) }}</span>
            </div>
            
            <div class="detail-item">
              <span class="detail-label">响应时间：</span>
              <span class="detail-value">{{ responseData.timestamp }}</span>
            </div>
          </div>
          
          <div class="response-actions">
            <p v-if="responseData.status === 'success'" class="redirect-notice">
              <el-icon><InfoFilled /></el-icon> 
              密码已修改成功，系统将在3秒后自动退出登录...
            </p>
            
            <div class="action-buttons">
              <el-button 
                v-if="responseData.status === 'success'"
                type="primary" 
                @click="userStore.clearUserInfo(); router.push('/login')"
                round
              >
                立即登录
              </el-button>
              
              <el-button 
                v-if="responseData.status === 'error'"
                type="primary" 
                @click="resetResponseData"
                round
              >
                重新尝试
              </el-button>
              
              <el-button 
                v-if="responseData.status === 'error'"
                @click="router.push('/dashboard')"
                round
              >
                返回主页
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.change-password-container {
  max-width: 800px;
  margin: 30px auto;
  padding: 0 20px;
}

.change-password-card {
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
}

.change-password-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.password-header {
  padding: 30px;
  background: linear-gradient(135deg, var(--el-color-primary-light-5), var(--el-color-primary));
  color: white;
  text-align: center;
}

.header-content {
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

.password-content {
  padding: 30px;
  background-color: white;
}

/* 步骤指南样式 */
.password-guide {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.guide-step {
  display: flex;
  align-items: center;
  opacity: 0.6;
  transition: all 0.3s ease;
}

.guide-step.active {
  opacity: 1;
}

.step-circle {
  width: 36px;
  height: 36px;
  background-color: var(--el-color-primary-light-8);
  color: var(--el-color-primary);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  margin-right: 12px;
  transition: all 0.3s ease;
}

.active .step-circle {
  background-color: var(--el-color-primary);
  color: white;
}

.step-desc {
  flex: 1;
}

.step-desc h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.step-desc p {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.guide-line {
  height: 2px;
  flex: 1;
  background-color: var(--el-border-color-light);
  margin: 0 15px;
}

/* 表单区域样式 */
.form-section {
  margin-top: 20px;
}

.password-form {
  margin-top: 20px;
}

.form-actions {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  gap: 16px;
  flex-wrap: wrap;
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

.reset-btn {
  margin-top: 10px;
  width: 100%;
}

/* 响应结果卡片样式 */
.response-card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  margin-top: 20px;
}

.response-status {
  padding: 25px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.response-status.success {
  background-color: var(--el-color-success-lighter);
  color: var(--el-color-success);
}

.response-status.error {
  background-color: var(--el-color-danger-lighter);
  color: var(--el-color-danger);
}

.response-status.pending {
  background-color: var(--el-color-info-lighter);
  color: var(--el-color-info);
}

.status-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.status-text h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.response-details {
  padding: 20px;
}

.response-details h4 {
  margin: 0 0 12px;
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.detail-item {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.detail-label {
  font-weight: 500;
  color: var(--el-text-color-secondary);
  width: 100px;
  flex-shrink: 0;
}

.detail-value {
  flex: 1;
  word-break: break-all;
  color: var(--el-text-color-primary);
}

.detail-value.success {
  color: var(--el-color-success);
  font-weight: 500;
}

.detail-value.error {
  color: var(--el-color-danger);
  font-weight: 500;
}

.response-actions {
  padding: 20px;
  background-color: var(--el-color-info-lighter);
}

.redirect-notice {
  font-size: 14px;
  color: var(--el-color-info-dark);
  margin: 0 0 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .change-password-container {
    padding: 10px;
    margin: 15px auto;
  }
  
  .password-header {
    padding: 20px;
  }
  
  .password-content {
    padding: 20px;
  }
  
  .title {
    font-size: 24px;
  }
  
  .password-guide {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
  
  .guide-step {
    width: 100%;
  }
  
  .guide-line {
    display: none;
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
  
  .detail-item {
    flex-direction: column;
  }
  
  .detail-label {
    width: 100%;
    margin-bottom: 5px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .action-buttons button {
    width: 100%;
  }
}
</style> 