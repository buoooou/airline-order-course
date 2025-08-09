import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService, LoginRequest } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="login-container">
      <div class="background-animation">
        <div class="floating-shapes">
          <div class="shape shape-1"></div>
          <div class="shape shape-2"></div>
          <div class="shape shape-3"></div>
        </div>
      </div>
      
      <div class="login-card">
        <div class="logo-section">
          <div class="logo-icon">🔐</div>
          <h2>航空订单管理系统</h2>
          <h3>用户登录</h3>
        </div>
        
        <form (ngSubmit)="onSubmit()" #loginForm="ngForm">
          <div class="form-group">
            <label for="username">用户名</label>
            <div class="input-wrapper">
              <span class="input-icon">👤</span>
              <input 
                type="text" 
                id="username" 
                name="username" 
                [(ngModel)]="loginRequest.username" 
                required
                class="form-control"
                placeholder="请输入用户名">
            </div>
          </div>
          
          <div class="form-group">
            <label for="password">密码</label>
            <div class="input-wrapper">
              <span class="input-icon">🔒</span>
              <input 
                type="password" 
                id="password" 
                name="password" 
                [(ngModel)]="loginRequest.password" 
                required
                class="form-control"
                placeholder="请输入密码">
            </div>
          </div>
          
          <div class="form-group">
            <button type="submit" [disabled]="!loginForm.valid || loading" class="btn btn-primary">
              <span class="btn-icon">{{ loading ? '⏳' : '🚀' }}</span>
              {{ loading ? '登录中...' : '登录' }}
            </button>
          </div>
          
          <div class="form-group">
            <a routerLink="/register" class="link">
              <span class="link-icon">📝</span>
              还没有账号？立即注册
            </a>
          </div>
        </form>
        
        <div *ngIf="error" class="alert alert-danger">
          <span class="alert-icon">⚠️</span>
          {{ error }}
        </div>
      </div>
    </div>
  `,
  styles: [`
    .login-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background: linear-gradient(135deg, #0f0f23 0%, #1a1a2e 50%, #16213e 100%);
      position: relative;
      overflow: hidden;
    }
    
    .background-animation {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      z-index: 1;
    }
    
    .floating-shapes {
      position: relative;
      width: 100%;
      height: 100%;
    }
    
    .shape {
      position: absolute;
      border-radius: 50%;
      background: linear-gradient(45deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
      animation: float 6s ease-in-out infinite;
    }
    
    .shape-1 {
      width: 60px;
      height: 60px;
      top: 20%;
      left: 10%;
      animation-delay: 0s;
    }
    
    .shape-2 {
      width: 80px;
      height: 80px;
      top: 60%;
      right: 15%;
      animation-delay: 2s;
    }
    
    .shape-3 {
      width: 40px;
      height: 40px;
      bottom: 30%;
      left: 20%;
      animation-delay: 4s;
    }
    
    @keyframes float {
      0%, 100% { transform: translateY(0px) rotate(0deg); }
      50% { transform: translateY(-20px) rotate(180deg); }
    }
    
    .login-card {
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(20px);
      border: 1px solid rgba(255, 255, 255, 0.2);
      padding: 2.5rem;
      border-radius: 20px;
      width: 100%;
      max-width: 450px;
      z-index: 2;
      position: relative;
      box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);
    }
    
    .logo-section {
      text-align: center;
      margin-bottom: 2rem;
    }
    
    .logo-icon {
      font-size: 3rem;
      margin-bottom: 1rem;
      animation: pulse 2s ease-in-out infinite;
    }
    
    @keyframes pulse {
      0%, 100% { transform: scale(1); }
      50% { transform: scale(1.1); }
    }
    
    h2 {
      text-align: center;
      color: #ffffff;
      margin-bottom: 0.5rem;
      font-size: 1.8rem;
      font-weight: 700;
      text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
    }
    
    h3 {
      text-align: center;
      color: #b8b8b8;
      margin-bottom: 2rem;
      font-size: 1.1rem;
      font-weight: 300;
    }
    
    .form-group {
      margin-bottom: 1.5rem;
    }
    
    label {
      display: block;
      margin-bottom: 0.5rem;
      color: #ffffff;
      font-weight: 500;
      font-size: 0.9rem;
    }
    
    .input-wrapper {
      position: relative;
      display: flex;
      align-items: center;
    }
    
    .input-icon {
      position: absolute;
      left: 1rem;
      font-size: 1.1rem;
      z-index: 2;
    }
    
    .form-control {
      width: 100%;
      padding: 0.75rem 1rem 0.75rem 3rem;
      border: 1px solid rgba(255, 255, 255, 0.3);
      border-radius: 15px;
      font-size: 1rem;
      background: rgba(255, 255, 255, 0.1);
      color: #ffffff;
      backdrop-filter: blur(10px);
      transition: all 0.3s ease;
    }
    
    .form-control::placeholder {
      color: rgba(255, 255, 255, 0.6);
    }
    
    .form-control:focus {
      outline: none;
      border-color: #667eea;
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2);
      background: rgba(255, 255, 255, 0.15);
    }
    
    .btn {
      width: 100%;
      padding: 0.75rem;
      border: none;
      border-radius: 15px;
      font-size: 1rem;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 0.5rem;
      backdrop-filter: blur(10px);
    }
    
    .btn-primary {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
      border: 1px solid rgba(255, 255, 255, 0.2);
    }
    
    .btn-primary:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 12px 30px rgba(102, 126, 234, 0.4);
    }
    
    .btn-primary:disabled {
      background: rgba(255, 255, 255, 0.2);
      color: rgba(255, 255, 255, 0.6);
      cursor: not-allowed;
      transform: none;
      box-shadow: none;
    }
    
    .btn-icon {
      font-size: 1.1rem;
    }
    
    .link {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 0.5rem;
      text-align: center;
      color: #b8b8b8;
      text-decoration: none;
      margin-top: 1rem;
      font-size: 0.9rem;
      transition: all 0.3s ease;
    }
    
    .link:hover {
      color: #ffffff;
      text-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
    }
    
    .link-icon {
      font-size: 1rem;
    }
    
    .alert {
      padding: 1rem;
      border-radius: 15px;
      margin-top: 1rem;
      display: flex;
      align-items: center;
      gap: 0.5rem;
      backdrop-filter: blur(10px);
    }
    
    .alert-danger {
      background: rgba(220, 53, 69, 0.2);
      color: #ff6b6b;
      border: 1px solid rgba(220, 53, 69, 0.3);
    }
    
    .alert-icon {
      font-size: 1.1rem;
    }
    
    @media (max-width: 768px) {
      .login-card {
        margin: 1rem;
        padding: 2rem;
        max-width: 400px;
      }
      
      h2 {
        font-size: 1.5rem;
      }
    }
  `]
})
export class LoginComponent {
  loginRequest: LoginRequest = {
    username: '',
    password: ''
  };
  
  loading = false;
  error = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    if (this.loginRequest.username && this.loginRequest.password) {
      this.loading = true;
      this.error = '';
      
      this.authService.login(this.loginRequest).subscribe({
        next: (response) => {
          this.loading = false;
          this.router.navigate(['/orders']);
        },
        error: (error) => {
          this.loading = false;
          this.error = error.error?.message || '登录失败，请检查用户名和密码';
        }
      });
    }
  }
} 