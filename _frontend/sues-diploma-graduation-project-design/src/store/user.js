import {defineStore} from 'pinia'
import {computed, ref} from 'vue'

export const useUserStore = defineStore('user', () => {
  // 从 localStorage 初始化状态
  const userId = ref(localStorage.getItem('userId') || null)
  const userRole = ref(localStorage.getItem('userRole') || null)
  const userProfile = ref(JSON.parse(localStorage.getItem('userProfile')) || null)
  const healthProfile = ref(JSON.parse(localStorage.getItem('healthProfile')) || null)

  // 计算属性：是否已认证
  const isAuthenticated = computed(() => !!userId.value)
  // 计算属性：是否是管理员
  const isAdmin = computed(() => userRole.value === 'admin')

  // 设置用户信息
  const setUserInfo = async (info) => {
    return new Promise((resolve) => {
      userId.value = info.userId
      userRole.value = info.role
      // 保存到 localStorage
      localStorage.setItem('userId', info.userId)
      localStorage.setItem('userRole', info.role)
      // 确保状态更新完成
      setTimeout(() => {
        resolve()
      }, 0)
    })
  }

  // 设置用户档案
  const setUserProfile = (profile) => {
    userProfile.value = profile
    // 保存到 localStorage
    localStorage.setItem('userProfile', JSON.stringify(profile))
  }

  // 设置健康档案
  const setHealthProfile = (profile) => {
    healthProfile.value = profile
    // 保存到 localStorage
    localStorage.setItem('healthProfile', JSON.stringify(profile))
  }

  // 清除用户信息
  const clearUserInfo = () => {
    userId.value = null
    userRole.value = null
    userProfile.value = null
    healthProfile.value = null
    // 清除 localStorage
    localStorage.removeItem('userId')
    localStorage.removeItem('userRole')
    localStorage.removeItem('userProfile')
    localStorage.removeItem('healthProfile')
  }

  return {
    userId,
    userRole,
    userProfile,
    healthProfile,
    isAuthenticated,
    isAdmin,
    setUserInfo,
    setUserProfile,
    setHealthProfile,
    clearUserInfo
  }
}) 