/**
 * 用户模型
 */
export interface User {
  id: number;
  username: string;
  role: string;
}

/**
 * 登录请求模型
 */
export interface LoginRequest {
  username: string;
  password: string;
}

/**
 * 登录响应模型
 */
export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresAt: string;
  user: User;
  loginTime: string;
  message: string;
}

/**
 * API响应基础模型
 */
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}
