import request from '@/utils/request'
import {ApiPath} from '@/config/api.config'

/**
 * 用户登录
 * @param {Object} data
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise<Object>} 返回登录结果
 */
export function login(data) {
  return request({
    url: ApiPath.USER.LOGIN,
    method: 'post',
    data: {
      username: data.username,
      passwordHash: data.password // 在API调用时将password转换为passwordHash
    }
  })
}

/**
 * 获取用户完整信息
 * @param {number} userId - 用户ID
 * @returns {Promise<Object>} 返回用户信息
 */
export function getUserFullInfo(userId) {
  return request({
    url: ApiPath.USER.GET_INFO,
    method: 'get',
    params: { userId }
  })
}

// 为了保持兼容性，添加getUserInfo作为别名
export const getUserInfo = getUserFullInfo

/**
 * 更新用户信息
 * @param {Object} data - 用户信息
 * @param {number} data.userId - 用户ID
 * @param {string} [data.fullName] - 用户的全名
 * @param {string} [data.gender] - 用户性别：男、女
 * @param {string} [data.birthDate] - 用户出生日期
 * @param {number} [data.height] - 用户身高（单位：cm）
 * @param {number} [data.weight] - 用户体重（单位：kg）
 * @param {string} [data.email] - 用户邮箱地址
 * @param {string} [data.phoneNumber] - 用户电话号码
 * @returns {Promise<Object>} 返回更新结果
 */
export function updateUserInfo(data) {
  return request({
    url: ApiPath.USER.UPDATE_INFO,
    method: 'put',
    data
  })
}

/**
 * 修改密码
 * @param {Object} data - 密码信息
 * @returns {Promise<Object>} 返回修改结果
 */
export function changePassword(data) {
  return request({
    url: ApiPath.USER.CHANGE_PASSWORD,
    method: 'put',
    data
  })
}

/**
 * 用户注册
 * @param {Object} data - 注册信息
 * @param {string} data.username - 用户名
 * @param {string} data.passwordHash - 密码
 * @param {string} data.fullName - 姓名
 * @param {string} data.gender - 性别
 * @param {string} data.birthDate - 出生日期
 * @param {number} data.height - 身高
 * @param {number} data.weight - 体重
 * @param {string} data.email - 邮箱
 * @param {string} data.phoneNumber - 手机号
 * @returns {Promise<Object>} 返回注册结果
 */
export function register(data) {
  return request({
    url: ApiPath.USER.REGISTER,
    method: 'post',
    data: {
      ...data,
      passwordHash: data.passwordHash // 确保密码字段名称正确
    }
  })
} 