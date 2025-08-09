import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService, Order, OrderStatus } from '../../core/services/order.service';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="order-detail-container">
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
            <button class="btn btn-back" (click)="goBack()">
              <span class="btn-icon">←</span>
              返回
            </button>
            <div class="logo-section">
              <div class="logo-icon">📋</div>
              <h1>订单详情</h1>
            </div>
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
        
        <div *ngIf="!loading && !error && order" class="order-detail-card">
          <div class="order-header">
            <div class="order-title">
              <h2>订单号: {{ order.orderNumber }}</h2>
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
          
          <div class="order-info">
            <div class="info-section">
              <h3>基本信息</h3>
              <div class="info-grid">
                <div class="info-item">
                  <span class="label">订单ID</span>
                  <span class="value">{{ order.id }}</span>
                </div>
                <div class="info-item">
                  <span class="label">航班号</span>
                  <span class="value">{{ order.flightNumber }}</span>
                </div>
                <div class="info-item">
                  <span class="label">出发城市</span>
                  <span class="value">{{ order.departureCity }}</span>
                </div>
                <div class="info-item">
                  <span class="label">到达城市</span>
                  <span class="value">{{ order.arrivalCity }}</span>
                </div>
                <div class="info-item">
                  <span class="label">出发时间</span>
                  <span class="value">{{ formatDate(order.departureTime) }}</span>
                </div>
                <div class="info-item">
                  <span class="label">到达时间</span>
                  <span class="value">{{ formatDate(order.arrivalTime) }}</span>
                </div>
                <div class="info-item">
                  <span class="label">订单金额</span>
                  <span class="value amount">¥{{ order.amount }}</span>
                </div>
                <div class="info-item">
                  <span class="label">用户ID</span>
                  <span class="value">{{ order.user.id }}</span>
                </div>
              </div>
            </div>
            
            <div class="status-section">
              <h3>订单状态</h3>
              <div class="status-timeline">
                <div class="timeline-item" [class.active]="isStatusActive(OrderStatus.PENDING_PAYMENT)">
                  <div class="timeline-dot"></div>
                  <div class="timeline-content">
                    <h4>待支付</h4>
                    <p>订单已创建，等待用户支付</p>
                  </div>
                </div>
                
                <div class="timeline-item" [class.active]="isStatusActive(OrderStatus.PAID)">
                  <div class="timeline-dot"></div>
                  <div class="timeline-content">
                    <h4>已支付</h4>
                    <p>用户已完成支付</p>
                  </div>
                </div>
                
                <div class="timeline-item" [class.active]="isStatusActive(OrderStatus.TICKETING_IN_PROGRESS)">
                  <div class="timeline-dot"></div>
                  <div class="timeline-content">
                    <h4>出票中</h4>
                    <p>系统正在处理出票</p>
                  </div>
                </div>
                
                <div class="timeline-item" [class.active]="isStatusActive(OrderStatus.TICKETED)">
                  <div class="timeline-dot"></div>
                  <div class="timeline-content">
                    <h4>已出票</h4>
                    <p>出票成功，订单完成</p>
                  </div>
                </div>
                
                <div class="timeline-item" [class.active]="isStatusActive(OrderStatus.TICKETING_FAILED)">
                  <div class="timeline-dot"></div>
                  <div class="timeline-content">
                    <h4>出票失败</h4>
                    <p>出票处理失败，可重试</p>
                  </div>
                </div>
                
                <div class="timeline-item" [class.active]="isStatusActive(OrderStatus.CANCELLED)">
                  <div class="timeline-dot"></div>
                  <div class="timeline-content">
                    <h4>已取消</h4>
                    <p>订单已被取消</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="order-actions">
            <button 
              *ngIf="order.status === OrderStatus.PENDING_PAYMENT" 
              class="btn btn-success" 
              (click)="payOrder()">
              <span class="btn-icon">💳</span>
              立即支付
            </button>
            
            <button 
              *ngIf="order.status === OrderStatus.TICKETING_FAILED" 
              class="btn btn-warning" 
              (click)="retryTicketing()">
              <span class="btn-icon">🔄</span>
              重试出票
            </button>
            
            <button 
              *ngIf="canCancel(order.status)" 
              class="btn btn-danger" 
              (click)="cancelOrder()">
              <span class="btn-icon">❌</span>
              取消订单
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .order-detail-container {
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
      max-width: 1000px;
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
    
    .order-detail-card {
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(20px);
      border: 1px solid rgba(255, 255, 255, 0.2);
      border-radius: 20px;
      padding: 2rem;
      box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);
    }
    
    .order-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 2rem;
      padding-bottom: 1.5rem;
      border-bottom: 1px solid rgba(255, 255, 255, 0.2);
    }
    
    .order-title h2 {
      margin: 0 0 0.5rem 0;
      color: #ffffff;
      font-size: 1.5rem;
      font-weight: 700;
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
    
    .order-info {
      margin-bottom: 2rem;
    }
    
    .info-section, .status-section {
      margin-bottom: 2rem;
    }
    
    .info-section h3, .status-section h3 {
      color: #ffffff;
      margin-bottom: 1.5rem;
      font-size: 1.3rem;
      font-weight: 600;
    }
    
    .info-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 1rem;
    }
    
    .info-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem;
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
    
    .status-timeline {
      position: relative;
    }
    
    .timeline-item {
      display: flex;
      align-items: flex-start;
      margin-bottom: 1.5rem;
      opacity: 0.5;
      transition: all 0.3s ease;
    }
    
    .timeline-item.active {
      opacity: 1;
    }
    
    .timeline-dot {
      width: 12px;
      height: 12px;
      border-radius: 50%;
      background-color: rgba(255, 255, 255, 0.3);
      margin-right: 1rem;
      margin-top: 0.5rem;
      flex-shrink: 0;
      transition: all 0.3s ease;
    }
    
    .timeline-item.active .timeline-dot {
      background-color: #667eea;
      box-shadow: 0 0 10px rgba(102, 126, 234, 0.5);
    }
    
    .timeline-content h4 {
      margin: 0 0 0.25rem 0;
      color: #ffffff;
      font-weight: 600;
    }
    
    .timeline-content p {
      margin: 0;
      color: #b8b8b8;
      font-size: 0.875rem;
    }
    
    .order-actions {
      display: flex;
      gap: 1rem;
      justify-content: center;
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
    
    .btn-success { 
      background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
      color: white;
      box-shadow: 0 8px 25px rgba(40, 167, 69, 0.3);
    }
    .btn-warning { 
      background: linear-gradient(135deg, #ffc107 0%, #fd7e14 100%);
      color: #212529;
      box-shadow: 0 8px 25px rgba(255, 193, 7, 0.3);
    }
    .btn-danger { 
      background: linear-gradient(135deg, #dc3545 0%, #e83e8c 100%);
      color: white;
      box-shadow: 0 8px 25px rgba(220, 53, 69, 0.3);
    }
    
    .btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 12px 30px rgba(0, 0, 0, 0.3);
    }
    
    .btn-icon {
      font-size: 1rem;
    }
    
    @media (max-width: 768px) {
      .content {
        padding: 1rem;
      }
      
      .order-detail-card {
        padding: 1.5rem;
      }
      
      .info-grid {
        grid-template-columns: 1fr;
      }
      
      .order-actions {
        flex-direction: column;
      }
    }
  `]
})
export class OrderDetailComponent implements OnInit {
  order: Order | null = null;
  loading = false;
  error = '';
  OrderStatus = OrderStatus; // 在组件中暴露枚举

  constructor(
    private orderService: OrderService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    const orderId = this.route.snapshot.paramMap.get('id');
    if (orderId) {
      this.loadOrder(Number(orderId));
    }
  }

  loadOrder(orderId: number) {
    this.loading = true;
    this.error = '';
    
    this.orderService.getOrderById(orderId).subscribe({
      next: (response) => {
        this.loading = false;
        this.order = response.data;
      },
      error: (error) => {
        this.loading = false;
        this.error = error.error?.message || '加载订单详情失败';
      }
    });
  }

  payOrder() {
    if (this.order) {
      this.orderService.payOrder(this.order.id).subscribe({
        next: () => {
          this.loadOrder(this.order!.id);
        },
        error: (error) => {
          this.error = error.error?.message || '支付失败';
        }
      });
    }
  }

  cancelOrder() {
    if (this.order && confirm('确定要取消这个订单吗？')) {
      this.orderService.cancelOrder(this.order.id).subscribe({
        next: () => {
          this.loadOrder(this.order!.id);
        },
        error: (error) => {
          this.error = error.error?.message || '取消订单失败';
        }
      });
    }
  }

  retryTicketing() {
    if (this.order) {
      this.orderService.retryTicketing(this.order.id).subscribe({
        next: () => {
          this.loadOrder(this.order!.id);
        },
        error: (error) => {
          this.error = error.error?.message || '重试出票失败';
        }
      });
    }
  }

  goBack() {
    this.router.navigate(['/orders']);
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

  isStatusActive(status: OrderStatus): boolean {
    if (!this.order) return false;
    
    const statusOrder = [
      OrderStatus.PENDING_PAYMENT,
      OrderStatus.PAID,
      OrderStatus.TICKETING_IN_PROGRESS,
      OrderStatus.TICKETED,
      OrderStatus.TICKETING_FAILED,
      OrderStatus.CANCELLED
    ];
    
    const currentIndex = statusOrder.indexOf(this.order.status);
    const targetIndex = statusOrder.indexOf(status);
    
    return targetIndex <= currentIndex;
  }

  canCancel(status: OrderStatus): boolean {
    return status !== OrderStatus.TICKETED && status !== OrderStatus.CANCELLED;
  }
} 