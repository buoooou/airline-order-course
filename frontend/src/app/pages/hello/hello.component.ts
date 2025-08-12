import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

// NG-ZORRO 组件
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzTypographyModule } from 'ng-zorro-antd/typography';

@Component({
  selector: 'app-hello',
  standalone: true,
  imports: [
    CommonModule,
    NzButtonModule,
    NzCardModule,
    NzIconModule,
    NzTypographyModule
  ],
  template: `
    <div class="hello-container">
      <nz-card class="hello-card">
        <div class="hello-content">
          <h1 nz-typography>Hello, {{ username }}!</h1>
          <p nz-typography>欢迎使用航空订单管理系统</p>
          <p nz-typography>登录成功，欢迎开始您的旅程！</p>
          
          <div class="hello-actions">
            <button 
              nz-button 
              nzType="primary" 
              (click)="refreshPage()"
              class="action-button"
            >
              刷新页面
            </button>
            
            <button 
              nz-button 
              (click)="logout()"
              class="action-button"
            >
              退出登录
            </button>
          </div>
        </div>
      </nz-card>
    </div>
  `,
  styles: [`
    .hello-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 20px;
    }

    .hello-card {
      width: 100%;
      max-width: 500px;
      text-align: center;
      border-radius: 12px;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    }

    .hello-content {
      padding: 40px 20px;
    }

    .hello-icon {
      font-size: 80px;
      margin-bottom: 20px;
      color: #1890ff;
    }

    h1 {
      font-size: 36px;
      margin-bottom: 16px;
      color: #333;
    }

    p {
      font-size: 18px;
      color: #666;
      margin-bottom: 8px;
    }

    .hello-actions {
      margin-top: 40px;
      display: flex;
      gap: 16px;
      justify-content: center;
      flex-wrap: wrap;
    }

    .action-button {
      min-width: 140px;
    }

    @media (max-width: 480px) {
      .hello-content {
        padding: 30px 15px;
      }
      
      .hello-icon {
        font-size: 60px;
      }
      
      h1 {
        font-size: 28px;
      }
      
      p {
        font-size: 16px;
      }
    }
  `]
})
export class HelloComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  get username(): string {
    return this.authService.getUsername() || '用户';
  }

  refreshPage(): void {
    window.location.reload();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}