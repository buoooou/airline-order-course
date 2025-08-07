import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { OrderService } from '../../core/services/order';
import { AuthService } from '../../core/services/auth';
import { Order, OrderStatus } from '../../core/models/order.model';

/**
 * 订单详情页面组件
 * 显示单个订单的详细信息，包括订单状态、航班信息、乘客信息等
 */
@Component({
  selector: 'app-order-detail',
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatDividerModule,
    MatListModule
  ],
  templateUrl: './order-detail.html',
  styleUrl: './order-detail.scss'
})
export class OrderDetail implements OnInit {
  
  /** 订单信息 */
  order: Order | null = null;
  
  /** 加载状态 */
  loading = false;
  
  /** 订单ID */
  orderId: string | null = null;
  
  /** 当前用户是否为管理员 */
  isAdmin = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // 获取路由参数中的订单ID
    this.orderId = this.route.snapshot.paramMap.get('id');
    
    // 检查用户权限
    this.isAdmin = this.authService.isAdmin();
    
    if (this.orderId) {
      this.loadOrderDetail();
    } else {
      this.showError('订单ID无效');
      this.goBack();
    }
  }

  /**
   * 加载订单详情
   */
  loadOrderDetail(): void {
    if (!this.orderId) return;
    
    this.loading = true;
    this.cdr.detectChanges(); // 手动触发变更检测
    
    this.orderService.getOrderById(parseInt(this.orderId)).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.order = response.data;
          this.loading = false;
          console.log('订单数据加载成功:', this.order);
        } else {
          this.showError('加载订单详情失败: ' + (response.message || '未知错误'));
          this.loading = false;
        }
        this.cdr.detectChanges(); // 手动触发变更检测
      },
      error: (error) => {
        console.error('加载订单详情失败:', error);
        this.showError('加载订单详情失败，请稍后重试: ' + (error.message || error.status || '网络错误'));
        this.loading = false;
        this.cdr.detectChanges(); // 手动触发变更检测
      }
    });
  }

  /**
   * 获取订单状态的显示样式
   */
  getStatusChipColor(status: OrderStatus): string {
    switch (status) {
      case OrderStatus.PENDING_PAYMENT:
        return 'warn';
      case OrderStatus.PAID:
        return 'primary';
      case OrderStatus.TICKETING_IN_PROGRESS:
        return 'accent';
      case OrderStatus.TICKETED:
        return 'primary';
      case OrderStatus.TICKETING_FAILED:
        return 'warn';
      case OrderStatus.CANCELLED:
        return '';
      default:
        return '';
    }
  }

  /**
   * 获取订单状态的图标
   */
  getStatusIcon(status: OrderStatus): string {
    switch (status) {
      case OrderStatus.PENDING_PAYMENT:
        return 'payment';
      case OrderStatus.PAID:
        return 'check_circle';
      case OrderStatus.TICKETING_IN_PROGRESS:
        return 'hourglass_empty';
      case OrderStatus.TICKETED:
        return 'confirmation_number';
      case OrderStatus.TICKETING_FAILED:
        return 'error';
      case OrderStatus.CANCELLED:
        return 'cancel';
      default:
        return 'info';
    }
  }

  /**
   * 检查订单是否可以取消
   */
  canCancelOrder(): boolean {
    if (!this.order) return false;
    
    return this.order.status === OrderStatus.PENDING_PAYMENT ||
           this.order.status === OrderStatus.PAID ||
           this.order.status === OrderStatus.TICKETING_FAILED;
  }

  /**
   * 检查订单是否可以支付
   */
  canPayOrder(): boolean {
    if (!this.order) return false;
    
    return this.order.status === OrderStatus.PENDING_PAYMENT;
  }

  /**
   * 支付订单
   */
  payOrder(): void {
    if (!this.order || !this.canPayOrder()) return;
    
    this.loading = true;
    this.cdr.detectChanges();
    
    this.orderService.payOrder(this.order.id).subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.order = response.data;
          this.showSuccess('订单支付成功');
        } else {
          this.showError('订单支付失败: ' + response.message);
        }
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('订单支付失败:', error);
        this.showError('订单支付失败，请稍后重试');
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * 取消订单
   */
  cancelOrder(): void {
    if (!this.order || !this.canCancelOrder()) return;
    
    if (!confirm('确定要取消这个订单吗？')) return;
    
    this.loading = true;
    this.cdr.detectChanges();
    
    this.orderService.cancelOrder(this.order.id, '用户主动取消').subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.order = response.data;
          this.showSuccess('订单取消成功');
        } else {
          this.showError('订单取消失败: ' + response.message);
        }
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('订单取消失败:', error);
        this.showError('订单取消失败，请稍后重试');
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * 返回订单列表
   */
  goBack(): void {
    this.router.navigate(['/orders']);
  }

  /**
   * 格式化日期时间
   */
  formatDateTime(dateTime: string | null | undefined): string {
    if (!dateTime) return '-';
    
    const date = new Date(dateTime);
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  }

  /**
   * 格式化金额
   */
  formatAmount(amount: number): string {
    return `¥${amount.toFixed(2)}`;
  }

  /**
   * 显示成功消息
   */
  private showSuccess(message: string): void {
    this.snackBar.open(message, '关闭', {
      duration: 3000,
      panelClass: ['success-snackbar']
    });
  }

  /**
   * 显示错误消息
   */
  private showError(message: string): void {
    this.snackBar.open(message, '关闭', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
  }
}
