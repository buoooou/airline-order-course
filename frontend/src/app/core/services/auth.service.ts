import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

interface AuthResponse {
    code: number;
    message: string;
    data: {
        token: string;
    }
}

@Injectable({ providedIn: 'root' })
export class AuthService {
    private readonly apiUrl = `${environment.apiUrl}/api/auth`;
    private token: string | null = null;
    private tokenKey = 'auth_token';

    constructor(private http: HttpClient, private router: Router) {
        // 从本地存储中恢复token
        const storedToken = localStorage.getItem(this.tokenKey);
        if (storedToken) {
            this.token = storedToken;
        }
    }

    login(username: string, password: string): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/login`, {
            username,
            password
        }).pipe(
            tap(response => {
                if (response.code === 200 && response.data?.token) {
                    this.token = response.data.token;
                    localStorage.setItem(this.tokenKey, this.token);
                }
            }),
            catchError(error => {
                return throwError(() => new Error(error.error?.message || '登录失败'));
            })
        );
    }

    logout(): void {
        this.token = '';
        localStorage.removeItem(this.tokenKey);
        this.router.navigate(['/login']);
    }

    getToken(): string | null {
        // 判断是否有token，如果没有，则从本地存储中获取
        if (!this.token) {
            this.token = localStorage.getItem(this.tokenKey);
        }
        return this.token;
    }

    isAuthenticated(): boolean {
        return !!this.token;
    }
}