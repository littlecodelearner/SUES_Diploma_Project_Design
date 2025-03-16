import http from '@/utils/request'
import { ApiPath } from '@/config/api.config'
import request from '@/utils/request'

/**
 * 获取用户健康档案
 * @param {number} userId - 用户ID
 * @returns {Promise<{
 *   code: number,
 *   message: string,
 *   data: {
 *     userId: number,
 *     medicalHistory: string,
 *     allergyHistory: string,
 *     exerciseHabits: string,
 *     healthGoals: string
 *   }
 * }>} 返回健康档案信息
 */
export const getHealthProfile = (userId) => {
  return http.get(`${ApiPath.HEALTH.PROFILE}/${userId}`)
}

/**
 * 创建健康档案
 * @param {Object} data - 健康档案数据
 * @param {number} data.userId - 用户ID
 * @param {string} [data.medicalHistory] - 疾病史
 * @param {string} [data.allergyHistory] - 过敏史
 * @param {string} [data.exerciseHabits] - 运动习惯
 * @param {string} [data.healthGoals] - 健康目标
 * @returns {Promise<{code: number, message: string, data: Object}>}
 */
export const createHealthProfile = (data) => {
  return http.post(ApiPath.HEALTH.CREATE, data)
}

/**
 * 更新健康档案
 * @param {Object} data - 健康档案数据
 * @param {number} data.userId - 用户ID
 * @param {string} [data.medicalHistory] - 疾病史
 * @param {string} [data.allergyHistory] - 过敏史
 * @param {string} [data.exerciseHabits] - 运动习惯
 * @param {string} [data.healthGoals] - 健康目标
 * @returns {Promise<{code: number, message: string, data: Object}>}
 */
export const updateHealthProfile = (data) => {
  return http.put(ApiPath.HEALTH.UPDATE, data)
}

/**
 * 删除健康档案
 * @param {number} userId - 用户ID
 * @returns {Promise<{code: number, message: string, data: Object}>}
 */
export const deleteHealthProfile = (userId) => {
  return http.delete(`${ApiPath.HEALTH.DELETE}/${userId}`)
}

/**
 * 获取用户所有健康数据
 * @param {string} userId 用户ID
 * @returns {Promise} 返回健康数据列表
 */
export function getAllHealthData(userId) {
  return request({
    url: '/healthData',
    method: 'get',
    params: {
      userId
    }
  })
} 