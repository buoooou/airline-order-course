export interface User {
    id: number;
    username: string;
}

/**
 * API响应基础模型
 */
export interface ApiResponse<T> {
    code: number;
    message: string;
    data: T;
}