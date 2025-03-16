import {createRouter, createWebHistory} from 'vue-router'
import {useUserStore} from '@/store/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/Register.vue'),
      meta: { title: '注册' }
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: () => import('@/views/DashboardWithDecorator.vue'),
      meta: { 
        title: '仪表板',
        requiresAuth: true
      }
    },
    // 健康档案相关路由
    {
      path: '/health',
      component: () => import('@/views/health/Layout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: 'create',
          name: 'CreateHealthProfile',
          component: () => import('@/views/health/CreateProfile.vue'),
          meta: { title: '创建健康档案' }
        },
        {
          path: 'edit',
          name: 'EditHealthProfile',
          component: () => import('@/views/health/EditProfile.vue'),
          meta: { title: '编辑健康档案' }
        }
      ]
    },
    // 用户相关路由
    {
      path: '/user',
      component: () => import('@/views/user/Layout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: 'profile',
          name: 'UserProfile',
          component: () => import('@/views/user/Profile.vue'),
          meta: { title: '个人资料' }
        },
        {
          path: 'change-password',
          name: 'ChangePassword',
          component: () => import('@/views/user/ChangePassword.vue'),
          meta: { title: '修改密码' }
        }
      ]
    },
    // 管理员路由
    {
      path: '/admin',
      component: () => import('@/views/admin/Layout.vue'),
      meta: { 
        requiresAuth: true,
        requiresAdmin: true
      },
      redirect: '/admin/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'AdminDashboard',
          component: () => import('@/views/admin/Dashboard.vue'),
          meta: { title: '用户管理' }
        },
        {
          path: 'exercise-type',
          name: 'AdminExerciseType',
          component: () => import('@/views/admin/ExerciseType.vue'),
          meta: { title: '运动类型管理' }
        },
        {
          path: 'food-nutrition',
          name: 'AdminFoodNutrition',
          component: () => import('@/views/admin/FoodNutrition.vue'),
          meta: { title: '食物营养管理' }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 日常运动健康管理系统`
  }

  const userStore = useUserStore()

  // 需要认证的页面
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!userStore.isAuthenticated) {
      // 显示友好的提示信息
      ElMessage({
        type: 'warning',
        dangerouslyUseHTMLString: true,
        message: `
          <div class="message-container warning-message">
            <div class="message-icon">
              <span class="icon">🔒</span>
              <div class="icon-background"></div>
            </div>
            
            <div class="message-content">
              <div class="message-header">
                <h3>需要登录</h3>
                <p>访问此页面需要先登录您的账号</p>
              </div>
              
              <div class="message-body">
                <div class="info-item">
                  <span class="info-icon">💡</span>
                  <span>即将为您跳转到登录页面...</span>
                </div>
              </div>
            </div>
          </div>
        `,
        duration: 3000,
        showClose: true,
        customClass: 'global-message warning'
      })

      // 延迟跳转，让用户看到提示信息
      setTimeout(() => {
        next({
          path: '/login',
          query: { redirect: to.fullPath }
        })
      }, 2000)
      return
    }

    // 需要管理员权限的页面
    if (to.matched.some(record => record.meta.requiresAdmin)) {
      if (!userStore.isAdmin) {
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
                  <h3>访问受限</h3>
                  <p>此页面仅管理员可访问</p>
                </div>
                
                <div class="message-body">
                  <div class="info-item">
                    <span class="info-icon">💡</span>
                    <span>即将返回用户仪表板...</span>
                  </div>
                </div>
              </div>
            </div>
          `,
          duration: 3000,
          showClose: true,
          customClass: 'global-message error'
        })

        setTimeout(() => {
          next({ path: '/dashboard' })
        }, 2000)
        return
      }
    }
  }

  next()
})

export default router
