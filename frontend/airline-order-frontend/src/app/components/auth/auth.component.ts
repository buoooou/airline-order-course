import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.scss'
})
export class AuthComponent {
  isLoginMode = true;
  isLoading = false;
  error: string | null = null;
  
  // 登录表单数据
  loginForm = {
    username: '',
    password: ''
  };
  
  // 注册表单数据
  registerForm = {
    username: '',
    password: '',
    email: '',
    fullName: '',
    phoneNumber: ''
  };

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  // 切换登录/注册模式
  toggleMode(): void {
    this.isLoginMode = !this.isLoginMode;
    this.error = null;
  }

  // 提交表单
  onSubmit(): void {
    this.isLoading = true;
    this.error = null;
    
    if (this.isLoginMode) {
      // 登录处理
      this.authService.login(this.loginForm.username, this.loginForm.password)
        .subscribe({
          next: (response) => {
            this.isLoading = false;
            if (response.success) {
              this.router.navigate(['/']);
            } else {
              this.error = response.message || '登录失败，请检查用户名和密码';
            }
          },
          error: (err) => {
            this.isLoading = false;
            this.error = err.error?.message || '登录失败，请稍后再试';
          }
        });
    } else {
      // 注册处理
      this.authService.register(this.registerForm)
        .subscribe({
          next: (response) => {
            this.isLoading = false;
            if (response.success) {
              // 注册成功后自动切换到登录模式
              this.isLoginMode = true;
              this.loginForm.username = this.registerForm.username;
              this.loginForm.password = '';
              // 清空注册表单
              this.registerForm = {
                username: '',
                password: '',
                email: '',
                fullName: '',
                phoneNumber: ''
              };
            } else {
              this.error = response.message || '注册失败，请稍后再试';
            }
          },
          error: (err) => {
            this.isLoading = false;
            this.error = err.error?.message || '注册失败，请稍后再试';
          }
        });
    }
  }
}