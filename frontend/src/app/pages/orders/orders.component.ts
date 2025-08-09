import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { OrderService, Order, OrderStatus } from '../../core/services/order.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="orders-container">
      <div class="background-animation">
        <div class="floating-shapes">
          <div class="shape shape-1"></div>
          <div class="shape shape-2"></div>
          <div class="shape shape-3"></div>
          <div class="shape shape-4"></div>
        </div>
      </div>
      
      <header class="header">
        <div class="header-content">
          <div class="header-left">
            <div class="logo-section">
              <div class="logo-icon">✈️</div>
              <h1>我的订单</h1>
            </div>
          </div>
          <div class="header-actions">
            <button class="btn btn-primary" (click)="createOrder()">
              <span class="btn-icon">➕</span>
              创建订单
            </button>
            <button class="btn btn-secondary" (click)="logout()">
              <span class="btn-icon">🚪</span>
              退出登录
            </button>
          </div>
        </div>
      </header>
      
      <div class="content">
        <div *ngIf="loading" class="loading">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>
        
        <div *ngIf="error" class="alert alert-danger">
          <span class="alert-icon">⚠️</span>
          {{ error }}
        </div>
        
        <div *ngIf="!loading && !error" class="orders-list">
          <div *ngIf="orders.length === 0" class="empty-state">
            <div class="empty-icon">📋</div>
            <h3>暂无订单</h3>
            <p>开始创建您的第一个航空订单吧！</p>
            <button class="btn btn-primary" (click)="createOrder()">
              <span class="btn-icon">✈️</span>
              创建第一个订单
            </button>
          </div>
          
          <div *ngFor="let order of orders" class="order-card">
            <div class="order-header">
              <div class="order-title">
                <h3>订单号: {{ order.orderNumber }}</h3>
                <div class="order-meta">
                  <span class="meta-item">
                    <span class="meta-icon">📅</span>
                    {{ formatDate(order.creationDate) }}
                  </span>
                </div>
              </div>
              <span class="status-badge status-{{ getStatusColor(order.status) }}">
                <span class="status-icon">{{ getStatusIcon(order.status) }}</span>
                {{ getStatusDisplayName(order.status) }}
              </span>
            </div>
            
            <div class="order-details">
              <div class="detail-grid">
                <div class="detail-item">
                  <span class="label">航班号</span>
                  <span class="value">{{ order.flightNumber }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">出发城市</span>
                  <span class="value">{{ order.departureCity }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">到达城市</span>
                  <span class="value">{{ order.arrivalCity }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">出发时间</span>
                  <span class="value">{{ formatDate(order.departureTime) }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">到达时间</span>
                  <span class="value">{{ formatDate(order.arrivalTime) }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">金额</span>
                  <span class="value amount">¥{{ order.amount }}</span>
                </div>
              </div>
            </div>
            
            <div class="order-actions">
              <button class="btn btn-sm btn-info" (click)="viewOrder(order.id)">
                <span class="btn-icon">👁️</span>
                查看详情
              </button>
              
              <button 
                *ngIf="order.status === OrderStatus.PENDING_PAYMENT" 
                class="btn btn-sm btn-success" 
                (click)="payOrder(order.id)">
                <span class="btn-icon">💳</span>
                立即支付
              </button>
              
              <button 
                *ngIf="order.status === OrderStatus.TICKETING_FAILED" 
                class="btn btn-sm btn-warning" 
                (click)="retryTicketing(order.id)">
                <span class="btn-icon">🔄</span>
                重试出票
              </button>
              
              <button 
                *ngIf="canCancel(order.status)" 
                class="btn btn-sm btn-danger" 
                (click)="cancelOrder(order.id)">
                <span class="btn-icon">❌</span>
                取消订单
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .orders-container {
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
    
    .shape-4 {
      width: 70px;
      height: 70px;
      bottom: 40%;
      right: 5%;
      animation-delay: 1s;
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
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .header-left {
      display: flex;
      align-items: center;
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
    
    .header-actions {
      display: flex;
      gap: 1rem;
    }
    
    .content {
      padding: 2rem;
      max-width: 1200px;
      margin: 0 auto;
      position: relative;
      z-index: 2;
    }
    
    .loading {
      text-align: center;
      padding: 3rem;
      color: #ffffff;
    }
    
    .loading-spinner {
      width: 40px;
      height: 40px;
      border: 3px solid rgba(255, 255, 255, 0.3);
      border-top: 3px solid #667eea;
      border-radius: 50%;
      animation: spin 1s linear infinite;
      margin: 0 auto 1rem;
    }
    
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
    
    .alert {
      padding: 1rem;
      border-radius: 15px;
      margin-bottom: 1rem;
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
    
    .orders-list {
      display: grid;
      gap: 1.5rem;
    }
    
    .empty-state {
      text-align: center;
      padding: 4rem 2rem;
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(20px);
      border-radius: 20px;
      border: 1px solid rgba(255, 255, 255, 0.2);
      box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);
    }
    
    .empty-icon {
      font-size: 4rem;
      margin-bottom: 1rem;
      opacity: 0.7;
    }
    
    .empty-state h3 {
      color: #ffffff;
      margin-bottom: 0.5rem;
      font-size: 1.5rem;
    }
    
    .empty-state p {
      color: #b8b8b8;
      margin-bottom: 2rem;
      font-size: 1.1rem;
    }
    
    .order-card {
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(20px);
      border: 1px solid rgba(255, 255, 255, 0.2);
      border-radius: 20px;
      padding: 1.5rem;
      box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
      transition: all 0.3s ease;
    }
    
    .order-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
    }
    
    .order-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 1.5rem;
    }
    
    .order-title h3 {
      margin: 0 0 0.5rem 0;
      color: #ffffff;
      font-size: 1.2rem;
      font-weight: 600;
    }
    
    .order-meta {
      display: flex;
      gap: 1rem;
    }
    
    .meta-item {
      display: flex;
      align-items: center;
      gap: 0.25rem;
      color: #b8b8b8;
      font-size: 0.85rem;
    }
    
    .meta-icon {
      font-size: 0.9rem;
    }
    
    .status-badge {
      padding: 0.5rem 1rem;
      border-radius: 20px;
      font-size: 0.875rem;
      font-weight: 600;
      display: flex;
      align-items: center;
      gap: 0.5rem;
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.2);
    }
    
    .status-warning { 
      background: rgba(255, 193, 7, 0.2); 
      color: #ffd43b; 
    }
    .status-info { 
      background: rgba(23, 162, 184, 0.2); 
      color: #74c0fc; 
    }
    .status-primary { 
      background: rgba(102, 126, 234, 0.2); 
      color: #a5b4fc; 
    }
    .status-danger { 
      background: rgba(220, 53, 69, 0.2); 
      color: #ff6b6b; 
    }
    .status-success { 
      background: rgba(40, 167, 69, 0.2); 
      color: #51cf66; 
    }
    .status-secondary { 
      background: rgba(108, 117, 125, 0.2); 
      color: #adb5bd; 
    }
    
    .status-icon {
      font-size: 1rem;
    }
    
    .order-details {
      margin-bottom: 1.5rem;
    }
    
    .detail-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 1rem;
    }
    
    .detail-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.75rem;
      background: rgba(255, 255, 255, 0.05);
      border-radius: 10px;
      border: 1px solid rgba(255, 255, 255, 0.1);
    }
    
    .label {
      color: #b8b8b8;
      font-weight: 500;
      font-size: 0.9rem;
    }
    
    .value {
      color: #ffffff;
      font-weight: 600;
    }
    
    .amount {
      color: #51cf66;
      font-weight: 700;
    }
    
    .order-actions {
      display: flex;
      gap: 0.75rem;
      flex-wrap: wrap;
    }
    
    .btn {
      padding: 0.5rem 1rem;
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
    
    .btn-sm {
      padding: 0.4rem 0.8rem;
      font-size: 0.8rem;
    }
    
    .btn-primary { 
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
    }
    .btn-secondary { 
      background: rgba(255, 255, 255, 0.1);
      color: white;
    }
    .btn-success { 
      background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
      color: white;
      box-shadow: 0 4px 15px rgba(40, 167, 69, 0.3);
    }
    .btn-info { 
      background: linear-gradient(135deg, #17a2b8 0%, #6f42c1 100%);
      color: white;
      box-shadow: 0 4px 15px rgba(23, 162, 184, 0.3);
    }
    .btn-warning { 
      background: linear-gradient(135deg, #ffc107 0%, #fd7e14 100%);
      color: #212529;
      box-shadow: 0 4px 15px rgba(255, 193, 7, 0.3);
    }
    .btn-danger { 
      background: linear-gradient(135deg, #dc3545 0%, #e83e8c 100%);
      color: white;
      box-shadow: 0 4px 15px rgba(220, 53, 69, 0.3);
    }
    
    .btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
    }
    
    .btn-icon {
      font-size: 0.9rem;
    }
    
    @media (max-width: 768px) {
      .header-content {
        flex-direction: column;
        gap: 1rem;
      }
      
      .header-actions {
        width: 100%;
        justify-content: center;
      }
      
      .content {
        padding: 1rem;
      }
      
      .detail-grid {
        grid-template-columns: 1fr;
      }
      
      .order-actions {
        justify-content: center;
      }
    }
  `]
})
export class OrdersComponent implements OnInit {
  orders: Order[] = [];
  loading = false;
  error = '';
  OrderStatus = OrderStatus; // 在组件中暴露枚举

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.loading = true;
    this.error = '';
    console.log('[OrdersComponent] 开始加载订单');
    this.orderService.getUserOrders().subscribe({
      next: (response) => {
        this.loading = false;
        this.orders = Array.isArray(response.data) ? response.data : [];
        console.log('[OrdersComponent] 订单加载成功:', this.orders);
        // 添加调试信息，显示每个订单的状态
        this.orders.forEach(order => {
          console.log(`[OrdersComponent] 订单ID: ${order.id}, 状态: ${order.status}, 订单号: ${order.orderNumber}`);
        });
      },
      error: (error) => {
        this.loading = false;
        this.error = error.error?.message || '加载订单失败';
        console.error('[OrdersComponent] 订单加载失败:', error);
      }
    });
  }

  viewOrder(orderId: number) {
    this.router.navigate(['/orders', orderId]);
  }

  payOrder(orderId: number) {
    console.log('[OrdersComponent] 开始支付订单:', orderId);
    // 找到对应的订单并显示其状态
    const order = this.orders.find(o => o.id === orderId);
    if (order) {
      console.log('[OrdersComponent] 订单状态:', order.status);
      console.log('[OrdersComponent] 订单详情:', order);
    }
    this.orderService.payOrder(orderId).subscribe({
      next: (response) => {
        console.log('[OrdersComponent] 支付成功:', response);
        this.loadOrders();
      },
      error: (error) => {
        console.error('[OrdersComponent] 支付失败:', error);
        console.error('[OrdersComponent] 支付失败详情:', {
          status: error.status,
          statusText: error.statusText,
          error: error.error,
          message: error.error?.message
        });
        this.error = error.error?.message || '支付失败';
      }
    });
  }

  cancelOrder(orderId: number) {
    if (confirm('确定要取消这个订单吗？')) {
      this.orderService.cancelOrder(orderId).subscribe({
        next: () => {
          this.loadOrders();
        },
        error: (error) => {
          this.error = error.error?.message || '取消订单失败';
        }
      });
    }
  }

  retryTicketing(orderId: number) {
    this.orderService.retryTicketing(orderId).subscribe({
      next: () => {
        this.loadOrders();
      },
      error: (error) => {
        this.error = error.error?.message || '重试出票失败';
      }
    });
  }

  createOrder() {
    this.router.navigate(['/orders/create']);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getStatusDisplayName(status: OrderStatus): string {
    return this.orderService.getStatusDisplayName(status);
  }

  getStatusColor(status: OrderStatus): string {
    return this.orderService.getStatusColor(status);
  }

  getStatusIcon(status: OrderStatus): string {
    const icons: { [key: string]: string } = {
      'PENDING_PAYMENT': '💳',
      'PAID': '✅',
      'TICKETING': '🎫',
      'TICKETED': '✈️',
      'TICKETING_FAILED': '❌',
      'CANCELLED': '🚫'
    };
    return icons[status] || '📋';
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleString('zh-CN');
  }

  canCancel(status: OrderStatus): boolean {
    return status !== OrderStatus.TICKETED && status !== OrderStatus.CANCELLED;
  }
} 