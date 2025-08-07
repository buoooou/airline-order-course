import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService) {}

  /**
   * HTTP请求拦截器
   * 自动为所有请求添加JWT token到Authorization头
   */
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // 获取当前的认证token
    const token = this.authService.getToken();

    // 如果存在token，则克隆请求并添加Authorization头
    if (token) {
      const authReq = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
      return next.handle(authReq);
    }

    // 如果没有token，直接传递原始请求
    return next.handle(req);
  }
}
