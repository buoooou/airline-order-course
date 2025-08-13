import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/authapi';

@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  
  loginForm: FormGroup;
  loginAttempts: number = 0;
  maxLoginAttempts: number = 5;
  errorMessage: string = '';

  constructor(
    public router: Router,
    private auth: AuthService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit(): void {
    this.login();
  }

  login(): void {
    if (this.loginAttempts >= this.maxLoginAttempts) {
      this.errorMessage = '登录尝试次数过多，请稍后再试。';
      return;
    }

    if (this.loginForm.invalid) {
      this.errorMessage = '请输入用户名和密码。';
      return;
    }

    const { username, password } = this.loginForm.value;
    this.auth.login(username, password)
      .then((response: any) => {
        localStorage.setItem('token', response.token ?? '');
        this.router.navigate(['/order-status']);
      })
      .catch((error: any) => {
        if (error.message) {
          this.errorMessage = error.message;
        } else if (error.status === 0) {
          this.errorMessage = '网络连接失败，请检查网络后重试';
        } else if (error.status === 500) {
          this.errorMessage = '服务器内部错误，请联系管理员';
        } else {
          this.errorMessage = '登录失败，请检查用户名和密码';
        }
        this.loginAttempts++;
        this.cdr.detectChanges(); // 强制触发变更检测
      })
  };
}

