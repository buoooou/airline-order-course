import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  imports: [FormsModule],
  standalone: true
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  onSubmit() {
    console.log('尝试登录，用户名:', this.username);
    console.log('请求URL:', `${environment.apiUrl}/auth/login`);
    console.log('请求参数:', { username: this.username, password: this.password });

    this.http.post(`${environment.apiUrl}/auth/login`, {
      username: this.username,
      password: this.password
    }).subscribe({
      next: (response: any) => {
        console.log('登录成功', response);
        if (typeof window !== 'undefined' && window.localStorage) {
          localStorage.setItem('token', response.token);
        }
        this.router.navigate(['/orders']);
      },
      error: (error) => {
        console.error('登录失败，详细错误信息:', error);
        console.error('错误状态码:', error.status);
        console.error('错误响应:', error.error);
        alert('登录失败，请检查用户名和密码或联系管理员');
      }
    });
  }
}