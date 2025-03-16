/**
 * 运动相关工具函数
 */

/**
 * 计算运动强度级别
 * @param {Object} record 运动记录
 * @returns {string} 强度级别：'high', 'medium', 'low'
 */
export const calculateIntensity = (record) => {
  if (!record) return 'low'
  
  // 高强度运动类型
  const highIntensityTypes = [
    '跑步', '高强度间歇训练', '有氧运动', '游泳', '篮球', '足球'
  ]
  
  // 中等强度运动类型
  const mediumIntensityTypes = [
    '骑行', '快走', '舞蹈', '排球', '羽毛球', '乒乓球'
  ]
  
  // 检查是否有高强度运动类型
  const hasHighIntensityType = record.exerciseTypesList?.some(
    type => highIntensityTypes.includes(type.exerciseName)
  )
  
  // 检查是否有中等强度运动类型
  const hasMediumIntensityType = record.exerciseTypesList?.some(
    type => mediumIntensityTypes.includes(type.exerciseName)
  )
  
  // 基于心率、时长、距离和类型的综合评估
  if (
    (hasHighIntensityType && record.heartRate > 140) || 
    (record.duration > 60 && record.heartRate > 130) || 
    (record.distance > 8)
  ) {
    return 'high'
  } else if (
    hasMediumIntensityType || 
    record.duration > 30 || 
    (record.distance > 3 && record.distance <= 8) ||
    (record.heartRate > 110 && record.heartRate <= 140)
  ) {
    return 'medium'
  }
  
  return 'low'
}

/**
 * 获取运动类型的颜色
 * @param {Object} record 运动记录
 * @returns {string} 颜色代码
 */
export const getExerciseTypeColor = (record) => {
  if (!record?.exerciseTypesList?.length) return '#dcdfe6'
  
  const typeColorMap = {
    '跑步': '#f56c6c',
    '步行': '#67c23a',
    '骑行': '#e6a23c',
    '游泳': '#409eff',
    '力量训练': '#909399',
    '篮球': '#e6a23c',
    '足球': '#67c23a',
    '瑜伽': '#bc9fdb',
    '健身操': '#c599ec',
    '舞蹈': '#ffd04b'
  }
  
  // 使用第一个运动类型的颜色，如果没有匹配则使用默认颜色
  return typeColorMap[record.exerciseTypesList[0].exerciseName] || '#409eff'
}

/**
 * 模拟生成历史趋势数据（在实际应用中应从API获取）
 * @param {string} dataType 数据类型：'time', 'distance', 'count', 'heartRate'
 * @returns {Array} 趋势数据数组
 */
export const generateHistoryData = (dataType) => {
  // 在真实应用中，这些数据应该从API获取
  const mockData = {
    'time': [30, 45, 20, 60, 40, 25, 50],
    'distance': [2.5, 3.2, 2.0, 5.0, 4.2, 3.0, 4.5],
    'count': [1, 2, 1, 2, 1, 1, 2],
    'heartRate': [125, 135, 120, 140, 130, 125, 135]
  }
  
  return mockData[dataType] || [0, 0, 0, 0, 0]
}

/**
 * 计算同比变化百分比
 * @param {number} current 当前值
 * @param {number} previous 之前值
 * @returns {number} 变化百分比
 */
export const calculateChange = (current, previous) => {
  if (!previous) return 0
  return Math.round((current - previous) / previous * 100)
} 