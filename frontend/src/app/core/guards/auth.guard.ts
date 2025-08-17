// 路由守卫，用于检查用户是否已登录，未登录则重定向到登录页

import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
    constructor(private authService: AuthService, private router: Router) { }

    canActivate(): boolean {
        if (this.authService.isAuthenticated()) {
            return true;
        }

        // 未认证用户重定向到登录页
        this.router.navigate(['/login']);
        return false;
    }
}