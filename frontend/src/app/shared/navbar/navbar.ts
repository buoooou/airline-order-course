import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, NavigationEnd } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatBadgeModule } from '@angular/material/badge';
import { MatDividerModule } from '@angular/material/divider';

import { AuthService } from '../../core/services/auth';
import { User } from '../../core/models/user.model';

@Component({
  selector: 'app-navbar',
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatBadgeModule,
    MatDividerModule
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss'
})
export class Navbar implements OnInit, OnDestroy {
  currentUser: User | null = null;
  currentRoute: string = '';
  private userSubscription?: Subscription;
  private routerSubscription?: Subscription;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // 订阅当前用户状态变化
    this.userSubscription = this.authService.currentUser$.subscribe(
      user => this.currentUser = user
    );

    // 订阅路由变化
    this.routerSubscription = this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.currentRoute = event.url;
      });

    // 初始化当前路由
    this.currentRoute = this.router.url;
  }

  ngOnDestroy(): void {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
  }

  /**
   * 检查是否已登录
   */
  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  /**
   * 检查是否在登录页面
   */
  isOnLoginPage(): boolean {
    return this.currentRoute === '/login';
  }

  /**
   * 检查是否在注册页面
   */
  isOnRegisterPage(): boolean {
    return this.currentRoute === '/register';
  }

  /**
   * 检查是否在认证相关页面（登录或注册）
   */
  isOnAuthPage(): boolean {
    return this.isOnLoginPage() || this.isOnRegisterPage();
  }

  /**
   * 检查是否为管理员
   */
  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  /**
   * 用户登出
   */
  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  /**
   * 导航到订单列表
   */
  navigateToOrders(): void {
    this.router.navigate(['/orders']);
  }

  /**
   * 导航到航班管理（仅管理员）
   */
  navigateToFlights(): void {
    if (this.isAdmin()) {
      this.router.navigate(['/flights']);
    }
  }

  /**
   * 导航到定时任务管理（仅管理员）
   */
  navigateToScheduledTasks(): void {
    if (this.isAdmin()) {
      this.router.navigate(['/scheduled-tasks']);
    }
  }

  /**
   * 导航到登录页面
   */
  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }

  /**
   * 导航到注册页面
   */
  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}
