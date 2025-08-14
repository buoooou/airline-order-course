import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: 'GUEST' | 'USER' | 'ADMIN';
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
}

export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  fullName?: string;
  phone?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api';
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.loadUserFromStorage();
  }

  login(credentials: LoginRequest): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/auth/login`, credentials)
      .pipe(
        tap(response => {
          if (response.success && response.data) {
            this.setSession(response.data);
          }
        })
      );
  }

  register(userData: RegisterRequest): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/auth/register`, userData);
  }

  logout(): void {
    const token = this.getToken();
    if (token) {
      this.http.post(`${this.API_URL}/auth/logout`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      }).subscribe();
    }
    
    this.clearSession();
    this.router.navigate(['/auth/login']);
  }

  refreshToken(): Observable<any> {
    const refreshToken = localStorage.getItem('refreshToken');
    return this.http.post<any>(`${this.API_URL}/auth/refresh`, { refreshToken })
      .pipe(
        tap(response => {
          if (response.success && response.data) {
            this.setSession(response.data);
          }
        })
      );
  }

  private setSession(authResult: AuthResponse): void {
    localStorage.setItem('accessToken', authResult.accessToken);
    localStorage.setItem('refreshToken', authResult.refreshToken);
    localStorage.setItem('expiresAt', (Date.now() + authResult.expiresIn * 1000).toString());
    
    // Get user info
    this.getCurrentUser().subscribe();
  }

  private clearSession(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('expiresAt');
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  private loadUserFromStorage(): void {
    const userData = localStorage.getItem('currentUser');
    if (userData && this.isLoggedIn()) {
      this.currentUserSubject.next(JSON.parse(userData));
    }
  }

  getCurrentUser(): Observable<any> {
    return this.http.get<any>(`${this.API_URL}/users/me`)
      .pipe(
        tap(response => {
          if (response.success && response.data) {
            localStorage.setItem('currentUser', JSON.stringify(response.data));
            this.currentUserSubject.next(response.data);
          }
        })
      );
  }

  getToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  isLoggedIn(): boolean {
    const expiresAt = localStorage.getItem('expiresAt');
    if (!expiresAt) return false;
    return Date.now() < parseInt(expiresAt);
  }

  isAdmin(): boolean {
    const user = this.currentUserSubject.value;
    return user?.role === 'ADMIN';
  }

  isUser(): boolean {
    const user = this.currentUserSubject.value;
    return user?.role === 'USER' || user?.role === 'ADMIN';
  }
}