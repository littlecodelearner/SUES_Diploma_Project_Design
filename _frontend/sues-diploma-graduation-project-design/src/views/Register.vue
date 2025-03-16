<script setup>
import {computed, reactive, ref} from 'vue'
import {ElMessage} from 'element-plus'
import {useRouter} from 'vue-router'
import {register} from '@/api/user'
import {formatDate} from '@/utils/date'
import {Lock, Message, Phone, User, UserFilled} from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

// 表单数据
const formData = reactive({
  username: '',
  passwordHash: '',
  confirmPassword: '',
  fullName: '',
  gender: '',
  birthDate: '',
  height: null,
  weight: null,
  email: '',
  phoneNumber: ''
})

// 日期选择器配置
const datePickerOptions = {
  disabledDate(time) {
    return time.getTime() > Date.now()
  },
  shortcuts: [
    {
      text: '18岁',
      value: new Date(Date.now() - 18 * 365 * 24 * 60 * 60 * 1000)
    },
    {
      text: '30岁',
      value: new Date(Date.now() - 30 * 365 * 24 * 60 * 60 * 1000)
    }
  ]
}

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { max: 50, message: '用户名不能超过50个字符', trigger: 'blur' }
  ],
  passwordHash: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { max: 512, message: '密码不能超过512个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value) => {
        if (value !== formData.passwordHash) {
          return Promise.reject('两次输入的密码不一致')
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ],
  fullName: [{ max: 100, message: '姓名不能超过100个字符', trigger: 'blur' }],
  gender: [
    { max: 3, message: '性别不能超过3个字符', trigger: 'blur' },
    {
      validator: (rule, value) => {
        if (value && !['男', '女'].includes(value)) {
          return Promise.reject('性别只能是男或女')
        }
        return Promise.resolve()
      },
      trigger: 'change'
    }
  ],
  birthDate: [
    {
      validator: (rule, value) => {
        if (!value) return Promise.resolve()

        // 验证日期格式
        let date
        if (typeof value === 'string' && value.includes('T')) {
          // 如果是ISO格式的字符串（带T的），尝试解析
          date = new Date(value)
        } else {
          // 否则按照普通日期格式解析
          date = new Date(value)
        }
        
        if (isNaN(date.getTime())) {
          return Promise.reject('日期格式不正确')
        }

        // 验证日期范围
        if (date > new Date()) {
          return Promise.reject('出生日期不能晚于今天')
        }

        return Promise.resolve()
      },
      trigger: 'change'
    }
  ],
  height: [
    { required: true, message: '请输入身高，此数据将用于后续健康指标的计算', trigger: 'blur' },
    { type: 'number', message: '身高必须为数字', trigger: 'blur' },
    {
      validator: (rule, value) => {
        if (!value) return Promise.reject('身高是必填项')
        if (value < 30 || value > 500) {
          return Promise.reject('身高必须在 30-500cm 之间')
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ],
  weight: [
    { required: true, message: '请输入体重，此数据将用于后续健康指标的计算（可在后续记录中更新）', trigger: 'blur' },
    { type: 'number', message: '体重必须为数字', trigger: 'blur' },
    {
      validator: (rule, value) => {
        if (!value) return Promise.reject('体重是必填项')
        if (value < 10 || value > 500) {
          return Promise.reject('体重必须在 10-500kg 之间')
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ],
  email: [
    { max: 255, message: '邮箱地址不能超过255个字符', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phoneNumber: [
    { max: 20, message: '电话号码不能超过20个字符', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    loading.value = true
    await formRef.value.validate()

    // 准备提交数据
    const submitData = {
      username: formData.username,
      passwordHash: formData.passwordHash,
      fullName: formData.fullName || undefined,
      gender: formData.gender || undefined,
      birthDate: formData.birthDate ? formatDate(formData.birthDate, 'datetime') : undefined,
      height: formData.height || undefined,
      weight: formData.weight || undefined,
      email: formData.email || undefined,
      phoneNumber: formData.phoneNumber || undefined
    }

    // 移除所有 undefined 的字段
    Object.keys(submitData).forEach(key => {
      if (submitData[key] === undefined) {
        delete submitData[key]
      }
    })

    // 添加日志输出
    if (import.meta.env.DEV) {
      console.log('提交的注册数据:', submitData)
      if (submitData.birthDate) {
        console.log('格式化后的出生日期:', submitData.birthDate)
        console.log('出生日期是否包含中国时区标识(+08:00):', submitData.birthDate.includes('+08:00'))
      }
    }

    const response = await register(submitData)

    if (response.code === 200) {
      ElMessage({
        type: 'success',
        dangerouslyUseHTMLString: true,
        message: `
          <div class="message-container success-message">
            <div class="message-icon">
              <span class="icon">🎉</span>
              <div class="icon-background"></div>
            </div>
            
            <div class="message-content">

              <div class="message-header">
                <h3>注册成功</h3>
                <h3>欢迎加入每日运动健康管理系统</h3>
              </div>
              
              <div class="message-body">
                <div class="info-item">
                  <h3> 💡 即将为您跳转到登录页面...</h3>
                </div>
                </div>
              
            </div>
          </div>
        `,
        duration: 3000,
        showClose: true,
        customClass: 'global-message success'
      })
      
      // 延迟跳转时间稍微延长，让用户有足够时间看到动画效果
      setTimeout(() => {
        router.push('/login')
      }, 3000)
    } else {
      ElMessage.error(response.message || '注册失败')
    }
  } catch (error) {
    console.error('注册失败:', error)
    
    // 获取错误信息
    let errorTitle = '注册遇到了一点小问题'
    let errorMessage = '服务器暂时无法处理您的请求'
    let errorSuggestion = '请稍后重试，或联系管理员获取帮助'
    
    // 处理特定的业务错误
    if (error.response?.data) {
      const { code, message } = error.response.data
      
      switch (code) {
        case 1002:
          errorTitle = '用户名已被注册'
          errorMessage = '该用户名无法使用'
          errorSuggestion = '请尝试使用其他用户名重新注册'
          break
        case 1003:
          errorTitle = '邮箱已被注册'
          errorMessage = '该邮箱无法使用'
          errorSuggestion = '请使用其他邮箱地址，或通过该邮箱找回已有账号'
          break
        default:
          // 如果有后端返回的错误信息，优先使用后端的错误信息
          if (message) {
            errorMessage = message
          }
          // 如果是服务器错误
          if (error.response.status >= 500) {
            errorTitle = '服务器错误'
            errorMessage = '服务器处理请求时出现异常'
            errorSuggestion = '请稍后重试，如果问题持续存在请联系管理员'
          }
      }
    } else if (error.code === 'ECONNABORTED') {
      // 处理请求超时
      errorTitle = '请求超时'
      errorMessage = '服务器响应时间过长'
      errorSuggestion = '请检查网络连接是否正常，稍后重试'
    } else if (!error.response) {
      // 处理网络错误
      errorTitle = '网络连接失败'
      errorMessage = '无法连接到服务器'
      errorSuggestion = '请检查网络连接是否正常'
    }

    ElMessage({
      type: 'error',
      dangerouslyUseHTMLString: true,
      message: `
        <div class="message-container error-message">
          <div class="message-icon">
            <span class="icon">⚠️</span>
            <div class="icon-background"></div>
          </div>
          
          <div class="message-content">
            <div class="message-header">
              <h3>${errorTitle}</h3>
              <p class="subtitle">${errorMessage}</p>
            </div>
            
            <div class="message-body">
              <div class="info-item">
                <span class="info-icon">💡</span>
                <span>${errorSuggestion}</span>
              </div>
            </div>
          </div>
        </div>
      `,
      duration: 5000,
      showClose: true,
      customClass: 'global-message error'
    })
  } finally {
    loading.value = false
  }
}

// 计算表单完成度
const formProgress = computed(() => {
  const requiredFields = ['username', 'passwordHash', 'confirmPassword', 'height', 'weight']
  const filledRequired = requiredFields.filter(field => {
    // 对于密码确认，需要检查是否与密码相同
    if (field === 'confirmPassword') {
      return formData[field] && formData[field] === formData.passwordHash
    }
    // 对于身高和体重，需要检查是否为有效数字
    if (field === 'height' || field === 'weight') {
      return formData[field] && formData[field] > 0
    }
    // 其他字段只需检查是否有值
    return formData[field]
  }).length
  return Math.round((filledRequired / requiredFields.length) * 100)
})

// 添加一个计算属性来判断是否可以提交
const canSubmit = computed(() => {
  return formData.username && 
         formData.passwordHash && 
         formData.confirmPassword && 
         formData.passwordHash === formData.confirmPassword &&
         formData.height > 0 &&
         formData.weight > 0
})
</script>

<template>
  <div class="register-container">
    <div class="fitness-background"></div>
        <div class="decoration decoration-left-1">💪</div>
        <div class="decoration decoration-left-2">🎯</div>
        <div class="decoration decoration-left-3">🏃</div>
        <div class="decoration decoration-right-1">🏋️</div>
        <div class="decoration decoration-right-2">🏆</div>
        <div class="decoration decoration-right-3">⚡</div>
        
    <el-card class="register-card">
      <h2 class="register-title">健康生活 从这里开始</h2>
      <p class="register-subtitle">加入每日运动健康管理系统</p>

      <!-- 提示信息 -->
          <div class="form-tips">
            <el-alert type="info" :closable="false" class="custom-alert">
              <template #title>
                <div class="tips-header">填写说明</div>
              </template>
              <div class="tips-content">
                    <div class="required-info">
                      <span class="required-field">*</span>
                      <span class="required-text">用户名、密码、身高和体重为必填项</span>
                    </div>
                    <div class="optional-info">
                      <span class="optional-icon">💡</span>
                      <span class="optional-text">其他信息为选填项，可以在注册后在【个人资料】完善</span>
                    </div>
              </div>
            </el-alert>
          </div>

      <!-- 进度条 -->
          <div class="progress-section">
            <span class="progress-label">必填项完成度</span>
            <el-progress
              :percentage="formProgress"
              :format="percentage => `${percentage}%`"
              :stroke-width="10"
              class="custom-progress"
            />
          </div>

      <!-- 表单内容 -->
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-position="top"
        class="register-form"
        :disabled="loading"
      >
        <!-- 账号信息部分 -->
        <div class="form-section">
          <h3 class="section-title">账号信息</h3>
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="formData.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item label="密码" prop="passwordHash">
            <el-input
              v-model="formData.passwordHash"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="formData.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>
        </div>

        <!-- 个人信息部分 -->
        <div class="form-section">
          <h3 class="section-title">个人信息 <span class="section-optional">(选填)</span></h3>
          
          <!-- 添加身高体重提示信息 -->
          <el-alert
            type="warning"
            :closable="false"
            class="metrics-alert"
            style="margin-bottom: 20px;"
          >
            <template #title>
              <div class="metrics-tips">
                <span style="font-weight: bold;">📊 重要提示：</span>
                <div class="metrics-description">
                  <p>身高和体重是必填项，这些数据将用于：</p>
                  <ul style="margin: 5px 0; padding-left: 20px;">
                    <li>计算您的 BMI 指数</li>
                    <li>制定个性化的运动建议</li>
                    <li>追踪您的健康状况变化</li>
                  </ul>
                  <p style="color: #e6a23c; font-size: 0.9em;">* 身高和体重数据可在登录后的【个人资料】中随时更新</p>
                  <p style="color: #e6a23c; font-size: 0.9em;">* 体重数据也会在后续新记录的数据中随时更新</p>
                </div>
              </div>
            </template>
          </el-alert>

          <el-form-item label="姓名" prop="fullName">
            <el-input
              v-model="formData.fullName"
              placeholder="请输入姓名"
              :prefix-icon="UserFilled"
            />
          </el-form-item>

          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="formData.gender" class="gender-group">
              <el-radio label="男">男</el-radio>
              <el-radio label="女">女</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="出生日期" prop="birthDate">
            <el-date-picker
              v-model="formData.birthDate"
              type="date"
              placeholder="选择出生日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              :disabled-date="datePickerOptions.disabledDate"
              :shortcuts="datePickerOptions.shortcuts"
              style="width: 100%"
            />
          </el-form-item>

          <div class="metrics-group">
            <el-form-item label="身高(cm)" prop="height" class="metric-item">
              <el-input-number
                v-model="formData.height"
                :min="30"
                :max="500"
                placeholder="身高"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="体重(kg)" prop="weight" class="metric-item">
              <el-input-number
                v-model="formData.weight"
                :min="10"
                :max="500"
                placeholder="体重"
                style="width: 100%"
              />
            </el-form-item>
          </div>
        </div>

        <!-- 联系方式部分 -->
        <div class="form-section">
          <h3 class="section-title">联系方式 <span class="section-optional">(选填)</span></h3>
            <el-form-item label="邮箱" prop="email">
            <el-input
              v-model="formData.email"
              placeholder="请输入邮箱"
              :prefix-icon="Message"
            />
            </el-form-item>

          <el-form-item label="手机号码" prop="phoneNumber">
            <el-input
              v-model="formData.phoneNumber"
              placeholder="请输入手机号码"
              :prefix-icon="Phone"
            />
            </el-form-item>
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <el-button 
            type="primary" 
            :loading="loading" 
            :disabled="!canSubmit"
            @click="handleSubmit"
          >
            {{ loading ? '注册中...' : '开启健康之旅' }}
          </el-button>
          <el-button class="reset-button" @click="() => formRef?.resetFields()">
            重置 
          </el-button>
        </div>

        <!-- 登录链接 -->
        <div class="login-link" style="text-align: center; margin-top: 20px; color: #95f;">
          已有账号？请点击👉<el-link type="primary" @click="router.push('/login')">立即登录</el-link>
        </div>
      </el-form>
    </el-card>

    <!-- 霓虹灯效果 -->
    <div class="neon-effects">
      <div class="neon-sign neon-sign-left-1">HEALTH</div>
      <div class="neon-sign neon-sign-left-2">FITNESS</div>
      <div class="neon-sign neon-sign-left-3">POWER</div>
      <div class="neon-sign neon-sign-right-1">STRONG</div>
      <div class="neon-sign neon-sign-right-2">ENERGY</div>
      <div class="neon-sign neon-sign-right-3">LIFE</div>
    </div>
  </div>
</template>

<style>
@import '../styles/register.css';

/* 额外的组件特定样式 */
.metrics-group {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.metric-item {
  margin-bottom: 0;
}

.gender-group {
  display: flex;
  gap: 30px;
}

.gender-group :deep(.el-radio__label) {
  color: #fff;
  font-size: 1.1em;
}

.gender-group :deep(.el-radio__input.is-checked + .el-radio__label) {
  color: #ffd700;
}

.gender-group :deep(.el-radio__input.is-checked .el-radio__inner) {
  background: #ffd700;
  border-color: #ffd700;
}

/* 日期选择器样式 */
:deep(.el-date-picker) {
  background: rgba(30, 41, 59, 0.95);
  border: 1px solid rgba(255, 165, 0, 0.2);
  backdrop-filter: blur(20px);
}

:deep(.el-date-picker__header) {
  color: #fff;
}

:deep(.el-date-picker__header-label) {
  color: #ffd700;
}

:deep(.el-picker-panel__icon-btn) {
  color: #ffd700;
}

:deep(.el-date-table th) {
  color: rgba(255, 255, 255, 0.8);
}

:deep(.el-date-table td.available:hover) {
  color: #ffd700;
}

:deep(.el-date-table td.current:not(.disabled) span) {
  background-color: #ffd700;
  color: #000;
}

/* 数字输入框样式 */
:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-input-number .el-input__wrapper) {
  background: rgba(30, 41, 59, 0.8);
  border: 1px solid rgba(255, 165, 0, 0.2);
}

:deep(.el-input-number__decrease),
:deep(.el-input-number__increase) {
  background: rgba(30, 41, 59, 0.9);
  border-color: rgba(255, 165, 0, 0.2);
  color: #ffd700;
}

:deep(.el-input-number__decrease:hover),
:deep(.el-input-number__increase:hover) {
  color: #fff;
  background: rgba(255, 165, 0, 0.2);
}

/* 登录链接样式 */
.login-link {
  text-align: center;
  margin-top: 20px;
  color: rgba(255, 255, 255, 0.8);
}

.login-link :deep(.el-link) {
  color: #ffd700 !important;
  font-weight: 600;
  margin-left: 8px;
}

.login-link :deep(.el-link:hover) {
  color: #fff !important;
  text-shadow: 0 0 10px rgba(255, 215, 0, 0.5);
}

/* 可选标记样式 */
.section-optional {
  font-size: 0.8em;
  color: rgba(255, 255, 255, 0.6);
  font-weight: normal;
  margin-left: 8px;
}

/* 身高体重提示框样式 */
.metrics-alert {
  background: rgba(255, 165, 0, 0.1) !important;
  border: 1px solid rgba(255, 165, 0, 0.2) !important;
  border-radius: 8px;
}

.metrics-alert :deep(.el-alert__title) {
  color: #333 !important;
  font-size: 1em;
}

.metrics-tips {
  color: #333;
}

.metrics-description {
  margin-top: 8px;
  color: #333;
}

.metrics-description p {
  color: #333;
  font-weight: 500;
}

.metrics-description ul {
  list-style-type: none;
}

.metrics-description ul li {
  margin: 5px 0;
  position: relative;
  padding-left: 20px;
  color: #444;
}

.metrics-description ul li:before {
  content: "✓";
  position: absolute;
  left: 0;
  color: #f90;
}

.metrics-description p:last-child {
  color: #d37a0f;
  font-weight: 600;
}
</style>
