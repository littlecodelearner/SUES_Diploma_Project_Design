<script setup>
import {computed, onMounted, reactive, ref, watch} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import {Lock, User, UserFilled} from '@element-plus/icons-vue'
import {useUserStore} from '@/store/user'
import {login} from '@/api/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

// 图片错误处理函数
const handleImageError = (event, fileName) => {
  console.warn(`图片加载失败: ${fileName}`)
  // 设置一个默认的占位图片
  event.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="240" height="180" viewBox="0 0 240 180"%3E%3Crect width="240" height="180" fill="%23333"/%3E%3Ctext x="120" y="90" fill="%23fff" font-family="Arial" font-size="14" text-anchor="middle" dominant-baseline="middle"%3E图片加载失败%3C/text%3E%3C/svg%3E'
  // 防止图片持续触发错误事件
  event.target.onerror = null
}

// 身份选择
const userRole = ref('user') // 'user' 或 'admin'

// 普通用户表单数据
const userFormData = reactive({
  username: '',
  passwordHash: '',
  remember: false,
  rememberPassword: false
})

// 管理员表单数据
const adminFormData = reactive({
  username: '',
  passwordHash: ''
})

// 当前表单数据（根据角色动态切换）
const formData = computed(() => 
  userRole.value === 'admin' ? adminFormData : userFormData
)

// 监听角色切换
watch(userRole, (newRole) => {
  // 切换角色时重置表单
  formRef.value?.resetFields()
  if (newRole === 'user') {
    // 切换到用户模式时，加载保存的用户信息
    initRememberedInfo()
  } else {
    // 切换到管理员模式时，清空表单
    adminFormData.username = ''
    adminFormData.passwordHash = ''
  }
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { max: 50, message: '用户名不能超过50个字符', trigger: 'blur' }
  ],
  passwordHash: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { max: 512, message: '密码不能超过512个字符', trigger: 'blur' }
  ]
}

// 保存用户登录凭据（仅用于普通用户）
const saveUserCredentials = () => {
  if (userRole.value !== 'user') return

  // 保存用户名（如果选择记住用户名）
  if (userFormData.remember) {
    localStorage.setItem('rememberedUsername', userFormData.username)
  } else {
    localStorage.removeItem('rememberedUsername')
  }

  // 保存密码（如果选择记住密码）
  if (userFormData.rememberPassword) {
    try {
      localStorage.setItem('rememberedPassword', btoa(userFormData.passwordHash))
    } catch (e) {
      console.error('密码保存失败:', e)
      localStorage.removeItem('rememberedPassword')
    }
  } else {
    localStorage.removeItem('rememberedPassword')
  }
}

// 处理管理员登录
const handleAdminLogin = async () => {
  if (adminFormData.username === 'master' && adminFormData.passwordHash === 'master') {
    await userStore.setUserInfo({
      userId: 'admin',
      role: 'admin'
    })
    ElMessage.success('管理员登录成功')
    await router.push('/admin/dashboard')
  } else {
    ElMessage.error('管理员账号或密码错误')
  }
}

// 处理普通用户登录
const handleUserLogin = async () => {
  const submitData = {
    username: userFormData.username,
    password: userFormData.passwordHash
  }

  const response = await login(submitData)

  if (response.code === 200) {
    // 保存用户信息
    await userStore.setUserInfo({
      userId: response.data.userId,
      role: 'user'
    })
    
    // 登录成功后保存凭据
    saveUserCredentials()

    ElMessage.success('登录成功')
    await router.push('/dashboard')
  } else {
    ElMessage.error(response.message || '登录失败')
  }
}

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    loading.value = true
    await formRef.value.validate()

    // 根据角色调用不同的登录处理函数
    if (userRole.value === 'admin') {
      await handleAdminLogin()
    } else {
      await handleUserLogin()
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error('登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 跳转到注册页面
const goToRegister = async () => {
  try {
    ElMessage.info('跳转到注册页面...')
    await router.push('/register')
  } catch (error) {
    console.error('跳转到注册页面失败:', error)
    ElMessage.error('跳转失败，请稍后重试')
  }
}

// 在组件挂载时检查是否有记住的用户信息（仅用于普通用户）
const initRememberedInfo = () => {
  if (userRole.value !== 'user') return

  const rememberedUsername = localStorage.getItem('rememberedUsername')
  const rememberedPassword = localStorage.getItem('rememberedPassword')
  
  if (rememberedUsername) {
    userFormData.username = rememberedUsername
    userFormData.remember = true
  }
  
  if (rememberedPassword) {
    try {
      userFormData.passwordHash = atob(rememberedPassword)
      userFormData.rememberPassword = true
    } catch (e) {
      console.error('密码解析失败:', e)
      localStorage.removeItem('rememberedPassword')
    }
  }
}

// 重置用户登录信息
const resetUserForm = () => {
  if (userRole.value !== 'user') return

  // 清空用户名和密码
  userFormData.username = ''
  userFormData.passwordHash = ''
  
  ElMessage.success('登录信息已清空')
}

// 监听记住选项的变化（仅用于普通用户）
const handleRememberChange = () => {
  if (userRole.value !== 'user') return

  // 如果取消记住用户名，立即移除存储的用户名
  if (!userFormData.remember) {
    localStorage.removeItem('rememberedUsername')
  }
  // 如果取消记住密码，立即移除存储的密码
  if (!userFormData.rememberPassword) {
    localStorage.removeItem('rememberedPassword')
  }
}

// 移除所有光标相关的代码和事件监听
const {
  cursorDot,
  cursorOutline,
  cursorStar,
  handleMouseMove,
  handleMouseDown,
  handleMouseUp
} = (() => {
  return {}
})()

// 添加随机打乱数组的函数
const shuffleArray = (array) => {
  for (let i = array.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [array[i], array[j]] = [array[j], array[i]];
  }
  return array;
}

// 修改图片加载逻辑
const filmImages = ref([])

onMounted(async () => {
  try {
    // 使用 Vite 的 import.meta.glob 动态导入所有图片
    const imageFiles = import.meta.glob('/src/assets/images/film/*.(jpg|jpeg|png|gif|webp)', {
      eager: true,
      import: 'default'
    })

    // 处理每个图片文件
    const loadedImages = []
    for (const [path, module] of Object.entries(imageFiles)) {
      try {
        // 从路径中提取文件名
        const fileName = path.split('/').pop()
        console.log('Loading image:', fileName)
        loadedImages.push({
          src: module,
          alt: `健身图片 - ${fileName}`,
          fileName
        })
      } catch (err) {
        console.warn(`加载图片失败: ${path}`, err)
      }
    }

    // 确保至少有3张图片
    while (loadedImages.length < 3) {
      const defaultImage = {
        src: 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="260" height="200" viewBox="0 0 260 200"%3E%3Crect width="260" height="200" fill="%23333"/%3E%3Ctext x="130" y="100" fill="%23fff" font-family="Arial" font-size="16" text-anchor="middle" dominant-baseline="middle"%3E示例图片 ${loadedImages.length + 1}%3C/text%3E%3C/svg%3E',
        alt: '示例图片',
        fileName: `default-${loadedImages.length + 1}.svg`
      }
      loadedImages.push(defaultImage)
    }
    
    // 随机打乱图片顺序
    filmImages.value = shuffleArray([...loadedImages])
    console.log('成功加载并打乱图片:', filmImages.value.length, '张')
  } catch (err) {
    console.error('加载图片文件夹失败:', err)
    // 发生错误时使用示例图片
    filmImages.value = Array(3).fill().map((_, index) => ({
      src: `data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="260" height="200" viewBox="0 0 260 200"%3E%3Crect width="260" height="200" fill="%23333"/%3E%3Ctext x="130" y="100" fill="%23fff" font-family="Arial" font-size="16" text-anchor="middle" dominant-baseline="middle"%3E示例图片 ${index + 1}%3C/text%3E%3C/svg%3E`,
      alt: `示例图片 ${index + 1}`,
      fileName: `error-${index + 1}.svg`
    }))
  }
})

// 仅在初始为用户模式时加载保存的信息
if (userRole.value === 'user') {
  initRememberedInfo()
}
</script>

<template>
  <div class="app-container auth-page">
    <div class="auth-page-container">
      <!-- 动画效果元素 -->
      <div class="hologram-rings"></div>
      <div class="light-beams">
        <div class="light-beam"></div>
        <div class="light-beam"></div>
        <div class="light-beam"></div>
        <div class="light-beam"></div>
      </div>
      <div class="cyber-rain"></div>

      <!-- 原有的登录内容 -->
      <div class="login-container">
        <!-- 霓虹灯效果 -->
        <div class="rgb-effects">
          <!-- 霓虹标语组 -->
          <div class="neon-signs">
            <!-- 主标语 - 左上角 -->
            <div class="neon-sign neon-sign-main">Health 👀 Body</div>
            <!-- 第二标语 - 右上角 -->
            <div class="neon-sign neon-sign-right">POWER UP!🦾</div>
            <!-- 第三标语 - 左下角 -->
            <div class="neon-sign neon-sign-left">✨BE STRONG🎯</div>
          </div>
          
          <!-- 全息投影环 -->
          <div class="hologram-rings"></div>
          
          <!-- 动态光束群 -->
          <div class="light-beams">
            <div class="light-beam"></div>
            <div class="light-beam"></div>
            <div class="light-beam"></div>
            <div class="light-beam"></div>
          </div>
          
          <!-- 数字雨 -->
          <div class="cyber-rain"></div>

          <!-- 胶片动画 -->
          <div class="film-strip">
            <div class="film-strip-container">
              <!-- 第一组图片 -->
              <div class="film-group">
                <div v-for="(image, index) in filmImages" :key="`first-${index}`" class="film-image">
                  <div class="film-image-content">
                    <img :src="image.src" :alt="`film-${index}`" loading="lazy" />
                  </div>
                  <div class="film-holes"></div>
                </div>
              </div>
              <!-- 第二组图片(完全相同) -->
              <div class="film-group">
                <div v-for="(image, index) in filmImages" :key="`second-${index}`" class="film-image">
                  <div class="film-image-content">
                    <img :src="image.src" :alt="`film-${index}`" loading="lazy" />
                  </div>
                  <div class="film-holes"></div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 登录卡片 -->
        <el-card class="login-card">
          <template #header>
            <div class="login-header">
              <h2 class="login-title" data-text="欢迎回来">欢迎回来</h2>
              <p class="login-subtitle">登录您的日常运动健康管理系统账号</p>
            </div>
          </template>

          <el-form
            ref="formRef"
            :model="formData"
            :rules="rules"
            label-position="top"
            class="login-form"
            :disabled="loading"
          >
            <!-- 身份选择 -->
            <div class="role-selector">
              <el-radio-group v-model="userRole" size="large">
                <el-radio-button label="user">
                  <el-icon><User /></el-icon>
                  普通用户
                </el-radio-button>
                <el-radio-button label="admin">
                  <el-icon><UserFilled /></el-icon>
                  管理员
                </el-radio-button>
              </el-radio-group>
            </div>

            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="formData.username"
                :placeholder="userRole === 'admin' ? '请输入管理员用户名' : '请输入您的用户名'"
                :prefix-icon="User"
              />
            </el-form-item>

            <el-form-item label="密码" prop="passwordHash">
              <el-input
                v-model="formData.passwordHash"
                type="password"
                :placeholder="userRole === 'admin' ? '请输入管理员密码' : '请输入您的密码'"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <!-- 普通用户的记住选项 -->
            <template v-if="userRole === 'user'">
              <div class="remember-options">
                <el-checkbox v-model="userFormData.remember" @change="handleRememberChange">
                  记住用户名
                </el-checkbox>
                <el-checkbox v-model="userFormData.rememberPassword" @change="handleRememberChange">
                  记住密码
                </el-checkbox>
              </div>
            </template>

            <div class="form-actions">
              <el-button type="primary" :loading="loading" @click="handleSubmit" class="submit-btn">
                登录
              </el-button>
              <template v-if="userRole === 'user'">
                <el-button type="text" @click="goToRegister" class="register-btn">
                  还没有账号？立即注册
                </el-button>
                <el-button type="text" @click="resetUserForm" class="reset-btn">
                  清空登录信息
                </el-button>
              </template>
            </div>
          </el-form>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import '@/styles/login.css';
@import '@/styles/film.css';

/* 额外的局部样式 */
.remember-options {
  margin-bottom: 20px;
  display: flex;
  gap: 20px;
}

.form-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: center;
}

.submit-btn {
  width: 100%;
  height: 40px;
  font-size: 16px;
}

.register-btn,
.reset-btn {
  color: rgba(255, 255, 255, 0.8) !important;
}

.register-btn:hover,
.reset-btn:hover {
  color: #fff !important;
  text-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
}
</style>
