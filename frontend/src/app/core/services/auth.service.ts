import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { ApiResponseDTO, AuthResponse } from '../../shared/models/api-response.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = '/api/auth';
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'current_userid';

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$: Observable<boolean> = this.isAuthenticatedSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.isAuthenticatedSubject.next(this.isLoggedIn());
  }

  login(credentials: {username: string, password: string}): Observable<ApiResponseDTO<AuthResponse>>{
    return this.http.post<ApiResponseDTO<AuthResponse>>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        console.log('auth.service#login  response.token:' + response.data.token + ', response.userid:' + response.data.userId.toString());
        // 存储令牌到本地存储
        localStorage.setItem(this.TOKEN_KEY, response.data.token);
        localStorage.setItem(this.USER_KEY, response.data.userId.toString());
        this.isAuthenticatedSubject.next(true);
        console.log('登录成功，用户信息已保存.');
      })
    );
  }

  getCurrentUserId(): number | null {
    const userId = localStorage.getItem(this.USER_KEY);
    return userId ? parseInt(userId, 10) : null;
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem(this.TOKEN_KEY);
  }

  logout() {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']);
  }

  getToken() {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  // isTokenValid(): boolean {
  //   const token = this.getToken();
  //   if (!token) return false;
    
  //   // 解析令牌过期时间（需安装 jwt-decode）
  //   const { exp } = jwtDecode(token);
  //   return Date.now() < exp * 1000; 
  // }
}

