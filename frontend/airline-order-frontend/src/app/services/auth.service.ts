import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private tokenKey = 'auth_token';
  private userKey = 'user_info';

  constructor(private http: HttpClient, @Inject(PLATFORM_ID) private platformId: Object) { }

  /**
   * 用户登录
   * @param username 用户名
   * @param password 密码
   * @returns 登录结果，包含JWT令牌
   */
  login(username: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, { username, password })
      .pipe(
        tap(response => {
          if (response.success && response.data) {
            this.storeToken(response.data.token);
            this.storeUserInfo({
              id: response.data.id,
              username: response.data.username,
              role: response.data.role
            });
          }
        })
      );
  }

  /**
   * 用户注册
   * @param userData 用户注册数据
   * @returns 注册结果
   */
  register(userData: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/register`, userData);
  }

  /**
   * 用户登出
   */
  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem(this.tokenKey);
      localStorage.removeItem(this.userKey);
    }
  }

  /**
   * 获取JWT令牌
   * @returns JWT令牌
   */
  getToken(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem(this.tokenKey);
    }
    return null;
  }

  /**
   * 存储JWT令牌
   * @param token JWT令牌
   */
  private storeToken(token: string): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem(this.tokenKey, token);
    }
  }

  /**
   * 存储用户信息
   * @param user 用户信息
   */
  private storeUserInfo(user: any): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem(this.userKey, JSON.stringify(user));
    }
  }

  /**
   * 获取用户信息
   * @returns 用户信息
   */
  getUserInfo(): any {
    if (isPlatformBrowser(this.platformId)) {
      const userInfo = localStorage.getItem(this.userKey);
      return userInfo ? JSON.parse(userInfo) : null;
    }
    return null;
  }

  /**
   * 检查用户是否已登录
   * @returns 是否已登录
   */
  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  /**
   * 检查用户是否具有管理员角色
   * @returns 是否为管理员
   */
  isAdmin(): boolean {
    const userInfo = this.getUserInfo();
    return userInfo && userInfo.role === 'ROLE_ADMIN';
  }
}