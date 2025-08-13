import axios, { AxiosInstance, AxiosError, AxiosResponse } from 'axios';
import { Injectable } from '@angular/core';

const API_BASE_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private authApi: AxiosInstance;

  constructor() {
    this.authApi = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }

  async login(usernameOrEmail: string, password: string): Promise<any> {
    try {
      const response: AxiosResponse = await this.authApi.post('/api/auth/login', { 
        usernameOrEmail, 
        password 
      });
      console.log("登录返回", response);
      if (!response.data?.token) {
        throw new Error('登录失败，令牌无效');
      }
      return response.data;
    } catch (error: any) {
      console.error("登录请求失败", error);
      if (error instanceof AxiosError) {
        const status = error.response?.status;
        const err = new Error();
        (err as any).status = status; // 扩展 Error 对象
        if (status === 401) {
          err.message = '用户名或密码错误';
        } else if (status === 500) {
          err.message = '服务器内部错误，请稍后重试';
        } else if (status === 400) {
          err.message = '请求参数错误，请检查输入';
        }
        throw err;
      }
      const err = new Error('登录失败，请检查网络连接或联系管理员');
      (err as any).status = 0; // 标记为网络错误
      throw err;
    }
  }

  async register(username: string, email: string, password: string): Promise<any> {
    console.log("注册时传参",username, email, password);
    try {
      console.log("正在发送注册请求到: /api/auth/register");
      const response: AxiosResponse = await this.authApi.post('/api/auth/register', {
        username,
        email,
        password
      });
      console.log("注册请求成功", response.data);
      return response.data;
    } catch (error: any) {
      console.error("注册请求失败", error);
      if (error instanceof AxiosError) {
        const status = error.response?.status;
        const err = new Error();
        (err as any).status = status; // 扩展 Error 对象
        if (status === 302) {
          console.error("后端返回302重定向，目标URL:", error.response?.headers?.['location'] || error.response?.data?.url || '未知');
          err.message = '请求被重定向，请检查后端配置';
        } else if (status === 400) {
          err.message = '用户名或邮箱已存在';
        } else if (status === 500) {
          err.message = '服务器内部错误，请稍后重试';
        }
        throw err;
      }
      const err = new Error('注册失败，请检查网络连接');
      (err as any).status = 0; // 标记为网络错误
      throw err;
    }
  }
}