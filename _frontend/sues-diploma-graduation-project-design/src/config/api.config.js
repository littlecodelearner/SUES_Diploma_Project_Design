/**
 * API 配置文件
 * 集中管理所有的 API 地址配置
 * 方便未来迁移到公网环境
 */

// 开发环境 API 地址
export const DEV_API_URL = 'http://localhost:21167'

// 生产环境 API 地址
export const PROD_API_URL = 'http://124.223.208.42:21167'

/**
 * 获取当前环境的 API 基础地址
 * @returns {string} API 基础地址
 */
export const getApiBaseUrl = () => {
  // 优先使用环境变量中的配置
  if (import.meta.env.VITE_API_BASE_URL) {
    return import.meta.env.VITE_API_BASE_URL
  }
  
  // 如果环境变量中没有配置，则根据当前环境选择
  return import.meta.env.MODE === 'production' ? PROD_API_URL : DEV_API_URL
}

/**
 * API 路径配置
 * 集中管理所有的 API 路径
 * 方便统一管理和修改
 */
export const ApiPath = {
  // 用户相关接口
  USER: {
    LOGIN: '/users/login',
    REGISTER: '/users/register',
    PROFILE: '/users/profile',
    GET_INFO: '/users/getInfo',
    UPDATE_INFO: '/users/updateInfo',
    CHANGE_PASSWORD: '/users/changePwd'
  },
  
  // 健康档案相关接口
  HEALTH: {
    PROFILE: '/healthProfile/get',
    CREATE: '/healthProfile/create',
    UPDATE: '/healthProfile/update',
    DELETE: '/healthProfile/delete'
  }
}