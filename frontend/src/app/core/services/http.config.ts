export const HTTP_CONFIG = {
  // API基础配置
  api: {
    baseUrl: '/api',
    timeout: 30000, // 30秒超时
    retryCount: 3,
    retryDelay: 1000
  },

  // 请求头配置
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'X-Requested-With': 'XMLHttpRequest'
  },

  // 错误码配置
  errorCodes: {
    400: '请求参数错误',
    401: '未授权，请重新登录',
    403: '权限不足',
    404: '请求的资源不存在',
    408: '请求超时',
    500: '服务器内部错误',
    502: '网关错误',
    503: '服务不可用',
    504: '网关超时'
  } as { [key: number]: string },

  // 文件上传配置
  upload: {
    maxSize: 10 * 1024 * 1024, // 10MB
    allowedTypes: [
      'image/jpeg',
      'image/png',
      'image/gif',
      'application/pdf',
      'application/msword',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
    ]
  }
} as const;

// 导出类型定义
export type HttpConfig = typeof HTTP_CONFIG;