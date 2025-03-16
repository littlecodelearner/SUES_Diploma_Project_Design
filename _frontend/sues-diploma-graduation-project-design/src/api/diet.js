import http from '@/utils/request'

/**
 * 批量添加饮食记录
 * @param {Array<Object>} records - 饮食记录数组
 * @returns {Promise<{code: number, message: string, data: Object}>}
 */
export const addDietRecordsInBulk = (records) => {
  return http.post('/dietRecords', records)
}

/**
 * 批量修改饮食记录
 * @param {Array<Object>} records - 饮食记录数组
 * @returns {Promise<{code: number, message: string, data: Object}>}
 */
export const updateDietRecordsInBulk = (records) => {
  return http.put('/dietRecords', records)
}

/**
 * 批量删除饮食记录
 * @param {Object} params - 删除参数
 * @param {Array<number>} params.dietIdList - 要删除的饮食记录ID列表
 * @returns {Promise<{code: number, message: string, data: Object}>}
 */
export const deleteDietRecordInBulk = (params) => {
  return http.delete('/dietRecords', { data: params })
}

/**
 * 分页获取饮食记录列表
 * @param {Object} params - 查询参数
 * @param {number} params.userId - 用户ID
 * @param {number} [params.current=1] - 当前页码
 * @param {number} [params.size=15] - 每页记录数
 * @param {boolean} [params.isAsc=true] - 是否升序排序
 * @param {string} [params.startDateTime] - 开始时间
 * @param {string} [params.endDateTime] - 结束时间
 * @returns {Promise<{
 *   code: number,
 *   message: string,
 *   data: {
 *     current: number,
 *     size: number,
 *     total: number,
 *     pages: number,
 *     hasPrevious: boolean,
 *     hasNext: boolean,
 *     dataList: Array<{
 *       dietId: number,
 *       foodsDetailsDTOList: Array<{
 *         foodId: number,
 *         quantity: number,
 *         foodName: string,
 *         calories: number,
 *         protein: number,
 *         fat: number,
 *         carbohydrates: number,
 *         water: number,
 *         foodType: string
 *       }>,
 *       mealType: string,
 *       mealTime: string,
 *       mealNote: string,
 *       mealPlace: string
 *     }>
 *   }
 * }>}
 */
export const listPaginatedDietRecordsByTimeRange = (params) => {
  return http.post('/dietRecords/listByPage', {
    current: 1,
    size: 15,
    isAsc: true,
    ...params
  })
}

/**
 * 获取营养分析数据
 * @param {Object} params - 查询参数
 * @param {number} params.userId - 用户ID
 * @param {string} [params.startDateTime] - 开始时间
 * @param {string} [params.endDateTime] - 结束时间
 * @returns {Promise<{
 *   code: number,
 *   message: string,
 *   data: {
 *     totalCalories: number,
 *     totalProtein: number,
 *     totalFat: number,
 *     totalCarbohydrates: number,
 *     totalWater: number,
 *     dailyAverageCalories: number,
 *     dailyAverageProtein: number,
 *     dailyAverageFat: number,
 *     dailyAverageCarbohydrates: number,
 *     dailyAverageWater: number,
 *     mealTypeDistribution: {
 *       [key: string]: number
 *     },
 *     nutrientPercentages: {
 *       protein: number,
 *       fat: number,
 *       carbohydrates: number
 *     }
 *   }
 * }>}
 */
export const getNutritionAnalysis = (params) => {
  return http.post('/dietRecords/nutritionAnalysis', params)
}

/**
 * 获取所有的进餐类型
 * @returns {Promise<{code: number, message: string, data: Object}>}
 */
export function getMealTypes() {
  return http.get('/dietRecords/mealTypes')
}

/**
 * 进行某时间段内的营养摄入计算
 * @param {object} params 进行饮食记录计算的请求数据
 * @param {number} params.current 当前页码。默认从1开始，不能超过1000
 * @param {number} params.size 每页展示的数据量。默认为15，不能超过50
 * @param {boolean} params.isAsc 是否升序，默认true
 * @param {string} params.startDateTime 开始日期时间
 * @param {string} params.endDateTime 结束日期时间
 * @param {number} params.userId 用户ID
 * @returns {Promise}
 */
export function calculateNutritionIntake(params) {
  return http.post('/dietRecords/calculateIntakeByTimeRange', params)
} 