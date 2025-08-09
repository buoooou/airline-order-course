import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="home-container">
      <div class="background-animation">
        <div class="floating-shapes">
          <div class="shape shape-1"></div>
          <div class="shape shape-2"></div>
          <div class="shape shape-3"></div>
          <div class="shape shape-4"></div>
        </div>
      </div>
      
      <div class="welcome-card">
        <div class="logo-section">
          <div class="logo-icon">✈️</div>
          <h1>航空订单管理系统</h1>
          <p class="subtitle">智能航空订单管理平台</p>
        </div>
        
        <div class="features">
          <div class="feature-item">
            <div class="feature-icon">🚀</div>
            <span>快速订单处理</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon">🔒</div>
            <span>安全可靠</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon">📊</div>
            <span>实时数据</span>
          </div>
        </div>
        
        <div class="actions">
          <button class="btn btn-primary" (click)="goToLogin()">
            <span class="btn-icon">🔐</span>
            登录系统
          </button>
          <button class="btn btn-secondary" (click)="goToRegister()">
            <span class="btn-icon">📝</span>
            注册账号
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .home-container {
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
      width: 80px;
      height: 80px;
      top: 20%;
      left: 10%;
      animation-delay: 0s;
    }
    
    .shape-2 {
      width: 120px;
      height: 120px;
      top: 60%;
      right: 15%;
      animation-delay: 2s;
    }
    
    .shape-3 {
      width: 60px;
      height: 60px;
      bottom: 30%;
      left: 20%;
      animation-delay: 4s;
    }
    
    .shape-4 {
      width: 100px;
      height: 100px;
      top: 10%;
      right: 30%;
      animation-delay: 1s;
    }
    
    @keyframes float {
      0%, 100% { transform: translateY(0px) rotate(0deg); }
      50% { transform: translateY(-20px) rotate(180deg); }
    }
    
    .welcome-card {
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(20px);
      border: 1px solid rgba(255, 255, 255, 0.2);
      padding: 3rem;
      border-radius: 20px;
      text-align: center;
      max-width: 600px;
      z-index: 2;
      position: relative;
      box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);
    }
    
    .logo-section {
      margin-bottom: 2rem;
    }
    
    .logo-icon {
      font-size: 4rem;
      margin-bottom: 1rem;
      animation: pulse 2s ease-in-out infinite;
    }
    
    @keyframes pulse {
      0%, 100% { transform: scale(1); }
      50% { transform: scale(1.1); }
    }
    
    h1 {
      color: #ffffff;
      margin-bottom: 0.5rem;
      font-size: 2.5rem;
      font-weight: 700;
      text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
    }
    
    .subtitle {
      color: #b8b8b8;
      margin-bottom: 2rem;
      font-size: 1.2rem;
      font-weight: 300;
    }
    
    .features {
      display: flex;
      justify-content: center;
      gap: 2rem;
      margin-bottom: 2rem;
      flex-wrap: wrap;
    }
    
    .feature-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 0.5rem;
      color: #ffffff;
    }
    
    .feature-icon {
      font-size: 1.5rem;
      background: rgba(255, 255, 255, 0.1);
      padding: 0.5rem;
      border-radius: 50%;
      backdrop-filter: blur(10px);
    }
    
    .feature-item span {
      font-size: 0.9rem;
      font-weight: 500;
    }
    
    .actions {
      display: flex;
      gap: 1rem;
      justify-content: center;
      flex-wrap: wrap;
    }
    
    .btn {
      padding: 1rem 2rem;
      border: none;
      border-radius: 15px;
      cursor: pointer;
      font-size: 1rem;
      font-weight: 600;
      transition: all 0.3s ease;
      display: flex;
      align-items: center;
      gap: 0.5rem;
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.2);
    }
    
    .btn-primary {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
    }
    
    .btn-secondary {
      background: rgba(255, 255, 255, 0.1);
      color: white;
      border: 1px solid rgba(255, 255, 255, 0.3);
    }
    
    .btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 12px 30px rgba(0, 0, 0, 0.3);
    }
    
    .btn-icon {
      font-size: 1.1rem;
    }
    
    @media (max-width: 768px) {
      .welcome-card {
        margin: 1rem;
        padding: 2rem;
      }
      
      h1 {
        font-size: 2rem;
      }
      
      .features {
        gap: 1rem;
      }
      
      .actions {
        flex-direction: column;
      }
    }
  `]
})
export class HomeComponent {
  constructor(private router: Router) {}

  goToLogin() {
    this.router.navigate(['/login']);
  }

  goToRegister() {
    this.router.navigate(['/register']);
  }
} 