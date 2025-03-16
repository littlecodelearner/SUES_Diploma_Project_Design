<script setup>
import {onMounted, provide, ref, watch} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import {Expand, Fold, Food, Moon, Sunny, Trophy, User} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 侧边栏折叠状态
const isCollapse = ref(false)

// 当前激活的菜单项
const activeMenu = ref(route.path)

// 监听路由变化
watch(() => route.path, (newPath) => {
  console.log('路由变化:', newPath)
  activeMenu.value = newPath
})

// 处理菜单点击
const handleMenuSelect = async (index) => {
  console.log('菜单点击:', index)
  try {
    if (route.path !== index) {
      await router.push(index)
    }
  } catch (error) {
    console.error('路由导航错误:', error)
    ElMessage.error('页面跳转失败，请稍后重试')
  }
}

// 处理退出登录
const handleLogout = async () => {
  try {
    localStorage.removeItem('userId')
    localStorage.removeItem('userRole')
    await router.push('/login')
    ElMessage.success('已退出登录')
  } catch (error) {
    console.error('退出登录错误:', error)
    ElMessage.error('退出登录失败，请稍后重试')
  }
}

// 主题配置
const isDarkMode = ref(false)

// 提供isDarkMode状态给子组件
provide('isDarkMode', isDarkMode)

// 切换主题
const toggleTheme = () => {
  isDarkMode.value = !isDarkMode.value
  document.documentElement.classList.toggle('dark-mode')
  console.log('当前主题模式:', isDarkMode.value ? '深色' : '浅色')

}

// 页面加载动画状态
const pageLoading = ref(true)

// 初始化动画
onMounted(() => {
  setTimeout(() => {
    pageLoading.value = false
  }, 300)
})
</script>

<template>
  <el-container class="admin-layout" :class="{ 'dark': isDarkMode, 'loading': pageLoading }">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '200px'" class="aside">
      <div class="logo" :class="{ 'collapsed': isCollapse }">
        <div class="logo-content">
          <el-icon class="logo-icon"><component :is="User" /></el-icon>
          <h1 v-show="!isCollapse">管理员操作系统</h1>
        </div>
      </div>
      
      <el-menu
        :collapse="isCollapse"
        :router="false"
        :default-active="activeMenu"
        :class="{ 'collapsed': isCollapse }"
        @select="handleMenuSelect"
      >
        <el-menu-item index="/admin/dashboard" class="menu-item">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/exercise-type" class="menu-item">
          <el-icon><Trophy /></el-icon>
          <template #title>运动类型管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/food-nutrition" class="menu-item">
          <el-icon><Food /></el-icon>
          <template #title>食物营养管理</template>
        </el-menu-item>
      </el-menu>

      <div class="collapse-trigger" @click="isCollapse = !isCollapse">
        <el-icon><component :is="isCollapse ? Expand : Fold" /></el-icon>
      </div>
    </el-aside>

    <!-- 主要内容区 -->
    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ route.meta.title || '管理系统' }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-button-group>
            <el-tooltip content="切换主题" placement="bottom">
              <el-button :class="{ 'cyberpunk-btn': isDarkMode }" :icon="isDarkMode ? Sunny : Moon" @click="toggleTheme" />
            </el-tooltip>
            <el-button type="danger" @click="handleLogout">退出登录</el-button>
          </el-button-group>
        </div>
      </el-header>

      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <keep-alive>
              <component :is="Component" />
            </keep-alive>
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.admin-layout {
  --primary-color: #2B5BA1;
  --secondary-color: #38B2AC;
  --accent-color: #F6AD55;
  --bg-color: #F0F2F5;
  --card-bg: #FFFFFF;
  --text-color: #1F2937;
  --border-color: #E5E7EB;
  --shadow-color: rgba(0, 0, 0, 0.1);
  --menu-bg: #304156;
  --menu-text: #BFCBD9;
  --menu-active: #409EFF;
  --menu-hover: #263445;
  --animation-duration: 0.3s;
  --animation-timing: cubic-bezier(0.4, 0, 0.2, 1);
  --cursor-size: 24px;
  --cursor-glow: 0 0 10px;
  --cursor-stroke: 2.5px;
  --cursor-pink: #FDA4AF;
  --cursor-pink-glow: rgba(253, 164, 175, 0.6);

  height: 100vh;
  overflow: hidden;
  transition: all var(--animation-duration) var(--animation-timing);
  cursor: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="%23FDA4AF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21.5 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3m-3.5 3.5L19 4"></path><defs><filter id="glow"><feGaussianBlur stdDeviation="2" result="coloredBlur"/><feMerge><feMergeNode in="coloredBlur"/><feMergeNode in="SourceGraphic"/></feMerge></filter></defs></svg>') 0 0, auto;
}

/* 针对可点击元素的光标样式 */
.admin-layout button,
.admin-layout a,
.admin-layout [role="button"],
.admin-layout .el-menu-item,
.admin-layout .collapse-trigger {
  cursor: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="%23FDA4AF" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M21.5 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3m-3.5 3.5L19 4"></path><defs><filter id="glow"><feGaussianBlur stdDeviation="3" result="coloredBlur"/><feMerge><feMergeNode in="coloredBlur"/><feMergeNode in="SourceGraphic"/></feMerge></filter></defs></svg>') 4 4, pointer;
  transition: all var(--animation-duration) var(--animation-timing);
}

/* 悬浮状态的光标样式 */
.admin-layout button:hover,
.admin-layout a:hover,
.admin-layout [role="button"]:hover,
.admin-layout .el-menu-item:hover,
.admin-layout .collapse-trigger:hover {
  cursor: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="%23F6AD55" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" filter="drop-shadow(0 0 5px %23F6AD55)"><path d="M21.5 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3m-3.5 3.5L19 4" style="filter: drop-shadow(0 0 6px %23F6AD55)"></path></svg>') 6 6, pointer;
}

/* 激活状态的光标样式 */
.admin-layout button:active,
.admin-layout a:active,
.admin-layout [role="button"]:active,
.admin-layout .el-menu-item.is-active,
.admin-layout .collapse-trigger:active {
  cursor: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" viewBox="0 0 24 24" fill="none" stroke="%2338B2AC" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="round" filter="drop-shadow(0 0 6px %2338B2AC)"><path d="M21.5 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3m-3.5 3.5L19 4" style="filter: drop-shadow(0 0 8px %2338B2AC)"></path></svg>') 5 5, pointer;
}

.admin-layout.dark {
  /* 全局变量更新：采用更明亮的颜色 */
  --primary-color: #00ffff;      /* 更亮的青色 */
  --secondary-color: #ff00ff;    /* 更亮的粉色 */
  --accent-color: #ffff00;       /* 明亮的黄色 */
  --bg-color: #090b13;          /* 保持深色背景 */
  --card-bg: rgba(10, 10, 20, 0.9);
  --text-color: #ffffff;         /* 更亮的文字颜色 */
  --border-color: rgba(255,255,255,0.2);
  --menu-bg: rgba(9, 11, 19, 0.8);
  --menu-text: #ffffff;          /* 更亮的菜单文字 */
  --menu-active: #00ffff;
  --menu-hover: rgba(0, 255, 255, 0.4);
  background: linear-gradient(135deg, #0d0e15, #090b13);
  position: relative;
  overflow: hidden;
}

/* 添加缓慢流动的霓虹光线效果 */
.admin-layout.dark::before {
  content: "";
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(0, 255, 255, 0.1), transparent 40%),
              radial-gradient(circle at 70% 30%, rgba(255, 20, 147, 0.08), transparent 35%);
  animation: neonFlow 20s linear infinite;
  z-index: -2;
}

@keyframes neonFlow {
  0% { transform: translate(0, 0) rotate(0deg); }
  50% { transform: translate(10%, 10%) rotate(180deg); }
  100% { transform: translate(0, 0) rotate(360deg); }
}

/* Header: 使用蓝紫渐变背景和霓虹发光效果 */
.admin-layout.dark .header {
  background: linear-gradient(135deg, #1a1a2e, #16213e);
  box-shadow: 0 0 30px rgba(0, 255, 255, 0.4), 0 0 40px rgba(138, 43, 226, 0.4);
  border-bottom: 1px solid #1f1f2e;
  position: relative;
  z-index: 1;
}

/* 侧边栏: 采用半透明玻璃效果和霓虹边框 */
.admin-layout.dark .aside {
  background: rgba(9, 11, 19, 0.8);
  backdrop-filter: blur(8px);
  border-right: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.9);
}

/* 主要内容区: 深色背景加上内嵌霓虹边缘效果 */
.admin-layout.dark .main-container {
  background: rgba(9, 11, 19, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: inset 0 0 20px rgba(0, 255, 255, 0.1);
  position: relative;
  z-index: 1;
}

/* 菜单项样式优化 */
.admin-layout.dark .el-menu {
  background: transparent;
  border: none;
}

.admin-layout.dark .el-menu-item {
  background: transparent !important;
  transition: all 0.3s ease;
  color: #ffffff !important;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
}

.admin-layout.dark .el-menu-item:hover {
  background: rgba(0, 255, 255, 0.2) !important;
  color: #00ffff !important;
  text-shadow: 0 0 10px #00ffff, 0 0 20px #00ffff;
}

.admin-layout.dark .el-menu-item.is-active {
  background: rgba(0, 255, 255, 0.15) !important;
  color: #00ffff !important;
  text-shadow: 0 0 10px #00ffff, 0 0 20px #00ffff, 0 0 30px #00ffff;
  border-right: 2px solid #00ffff;
  box-shadow: inset 0 0 15px rgba(0, 255, 255, 0.2);
}

/* 按钮样式优化 */
.admin-layout.dark .el-button {
  color: #ffffff !important;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
  background: rgba(0, 255, 255, 0.2);
  border: 1px solid rgba(0, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.admin-layout.dark .el-button:hover {
  color: #00ffff !important;
  text-shadow: 0 0 10px #00ffff, 0 0 20px #00ffff;
  background: rgba(0, 255, 255, 0.3);
  border-color: #00ffff;
  box-shadow: 0 0 15px rgba(0, 255, 255, 0.4);
}

/* 添加细腻的网格叠加效果，增强未来感 */
.admin-layout.dark::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    linear-gradient(0deg, transparent 24%, rgba(255, 255, 255, 0.02) 25%, rgba(255, 255, 255, 0.02) 26%, transparent 27%, transparent 74%, rgba(255, 255, 255, 0.02) 75%, rgba(255, 255, 255, 0.02) 76%, transparent 77%, transparent),
    linear-gradient(90deg, transparent 24%, rgba(255, 255, 255, 0.02) 25%, rgba(255, 255, 255, 0.02) 26%, transparent 27%, transparent 74%, rgba(255, 255, 255, 0.02) 75%, rgba(255, 255, 255, 0.02) 76%, transparent 77%, transparent);
  background-size: 50px 50px;
  pointer-events: none;
  z-index: -1;
  animation: gridMove 15s linear infinite;
}

@keyframes gridMove {
  0% { transform: translateY(0); }
  100% { transform: translateY(50px); }
}

/* Logo区域样式优化 */
.admin-layout.dark .logo {
  background: linear-gradient(135deg, rgba(138, 43, 226, 0.2), rgba(0, 255, 255, 0.1));
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.admin-layout.dark .logo h1 {
  color: #ffffff;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5), 0 0 20px rgba(0, 255, 255, 0.5);
  font-weight: bold;
}

/* 面包屑导航样式优化 - 超级霓虹灯管效果 */
.admin-layout.dark .el-breadcrumb__item {
  font-weight: 800;
  font-size: 16px;
  position: relative;
  mix-blend-mode: screen;
  filter: contrast(150%) brightness(150%);
}

.admin-layout.dark .el-breadcrumb__item .el-breadcrumb__inner {
  color: rgb(255, 255, 255) !important;
  background: linear-gradient(to bottom, rgb(255, 255, 255) 0%, rgb(255, 140, 0) 50%, rgb(255, 255, 255) 100%);
  -webkit-background-clip: text;
  background-clip: text;
  filter: brightness(2) contrast(2);
  text-shadow: 0 0 4px rgb(255, 255, 255),
               0 0 8px rgb(255, 140, 0),
               0 0 12px rgb(255, 140, 0),
               0 0 16px rgb(255, 140, 0),
               0 0 20px rgb(255, 140, 0),
               0 0 24px rgb(255, 140, 0),
               0 0 28px rgb(255, 140, 0),
               0 0 32px rgb(255, 140, 0);
  font-weight: 800;
  letter-spacing: 2px;
  transition: all 0.3s ease;
  animation: ultraNeonGlow 2s infinite;
}

.admin-layout.dark .el-breadcrumb__item:first-child .el-breadcrumb__inner {
  color: rgb(255, 255, 255) !important;
  background: linear-gradient(to bottom, rgb(255, 255, 255) 0%, rgb(255, 140, 0) 50%, rgb(255, 255, 255) 100%);
  -webkit-background-clip: text;
  background-clip: text;
  filter: brightness(2) contrast(2);
  text-shadow: 0 0 4px rgb(255, 255, 255),
               0 0 8px rgb(255, 140, 0),
               0 0 12px rgb(255, 140, 0),
               0 0 16px rgb(255, 140, 0),
               0 0 20px rgb(255, 140, 0),
               0 0 24px rgb(255, 140, 0),
               0 0 28px rgb(255, 140, 0),
               0 0 32px rgb(255, 140, 0);
  animation: ultraNeonGlow 2s infinite alternate;
}

.admin-layout.dark .el-breadcrumb__separator {
  color: rgb(255, 255, 255) !important;
  filter: brightness(2) contrast(2);
  text-shadow: 0 0 4px rgb(255, 255, 255),
               0 0 8px rgb(255, 140, 0),
               0 0 12px rgb(255, 140, 0),
               0 0 16px rgb(255, 140, 0);
  font-weight: 800;
  margin: 0 12px;
  opacity: 1;
  animation: ultraSeparatorGlow 1.5s infinite alternate;
  font-size: 20px;
}

@keyframes ultraNeonGlow {
  0% {
    filter: brightness(2) contrast(2);
    text-shadow: 0 0 4px rgb(255, 255, 255),
                 0 0 8px rgb(255, 140, 0),
                 0 0 12px rgb(255, 140, 0),
                 0 0 16px rgb(255, 140, 0),
                 0 0 20px rgb(255, 140, 0),
                 0 0 24px rgb(255, 140, 0);
  }
  50% {
    filter: brightness(3) contrast(3);
    text-shadow: 0 0 8px rgb(255, 255, 255),
                 0 0 12px rgb(255, 140, 0),
                 0 0 16px rgb(255, 140, 0),
                 0 0 20px rgb(255, 140, 0),
                 0 0 24px rgb(255, 140, 0),
                 0 0 28px rgb(255, 140, 0),
                 0 0 32px rgb(255, 140, 0),
                 0 0 36px rgb(255, 140, 0);
  }
  100% {
    filter: brightness(2) contrast(2);
    text-shadow: 0 0 4px rgb(255, 255, 255),
                 0 0 8px rgb(255, 140, 0),
                 0 0 12px rgb(255, 140, 0),
                 0 0 16px rgb(255, 140, 0),
                 0 0 20px rgb(255, 140, 0),
                 0 0 24px rgb(255, 140, 0);
  }
}

@keyframes ultraSeparatorGlow {
  from {
    filter: brightness(2) contrast(2);
    text-shadow: 0 0 4px rgb(255, 255, 255),
                 0 0 8px rgb(255, 140, 0),
                 0 0 12px rgb(255, 140, 0);
  }
  to {
    filter: brightness(3) contrast(3);
    text-shadow: 0 0 8px rgb(255, 255, 255),
                 0 0 12px rgb(255, 140, 0),
                 0 0 16px rgb(255, 140, 0),
                 0 0 20px rgb(255, 140, 0),
                 0 0 24px rgb(255, 140, 0);
  }
}

/* 悬停效果增强 */
.admin-layout.dark .el-breadcrumb__inner:hover {
  transform: scale(1.2);
  filter: brightness(3) contrast(3);
  text-shadow: 0 0 8px rgb(255, 255, 255),
               0 0 12px rgb(255, 140, 0),
               0 0 16px rgb(255, 140, 0),
               0 0 20px rgb(255, 140, 0),
               0 0 24px rgb(255, 140, 0),
               0 0 28px rgb(255, 140, 0),
               0 0 32px rgb(255, 140, 0),
               0 0 36px rgb(255, 140, 0);
}

/* 面包屑容器样式增强 */
.admin-layout.dark .header-left {
  padding: 12px 20px;
  border-radius: 12px;
  background: rgba(0, 0, 0, 0.9);
  box-shadow: 0 0 30px rgba(255, 140, 0, 0.6),
              inset 0 0 35px rgba(255, 140, 0, 0.3);
  border: 2px solid rgba(255, 140, 0, 0.5);
  mix-blend-mode: screen;
  animation: containerGlow 3s infinite alternate;
}

@keyframes containerGlow {
  from {
    box-shadow: 0 0 30px rgba(255, 140, 0, 0.6),
                inset 0 0 35px rgba(255, 140, 0, 0.3);
  }
  to {
    box-shadow: 0 0 40px rgba(255, 140, 0, 0.8),
                inset 0 0 45px rgba(255, 140, 0, 0.4);
  }
}

.aside {
  background-color: var(--menu-bg);
  transition: all var(--animation-duration) var(--animation-timing);
  display: flex;
  flex-direction: column;
  height: 100vh;
  box-shadow: 2px 0 8px var(--shadow-color);
  z-index: 10;
}

.logo {
  height: 60px;
  padding: 0 16px;
  transition: all var(--animation-duration) var(--animation-timing);
  background-color: var(--menu-hover);
}

.logo-content {
  height: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 24px;
  color: var(--menu-active);
}

.logo h1 {
  margin: 0;
  font-size: 18px;
  color: var(--menu-text);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

:deep(.el-menu) {
  border-right: none;
  flex: 1;
  background-color: var(--menu-bg);
}

:deep(.el-menu--collapse) {
  width: 64px;
}

.menu-item {
  transition: all var(--animation-duration) var(--animation-timing);
}

:deep(.el-menu-item) {
  color: var(--menu-text);
  
  &:hover {
    background-color: var(--menu-hover) !important;
  }
  
  &.is-active {
    background-color: var(--menu-hover) !important;
    color: var(--menu-active) !important;
  }
}

.collapse-trigger {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--menu-text);
  cursor: pointer;
  background-color: var(--menu-hover);
  transition: all var(--animation-duration) var(--animation-timing);
}

/* 深色模式下的折叠触发器样式 */
.admin-layout.dark .collapse-trigger {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ff1493;
  cursor: pointer;
  background: rgba(255, 20, 147, 0.15);
  transition: all 0.3s ease;
  border-top: 1px solid rgba(255, 20, 147, 0.4);
  position: relative;
  font-size: 20px;
  filter: brightness(1.5) contrast(1.5);
  text-shadow: 0 0 10px #ff1493,
               0 0 20px #ff1493,
               0 0 30px #ff1493;
  animation: ultraPinkPulse 2s infinite alternate;
}

.admin-layout.dark .collapse-trigger:hover {
  background: rgba(255, 20, 147, 0.25);
  color: #ff69b4;
  filter: brightness(2) contrast(2);
  text-shadow: 0 0 15px #ff1493,
               0 0 25px #ff1493,
               0 0 35px #ff1493,
               0 0 45px #ff1493;
  box-shadow: 0 0 30px rgba(255, 20, 147, 0.6),
              inset 0 0 20px rgba(255, 20, 147, 0.4);
  transform: scale(1.1);
}

.admin-layout.dark .collapse-trigger:active {
  transform: scale(0.95);
  background: rgba(255, 20, 147, 0.35);
}

@keyframes ultraPinkPulse {
  0% {
    filter: brightness(1.5) contrast(1.5);
    text-shadow: 0 0 10px #ff1493,
                 0 0 20px #ff1493,
                 0 0 30px #ff1493;
    box-shadow: 0 0 20px rgba(255, 20, 147, 0.4),
                inset 0 0 10px rgba(255, 20, 147, 0.2);
  }
  50% {
    filter: brightness(2) contrast(2);
    text-shadow: 0 0 15px #ff1493,
                 0 0 25px #ff1493,
                 0 0 35px #ff1493,
                 0 0 45px #ff1493;
    box-shadow: 0 0 30px rgba(255, 20, 147, 0.6),
                inset 0 0 20px rgba(255, 20, 147, 0.4);
  }
  100% {
    filter: brightness(2.5) contrast(2.5);
    text-shadow: 0 0 20px #ff1493,
                 0 0 30px #ff1493,
                 0 0 40px #ff1493,
                 0 0 50px #ff1493;
    box-shadow: 0 0 40px rgba(255, 20, 147, 0.8),
                inset 0 0 30px rgba(255, 20, 147, 0.6);
  }
}

.main-container {
  background-color: var(--bg-color);
  transition: all var(--animation-duration) var(--animation-timing);
}

.header {
  background-color: var(--card-bg);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
  box-shadow: 0 2px 4px var(--shadow-color);
  z-index: 5;
  transition: all var(--animation-duration) var(--animation-timing);
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.main {
  background-color: var(--bg-color);
  padding: 20px;
  height: calc(100vh - 60px);
  overflow-y: auto;
  transition: all var(--animation-duration) var(--animation-timing);
}

/* 夜间模式下的main区域 */
.admin-layout.dark .main {
  height: calc(100vh - 60px);
  max-height: calc(100vh - 60px);
  padding-bottom: 2px; /* 减少底部填充 */
  min-height: 0; /* 防止最小高度导致的空白 */
}

/* 页面切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all var(--animation-duration) var(--animation-timing);
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

/* 加载动画 */
.loading {
  .aside,
  .main-container {
    opacity: 0;
    transform: translateY(20px);
  }
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .admin-layout {
    --animation-duration: 0.2s;
  }

  .aside {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 1000;
    transform: translateX(0);
  }

  .aside.collapsed {
    transform: translateX(-100%);
  }

  .header {
    padding: 0 12px;
  }

  .header-left {
    display: none;
  }

  .main {
    padding: 12px;
  }
}

/* 滚动条样式 */
.main::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.main::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: 3px;
}

.main::-webkit-scrollbar-track {
  background: transparent;
}

.main::-webkit-scrollbar-corner {
  background: transparent;
}

/* 当处于深色模式且切换主题按钮应用 cyberpunk-btn 类时的样式 */
.admin-layout.dark .cyberpunk-btn {
  background: linear-gradient(45deg, #ff00ff, #00ffff);
  box-shadow: 0 0 10px #ff00ff, 0 0 20px #ff00ff, 0 0 30px #00ffff;
  border: none;
  transition: transform 0.3s, box-shadow 0.3s;
  color: #000;
}

.admin-layout.dark .cyberpunk-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 0 20px #ff00ff, 0 0 30px #ff00ff, 0 0 40px #00ffff;
}
</style> 