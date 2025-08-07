import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  /**
   * 管理员权限守卫：检查用户是否为管理员
   * 如果不是管理员，重定向到无权限页面或登录页面
   */
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    if (this.authService.isLoggedIn() && this.authService.isAdmin()) {
      return true;
    } else if (this.authService.isLoggedIn()) {
      // 已登录但不是管理员，显示无权限提示
      alert('您没有权限访问此页面');
      this.router.navigate(['/orders']);
      return false;
    } else {
      // 未登录，重定向到登录页面
      this.router.navigate(['/login'], { 
        queryParams: { returnUrl: state.url } 
      });
      return false;
    }
  }
}
