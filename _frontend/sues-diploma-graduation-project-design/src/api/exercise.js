import http from '@/utils/request'

// 保存新的运动记录
export const saveExerciseRecord = (data) => {
  return http.post('/exerciseRecords/add', {
    userId: data.userId,
    exerciseTypeIdList: data.exerciseTypeIdList,
    duration: data.duration,
    distance: data.distance,
    caloriesBurned: data.caloriesBurned,
    heartRate: data.heartRate,
    exerciseNote: data.exerciseNote,
    exerciseDate: data.exerciseDate
  })
}

// 获取运动记录列表
export const getExerciseRecordList = ({ userId, current, size, startDate, endDate }) => {
  return http.get('/exerciseRecords/list', {
    params: {
      userId,
      current,
      size,
      startDate,
      endDate
    }
  })
}

/**
 * 更新运动记录
 * @param {Object} data - 更新的运动记录数据
 * @returns {Promise}
 */
export function updateExerciseRecord(data) {
  return http.put('/exerciseRecords/update', data)
}

// 删除运动记录
export const deleteExerciseRecord = (recordId) => {
  return http.delete('/exerciseRecords/delete', {
    data: [recordId]  // 将单个ID包装成数组,使用批量删除接口
  })
}

// 批量删除运动记录
export const batchDeleteExerciseRecords = (recordIds) => {
  return http.delete('/exerciseRecords/delete', {
    data: recordIds
  })
}

// 获取运动类型列表（用于选择运动类型）
export const getExerciseTypeList = () => {
  return http.get('/exerciseTypes').then(response => {
    if (response && response.code === 200) {
      return {
        code: 200,
        data: response.data || []
      }
    }
    return {
      code: response.code,
      message: response.message,
      data: []
    }
  })
}

/**
 * 获取运动记录列表（分页）
 * @param {Object} params 查询参数
 * @param {number} params.current 当前页码，默认从1开始，不能超过1000
 * @param {number} params.size 每页数量，默认为15，不能超过50
 * @param {boolean} params.isAsc 是否升序，默认true
 * @param {string} params.startDateTime 开始时间，格式：2023-10-05T14:30:00.123+08:00
 * @param {string} params.endDateTime 结束时间，格式：2023-10-05T14:30:00.123+08:00
 * @param {number} params.userId 用户ID
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
 *       exerciseRecordId: number,
 *       userId: number,
 *       duration: number,
 *       distance: number,
 *       caloriesBurned: number,
 *       heartRate: number,
 *       exerciseNote: string,
 *       exerciseDate: string,
 *       exerciseTypesList: Array<{
 *         exerciseTypeId: number,
 *         exerciseName: string
 *       }>
 *     }>
 *   }
 * }>}
 */
export const getExerciseRecords = (params) => {
  // 验证必要参数
  if (!params.userId) {
    console.error('API调用错误: 用户ID不能为空')
    return Promise.reject(new Error('用户ID不能为空'))
  }

  // 构建请求参数
  const requestParams = {
    current: Math.max(1, Math.min(1000, params.current || 1)),
    size: Math.max(15, Math.min(50, params.size || 15)),
    isAsc: params.isAsc ?? false, // 默认降序，最新记录在前
    userId: params.userId
  }

  // 只有当日期参数存在且非空时才添加到请求中
  if (params.startDateTime) {
    requestParams.startDateTime = params.startDateTime
    console.log('添加开始日期过滤:', params.startDateTime)
  }
  
  if (params.endDateTime) {
    requestParams.endDateTime = params.endDateTime
    console.log('添加结束日期过滤:', params.endDateTime)
  }

  console.log('发送运动记录请求参数:', JSON.stringify(requestParams, null, 2))
  
  return http.post('/exerciseRecords/listByPage', requestParams)
    .then(response => {
      console.log('运动记录API响应状态码:', response.code)
      if (response.code === 200 && response.data) {
        console.log('获取运动记录成功, 总条数:', response.data.total || 0)
      } else {
        console.warn('获取运动记录API响应异常:', response.message)
      }
      return response
    })
    .catch(error => {
      console.error('获取运动记录API错误:', error)
      throw error
    })
}

// 辅助函数：验证ISO日期字符串格式
function isValidISODateString(dateString) {
  const regex = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(\.\d{3})?(\+\d{2}:\d{2}|Z)$/
  return regex.test(dateString)
}

/**
 * 批量创建运动记录
 * @param {Array} records 运动记录数组
 * @param {number} records[].userId 用户ID
 * @param {Array<number>} records[].exerciseTypeIdList 多个运动类型ID
 * @param {number} records[].duration 运动时长（单位：分钟）
 * @param {number} records[].distance 运动距离（单位：km）
 * @param {number} records[].caloriesBurned 消耗的卡路里（单位：kcal）
 * @param {number} records[].heartRate 平均心率（单位：bpm）
 * @param {string} records[].exerciseNote 运动记录的备注、随笔和详细内容
 * @param {string} records[].exerciseDate 运动日期，格式：2023-10-05T14:30:00.123+08:00
 * @returns {Promise<{
 *   code: number,
 *   message: string,
 *   data: object
 * }>}
 */
export const createExerciseRecords = (records) => {
  return http.post('/exerciseRecords', records)
}

/**
 * 批量更新运动记录
 * @param {Array<{
 *   exerciseRecordId: number,
 *   exerciseTypeIdList: Array<number>,
 *   exerciseDate: string,
 *   duration: number,
 *   distance: number,
 *   caloriesBurned: number,
 *   heartRate: number,
 *   exerciseNote: string
 * }>} records - 要更新的运动记录数组
 * @returns {Promise<{
 *   code: number,
 *   message: string,
 *   data: object
 * }>}
 */
export const updateExerciseRecords = (records) => {
  // 确保每条记录都有必需的字段
  const validatedRecords = records.map(record => ({
    exerciseRecordId: record.exerciseRecordId,
    exerciseTypeIdList: record.exerciseTypeIdList || [],
    exerciseDate: record.exerciseDate,
    duration: record.duration || 0,
    distance: record.distance || 0,
    caloriesBurned: record.caloriesBurned || 0,
    heartRate: record.heartRate || 0,
    exerciseNote: record.exerciseNote || ''
  }))
  
  return http.put('/exerciseRecords', validatedRecords)
}

/**
 * 删除运动记录（支持单条和批量删除）
 * @param {Array<number>} recordIds - 要删除的运动记录ID数组
 * @returns {Promise<{
 *   code: number,
 *   message: string,
 *   data: object
 * }>}
 */
export const deleteExerciseRecords = (recordIds) => {
  // 确保 recordIds 是数组
  const ids = Array.isArray(recordIds) ? recordIds : [recordIds]
  
  // 使用 data 属性传递请求体数据
  return http.delete('/exerciseRecords', {
    data: ids
  })
}

// 获取运动统计数据
export const getExerciseStats = ({ userId, timeRange }) => {
  return http.get('/exerciseRecords/stats', {
    params: {
      userId,
      timeRange // 'today' 或 'week'
    }
  }).then(response => {
    if (response && response.code === 200) {
      return {
        code: 200,
        data: {
          duration: response.data.duration || 0,        // 运动时长（分钟）
          distance: response.data.distance || 0,        // 运动距离（公里）
          count: response.data.count || 0,             // 运动次数
          avgHeartRate: response.data.avgHeartRate || 0 // 平均心率
        }
      }
    }
    return {
      code: response.code,
      message: response.message,
      data: {
        duration: 0,
        distance: 0,
        count: 0,
        avgHeartRate: 0
      }
    }
  })
} 