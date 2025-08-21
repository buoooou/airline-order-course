import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzMessageComponent, NzMessageService } from 'ng-zorro-antd/message';
import { AuthService } from '../../core/services/auth.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NzFormModule, NzInputModule, NzButtonModule, NzIconModule, NzCardModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  loginForm: FormGroup;
  isLoading = false;

  constructor (
    private authService: AuthService,
    private fb: FormBuilder,
    private message: NzMessageService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['admin', [Validators.required, Validators.minLength(4)]],
      password: ['123456', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      const credentials = {
        username: this.loginForm.value.username,
        password: this.loginForm.value.password
      };
      this.authService.login(credentials).pipe(
        finalize(() => this.isLoading = false)
      ).subscribe({
        next: (token) => {
          console.log('登录成功！令牌:' + token);
          this.message.success('登录成功！令牌:' + token);
          this.router.navigate(['/orders']);
        },
        error: (err) => {
          console.log('登录失败: 用户名或密码错误！  用户名:' + this.loginForm.value.username + ', 密码:' + this.loginForm.value.password);
          this.message.error('登录失败: 用户名或密码错误！');
        }
      });
    } else {
      Object.values(this.loginForm.controls).forEach(control => {
        control.markAsDirty();
        control.updateValueAndValidity();
      });

      this.message.error('请填写正确的用户名和密码');
      return;
    }
    
  }
}
