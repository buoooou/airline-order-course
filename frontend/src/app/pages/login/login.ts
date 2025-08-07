import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';

import { AuthService } from '../../core/services/auth';
import { LoginRequest } from '../../core/models/user.model';

@Component({
  selector: 'app-login',
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatIconModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  loginRequest: LoginRequest = {
    username: '',
    password: ''
  };
  
  isLoading = false;
  returnUrl = '/orders';

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {
    // 获取登录成功后要返回的URL
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/orders';
    
    // 如果已经登录，直接跳转
    if (this.authService.isLoggedIn()) {
      this.router.navigate([this.returnUrl]);
    }
  }

  /**
   * 用户登录
   */
  onLogin(): void {
    if (!this.loginRequest.username || !this.loginRequest.password) {
      this.snackBar.open('请输入用户名和密码', '关闭', {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'top'
      });
      return;
    }

    this.isLoading = true;
    
    this.authService.login(this.loginRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        if (response.success) {
          this.snackBar.open('登录成功！', '关闭', {
            duration: 2000,
            horizontalPosition: 'center',
            verticalPosition: 'top'
          });
          // 登录成功后跳转到目标页面
          this.router.navigate([this.returnUrl]);
        } else {
          this.snackBar.open(response.message || '登录失败', '关闭', {
            duration: 3000,
            horizontalPosition: 'center',
            verticalPosition: 'top'
          });
        }
      },
      error: (error) => {
        this.isLoading = false;
        console.error('登录错误:', error);
        this.snackBar.open('登录失败，请检查用户名和密码', '关闭', {
          duration: 3000,
          horizontalPosition: 'center',
          verticalPosition: 'top'
        });
      }
    });
  }

  /**
   * 处理回车键登录
   */
  onKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      this.onLogin();
    }
  }

  /**
   * 导航到注册页面
   */
  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}
