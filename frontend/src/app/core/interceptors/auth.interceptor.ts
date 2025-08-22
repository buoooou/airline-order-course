// 认证拦截器

import { Injectable } from '@angular/core';
import {
    HttpRequest,
    HttpHandler,
    HttpEvent,
    HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    constructor(private authService: AuthService) { }

    intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
        const token = this.authService.getToken();

        // 打印请求路径和token
        console.log('请求URL:', request.url, 'token:', token);

        if (token && !request.url.includes('auth/login')) {
            const authRequest = request.clone({
                // headers: request.headers.set('Authorization', `Bearer ${token}`)
                setHeaders: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            return next.handle(authRequest);
        }

        return next.handle(request);
    }
}