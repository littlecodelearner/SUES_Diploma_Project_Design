/**
 * 日期时间格式配置
 */
export const DATE_FORMATS = {
  // 后端API日期时间格式 (ISO-8601)
  API_DATETIME: "YYYY-MM-DDTHH:mm:ss.SSSZ",
  // 显示用的日期时间格式
  DISPLAY_DATETIME: 'YYYY-MM-DD HH:mm',
  // 仅日期格式
  DATE_ONLY: 'YYYY-MM-DD',
  // 仅时间格式
  TIME_ONLY: 'HH:mm:ss'
}

/**
 * API相关配置
 */
export const API_CONFIG = {
  // 日期时间字段说明
  DATE_FIELD_NOTE: '所有与后端API交互的日期时间字段必须使用ISO-8601格式，例如：2025-01-07T15:06:18.000+0800',
  // 分页配置
  DEFAULT_PAGE_SIZE: 10,
  PAGE_SIZES: [10, 20, 50, 100]
}

/**
 * 表单验证规则
 */
export const VALIDATION_RULES = {
  // 运动时长限制（分钟）
  EXERCISE_DURATION: {
    MIN: 1,
    MAX: 1440
  },
  // 运动距离限制（公里）
  EXERCISE_DISTANCE: {
    MIN: 0,
    MAX: 1000,
    PRECISION: 2
  },
  // 卡路里限制
  CALORIES: {
    MIN: 0,
    MAX: 10000,
    PRECISION: 1
  },
  // 心率限制
  HEART_RATE: {
    MIN: 0,
    MAX: 250
  }
}

/**
 * 错误信息
 */
export const ERROR_MESSAGES = {
  DATE_FORMAT: '日期时间格式错误：请使用ISO-8601格式 (YYYY-MM-DDThh:mm:ss.sssZ)'
} 