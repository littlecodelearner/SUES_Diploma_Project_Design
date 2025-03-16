import {ElMessage} from 'element-plus'
import router from '@/router'

/**
 * HTTP请求拦截器配置
 * 
 * 重要说明：
 * 本项目不使用token进行身份验证。
 * 这是一个简单的本地项目，仅用于演示和学习目的。
 * 所有的请求都是无状态的，不需要进行token验证。
 * 用户认证仅通过用户信息对象进行简单的角色判断。
 */
export function setupInterceptors(instance) {
  // 请求拦截器
  instance.interceptors.request.use(
    config => {
      // 开发环境下打印请求信息
      if (import.meta.env.DEV) {
        console.log('Request:', {
          url: config.url,
          method: config.method,
          data: config.data,
          params: config.params
        })
      }

      return config
    },
    error => {
      ElMessage.error('请求发送失败，请检查网络连接')
      return Promise.reject(error)
    }
  )

  // 响应拦截器
  instance.interceptors.response.use(
    response => {
      // 开发环境下打印响应信息
      if (import.meta.env.DEV) {
        console.log('Response:', response.data)
      }

      // 处理业务错误码
      const { code, message } = response.data
      if (code !== 200) {
        ElMessage.error(message || '操作失败')
        return Promise.reject(new Error(message || '操作失败'))
      }

      return response.data
    },
    error => {
      // 处理HTTP错误
      if (error.response) {
        const { status } = error.response
        
        switch (status) {
          case 400:
            ElMessage.error('请求参数错误')
            break
          case 403:
            ElMessage.error('没有权限访问该资源')
            router.push('/dashboard')
            break
          case 404:
            ElMessage.error('请求的资源不存在')
            break
          case 500:
            ElMessage.error('服务器内部错误')
            break
          default:
            ElMessage.error('网络异常，请稍后重试')
        }
      } else if (error.request) {
        ElMessage.error('网络连接失败，请检查网络')
      } else {
        ElMessage.error('请求发送失败')
      }
      
      return Promise.reject(error)
    }
  )

  return instance
}
