import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { NzMessageService } from 'ng-zorro-antd/message';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const httpInterceptor: HttpInterceptorFn = (req, next) => {
  const message = inject(NzMessageService);
  const authService = inject(AuthService);
  
  // 添加通用请求头
  const token = authService.getToken();
  let headers = req.headers;
  
  if (token) {
    headers = headers.set('Authorization', `Bearer ${token}`);
  }
  
  if (!headers.has('Content-Type') && !(req.body instanceof FormData)) {
    headers = headers.set('Content-Type', 'application/json');
  }

  const authReq = req.clone({ headers });

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = '未知错误';
      
      if (error.error instanceof ErrorEvent) {
        // 客户端错误
        errorMessage = `错误: ${error.error.message}`;
      } else {
        // 服务器错误
        switch (error.status) {
          case 400:
            errorMessage = '请求参数错误';
            break;
          case 401:
            errorMessage = '未授权，请重新登录';
            // 可以在这里处理token过期，跳转到登录页
            break;
          case 403:
            errorMessage = '权限不足';
            break;
          case 404:
            errorMessage = '请求的资源不存在';
            break;
          case 500:
            errorMessage = '服务器内部错误';
            break;
          case 503:
            errorMessage = '服务暂时不可用';
            break;
          default:
            errorMessage = `服务器错误: ${error.status}`;
        }
      }
      
      message.error(errorMessage);
      return throwError(() => error);
    })
  );
};