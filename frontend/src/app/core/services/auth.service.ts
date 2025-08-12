import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { HTTP_CONFIG } from './http.config';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  result: 'SUCCESS' | 'ERROR';
  message: string;
  data: {
    token: string;
    type: string;
    username: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'token';
  private readonly USERNAME_KEY = 'username';
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient) {}

  /**
   * 用户登录
   */
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${HTTP_CONFIG.api.baseUrl}/auth/login`,
      credentials
    ).pipe(
      map(response => {
        if (response.result === 'SUCCESS' && response.data.token) {
          this.setToken(response.data.token);
          this.setUsername(response.data.username);
          this.isAuthenticatedSubject.next(true);
        }
        return response;
      })
    );
  }

  /**
   * 用户登出
   */
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USERNAME_KEY);
    this.isAuthenticatedSubject.next(false);
  }

  /**
   * 获取token
   */
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * 设置token
   */
  private setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  /**
   * 获取用户名
   */
  getUsername(): string | null {
    return localStorage.getItem(this.USERNAME_KEY);
  }

  /**
   * 设置用户名
   */
  private setUsername(username: string): void {
    localStorage.setItem(this.USERNAME_KEY, username);
  }

  /**
   * 检查是否有token
   */
  private hasToken(): boolean {
    return !!localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * 检查是否已登录
   */
  isLoggedIn(): boolean {
    return this.hasToken();
  }
}