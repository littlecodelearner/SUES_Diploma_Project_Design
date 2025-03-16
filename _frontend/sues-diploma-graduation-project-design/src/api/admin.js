import http from '@/utils/request'

/**
 * 获取用户详情列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @param {string} [params.userName] - 用户名搜索关键字
 * @returns {Promise} 返回用户列表数据
 */
export const getUserDetails = ({ page, size, userName }) => {
  return http.get('/admin/details', {
    params: {
      page,
      size,
      userName
    }
  })
}

/**
 * 分页获取所有用户的详细数据
 * @param {object} params 查询参数
 * @param {number} params.current 当前页码。默认从1开始，不能超过1000
 * @param {number} params.size 每页展示的数据量。默认为15，不能超过50
 * @param {boolean} params.isAsc 是否升序，默认true
 * @param {string} params.startDateTime 开始日期时间。举例：优先使用中国时区的日期格式`2023-10-05T14:30:00.123+08:00`
 * @param {string} params.endDateTime 结束日期时间。举例：优先使用中国时区的日期格式`2023-10-05T14:30:00.123+08:00`
 * @param {string} params.username 用户名
 * @returns {Promise} 返回用户详情分页数据
 */
export const listUsersDetailsByPage = (params) => {
  console.log('API调用参数:', params)
  
  // 确保分页参数是有效的数字
  const current = parseInt(params.current) || 1
  const size = parseInt(params.size) || 15
  
  // 构建请求体
  const requestBody = {
    current,
    size,
    isAsc: params.isAsc ?? true
  }

  // 只有当这些字段有值时才添加到请求体中
  if (params.startDateTime) {
    requestBody.startDateTime = params.startDateTime
  }
  if (params.endDateTime) {
    requestBody.endDateTime = params.endDateTime
  }
  if (params.username) {
    requestBody.username = params.username
  }
  
  console.log('发送请求体:', requestBody)
  
  return http({
    url: '/admin/userDetails',
    method: 'post',
    data: requestBody
  })
}

/**
 * 删除用户
 * @param {number[]} userIds - 要删除的用户ID数组
 * @returns {Promise} 返回删除结果
 */
export const deleteUsers = (userIds) => {
  return http.delete('/admin/users', {
    data: userIds
  })
}

/**
 * 更新用户信息
 * @param {Object} userData - 用户数据
 * @returns {Promise} 返回更新结果
 */
export const updateUser = (userData) => {
  return http.put('/admin/users', userData)
}

/**
 * 获取系统统计数据
 * @returns {Promise} 返回系统统计数据
 */
export const getSystemStats = () => {
  return http.get('/admin/stats')
}

/**
 * 批量删除用户
 * @param {number[]} userIds - 要删除的用户ID数组
 * @returns {Promise} 返回删除结果
 */
export const deleteUsersInBulk = (userIds) => {
  return http({
    url: '/admin',
    method: 'delete',
    data: {
      userIdList: userIds
    }
  })
} 