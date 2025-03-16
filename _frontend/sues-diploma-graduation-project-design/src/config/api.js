// API基础URL配置
export const getApiBaseUrl = () => {
  // 根据环境返回不同的基础URL
  if (import.meta.env.MODE === 'development') {
    return 'http://localhost:21167' // 开发环境
  } else if (import.meta.env.MODE === 'production') {
    return 'http://localhost:21167' // 生产环境
  } else {
    return 'http://localhost:21167' // 默认环境
  }
}

// API响应码
export const API_CODE = {
  SUCCESS: 200,
  ERROR: 500,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404
}

// API错误消息
export const API_MESSAGE = {
  NETWORK_ERROR: '网络连接异常，请检查网络后重试',
  SERVER_ERROR: '服务器异常，请稍后重试',
  UNAUTHORIZED: '请先登录',
  FORBIDDEN: '没有权限访问',
  NOT_FOUND: '请求的资源不存在'
}

// 请求超时时间（毫秒）
export const REQUEST_TIMEOUT = 10000

// 请求重试次数
export const REQUEST_RETRY_TIMES = 3

// 请求重试延迟（毫秒）
export const REQUEST_RETRY_DELAY = 1000 