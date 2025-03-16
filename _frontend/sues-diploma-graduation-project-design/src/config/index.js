// API 配置
export const API_CONFIG = {
  // 基础 URL 从环境变量中获取
  baseURL: import.meta.env.VITE_API_BASE_URL,
  // 超时时间
  timeout: 15000
  // 其他 API 相关配置...
}

// 环境判断
export const ENV = {
  isDev: import.meta.env.DEV,
  isProd: import.meta.env.PROD,
  mode: import.meta.env.MODE
}

// 可以添加其他配置...
