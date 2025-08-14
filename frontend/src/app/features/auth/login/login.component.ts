import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  template: `
    <div class="login-container min-h-screen flex items-center justify-center px-4 sm:px-4 lg:px-4">
      <!-- 背景装饰元素 -->
      <div class="background-decoration">
        <div class="floating-shape shape-1"></div>
        <div class="floating-shape shape-2"></div>
        <div class="floating-shape shape-3"></div>
        <div class="floating-shape shape-4"></div>
      </div>

      <div class="max-w-md w-full space-y-8 relative z-10">
        <!-- 头部区域 -->
        <div class="text-center animate-fade-in">
          <h2 class="text-3xl font-bold text-white mb-8">
            登录
          </h2>
        </div>

        <!-- 登录卡片 -->
        <div class="login-card animate-slide-up">
          <div class="card-content">
            <form [formGroup]="loginForm" (ngSubmit)="login()" class="space-y-6">
              <!-- 用户名输入框 -->
              <div class="input-group">
                <mat-form-field appearance="outline" class="w-full">
                  <mat-label>用户名或邮箱</mat-label>
                  <input matInput formControlName="usernameOrEmail" required>
                  <mat-icon matSuffix class="input-icon">person</mat-icon>
                  <mat-error *ngIf="loginForm.get('usernameOrEmail')?.hasError('required')">
                    请输入用户名或邮箱
                  </mat-error>
                </mat-form-field>
              </div>

              <!-- 密码输入框 -->
              <div class="input-group">
                <mat-form-field appearance="outline" class="w-full">
                  <mat-label>密码</mat-label>
                  <input matInput [type]="hidePassword ? 'password' : 'text'" formControlName="password" required>
                  <button mat-icon-button matSuffix (click)="hidePassword = !hidePassword" type="button" class="password-toggle">
                    <mat-icon class="input-icon">{{hidePassword ? 'visibility_off' : 'visibility'}}</mat-icon>
                  </button>
                  <mat-error *ngIf="loginForm.get('password')?.hasError('required')">
                    请输入密码
                  </mat-error>
                </mat-form-field>
              </div>

              <!-- 忘记密码链接 -->
              <div class="flex items-center justify-end mb-4">
                <a href="#" class="forgot-password-link">
                  忘记密码？
                </a>
              </div>

              <!-- 登录按钮 -->
              <button 
                mat-raised-button 
                type="submit" 
                [disabled]="loginForm.invalid || isLoading"
                class="login-button">
                <mat-spinner diameter="20" class="mr-2" *ngIf="isLoading"></mat-spinner>
                <span>{{ isLoading ? '登录中...' : '登录' }}</span>
              </button>
            </form>

            <!-- 分割线 -->
            <div class="divider">
              <span>或</span>
            </div>

            <!-- 注册链接 -->
            <div class="text-center">
              <p class="register-text">
                还没有账户？
                <a routerLink="/auth/register" class="register-link">
                  立即注册
                </a>
              </p>
            </div>
          </div>
        </div>

        <!-- 底部条款 -->
        <div class="text-center animate-fade-in-delay">
          <p class="text-sm text-blue-100 opacity-80">
            登录即表示您同意我们的
            <a href="#" class="terms-link">服务条款</a>
            和
            <a href="#" class="terms-link">隐私政策</a>
          </p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .login-container {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
      background-size: 400% 400%;
      animation: gradientShift 15s ease infinite;
      position: relative;
      overflow: hidden;
    }

    /* 背景动画 */
    @keyframes gradientShift {
      0% { background-position: 0% 50%; }
      33% { background-position: 100% 50%; }
      66% { background-position: 0% 100%; }
      100% { background-position: 0% 50%; }
    }

    /* 浮动装饰元素 */
    .background-decoration {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      pointer-events: none;
      z-index: 1;
    }

    .floating-shape {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.15);
      animation: float 8s ease-in-out infinite;
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.2);
    }

    .shape-1 {
      width: 100px;
      height: 100px;
      top: 15%;
      left: 8%;
      animation-delay: 0s;
    }

    .shape-2 {
      width: 150px;
      height: 150px;
      top: 55%;
      right: 10%;
      animation-delay: 2.5s;
    }

    .shape-3 {
      width: 80px;
      height: 80px;
      bottom: 25%;
      left: 15%;
      animation-delay: 5s;
    }

    .shape-4 {
        width: 60px;
        height: 60px;
        top: 30%;
        right: 25%;
        animation-delay: 1.5s;
      }

      .shape-5 {
        width: 120px;
        height: 120px;
        bottom: 10%;
        right: 30%;
        animation-delay: 3.5s;
      }

    @keyframes float {
      0%, 100% { transform: translateY(0px) rotate(0deg) scale(1); }
      25% { transform: translateY(-15px) rotate(90deg) scale(1.1); }
      50% { transform: translateY(-30px) rotate(180deg) scale(0.9); }
      75% { transform: translateY(-15px) rotate(270deg) scale(1.05); }
    }

    /* Logo 样式 */
    .logo-container {
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .logo-icon {
      width: 60px;
      height: 60px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      backdrop-filter: blur(10px);
      border: 2px solid rgba(255, 255, 255, 0.3);
      transition: all 0.3s ease;
    }

    .logo-icon:hover {
      transform: scale(1.1);
      background: rgba(255, 255, 255, 0.3);
    }

    .logo-icon mat-icon {
      color: white;
    }

    /* 登录卡片样式 */
    .login-card {
      background: rgba(255, 255, 255, 0.98);
      backdrop-filter: blur(25px);
      border-radius: 24px;
      box-shadow: 
        0 20px 40px rgba(0, 0, 0, 0.12),
        0 8px 16px rgba(0, 0, 0, 0.08),
        inset 0 1px 0 rgba(255, 255, 255, 0.8);
      border: 1px solid rgba(255, 255, 255, 0.3);
      overflow: hidden;
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      animation: cardSlideIn 0.8s ease-out;
    }

    @keyframes cardSlideIn {
      0% {
        opacity: 0;
        transform: translateY(30px) scale(0.95);
      }
      100% {
        opacity: 1;
        transform: translateY(0) scale(1);
      }
    }

    .login-card:hover {
      transform: translateY(-5px) scale(1.02);
      box-shadow: 
        0 25px 50px rgba(0, 0, 0, 0.15),
        0 12px 20px rgba(0, 0, 0, 0.1),
        inset 0 1px 0 rgba(255, 255, 255, 0.9);
    }

    .card-content {
      padding: 32px 28px;
    }

    /* 输入框样式 */
    .input-group {
      margin-bottom: 18px;
    }

    .input-icon {
      color: #667eea !important;
      transition: all 0.3s ease;
    }

    ::ng-deep .mat-mdc-form-field:focus-within .input-icon {
      color: #4f46e5 !important;
      transform: scale(1.1);
    }

    .password-toggle {
      transition: all 0.3s ease;
    }

    .password-toggle:hover {
      background: rgba(102, 126, 234, 0.1);
    }

    /* 忘记密码链接 */
    .forgot-password-link {
      color: #667eea;
      text-decoration: none;
      font-size: 14px;
      font-weight: 500;
      transition: all 0.3s ease;
      position: relative;
    }

    .forgot-password-link:hover {
      color: #4f46e5;
      transform: translateY(-1px);
    }

    .forgot-password-link::after {
      content: '';
      position: absolute;
      width: 0;
      height: 2px;
      bottom: -2px;
      left: 0;
      background: #667eea;
      transition: width 0.3s ease;
    }

    .forgot-password-link:hover::after {
      width: 100%;
    }

    /* 登录按钮样式 */
    .login-button {
      width: 100%;
      height: 52px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      border-radius: 16px;
      color: white;
      font-size: 16px;
      font-weight: 700;
      letter-spacing: 0.8px;
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      position: relative;
      overflow: hidden;
      box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
    }

    .login-button:not(:disabled):hover {
      transform: translateY(-4px) scale(1.02);
      box-shadow: 0 15px 35px rgba(102, 126, 234, 0.5);
      background: linear-gradient(135deg, #5a67d8 0%, #6b46c1 100%);
    }

    .login-button:not(:disabled):active {
      transform: translateY(-2px) scale(1.01);
      box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
    }

    .login-button:disabled {
      opacity: 0.5;
      cursor: not-allowed;
      transform: none;
      box-shadow: 0 3px 10px rgba(102, 126, 234, 0.2);
    }

    .login-button::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
      transition: left 0.6s ease;
    }

    .login-button:hover::before {
      left: 100%;
    }

    /* 分割线样式 */
    .divider {
      display: flex;
      align-items: center;
      margin: 24px 0;
      color: #9ca3af;
      font-size: 14px;
    }

    .divider::before,
    .divider::after {
      content: '';
      flex: 1;
      height: 1px;
      background: linear-gradient(to right, transparent, #e5e7eb, transparent);
    }

    .divider span {
      padding: 0 16px;
      background: rgba(255, 255, 255, 0.95);
      font-weight: 500;
    }

    /* 注册链接样式 */
    .register-text {
      color: #6b7280;
      font-size: 14px;
      margin: 0;
    }

    .register-link {
      color: #667eea;
      text-decoration: none;
      font-weight: 600;
      transition: all 0.3s ease;
      position: relative;
    }

    .register-link:hover {
      color: #4f46e5;
      transform: translateY(-1px);
    }

    .register-link::after {
      content: '';
      position: absolute;
      width: 0;
      height: 2px;
      bottom: -2px;
      left: 0;
      background: #667eea;
      transition: width 0.3s ease;
    }

    .register-link:hover::after {
      width: 100%;
    }

    /* 条款链接样式 */
    .terms-link {
      color: rgba(255, 255, 255, 0.9);
      text-decoration: none;
      font-weight: 500;
      transition: all 0.3s ease;
      border-bottom: 1px solid transparent;
    }

    .terms-link:hover {
      color: white;
      border-bottom-color: rgba(255, 255, 255, 0.7);
    }

    /* 动画效果 */
    .animate-fade-in {
      animation: fadeIn 0.8s ease-out;
    }

    .animate-slide-up {
      animation: slideUp 0.8s ease-out 0.2s both;
    }

    .animate-fade-in-delay {
      animation: fadeIn 0.8s ease-out 0.4s both;
    }

    @keyframes fadeIn {
      from {
        opacity: 0;
        transform: translateY(20px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    @keyframes slideUp {
      from {
        opacity: 0;
        transform: translateY(40px) scale(0.95);
      }
      to {
        opacity: 1;
        transform: translateY(0) scale(1);
      }
    }

    /* 响应式设计 */
    @media (max-width: 640px) {
      .card-content {
        padding: 28px 20px;
      }

      .logo-icon {
        width: 50px;
        height: 50px;
      }

      .logo-icon mat-icon {
        font-size: 28px;
      }

      .floating-shape {
        display: none;
      }

      .login-button {
        height: 44px;
        font-size: 14px;
      }
    }

    /* Material Design 覆盖样式 */
    ::ng-deep .mat-mdc-form-field-outline {
      color: rgba(102, 126, 234, 0.3) !important;
    }

    ::ng-deep .mat-focused .mat-mdc-form-field-outline {
      color: #667eea !important;
    }

    ::ng-deep .mat-mdc-form-field-label {
      color: #6b7280 !important;
    }

    ::ng-deep .mat-focused .mat-mdc-form-field-label {
      color: #667eea !important;
    }
  `]
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  hidePassword = true;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.loginForm = this.fb.group({
      usernameOrEmail: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    // 检查用户登录状态，如果已登录则跳转到首页
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/home']);
    }
  }



  login(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      
      this.authService.login(this.loginForm.value).subscribe({
        next: (response) => {
          this.isLoading = false;
          
          if (response.success) {
            // 登录成功后立即获取用户信息
            this.authService.getCurrentUser().subscribe({
              next: (user) => {
                this.snackBar.open('登录成功！', '关闭', { duration: 3000 });
                this.router.navigate(['/home']);
              },
              error: (userError) => {
                this.snackBar.open('登录成功但获取用户信息失败', '关闭', { duration: 3000 });
                this.router.navigate(['/home']);
              }
            });
          } else {
            this.snackBar.open(response.message || '登录失败', '关闭', { duration: 3000 });
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.snackBar.open(error.error?.message || '登录失败，请稍后重试', '关闭', { duration: 3000 });
        }
      });
    }
  }
}