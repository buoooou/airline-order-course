import { Injectable } from '@angular/core';
import { HttpClient} from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private tokenKey = 'auth_token'; // 本地存储的token键名
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());

  constructor(private http: HttpClient) {}

  /**
   * 发送用户名和密码到后端
   */
login(credentials: { username: string, password: string }): Observable<any> {
  const url = `${environment.apiUrl}/api/auth/login`;
  return this.http.post<any>(url, credentials).pipe(
    map((response: any) => {
      const token = response.token; 
      if (token) {
        this.setToken(token);
      }
      return response;
    }),
    catchError(error => {
      console.error('登录失败:', error);
      return of(null);
    })
  );
}


  /**
   * 检查用户是否已认证
   */
  isAuthenticated(): boolean {
    return this.hasToken();
  }

  /**
   * 存储token到localStorage
   */
  private setToken(token: string) {
    localStorage.setItem(this.tokenKey, token);
  }

  /**
   * 检查是否存在token
   */
  private hasToken(): boolean {
    return !!localStorage.getItem(this.tokenKey);
  }

}