import axios from 'axios'
import {REQUEST_CONFIG} from '@/config/request.config'
import {setupInterceptors} from './interceptors'
import {handleRequestError} from './error'

// 创建 axios 实例
const instance = axios.create({
  baseURL: '/api',
  timeout: REQUEST_CONFIG.timeout,
  headers: REQUEST_CONFIG.headers
})

// 设置拦截器
setupInterceptors(instance)

// 封装请求方法
const http = {
  get: (url, params = {}, config = {}) => {
    return instance.get(url, { params, ...config }).catch(handleRequestError)
  },

  post: (url, data = {}, config = {}) => {
    return instance.post(url, data, config).catch(handleRequestError)
  },

  put: (url, data = {}, config = {}) => {
    return instance.put(url, data, config).catch(handleRequestError)
  },

  delete: (url, config = {}) => {
    return instance.delete(url, config).catch(handleRequestError)
  }
}

export default http
