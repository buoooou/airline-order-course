import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Router } from '@angular/router';
import { interval, Subscription } from 'rxjs';
import { OrderService } from '../../core/services/order';
import { AuthService } from '../../core/services/auth';
import { Order, OrderStatus } from '../../core/models/order.model';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatSlideToggleModule,
    MatTooltipModule
  ],
  templateUrl: './order-list.html',
  styleUrl: './order-list.scss'
})
export class OrderList implements OnInit, OnDestroy {
  orders: Order[] = [];
  loading = false;
  displayedColumns: string[] = ['orderNumber', 'amount', 'status', 'creationDate', 'actions'];
  
  // 自动刷新相关
  autoRefreshEnabled = false;
  autoRefreshSubscription: Subscription | null = null;
  lastUpdateTime = new Date();
  refreshing = false;
  
  // 订单状态映射
  statusMap = {
    [OrderStatus.PENDING_PAYMENT]: { text: '待支付', color: 'warn' },
    [OrderStatus.PAID]: { text: '已支付', color: 'primary' },
    [OrderStatus.TICKETING_IN_PROGRESS]: { text: '出票中', color: 'accent' },
    [OrderStatus.TICKETING_FAILED]: { text: '出票失败', color: 'warn' },
    [OrderStatus.TICKETED]: { text: '已出票', color: 'primary' },
    [OrderStatus.CANCELLED]: { text: '已取消', color: '' }
  };

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  ngOnDestroy(): void {
    if (this.autoRefreshSubscription) {
      this.autoRefreshSubscription.unsubscribe();
    }
  }

  /**
   * 加载订单列表
   */
  loadOrders(silent: boolean = false): void {
    if (!silent) {
      this.loading = true;
    } else {
      this.refreshing = true;
    }
    this.cdr.detectChanges(); // 立即触发变更检测
    
    const previousOrdersCount = this.orders.length;
    const previousOrders = [...this.orders];
    
    this.orderService.getOrders().subscribe({
      next: (response) => {
        setTimeout(() => {
          if (response.success) {
            const newOrders = response.data || [];
            this.orders = newOrders;
            this.lastUpdateTime = new Date();
            
            // 检查是否有订单状态变化
            if (silent && this.hasOrderChanges(previousOrders, newOrders)) {
              this.showSuccess('📊 订单数据已更新！发现状态变化');
            }
          } else {
            this.showError('加载订单失败: ' + response.message);
          }
          this.loading = false;
          this.refreshing = false;
          this.cdr.detectChanges();
        }, 0);
      },
      error: (error) => {
        setTimeout(() => {
          console.error('加载订单失败:', error);
          if (!silent) {
            this.showError('加载订单失败，请稍后重试');
          }
          this.loading = false;
          this.refreshing = false;
          this.cdr.detectChanges();
        }, 0);
      }
    });
  }

  /**
   * 检查订单是否有变化
   */
  private hasOrderChanges(oldOrders: Order[], newOrders: Order[]): boolean {
    if (oldOrders.length !== newOrders.length) {
      return true;
    }
    
    for (let i = 0; i < oldOrders.length; i++) {
      const oldOrder = oldOrders.find(o => o.id === newOrders[i].id);
      if (!oldOrder || oldOrder.status !== newOrders[i].status) {
        return true;
      }
    }
    
    return false;
  }

  /**
   * 查看订单详情
   */
  viewOrder(order: Order): void {
    this.router.navigate(['/orders', order.id]);
  }

  /**
   * 取消订单
   */
  cancelOrder(order: Order): void {
    if (order.status !== OrderStatus.PENDING_PAYMENT && order.status !== OrderStatus.PAID) {
      this.showError('当前状态的订单无法取消');
      return;
    }

    if (confirm('确定要取消这个订单吗？')) {
      const statusUpdate = {
        status: OrderStatus.CANCELLED,
        reason: '用户主动取消'
      };
      
      this.orderService.updateOrderStatus(order.id, statusUpdate).subscribe({
        next: (response) => {
          if (response.success) {
            this.showSuccess('订单已取消');
            this.loadOrders(); // 重新加载订单列表
          } else {
            this.showError('取消订单失败: ' + response.message);
          }
        },
        error: (error) => {
          console.error('取消订单失败:', error);
          this.showError('取消订单失败，请稍后重试');
        }
      });
    }
  }

  /**
   * 支付订单
   */
  payOrder(order: Order): void {
    if (order.status !== OrderStatus.PENDING_PAYMENT) {
      this.showError('订单状态不允许支付');
      return;
    }

    // 模拟支付过程
    const statusUpdate = {
      status: OrderStatus.PAID,
      reason: '用户完成支付'
    };
    
    this.orderService.updateOrderStatus(order.id, statusUpdate).subscribe({
      next: (response) => {
        if (response.success) {
          this.showSuccess('支付成功');
          this.loadOrders(); // 重新加载订单列表
        } else {
          this.showError('支付失败: ' + response.message);
        }
      },
      error: (error) => {
        console.error('支付失败:', error);
        this.showError('支付失败，请稍后重试');
      }
    });
  }

  /**
   * 获取订单状态显示信息
   */
  getStatusInfo(status: OrderStatus) {
    return this.statusMap[status] || { text: '未知状态', color: '' };
  }

  /**
   * 检查是否可以取消订单
   */
  canCancel(order: Order): boolean {
    return order.status === OrderStatus.PENDING_PAYMENT || order.status === OrderStatus.PAID;
  }

  /**
   * 检查是否可以支付订单
   */
  canPay(order: Order): boolean {
    return order.status === OrderStatus.PENDING_PAYMENT;
  }

  /**
   * 刷新订单列表
   */
  refresh(): void {
    this.loadOrders();
  }

  /**
   * 切换自动刷新
   */
  toggleAutoRefresh(): void {
    // 由于使用了双向绑定，autoRefreshEnabled的值已经被mat-slide-toggle自动更新了
    // 我们只需要根据当前状态来启动或停止自动刷新
    if (this.autoRefreshEnabled) {
      // 开启自动刷新（每30秒）
      this.autoRefreshSubscription = interval(30000).subscribe(() => {
        this.loadOrders(true); // 静默刷新
      });
      this.showSuccess('已开启自动刷新（每30秒）');
    } else {
      // 停止自动刷新
      if (this.autoRefreshSubscription) {
        this.autoRefreshSubscription.unsubscribe();
        this.autoRefreshSubscription = null;
      }
      this.showSuccess('已停止自动刷新');
    }
    
    // 触发变更检测以确保UI更新
    this.cdr.detectChanges();
  }

  /**
   * 获取已支付订单数量
   */
  getPaidOrdersCount(): number {
    return this.orders.filter(order => 
      order.status === OrderStatus.PAID || 
      order.status === OrderStatus.TICKETING_IN_PROGRESS ||
      order.status === OrderStatus.TICKETED
    ).length;
  }

  /**
   * 获取待支付订单数量
   */
  getPendingOrdersCount(): number {
    return this.orders.filter(order => order.status === OrderStatus.PENDING_PAYMENT).length;
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
   * 检查当前用户是否为管理员
   */
  isAdmin(): boolean {
    return this.authService.isAdmin();
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
