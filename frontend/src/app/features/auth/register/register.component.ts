import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule
  ],
  template: `
    <div class="register-container min-h-screen flex items-center justify-center py-16 px-4 sm:px-6 lg:px-8">
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
            注册
          </h2>
        </div>
        
        <!-- 注册卡片 -->
        <div class="register-card animate-slide-up">
          <div class="card-content">
            <form [formGroup]="registerForm" (ngSubmit)="onSubmit()" class="space-y-6">
            <mat-form-field appearance="outline" class="w-full">
              <mat-label>用户名</mat-label>
              <input matInput formControlName="username" required>
              <mat-error *ngIf="registerForm.get('username')?.hasError('required')">
                用户名不能为空
              </mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="w-full">
              <mat-label>邮箱</mat-label>
              <input matInput type="email" formControlName="email" required>
              <mat-error *ngIf="registerForm.get('email')?.hasError('required')">
                邮箱不能为空
              </mat-error>
              <mat-error *ngIf="registerForm.get('email')?.hasError('email')">
                请输入有效的邮箱地址
              </mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="w-full">
              <mat-label>全名</mat-label>
              <input matInput formControlName="fullName" required>
              <mat-error *ngIf="registerForm.get('fullName')?.hasError('required')">
                全名不能为空
              </mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="w-full">
              <mat-label>密码</mat-label>
              <input matInput [type]="hidePassword ? 'password' : 'text'" formControlName="password" required>
              <button mat-icon-button matSuffix (click)="hidePassword = !hidePassword" type="button">
                <mat-icon>{{hidePassword ? 'visibility_off' : 'visibility'}}</mat-icon>
              </button>
              <mat-error *ngIf="registerForm.get('password')?.hasError('required')">
                密码不能为空
              </mat-error>
              <mat-error *ngIf="registerForm.get('password')?.hasError('minlength')">
                密码至少需要6位
              </mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="w-full">
              <mat-label>确认密码</mat-label>
              <input matInput [type]="hideConfirmPassword ? 'password' : 'text'" formControlName="confirmPassword" required>
              <button mat-icon-button matSuffix (click)="hideConfirmPassword = !hideConfirmPassword" type="button">
                <mat-icon>{{hideConfirmPassword ? 'visibility_off' : 'visibility'}}</mat-icon>
              </button>
              <mat-error *ngIf="registerForm.get('confirmPassword')?.hasError('required')">
                请确认密码
              </mat-error>
              <mat-error *ngIf="registerForm.hasError('passwordMismatch')">
                两次输入的密码不一致
              </mat-error>
            </mat-form-field>

              <button 
                mat-raised-button 
                type="submit" 
                [disabled]="registerForm.invalid || isLoading"
                class="register-button">
                <span>{{ isLoading ? '注册中...' : '注册' }}</span>
              </button>
            </form>

            <!-- 分割线 -->
            <div class="divider">
              <span>或</span>
            </div>

            <!-- 登录链接 -->
            <div class="text-center">
              <p class="login-text">
                已有账户？
                <a routerLink="/auth/login" class="login-link">
                  立即登录
                </a>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .register-container {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%, #f093fb 100%);
      background-size: 400% 400%;
      animation: gradientShift 15s ease infinite;
      position: relative;
      overflow: hidden;
    }

    /* 背景动画 */
    @keyframes gradientShift {
      0% { background-position: 0% 50%; }
      50% { background-position: 100% 50%; }
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
      background: rgba(255, 255, 255, 0.1);
      animation: float 6s ease-in-out infinite;
    }

    .shape-1 {
      width: 80px;
      height: 80px;
      top: 10%;
      left: 10%;
      animation-delay: 0s;
    }

    .shape-2 {
      width: 120px;
      height: 120px;
      top: 20%;
      right: 15%;
      animation-delay: 2s;
    }

    .shape-3 {
      width: 60px;
      height: 60px;
      bottom: 30%;
      left: 20%;
      animation-delay: 4s;
    }

    .shape-4 {
      width: 100px;
      height: 100px;
      bottom: 10%;
      right: 10%;
      animation-delay: 1s;
    }

    @keyframes float {
      0%, 100% { transform: translateY(0px) rotate(0deg); }
      33% { transform: translateY(-20px) rotate(120deg); }
      66% { transform: translateY(10px) rotate(240deg); }
    }

    /* 注册卡片样式 */
    .register-card {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(20px);
      border-radius: 20px;
      box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1), 0 0 0 1px rgba(255, 255, 255, 0.2);
      border: 1px solid rgba(255, 255, 255, 0.2);
      overflow: hidden;
      transition: all 0.3s ease;
    }

    .register-card:hover {
      transform: translateY(-3px);
      box-shadow: 0 20px 45px rgba(0, 0, 0, 0.12), 0 0 0 1px rgba(255, 255, 255, 0.3);
    }

    .card-content {
      padding: 32px 28px;
    }

    /* 注册按钮样式 */
    .register-button {
      width: 100%;
      height: 48px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      border-radius: 12px;
      color: white;
      font-size: 15px;
      font-weight: 600;
      letter-spacing: 0.5px;
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;
    }

    .register-button:not(:disabled):hover {
      transform: translateY(-2px);
      box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
      background: linear-gradient(135deg, #5a67d8 0%, #6b46c1 100%);
    }

    .register-button:not(:disabled):active {
      transform: translateY(0);
    }

    .register-button:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .register-button::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
      transition: left 0.5s;
    }

    .register-button:hover::before {
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

    /* 登录链接样式 */
    .login-text {
      color: #6b7280;
      font-size: 14px;
      margin: 0;
    }

    .login-link {
      color: #667eea;
      text-decoration: none;
      font-weight: 600;
      transition: all 0.3s ease;
      position: relative;
    }

    .login-link:hover {
      color: #4f46e5;
      transform: translateY(-1px);
    }

    .login-link::after {
      content: '';
      position: absolute;
      width: 0;
      height: 2px;
      bottom: -2px;
      left: 0;
      background: #667eea;
      transition: width 0.3s ease;
    }

    .login-link:hover::after {
      width: 100%;
    }

    /* 动画效果 */
    .animate-fade-in {
      animation: fadeIn 0.8s ease-out;
    }

    .animate-slide-up {
      animation: slideUp 0.8s ease-out 0.2s both;
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

      .floating-shape {
        display: none;
      }

      .register-button {
        height: 44px;
        font-size: 14px;
      }
    }

    /* Material Design 覆盖样式 */
    ::ng-deep .mat-mdc-form-field {
      width: 100%;
    }

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
export class RegisterComponent {
  registerForm: FormGroup;
  hidePassword = true;
  hideConfirmPassword = true;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      fullName: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      return { passwordMismatch: true };
    }
    return null;
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.isLoading = true;
      const { confirmPassword, ...registerData } = this.registerForm.value;
      
      this.authService.register(registerData).subscribe({
        next: () => {
          this.snackBar.open('注册成功！正在跳转到登录页面...', '关闭', {
            duration: 3000,
            horizontalPosition: 'center',
            verticalPosition: 'top'
          });
          this.router.navigate(['/auth/login']);
        },
        error: (error) => {
          console.error('Registration failed:', error);
          const errorMessage = error.error?.message || error.message || '注册失败，请稍后重试';
          this.snackBar.open(errorMessage, '关闭', {
            duration: 5000,
            horizontalPosition: 'center',
            verticalPosition: 'top',
            panelClass: ['error-snackbar']
          });
          this.isLoading = false;
        }
      });
    }
  }
}