import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { toNumber } from 'ng-zorro-antd/core/util';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  // 注意：在实际部署时，这里应该是完整的后端API地址
  // 在本地开发时，可以通过proxy.conf.json配置代理来避免跨域
  private readonly USERID = 'userid';
  private readonly USERNAME = 'username';
  private readonly ROLE = 'role';
  private readonly TOKEN_KEY = 'auth_token';

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: { username: string; password: string }): Observable<{
    success: boolean;
    code: number;
    message: string;
    data: { id: number; username: string; role: string; token: string };
  }> {
    return this.http
      .post<{
        success: boolean;
        code: number;
        message: string;
        data: { id: number; username: string; role: string; token: string };
      }>(`/api/login`, credentials)
      .pipe(
        tap((response) => {
          localStorage.setItem(this.USERID, String(response.data.id));
          localStorage.setItem(this.USERNAME, response.data.username);
          localStorage.setItem(this.ROLE, response.data.role);
          localStorage.setItem(this.TOKEN_KEY, response.data.token);
        })
      );
  }

  logout(): void {
    localStorage.removeItem(this.USERID);
    localStorage.removeItem(this.USERNAME);
    localStorage.removeItem(this.ROLE);
    localStorage.removeItem(this.TOKEN_KEY);
    this.router.navigate(['/login']);
  }

  getUserInfo(): { userid: string; username: string; role: string } | null {
    const userid = localStorage.getItem(this.USERID);
    const username = localStorage.getItem(this.USERNAME);
    const role = localStorage.getItem(this.ROLE);
    if (userid === null || username === null || role === null) {
      return null;
    }
    return { userid, username, role };
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
