import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { LoginRequest, LoginResponse, User, ApiResponse } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/auth';
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'current_user';

  // 当前用户状态
  private currentUserSubject = new BehaviorSubject<User | null>(this.getCurrentUser());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) { }

  /**
   * 检查是否在浏览器环境中
   */
  private isBrowser(): boolean {
    return typeof window !== 'undefined' && typeof localStorage !== 'undefined';
  }

  /**
   * 用户登录
   */
  login(loginRequest: LoginRequest): Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.API_URL}/login`, loginRequest)
      .pipe(
        tap(response => {
          if (response.success && response.data && this.isBrowser()) {
            // 保存token和用户信息到localStorage
            // 后端返回的是accessToken字段
            const token = response.data.accessToken;
            localStorage.setItem(this.TOKEN_KEY, token);
            
            const user: User = {
              id: response.data.user?.id || 0,
              username: response.data.user?.username || loginRequest.username,
              role: response.data.user?.role || 'USER'
            };
            localStorage.setItem(this.USER_KEY, JSON.stringify(user));
            this.currentUserSubject.next(user);
            
            console.log('登录成功，token已保存:', token ? '✓' : '✗');
            console.log('用户信息已保存:', user);
          }
        })
      );
  }

  /**
   * 用户注册
   */
  register(username: string, password: string, role: string = 'USER'): Observable<ApiResponse<any>> {
    const formData = new FormData();
    formData.append('username', username);
    formData.append('password', password);
    formData.append('role', role);

    return this.http.post<ApiResponse<any>>(`${this.API_URL}/register`, formData);
  }

  /**
   * 用户登出
   */
  logout(): void {
    if (this.isBrowser()) {
      localStorage.removeItem(this.TOKEN_KEY);
      localStorage.removeItem(this.USER_KEY);
    }
    this.currentUserSubject.next(null);
  }

  /**
   * 获取当前用户
   */
  getCurrentUser(): User | null {
    if (!this.isBrowser()) {
      return null;
    }
    
    const userStr = localStorage.getItem(this.USER_KEY);
    return userStr ? JSON.parse(userStr) : null;
  }

  /**
   * 获取认证token
   */
  getToken(): string | null {
    if (!this.isBrowser()) {
      return null;
    }
    
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * 检查是否已登录
   */
  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  /**
   * 检查用户是否有指定角色
   */
  hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    return user ? user.role === role : false;
  }

  /**
   * 检查是否为管理员
   */
  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }
}
