import { Component, OnInit,ChangeDetectorRef  } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { OrderService } from '../../../core/services/service/order.service';
import { Order } from '../../../core/model/order.model';

@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class OrderListComponent implements OnInit {
  orders: Order[] = [];
  isLoading = true;
  errorMessage: string | null = null;

  constructor(
    private orderService: OrderService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders() {
    this.isLoading = true;
    this.errorMessage = null;

    this.orderService.getAllOrders().subscribe({
      next: (data) => {
        console.log('接收到数据:', data);
        this.orders = Array.isArray(data) ? data : [];
        this.isLoading = false;
        console.log('isLoading==:', this.isLoading);

        // 手动触发变更检测
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('获取订单失败:', error);
        this.isLoading = false;
        this.errorMessage = '无法连接服务器，请稍后重试。';
        this.orders = [];
        this.cdr.detectChanges(); // 手动刷新
      }
    });
  }

  getStatusText(status: string | null | undefined): string {
    if (!status) return '未知状态';

    const map: { [key: string]: string } = {
      'PENDING_PAYMENT': '待支付',
      'PAID': '已支付',
      'TICKETING_IN_PROGRESS': '出票中',
      'TICKETING_FAILED': '出票失败',
      'TICKETED': '出票成功',
      'CANCELLED': '已取消'
    };

    return map[status] || status;
  }

  getStatusClass(status: string | null | undefined): string {
    if (!status) return 'status-unknown';

    const classMap: { [key: string]: string } = {
      'PENDING_PAYMENT': 'status-pending',
      'PAID': 'status-paid',
      'TICKETING_IN_PROGRESS': 'status-in-progress',
      'TICKETING_FAILED': 'status-failed',
      'TICKETED': 'status-ticketed',
      'CANCELLED': 'status-cancelled'
    };

    return classMap[status] || 'status-unknown';
  }
}