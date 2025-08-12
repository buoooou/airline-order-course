import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzMessageService } from 'ng-zorro-antd/message';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    NzFormModule,
    NzInputModule,
    NzButtonModule,
    NzIconModule,
    NzCardModule,
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  loading = false;
  passwordVisible = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private message: NzMessageService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.loading = true;
      // 使用 Angular 推荐的方式禁用/启用表单控件
      this.loginForm.disable();
      const credentials = this.loginForm.value;

      this.authService.login(credentials).subscribe({
        next: (response) => {
            this.loading = false;
            this.loginForm.enable();
            if (response.result === 'SUCCESS') {
              this.message.success('登录成功！');
              this.router.navigate(['/layout']);
            } else {
              this.message.error(response.message || '登录失败');
            }
          },
        error: (error) => {
          this.loading = false;
          this.loginForm.enable();
          this.message.error('登录失败，请检查用户名和密码');
          console.error('Login error:', error);
        }
      });
    }
  }

  // testMessage(): void {
  //   this.message.success('消息测试成功！');
  //   console.log('测试消息已发送');
  // }
}