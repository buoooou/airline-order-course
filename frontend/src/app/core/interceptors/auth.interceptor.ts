import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    console.log('AuthInterceptor:' + request.url);

    // 获取当前认证令牌
    const token = this.authService.getToken();
    console.log('Token:' + token ? '存在' : '不存在');

    // 如果令牌存在且请求不是登录请求，添加认证头
    if (token && !request.url.includes('/login')) {
      const authReq = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      });
      console.log('已添加Authorization到HEADER');
      return next.handle(authReq);
    }

    return next.handle(request);
  }
}
