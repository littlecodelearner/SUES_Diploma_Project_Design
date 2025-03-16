/**
 * 日期格式化工具函数
 */

/**
 * 将日期格式化为友好的显示格式（中国时区）
 * @param {string|Date} date - 日期对象或日期字符串
 * @param {boolean} includeTime - 是否包含时间
 * @param {boolean} showSeconds - 是否显示秒数
 * @returns {string} 格式化后的日期字符串
 */
export const formatDate = (date, includeTime = false, showSeconds = false) => {
  if (!date) return '--';
  
  try {
    // 创建日期对象并确保正确处理时区
    let dateObj;
    
    if (typeof date === 'string') {
      // 尝试解析日期字符串
      // 特殊处理类似 "2025-03-12T16:00:00.000+00:00" 这样的格式
      if (date.includes('T') && (date.includes('Z') || date.includes('+'))) {
        console.log('处理ISO格式日期:', date);
      }
      
      // 创建基于当前时区的日期对象
      dateObj = new Date(date);
      
      // 如果日期无效，尝试特殊格式处理
      if (isNaN(dateObj.getTime())) {
        console.warn('标准解析失败，尝试特殊处理:', date);
        
        // 尝试移除时区部分再解析
        if (date.includes('+')) {
          const simplifiedDate = date.split('+')[0];
          dateObj = new Date(simplifiedDate);
        }
      }
    } else if (date instanceof Date) {
      dateObj = new Date(date);
    } else {
      console.warn('不支持的日期格式:', date);
      return '--';
    }
    
    // 检查日期是否有效
    if (isNaN(dateObj.getTime())) {
      console.warn('无效的日期:', date);
      return '--';
    }
    
    // 获取年、月、日
    const year = dateObj.getFullYear();
    const month = String(dateObj.getMonth() + 1).padStart(2, '0');
    const day = String(dateObj.getDate()).padStart(2, '0');
    
    if (!includeTime) {
      return `${year}-${month}-${day}`;
    }
    
    // 格式化时间
    const hours = String(dateObj.getHours()).padStart(2, '0');
    const minutes = String(dateObj.getMinutes()).padStart(2, '0');
    const seconds = showSeconds ? String(dateObj.getSeconds()).padStart(2, '0') : null;
    
    // 返回完整的日期时间格式
    return seconds ? 
      `${year}-${month}-${day} ${hours}:${minutes}:${seconds}` :
      `${year}-${month}-${day} ${hours}:${minutes}`;
  } catch (e) {
    console.error('日期格式化错误:', e, date);
    return '--';
  }
}

/**
 * 格式化日期为后端支持的格式
 * @param {Date|string} date - 要格式化的日期
 * @param {string} [type='datetime'] - 格式化类型：'date' 或 'datetime'
 * @returns {string|null} 格式化后的日期字符串，无效日期返回 null
 */
export function formatDateForBackend(date, type = 'datetime') {
  if (!date) return null

  // 如果已经是字符串格式，检查是否符合支持的格式
  if (typeof date === 'string' && isValidDateFormat(date)) {
    return date
  }

  const d = new Date(date)
  if (isNaN(d.getTime())) return null

  if (type === 'date') {
    // 格式化为 "yyyy-MM-dd" 格式
    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  } else {
    // 使用中国时区格式化（UTC+8）
    return formatDateWithChineseTimezone(d)
  }
}

/**
 * 格式化日期为中国时区（UTC+8）的ISO格式
 * @param {Date|string} date - 要格式化的日期
 * @returns {string} 格式化后的日期字符串，格式为：2023-10-05T14:30:00.000+08:00
 */
export function formatDateWithChineseTimezone(date) {
  if (!date) return null
  
  let d
  try {
    // 如果是字符串，先尝试解析
    if (typeof date === 'string') {
      // 如果已经是正确的格式，直接返回
      if (isValidDateFormat(date)) {
        return date
      }
      // 否则创建新的日期对象
      d = new Date(date)
    } else {
      d = new Date(date)
    }
    
    if (isNaN(d.getTime())) {
      console.warn('Invalid date:', date)
      return null
    }

    // 创建一个新的日期对象，设置为北京时间
    const beijingDate = new Date(d.getTime() - (d.getTimezoneOffset() * 60000))

    // 生成ISO格式的日期字符串，但不包含时区信息
    const isoString = beijingDate.toISOString()

    // 替换Z为+08:00
    return isoString.replace('Z', '+08:00')
  } catch (error) {
    console.error('Error formatting date:', error)
    return null
  }
}

/**
 * 检查日期字符串是否符合后端支持的格式
 * @param {string} dateStr - 要检查的日期字符串
 * @returns {boolean} 是否符合格式
 */
export function isValidDateFormat(dateStr) {
  if (!dateStr) return false

  // 支持的日期格式
  const formats = [
    /^\d{4}-\d{2}-\d{2}$/, // "yyyy-MM-dd"
    /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}\+08:00$/ // "yyyy-MM-ddTHH:mm:ss.SSS+08:00"
  ]

  return formats.some(format => format.test(dateStr))
}

/**
 * 格式化日期时间为显示格式
 * @param {Date|string} date - 要格式化的日期
 * @param {boolean} [showTime=true] - 是否显示时间
 * @returns {string} 格式化后的日期时间字符串
 */
export function formatDateTime(date, showTime = true) {
  if (!date) return ''

  const d = new Date(date)
  if (isNaN(d.getTime())) return ''

  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const dateStr = `${year}-${month}-${day}`

  if (!showTime) return dateStr

  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  
  return `${dateStr} ${hours}:${minutes}:${seconds}`
}

/**
 * 获取相对时间描述
 * @param {Date|string} date - 要计算的日期
 * @returns {string} 相对时间描述
 */
export function getRelativeTime(date) {
  if (!date) return ''

  const d = new Date(date)
  if (isNaN(d.getTime())) return ''

  const now = new Date()
  const diff = now.getTime() - d.getTime()
  const diffMinutes = Math.floor(diff / (1000 * 60))
  const diffHours = Math.floor(diff / (1000 * 60 * 60))
  const diffDays = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (diffMinutes < 1) return '刚刚'
  if (diffMinutes < 60) return `${diffMinutes}分钟前`
  if (diffHours < 24) return `${diffHours}小时前`
  if (diffDays < 30) return `${diffDays}天前`
  
  return formatDateTime(date)
}

/**
 * 将ISO日期字符串转换为本地时间并格式化显示
 * 这个函数专门用于解决后端返回的ISO格式时间在前端显示时可能存在的时区问题
 * @param {string} isoString - ISO格式的日期字符串
 * @param {boolean} showTime - 是否显示时间
 * @param {boolean} showSeconds - 是否显示秒 (仅当showTime为true时有效)
 * @returns {string} 格式化后的日期字符串
 */
export function formatISOtoLocal(isoString, showTime = false, showSeconds = false) {
  if (!isoString) return '--';
  
  try {
    // 创建日期对象
    const date = new Date(isoString);
    
    // 检查日期是否有效
    if (isNaN(date.getTime())) {
      console.warn('无效的ISO日期字符串:', isoString);
      return '--';
    }
    
    // 获取本地时间各部分
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    
    // 如果不显示时间，只返回日期部分
    if (!showTime) {
      return `${year}-${month}-${day}`;
    }
    
    // 获取时间部分 - 由于后端统一存储为UTC 16:00 (对应东八区00:00)
    // 我们直接显示00:00，而不是实际转换时间
    const timeStr = showSeconds ? '00:00:00' : '00:00';
    
    return `${year}-${month}-${day} ${timeStr}`;
  } catch (e) {
    console.error('格式化ISO日期出错:', e, isoString);
    return '--';
  }
}

/**
 * 获取指定日期范围的开始和结束时间
 * @param {string} range 日期范围：'today' | 'week' | 'month'
 * @returns {Object} 包含开始和结束时间的对象
 */
export const getDateRange = (range) => {
  const now = new Date()
  const start = new Date()
  
  switch (range) {
    case 'today':
      start.setHours(0, 0, 0, 0)
      break
    case 'week':
      start.setDate(now.getDate() - now.getDay())
      start.setHours(0, 0, 0, 0)
      break
    case 'month':
      start.setDate(1)
      start.setHours(0, 0, 0, 0)
      break
    default:
      start.setHours(0, 0, 0, 0)
  }

  // 设置结束时间为当天的最后一毫秒
  now.setHours(23, 59, 59, 999)
  
  return {
    start: formatDateWithChineseTimezone(start),
    end: formatDateWithChineseTimezone(now)
  }
}
