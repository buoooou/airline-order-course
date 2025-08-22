import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService } from '../../core/services/order.service';
import { Order } from '../../core/models/order.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-order-detail',
  templateUrl: './order-detail.html',
  styleUrls: ['./order-detail.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class OrderDetailComponent implements OnInit {
  order: Order | null = null;
  loading = true;
  errorMessage = '';
  actionMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    const orderId = Number(this.route.snapshot.paramMap.get('id'));
    if (!isNaN(orderId)) {
      this.loadOrderDetail(orderId);
    } else {
      this.errorMessage = '无效的订单ID';
      this.loading = false;
    }
  }

  loadOrderDetail(orderId: number): void {
    this.loading = true;
    this.orderService.getOrderById(orderId).subscribe({
      next: (apiResponse) => {
        this.order = apiResponse.data;
        this.loading = false;
        this.actionMessage = '';
        this.cdr.markForCheck(); // 手动触发视图更新
      },
      error: (err) => {
        this.errorMessage = '加载订单详情失败: ' + (err.message || '未知错误');
        this.loading = false;
        this.cdr.markForCheck(); // 错误场景也需要
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/orders']);
  }

  payOrder(orderId: number): void {
    this.orderService.payOrder(orderId).subscribe({
      next: (apiResponse) => {
        this.order = apiResponse.data;
        this.actionMessage = '订单支付成功';
        this.cdr.markForCheck(); // 手动触发视图更新
      },
      error: (err) => {
        this.actionMessage = '支付失败: ' + (err.error?.message || err.message || '未知错误');
        this.cdr.markForCheck(); // 手动触发视图更新
      }
    });
  }

  cancelOrder(orderId: number): void {
    this.orderService.cancelOrder(orderId).subscribe({
      next: (apiResponse) => {
        this.order = apiResponse.data;
        this.actionMessage = '订单取消成功';
        this.cdr.markForCheck(); // 手动触发视图更新
      },
      error: (err) => {
        this.actionMessage = '取消失败: ' + (err.error?.message || err.message || '未知错误');
        this.cdr.markForCheck(); // 手动触发视图更新
      }
    });
  }

  retryTicketing(orderId: number): void {
    this.orderService.retryTicketing(orderId).subscribe({
      next: (apiResponse) => {
        this.order = apiResponse.data;
        this.actionMessage = '重新出票成功';
        this.cdr.markForCheck(); // 手动触发视图更新
      },
      error: (err) => {
        this.actionMessage = '重新出票失败: ' + (err.error?.message || err.message || '未知错误');
        this.cdr.markForCheck(); // 手动触发视图更新
      }
    });
  }
}