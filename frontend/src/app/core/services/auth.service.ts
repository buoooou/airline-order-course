import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = '/api/auth';
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'current_userid';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  login(credentials: {username: string, password: string}): Observable<AuthResponse>{
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        // 存储令牌到本地存储
        localStorage.setItem(this.TOKEN_KEY, response.token);
        localStorage.setItem(this.USER_KEY, response.userId.toString());
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
    this.router.navigate(['/login']);
  }

  getToken() {
    return localStorage.getItem(this.TOKEN_KEY);
  }
}

interface AuthResponse {
  token: string;
  userId: number;
}