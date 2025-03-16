import axios from 'axios'
import {ElMessage} from 'element-plus'

// 创建 axios 实例
const http = axios.create({
  // baseURL: '/api',
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 5000
})

// 请求拦截器
http.interceptors.request.use(
  config => {
    console.log('Request:', config)
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
http.interceptors.response.use(
  response => {
    console.log('Response:', response.data)
    // 返回数据中可能包含业务错误信息，但这里只处理HTTP成功情况
    return response.data
  },
  error => {
    console.log('Response Error:', error.response?.data || error)
    
    // 如果有响应数据且包含业务错误码，返回业务错误信息而不是抛出异常
    // 这样业务错误可以在组件内处理
    if (error.response?.data && (typeof error.response.data === 'object')) {
      return error.response.data
    }
    
    // 处理网络错误等情况
    // 404 错误不在这里处理，让组件自己处理
    if (error.response?.status !== 404) {
      ElMessage({
        type: 'error',
        message: '⚠️ 请求失败\n💡 请稍后重试'
      })
    }
    return Promise.reject(error)
  }
)

export default http
