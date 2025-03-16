export function handleRequestError(error) {
  // 只在开发环境打印错误
  if (import.meta.env.DEV) {
    console.error('Request error:', error)
  }
  
  // 直接返回错误，让具体的业务代码处理错误提示
  return Promise.reject(error)
}
