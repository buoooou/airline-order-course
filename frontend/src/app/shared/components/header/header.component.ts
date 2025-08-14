import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService, User } from '../../../core/services/auth.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule
  ],
  template: `
    <mat-toolbar color="primary" class="shadow-lg">
      <div class="flex items-center justify-between w-full">
        <!-- Logo and Brand -->
        <div class="flex items-center space-x-4">
          <button mat-icon-button routerLink="/home">
            <mat-icon>flight</mat-icon>
          </button>
          <span class="text-xl font-bold cursor-pointer" routerLink="/home">
            天空机票
          </span>
        </div>

        <!-- Navigation -->
        <div class="hidden md:flex items-center space-x-4">
          <button mat-button routerLink="/home" routerLinkActive="active">
            <mat-icon>home</mat-icon>
            首页
          </button>
          <button mat-button routerLink="/flights" routerLinkActive="active">
            <mat-icon>search</mat-icon>
            搜索航班
          </button>
        </div>

        <!-- User Menu -->
        <div class="flex items-center space-x-2">
          <ng-container *ngIf="currentUser$ | async as user; else loginButton">
            <button mat-button [matMenuTriggerFor]="userMenu" class="flex items-center space-x-2">
              <mat-icon>account_circle</mat-icon>
              <span>{{ user.fullName || user.username }}</span>
              <mat-icon>arrow_drop_down</mat-icon>
            </button>
            <mat-menu #userMenu="matMenu">
              <button mat-menu-item routerLink="/profile">
                <mat-icon>person</mat-icon>
                个人中心
              </button>
              <button mat-menu-item routerLink="/profile/orders">
                <mat-icon>receipt</mat-icon>
                我的订单
              </button>
              <button mat-menu-item routerLink="/profile/passengers">
                <mat-icon>group</mat-icon>
                旅客管理
              </button>
              <mat-divider></mat-divider>
              <button mat-menu-item *ngIf="isAdmin()" routerLink="/admin">
                <mat-icon>admin_panel_settings</mat-icon>
                管理后台
              </button>
              <button mat-menu-item (click)="logout()">
                <mat-icon>logout</mat-icon>
                退出登录
              </button>
            </mat-menu>
          </ng-container>
          
          <ng-template #loginButton>
            <button mat-button routerLink="/auth/login">
              <mat-icon>login</mat-icon>
              登录
            </button>
            <button mat-raised-button color="accent" routerLink="/auth/register">
              注册
            </button>
          </ng-template>
        </div>
      </div>
    </mat-toolbar>
  `,
  styles: [`
    .active {
      background-color: rgba(255, 255, 255, 0.1);
    }
  `]
})
export class HeaderComponent implements OnInit {
  currentUser$: Observable<User | null>;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    this.currentUser$ = this.authService.currentUser$;
  }

  ngOnInit(): void {}

  logout(): void {
    this.authService.logout();
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }
}