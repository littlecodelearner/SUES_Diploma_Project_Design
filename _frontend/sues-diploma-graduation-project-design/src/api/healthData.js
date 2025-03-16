import request from '@/utils/request'

/**
 * 添加健康数据
 * @param {Object} data - 健康数据
 * @param {number} data.userId - 用户ID
 * @param {number} [data.heartRate] - 心率(bpm)
 * @param {number} [data.weight] - 体重(kg)
 * @param {string} data.measurementDate - 测量日期时间
 * @returns {Promise<{code: number, message: string, data: Object}>}
 */
export const addHealthData = (data) => {
  return request.post('/healthData/add', data)
}

/**
 * 批量添加健康数据
 * @param {Array<Object>} dataList - 健康数据列表
 * @returns {Promise<{code: number, message: string, data: Object}>}
 */
export const addHealthDataBatch = (dataList) => {
  return request({
    url: '/healthData',
    method: 'post',
    data: dataList
  })
}

/**
 * 更新健康数据（单条）- 使用批量修改接口
 * @param {Object} data - 健康数据
 * @param {number} data.healthDataId - 健康数据ID (必需)
 * @param {number} data.userId - 用户ID (必需)
 * @param {number} [data.heartRate] - 当前心率(bpm)
 * @param {number} [data.weight] - 体重(kg)
 * @param {string} [data.measurementDate] - 健康数据记录日期
 * @returns {Promise<{code: number, message: string, data: Object}>}
 */
export const updateHealthData = (data) => {
  // 将单条数据转换为数组，使用批量修改接口
  return request.put('/healthData', [data])
}

/**
 * 批量修改健康数据
 * @param {Array<Object>} params - 修改健康数据的请求参数数组
 * @param {number} params[].healthDataId - 健康数据ID
 * @param {number} params[].userId - 用户ID
 * @param {number} params[].heartRate - 当前心率（单位：bpm）
 * @param {number} params[].weight - 体重（单位：kg）
 * @param {string} params[].measurementDate - 健康数据记录日期（每天记录一次）。举例：优先使用中国时区的日期格式`2023-10-05T14:30:00.123+08:00`
 * @returns {Promise<Object>} 响应结果
 */
export function updateHealthDataInBatch(params) {
  return request.put('/healthData', params)
}

/**
 * 批量删除健康数据
 * @param {Array<number>} healthDataIds - 健康数据ID列表
 * @returns {Promise<{code: number, message: string, data: Object}>}
 */
export const deleteHealthDataBatch = (healthDataIds) => {
  return request({
    url: '/healthData',
    method: 'delete',
    data: healthDataIds
  })
}

/**
 * 分页获取健康数据列表
 * @param {Object} params - 查询参数
 * @param {number} params.userId - 用户ID (必填)
 * @param {number} [params.current=1] - 当前页码，默认1，不能超过1000
 * @param {number} [params.size=15] - 每页数量，默认15，不能超过50
 * @param {boolean} [params.isAsc] - 是否升序，默认true
 * @param {string} [params.startDateTime] - 开始时间 (可选)
 * @param {string} [params.endDateTime] - 结束时间 (可选)
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
 *       healthDataId: number,
 *       userId: number,
 *       height: number,
 *       weight: number,
 *       bmi: number,
 *       heartRate: number,
 *       measurementDate: string
 *     }>
 *   }
 * }>}
 */
export const getHealthDataList = (params) => {
  // 构建请求数据，只包含非空参数
  const requestData = {
    userId: params.userId // 用户ID是必需的
  };
  
  // 只有当参数有值时才添加到请求体中
  if (params.current !== undefined) {
    // 限制页码不超过1000
    requestData.current = Math.min(params.current || 1, 1000);
  }
  
  if (params.size !== undefined) {
    // 限制每页数量不超过50
    requestData.size = Math.min(params.size || 15, 50);
  }
  
  if (params.isAsc !== undefined) {
    requestData.isAsc = params.isAsc;
  }
  
  if (params.startDateTime) {
    requestData.startDateTime = params.startDateTime;
  }
  
  if (params.endDateTime) {
    requestData.endDateTime = params.endDateTime;
  }
  
  return request({
    url: '/healthData/listByPage',
    method: 'post',
    data: requestData
  });
}

/**
 * 获取健康数据趋势图（服务端渲染，用作降级方案）
 * @param {Object} data - 查询参数
 * @param {number} data.userId - 用户ID
 * @param {number} data.offset - 偏移量(页码-1)
 * @param {number} data.size - 每页记录数
 * @param {string} [data.startDate] - 起始日期(可选)
 * @param {string} [data.endDate] - 结束日期(可选)
 * @returns {Promise<{
 *   code: number,
 *   message: string,
 *   data: string  // Base64 编码的图片数据
 * }>}
 */
export const getHealthDataTrend = (data) => {
  return request.post('/healthData/trendChart', data)
} 