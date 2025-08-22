import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { OrderService } from '../../core/services/order.service';
import { Order } from '../../core/models/order.model';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-order',
  templateUrl: './order.html',
  styleUrls: ['./order.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class OrderComponent implements OnInit {
  orders: Order[] = [];
  loading = true;
  errorMessage = '';

  constructor(
    private orderService: OrderService,
    private router: Router,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    // console.log('进入Order页面，当前token:', this.authService.getToken());
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading = true;
    this.orderService.getOrders().subscribe({
      next: (apiResponse) => {
        this.orders = apiResponse.data;
        this.loading = false;
        console.log('后端返回orders数据:', this.orders);
        this.cdr.markForCheck(); // 手动触发视图更新
      },
      error: (err) => {
        this.errorMessage = '加载订单失败: ' + (err.message || '未知错误');
        this.loading = false;
        this.cdr.markForCheck(); // 错误场景也需要
      }
    });
  }

  viewOrderDetail(orderId: number): void {
    this.router.navigate(['/orders', orderId]);
  }

  logout(): void {
    this.authService.logout();
  }
}