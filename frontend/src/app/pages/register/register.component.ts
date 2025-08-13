import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzMessageService } from 'ng-zorro-antd/message';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest } from '../../models/user.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NzFormModule,
    NzInputModule,
    NzButtonModule,
    NzCardModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  registerRequest: RegisterRequest = {
    username: '',
    email: '',
    password: '',
    role: 'USER'
  };
  confirmPassword = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private message: NzMessageService
  ) {}

  onSubmit() {
    if (!this.registerRequest.username || !this.registerRequest.email || !this.registerRequest.password) {
      this.message.error('请填写所有必填字段');
      return;
    }

    if (this.registerRequest.password !== this.confirmPassword) {
      this.message.error('两次输入的密码不一致');
      return;
    }

    if (this.registerRequest.password.length < 6) {
      this.message.error('密码长度至少为6位');
      return;
    }

    this.loading = true;
    this.authService.register(this.registerRequest).subscribe({
      next: (response: any) => {
        this.message.success('注册成功，即将跳转到订单页面');
        this.router.navigate(['/orders']);
      },
      error: (error: any) => {
        this.message.error('注册失败：' + (error.error?.message || '注册信息有误'));
        this.loading = false;
      }
    });
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
