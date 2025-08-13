import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { OrderService } from '../../services/order.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatBadgeModule } from '@angular/material/badge';
import { Order } from '../../models/order.model';

@Component({
  selector: 'app-order-status',
  templateUrl: './order-status.html',
  styleUrls: ['./order-status.scss'],
  imports: [MatTableModule, MatPaginatorModule, CommonModule, MatIconModule, MatBadgeModule]
})
export class OrderStatus implements OnInit {
  constructor(
    private orderService: OrderService,
    private router: Router,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadOrders(0);
  }

  dataSource: Order[] = [];
  displayedColumns: string[] = ['orderNumber', 'status', 'actions'];
  loading = false;

  viewDetails(orderId: string): void {
    if (!orderId) return this.showError('订单ID无效');
    this.router.navigate(['/order-info', orderId]);
  }

  cancelOrder(orderId: string): void {
    if (!orderId) return this.showError('订单ID无效');
    
    this.loading = true;
    this.orderService.cancelOrder(orderId).subscribe({
      next: () => {
        this.showSuccess('订单取消成功');
        this.loadOrders();
      },
      error: (err: any) => {
        if (err.status === 403) {
          err.message = '当前状态无法执行取消操作';
        } else if (err.status === 500) {
          err.message = '服务器内部错误，请稍后重试';
        } 
        this.handleError(err);
      }
    });
  }

  rebookOrder(orderId: string): void {
    if (!orderId) return this.showError('订单ID无效');
    
    this.loading = true;
    this.orderService.rebookOrder(orderId).subscribe({
      next: () => {
        this.showSuccess('重新预订成功');
        this.loadOrders();
      },
      error: (err: Error) => this.handleError(err)
    });
  }

  page = 1;
  pageSize = 10;
  total = 0;
  pageIndex = 0;
  
  // 确保分页控件的初始值与组件属性同步

  loadOrders(page: number = 0): void {
    const apiPage = page + 1; // 统一页码逻辑：后端接口期望从1开始，前端从0开始
    console.log('开始加载数据', { page: apiPage, pageSize: this.pageSize });
    this.loading = true;
    this.orderService.getOrders(apiPage, this.pageSize, true).subscribe({
      next: (res) => {
        console.log('数据加载成功', res);
        this.dataSource = res.orders || [];
        this.total = res.total || 0;
        this.pageIndex = this.page - 1;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err: Error) => {
        console.error('数据加载失败', err);
        this.handleError(err);
      }
    });
  }

  onPageChange(event: any): void {
    console.log('分页事件触发:', event);
    this.pageSize = event.pageSize;
    this.loadOrders(event.pageIndex);
  }

  private getMessage(msg: string | { message?: string }, defaultMsg: string): string {
    return typeof msg === 'string' ? msg : msg.message || defaultMsg;
  }

  private showError(msg: string | { message?: string }): void {
    this.snackBar.open(this.getMessage(msg, '操作失败'), '关闭', { duration: 3000 });
  }

  private showSuccess(msg: string | { message?: string }): void {
    this.snackBar.open(this.getMessage(msg, '操作成功'), '关闭', { duration: 3000 });
  }

  private handleError(error: Error | string | { error?: { message?: string } }): void {
    this.loading = false;
    let errorMessage = '操作失败';
    if (typeof error === 'string') {
      errorMessage = error;
    } else if (error instanceof Error) {
      errorMessage = error.message;
    } else if (error?.error?.message) {
      errorMessage = error.error.message;
    }
    this.showError(errorMessage);
  }
}