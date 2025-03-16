// 请求配置
export const REQUEST_CONFIG = {
  // 基础URL - 根据环境判断
  baseURL: (() => {
    const apiUrl = import.meta.env.VITE_API_BASE_URL
    if (!apiUrl) {
      console.warn('API base URL not configured, falling back to default localhost')
      return 'http://localhost:21167'
    }
    return apiUrl
  })(),

  // 超时时间
  timeout: 15000,

  // 请求头
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json'
  },

  // 状态码
  statusCode: {
    SUCCESS: 200,
    ERROR: 500,
    UNAUTHORIZED: 401,
    FORBIDDEN: 403,
    NOT_FOUND: 404,
    // 业务状态码
    USERNAME_EXISTS: 1002,
    EMAIL_EXISTS: 1003,
    USERNAME_NOT_EXIST: 1000,
    PASSWORD_ERROR: 1001
  },

  // 响应信息
  messages: {
    networkError: '网络连接失败，请检查网络设置',
    timeoutError: '请求超时，请稍后重试',
    serverError: '服务器错误，请联系管理员',
    unknownError: '未知错误，请稍后重试'
  },

  // 响应结构
  response: {
    codeField: 'code',
    messageField: 'message',
    dataField: 'data'
  }
}

// 错误消息模板
export const ERROR_TEMPLATES = {
  USERNAME_EXISTS: {
    title: '注册失败：用户名已存在',
    detail: '该用户名已被其他用户注册使用',
    suggestion: '请尝试使用其他用户名重新注册'
  },
  EMAIL_EXISTS: {
    title: '注册失败：邮箱已被注册',
    detail: '该邮箱地址已经被其他账号使用',
    suggestion: '请使用其他邮箱地址，或通过该邮箱找回已有账号'
  },
  NETWORK_ERROR: {
    title: '网络连接失败',
    detail: '无法连接到服务器',
    suggestion: '请检查：\n1. 网络连接是否正常\n2. 服务器地址是否正确\n3. 服务器是否在运行'
  },
  TIMEOUT_ERROR: {
    title: '请求超时',
    detail: '服务器响应时间过长',
    suggestion: '请检查网络连接是否正常，稍后重试'
  },
  SERVER_ERROR: {
    title: '服务器错误',
    detail: '服务器内部发生错误',
    suggestion: '请联系管理员处理'
  },
  UNKNOWN_ERROR: {
    title: '未知错误',
    detail: '发生未预期的错误',
    suggestion: '请刷新页面重试，如果问题持续存在请联系管理员'
  },
  USERNAME_NOT_EXIST: {
    title: '登录失败：用户不存在',
    detail: '该用户名尚未注册',
    suggestion: '请检查用户名是否正确，或立即注册新账号'
  },
  PASSWORD_ERROR: {
    title: '登录失败：密码错误',
    detail: '您输入的密码不正确',
    suggestion: '请检查密码是否正确，注意大小写'
  }
}

// 添加响应结构定义
export const RESPONSE_STRUCTURE = {
  code: null, // 状态码
  message: '', // 状态信息
  data: null // 返回的数据
}
