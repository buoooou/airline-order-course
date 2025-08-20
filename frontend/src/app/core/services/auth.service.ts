import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';

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

  login(credentials: {username: string, password: string}): Observable<AuthResponse>{
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        // 存储令牌到本地存储
        localStorage.setItem(this.TOKEN_KEY, response.token);
        localStorage.setItem(this.USER_KEY, response.userId.toString());
        this.isAuthenticatedSubject.next(true);
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

interface AuthResponse {
  token: string;
  userId: number;
}