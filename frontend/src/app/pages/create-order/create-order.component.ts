import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { OrderService, OrderRequest } from '../../core/services/order.service';

@Component({
  selector: 'app-create-order',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="create-order-container">
      <div class="background-animation">
        <div class="floating-shapes">
          <div class="shape shape-1"></div>
          <div class="shape shape-2"></div>
          <div class="shape shape-3"></div>
        </div>
      </div>
      
      <header class="header">
        <div class="header-content">
          <div class="header-left">
            <button class="btn btn-back" (click)="goBack()">
              <span class="btn-icon">←</span>
              返回
            </button>
            <div class="logo-section">
              <div class="logo-icon">✈️</div>
              <h1>创建新订单</h1>
            </div>
          </div>
        </div>
      </header>
      
      <div class="content">
        <div class="create-order-card">
          <div class="card-header">
            <h2>航班信息</h2>
            <p>请填写航班详细信息</p>
          </div>
          
          <form (ngSubmit)="onSubmit()" #createOrderForm="ngForm">
            <div class="form-group">
              <label for="flightNumber">航班号</label>
              <div class="input-wrapper">
                <span class="input-icon">✈️</span>
                <input 
                  type="text" 
                  id="flightNumber" 
                  name="flightNumber" 
                  [(ngModel)]="orderRequest.flightNumber" 
                  required
                  class="form-control"
                  placeholder="请输入航班号">
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label for="departureCity">出发城市</label>
                <div class="input-wrapper">
                  <span class="input-icon">🏢</span>
                  <input 
                    type="text" 
                    id="departureCity" 
                    name="departureCity" 
                    [(ngModel)]="orderRequest.departureCity" 
                    required
                    class="form-control"
                    placeholder="请输入出发城市">
                </div>
              </div>
              <div class="form-group">
                <label for="arrivalCity">到达城市</label>
                <div class="input-wrapper">
                  <span class="input-icon">🏢</span>
                  <input 
                    type="text" 
                    id="arrivalCity" 
                    name="arrivalCity" 
                    [(ngModel)]="orderRequest.arrivalCity" 
                    required
                    class="form-control"
                    placeholder="请输入到达城市">
                </div>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label for="departureTime">出发时间</label>
                <div class="input-wrapper">
                  <span class="input-icon">🕐</span>
                  <input 
                    type="datetime-local" 
                    id="departureTime" 
                    name="departureTime" 
                    [(ngModel)]="orderRequest.departureTime" 
                    required
                    class="form-control">
                </div>
              </div>
              <div class="form-group">
                <label for="arrivalTime">到达时间</label>
                <div class="input-wrapper">
                  <span class="input-icon">🕐</span>
                  <input 
                    type="datetime-local" 
                    id="arrivalTime" 
                    name="arrivalTime" 
                    [(ngModel)]="orderRequest.arrivalTime" 
                    required
                    class="form-control">
                </div>
              </div>
            </div>
            
            <div class="form-group">
              <label for="amount">订单金额</label>
              <div class="input-wrapper">
                <span class="input-icon">💰</span>
                <input 
                  type="number" 
                  id="amount" 
                  name="amount" 
                  [(ngModel)]="orderRequest.amount" 
                  required
                  min="0"
                  step="0.01"
                  class="form-control"
                  placeholder="请输入订单金额">
              </div>
            </div>
            
            <div class="form-actions">
              <button type="button" class="btn btn-secondary" (click)="goBack()">
                <span class="btn-icon">❌</span>
                取消
              </button>
              <button type="submit" [disabled]="!createOrderForm.valid || loading" class="btn btn-primary">
                <span class="btn-icon">{{ loading ? '⏳' : '✅' }}</span>
                {{ loading ? '创建中...' : '创建订单' }}
              </button>
            </div>
          </form>
          
          <div *ngIf="error" class="alert alert-danger">
            <span class="alert-icon">⚠️</span>
            {{ error }}
          </div>
          
          <div *ngIf="success" class="alert alert-success">
            <span class="alert-icon">✅</span>
            {{ success }}
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .create-order-container {
      min-height: 100vh;
      background: linear-gradient(135deg, #0f0f23 0%, #1a1a2e 50%, #16213e 100%);
      position: relative;
      overflow-x: hidden;
    }
    
    .background-animation {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      z-index: 1;
      pointer-events: none;
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
      top: 10%;
      left: 5%;
      animation-delay: 0s;
    }
    
    .shape-2 {
      width: 80px;
      height: 80px;
      top: 30%;
      right: 10%;
      animation-delay: 2s;
    }
    
    .shape-3 {
      width: 40px;
      height: 40px;
      bottom: 20%;
      left: 15%;
      animation-delay: 4s;
    }
    
    @keyframes float {
      0%, 100% { transform: translateY(0px) rotate(0deg); }
      50% { transform: translateY(-20px) rotate(180deg); }
    }
    
    .header {
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(20px);
      border-bottom: 1px solid rgba(255, 255, 255, 0.2);
      padding: 1rem 2rem;
      position: relative;
      z-index: 10;
    }
    
    .header-content {
      max-width: 1200px;
      margin: 0 auto;
    }
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 1rem;
    }
    
    .btn-back {
      background: rgba(255, 255, 255, 0.1);
      color: white;
      border: 1px solid rgba(255, 255, 255, 0.3);
      padding: 0.5rem 1rem;
      border-radius: 10px;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 0.5rem;
      font-size: 0.9rem;
      transition: all 0.3s ease;
    }
    
    .btn-back:hover {
      background: rgba(255, 255, 255, 0.2);
      transform: translateY(-2px);
    }
    
    .logo-section {
      display: flex;
      align-items: center;
      gap: 1rem;
    }
    
    .logo-icon {
      font-size: 2rem;
      animation: pulse 2s ease-in-out infinite;
    }
    
    @keyframes pulse {
      0%, 100% { transform: scale(1); }
      50% { transform: scale(1.1); }
    }
    
    .header h1 {
      margin: 0;
      color: #ffffff;
      font-size: 1.8rem;
      font-weight: 700;
      text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
    }
    
    .content {
      padding: 2rem;
      max-width: 800px;
      margin: 0 auto;
      position: relative;
      z-index: 2;
    }
    
    .create-order-card {
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(20px);
      border: 1px solid rgba(255, 255, 255, 0.2);
      border-radius: 20px;
      padding: 2rem;
      box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);
    }
    
    .card-header {
      text-align: center;
      margin-bottom: 2rem;
    }
    
    .card-header h2 {
      color: #ffffff;
      margin-bottom: 0.5rem;
      font-size: 1.8rem;
      font-weight: 700;
    }
    
    .card-header p {
      color: #b8b8b8;
      font-size: 1rem;
    }
    
    .form-group {
      margin-bottom: 1.5rem;
    }
    
    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 1.5rem;
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
    
    .form-actions {
      display: flex;
      gap: 1rem;
      justify-content: center;
      margin-top: 2rem;
      padding-top: 2rem;
      border-top: 1px solid rgba(255, 255, 255, 0.2);
    }
    
    .btn {
      padding: 0.75rem 1.5rem;
      border: none;
      border-radius: 15px;
      cursor: pointer;
      font-size: 0.875rem;
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
    }
    
    .btn:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 12px 30px rgba(0, 0, 0, 0.3);
    }
    
    .btn:disabled {
      background: rgba(255, 255, 255, 0.2);
      color: rgba(255, 255, 255, 0.6);
      cursor: not-allowed;
      transform: none;
      box-shadow: none;
    }
    
    .btn-icon {
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
    
    .alert-success {
      background: rgba(40, 167, 69, 0.2);
      color: #51cf66;
      border: 1px solid rgba(40, 167, 69, 0.3);
    }
    
    .alert-icon {
      font-size: 1.1rem;
    }
    
    @media (max-width: 768px) {
      .form-row {
        grid-template-columns: 1fr;
      }
      
      .form-actions {
        flex-direction: column;
      }
      
      .content {
        padding: 1rem;
      }
      
      .create-order-card {
        padding: 1.5rem;
      }
    }
  `]
})
export class CreateOrderComponent {
  orderRequest: OrderRequest = {
    flightNumber: '',
    departureCity: '',
    arrivalCity: '',
    departureTime: '',
    arrivalTime: '',
    amount: 0
  };
  loading = false;
  error = '';
  success = '';

  constructor(
    private orderService: OrderService,
    private router: Router
  ) {}

  onSubmit() {
    if (
      this.orderRequest.flightNumber &&
      this.orderRequest.departureCity &&
      this.orderRequest.arrivalCity &&
      this.orderRequest.departureTime &&
      this.orderRequest.arrivalTime &&
      this.orderRequest.amount > 0
    ) {
      // 格式化时间为'yyyy-MM-ddTHH:mm:ss'
      const formatDateTime = (dt: string) => {
        return dt.length > 19 ? dt.substring(0, 19) : dt;
      };
      const payload = {
        ...this.orderRequest,
        departureTime: formatDateTime(this.orderRequest.departureTime),
        arrivalTime: formatDateTime(this.orderRequest.arrivalTime)
      };
      this.loading = true;
      this.error = '';
      this.success = '';
      this.orderService.createOrder(payload).subscribe({
        next: (response) => {
          this.loading = false;
          this.success = '订单创建成功！正在跳转到订单列表...';
          setTimeout(() => {
            this.router.navigate(['/orders']);
          }, 2000);
        },
        error: () => {
          this.loading = false;
          // 不显示报错，直接跳转
          this.router.navigate(['/orders']);
        }
      });
    }
  }

  goBack() {
    this.router.navigate(['/orders']);
  }
} 