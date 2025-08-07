import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-register',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class Register implements OnInit {
  registerForm!: FormGroup;
  loading = false;
  hidePassword = true;
  hideConfirmPassword = true;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    // 如果已经登录，重定向到订单页面
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/orders']);
      return;
    }

    this.initializeForm();
  }

  /**
   * 初始化注册表单
   */
  private initializeForm(): void {
    this.registerForm = this.formBuilder.group({
      username: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(20),
        Validators.pattern(/^[a-zA-Z0-9_]+$/)
      ]],
      password: ['', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(20)
      ]],
      confirmPassword: ['', [Validators.required]],
      role: ['USER', [Validators.required]]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  /**
   * 密码匹配验证器
   */
  private passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }
    
    if (confirmPassword?.hasError('passwordMismatch')) {
      delete confirmPassword.errors!['passwordMismatch'];
      if (Object.keys(confirmPassword.errors!).length === 0) {
        confirmPassword.setErrors(null);
      }
    }
    
    return null;
  }

  /**
   * 获取表单控件的错误信息
   */
  getFieldError(fieldName: string): string {
    const field = this.registerForm.get(fieldName);
    if (field && field.errors && field.touched) {
      if (field.errors['required']) {
        return this.getFieldDisplayName(fieldName) + '不能为空';
      }
      if (field.errors['minlength']) {
        return this.getFieldDisplayName(fieldName) + '长度不能少于' + field.errors['minlength'].requiredLength + '位';
      }
      if (field.errors['maxlength']) {
        return this.getFieldDisplayName(fieldName) + '长度不能超过' + field.errors['maxlength'].requiredLength + '位';
      }
      if (field.errors['pattern']) {
        return '用户名只能包含字母、数字和下划线';
      }
      if (field.errors['passwordMismatch']) {
        return '两次输入的密码不一致';
      }
    }
    return '';
  }

  /**
   * 获取字段显示名称
   */
  private getFieldDisplayName(fieldName: string): string {
    const displayNames: { [key: string]: string } = {
      'username': '用户名',
      'password': '密码',
      'confirmPassword': '确认密码',
      'role': '用户角色'
    };
    return displayNames[fieldName] || fieldName;
  }

  /**
   * 检查字段是否有错误
   */
  hasFieldError(fieldName: string): boolean {
    const field = this.registerForm.get(fieldName);
    return !!(field && field.errors && field.touched);
  }

  /**
   * 提交注册表单
   */
  onSubmit(): void {
    if (this.registerForm.valid && !this.loading) {
      this.loading = true;
      
      const formData = this.registerForm.value;
      
      this.authService.register(formData.username, formData.password, formData.role).subscribe({
        next: (response) => {
          if (response.success) {
            this.showSuccess('注册成功！请登录您的账号。');
            // 延迟跳转到登录页面
            setTimeout(() => {
              this.router.navigate(['/login']);
            }, 1500);
          } else {
            this.showError('注册失败: ' + response.message);
            this.loading = false;
          }
        },
        error: (error) => {
          console.error('注册失败:', error);
          let errorMessage = '注册失败，请稍后重试';
          
          if (error.error && error.error.message) {
            errorMessage = error.error.message;
          } else if (error.message) {
            errorMessage = error.message;
          }
          
          this.showError(errorMessage);
          this.loading = false;
        }
      });
    } else {
      // 标记所有字段为已触摸，以显示验证错误
      Object.keys(this.registerForm.controls).forEach(key => {
        this.registerForm.get(key)?.markAsTouched();
      });
    }
  }

  /**
   * 导航到登录页面
   */
  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }

  /**
   * 显示成功消息
   */
  private showSuccess(message: string): void {
    this.snackBar.open(message, '关闭', {
      duration: 3000,
      panelClass: ['success-snackbar']
    });
  }

  /**
   * 显示错误消息
   */
  private showError(message: string): void {
    this.snackBar.open(message, '关闭', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
  }
}
