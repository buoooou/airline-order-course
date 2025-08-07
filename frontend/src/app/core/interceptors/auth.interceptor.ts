import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth';

/**
 * HTTP认证拦截器
 * 自动为所有HTTP请求添加JWT token
 * 处理401未授权错误，自动跳转到登录页
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log('🔍 HTTP拦截器被调用:', request.url);
    
    // 获取当前存储的token
    const token = this.authService.getToken();
    console.log('🔑 获取到的token:', token ? '存在' : '不存在');
    
    // 如果有token，则添加到请求头中
    if (token) {
      request = request.clone({
        setHeaders: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      console.log('✅ 已添加Authorization头部到请求');
    } else {
      console.log('❌ 没有token，跳过添加Authorization头部');
    }

    // 继续处理请求，并捕获错误
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('❌ HTTP请求错误:', error.status, error.message);
        
        // 处理401未授权错误
        if (error.status === 401) {
          console.warn('认证失败，跳转到登录页');
          // 清除本地存储的认证信息
          this.authService.logout();
          // 跳转到登录页
          this.router.navigate(['/login']);
        }
        
        // 处理403禁止访问错误
        if (error.status === 403) {
          console.warn('权限不足，访问被拒绝');
        }

        // 处理500服务器错误
        if (error.status === 500) {
          console.error('服务器内部错误');
        }

        // 处理网络错误
        if (error.status === 0) {
          console.error('网络连接错误，请检查网络连接');
        }

        return throwError(() => error);
      })
    );
  }
}
