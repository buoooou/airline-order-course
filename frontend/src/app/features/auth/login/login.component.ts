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
    <div class="login-container min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-md w-full space-y-8">
        <div class="text-center">
          <h2 class="mt-6 text-3xl font-extrabold text-gray-900">
            登录您的账户
          </h2>
          <p class="mt-2 text-sm text-gray-600">
            还没有账户？
            <a routerLink="/auth/register" class="font-medium text-indigo-600 hover:text-indigo-500">
              立即注册
            </a>
          </p>
        </div>

        <mat-card class="mt-8">
          <mat-card-content class="p-6">
            <form [formGroup]="loginForm" (ngSubmit)="login()" class="space-y-6">
              <mat-form-field appearance="outline" class="w-full">
                <mat-label>用户名或邮箱</mat-label>
                <input matInput formControlName="usernameOrEmail" required>
                <mat-icon matSuffix>person</mat-icon>
                <mat-error *ngIf="loginForm.get('usernameOrEmail')?.hasError('required')">
                  请输入用户名或邮箱
                </mat-error>
              </mat-form-field>

              <mat-form-field appearance="outline" class="w-full">
                <mat-label>密码</mat-label>
                <input matInput [type]="hidePassword ? 'password' : 'text'" formControlName="password" required>
                <button mat-icon-button matSuffix (click)="hidePassword = !hidePassword" type="button">
                  <mat-icon>{{hidePassword ? 'visibility_off' : 'visibility'}}</mat-icon>
                </button>
                <mat-error *ngIf="loginForm.get('password')?.hasError('required')">
                  请输入密码
                </mat-error>
              </mat-form-field>

              <div class="flex items-center justify-between">
                <a href="#" class="text-sm text-indigo-600 hover:text-indigo-500">
                  忘记密码？
                </a>
              </div>

              <button 
                mat-raised-button 
                color="primary" 
                type="submit" 
                [disabled]="loginForm.invalid || isLoading"
                class="w-full py-3">
                <mat-spinner diameter="20" class="mr-2" *ngIf="isLoading"></mat-spinner>
                {{ isLoading ? '登录中...' : '登录' }}
              </button>
            </form>
          </mat-card-content>
        </mat-card>

        <div class="text-center">
          <p class="text-sm text-gray-600">
            登录即表示您同意我们的
            <a href="#" class="font-medium text-indigo-600 hover:text-indigo-500">服务条款</a>
            和
            <a href="#" class="font-medium text-indigo-600 hover:text-indigo-500">隐私政策</a>
          </p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .login-container {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
    // If already logged in, redirect to home
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
            this.snackBar.open('登录成功！', '关闭', { duration: 3000 });
            this.router.navigate(['/home']);
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