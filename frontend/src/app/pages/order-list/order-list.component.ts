import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzTagModule } from 'ng-zorro-antd/tag';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { OrderService } from '../../services/order.service';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
import { OrderDto, OrderStatus } from '../../models/order.model';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [
    CommonModule,
    NzTableModule,
    NzButtonModule,
    NzTagModule,
    NzCardModule,
    NzLayoutModule
  ],
  templateUrl: './order-list.component.html',
  styleUrl: './order-list.component.scss'
})
export class OrderListComponent implements OnInit {
  orders: OrderDto[] = [];
  loading = true;

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router,
    private message: NzMessageService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.loading = true;
    this.orderService
      .getAllOrders()
      .pipe(
        finalize(() => {
          this.loading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (orders: OrderDto[]) => {
          this.orders = orders || [];
        },
        error: (error: any) => {
          this.message.error('获取订单列表失败：' + (error?.error?.message || error?.message || '网络错误'));
        }
      });
  }

  viewOrderDetail(order: OrderDto) {
    this.router.navigate(['/orders', order.id]);
  }

  getStatusColor(status: OrderStatus): string {
    switch (status) {
      case OrderStatus.PENDING_PAYMENT:
        return 'orange';
      case OrderStatus.PAID:
        return 'blue';
      case OrderStatus.TICKETING_IN_PROGRESS:
        return 'processing';
      case OrderStatus.TICKETED:
        return 'green';
      case OrderStatus.TICKETING_IN_FAILED:
        return 'red';
      case OrderStatus.CANCELED:
        return 'default';
      default:
        return 'default';
    }
  }

  getStatusText(status: OrderStatus): string {
    switch (status) {
      case OrderStatus.PENDING_PAYMENT:
        return '待支付';
      case OrderStatus.PAID:
        return '已支付';
      case OrderStatus.TICKETING_IN_PROGRESS:
        return '出票中';
      case OrderStatus.TICKETED:
        return '已出票';
      case OrderStatus.TICKETING_IN_FAILED:
        return '出票失败';
      case OrderStatus.CANCELED:
        return '已取消';
      default:
        return '未知状态';
    }
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
