import request from '@/utils/request'

/**
 * 创建健康目标
 * @param {Object} data
 * @param {number} data.userId - 用户ID
 * @param {string} data.targetPlan - 目标计划
 * @param {string} data.targetDate - 目标达成日期
 * @param {number[]} data.exerciseTypeIdList - 关联的运动类型ID列表
 */
export function createHealthGoal(data) {
  return request({
    url: '/healthGoals/create',
    method: 'post',
    data
  })
}

/**
 * 批量创建健康目标
 * @param {Array<Object>} data - 创建健康目标的请求参数数组
 * @param {number} data[].userId - 用户ID
 * @param {string} data[].targetPlan - 目标计划
 * @param {string} data[].targetDate - 目标达成日期
 * @param {number[]} data[].exerciseTypeIdList - 关联的运动类型ID列表
 * @returns {Promise<{code: number, message: string, data: any}>}
 */
export function createHealthGoalInBatch(data) {
  return request({
    url: '/healthGoals',
    method: 'post',
    data
  })
}

/**
 * 分页获取健康目标列表
 * @param {Object} data - 查询参数
 * @param {number} data.userId - 用户ID
 * @param {number} data.offset - 偏移量(页码-1)
 * @param {number} data.size - 每页记录数
 * @param {string} [data.targetDate] - 目标达成日期（可选）
 * @returns {Promise<{
 *   code: number,
 *   message: string,
 *   data: Array<{
 *     goalId: number,
 *     userId: number,
 *     targetDate: string,
 *     targetPlan: string,
 *     isFinished: number,
 *     exerciseTypesList: Array<{
 *       exerciseTypeId: number,
 *       exerciseName: string
 *     }>
 *   }>
 * }>}
 */
export function getHealthGoalList(data) {
  return request({
    url: '/healthGoals/getGoals',
    method: 'post',
    data
  })
}

/**
 * 根据时间段来分页批量获取健康目标详情
 * @param {Object} data - 查询参数
 * @param {number} data.userId - 用户ID
 * @param {number} [data.current=1] - 当前页码，从1开始，不能超过1000
 * @param {number} [data.size=15] - 每页展示的数据量，不能超过50
 * @param {boolean} [data.isAsc=true] - 是否升序排序
 * @param {string} [data.startDateTime] - 开始日期时间，格式：2023-10-05T14:30:00.123+08:00
 * @param {string} [data.endDateTime] - 结束日期时间，格式：2023-10-05T14:30:00.123+08:00
 * @returns {Promise<{
 *   code: number,
 *   message: string,
 *   data: {
 *     current: number,
 *     size: number,
 *     pages: number,
 *     total: number,
 *     hasPrevious: boolean,
 *     hasNext: boolean,
 *     dataList: Array<{
 *       goalId: number,
 *       userId: number,
 *       targetDate: string,
 *       targetPlan: string,
 *       isFinished: string,
 *       isAbandoned: string,
 *       exerciseTypesList: Array<{
 *         exerciseTypeId: number,
 *         exerciseName: string
 *       }>
 *     }>
 *   }
 * }>}
 */
export function listPaginatedHealthGoalsByTimeRange(data) {
  return request({
    url: '/healthGoals/listByPage',
    method: 'post',
    data
  })
}

/**
 * 更新健康目标
 * @param {Object} data - 更新数据
 * @param {number} data.goalId - 健康目标计划ID
 * @param {string} [data.targetPlan] - 目标计划
 * @param {string} [data.targetDate] - 目标达成日期
 * @param {number} [data.isFinished] - 是否完成计划（0：未完成，1：完成）
 * @param {number[]} [data.exerciseTypeIdList] - 关联的运动类型ID列表
 * @returns {Promise<{code: number, message: string, data: any}>}
 */
export function updateHealthGoal(data) {
  return request({
    url: '/healthGoals/update',
    method: 'put',
    data
  })
}

/**
 * 批量修改健康目标
 * @param {Array<Object>} data - 修改健康目标的请求参数数组
 * @param {number} data[].goalId - 健康目标计划ID
 * @param {string} data[].targetPlan - 目标计划
 * @param {string} data[].targetDate - 目标达成日期
 * @param {string} data[].isFinished - 是否完成计划（0：未完成，1：完成）
 * @param {string} data[].isAbandoned - 是否放弃了计划（0：否，1：是）
 * @param {number[]} [data[].exerciseTypeIdList] - 关联的运动类型ID列表
 * @returns {Promise<{code: number, message: string, data: any}>}
 */
export function updateHealthGoalInBatch(data) {
  return request({
    url: '/healthGoals',
    method: 'put',
    data
  })
}

/**
 * 批量删除健康目标
 * @param {number[]} goalIds - 要删除的健康目标ID数组
 * @returns {Promise<{code: number, message: string, data: any}>}
 */
export function deleteHealthGoals(goalIds) {
  return request({
    url: '/healthGoals',
    method: 'delete',
    data: { goalIdList: goalIds }
  })
} 