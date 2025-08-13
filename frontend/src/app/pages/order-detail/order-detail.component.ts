import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzTagModule } from 'ng-zorro-antd/tag';
import { NzDescriptionsModule } from 'ng-zorro-antd/descriptions';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';
import { OrderDto, OrderStatus } from '../../models/order.model';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [
    CommonModule,
    NzCardModule,
    NzButtonModule,
    NzTagModule,
    NzDescriptionsModule,
    NzLayoutModule
  ],
  templateUrl: './order-detail.component.html',
  styleUrl: './order-detail.component.scss'
})
export class OrderDetailComponent implements OnInit {
  order: OrderDto | null = null;
  loading = true;
  actionLoading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService,
    private authService: AuthService,
    private message: NzMessageService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const orderId = this.route.snapshot.paramMap.get('id');
    if (orderId) {
      this.loadOrderDetail(+orderId);
    }
  }

  loadOrderDetail(id: number) {
    this.loading = true;
    this.orderService
      .getOrderById(id)
      .pipe(
        finalize(() => {
          this.loading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (order: OrderDto) => {
          this.order = order;
        },
        error: (error: any) => {
          this.message.error('获取订单详情失败：' + (error?.error?.message || error?.message || '网络错误'));
          this.router.navigate(['/orders']);
        }
      });
  }

  // 判断是否可以支付
  canPay(): boolean {
    return this.order?.status === OrderStatus.PENDING_PAYMENT;
  }

  // 判断是否可以开始出票
  canStartTicketing(): boolean {
    return this.order?.status === OrderStatus.PAID;
  }

  // 判断是否可以取消
  canCancel(): boolean {
    return this.order?.status === OrderStatus.PENDING_PAYMENT || 
           this.order?.status === OrderStatus.PAID ||
           this.order?.status === OrderStatus.TICKETING_IN_FAILED;
  }

  // 支付订单
  payOrder() {
    if (!this.order) return;
    
    this.actionLoading = true;
    this.orderService.payOrder(this.order.id).subscribe({
      next: (result: string) => {
        this.message.success('支付成功');
        this.loadOrderDetail(this.order!.id);
        this.actionLoading = false;
      },
      error: (error: any) => {
        this.message.error('支付失败：' + (error.error?.message || '网络错误'));
        this.actionLoading = false;
      }
    });
  }

  // 开始出票
  startTicketing() {
    if (!this.order) return;
    
    this.actionLoading = true;
    this.orderService.startTicketing(this.order.id).subscribe({
      next: (result: string) => {
        this.message.success('开始出票');
        this.loadOrderDetail(this.order!.id);
        this.actionLoading = false;
      },
      error: (error: any) => {
        this.message.error('开始出票失败：' + (error.error?.message || '网络错误'));
        this.actionLoading = false;
      }
    });
  }

  // 取消订单
  cancelOrder() {
    if (!this.order) return;
    
    this.actionLoading = true;
    this.orderService.cancelOrder(this.order.id).subscribe({
      next: (result: string) => {
        this.message.success('订单已取消');
        this.loadOrderDetail(this.order!.id);
        this.actionLoading = false;
      },
      error: (error: any) => {
        this.message.error('取消订单失败：' + (error.error?.message || '网络错误'));
        this.actionLoading = false;
      }
    });
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

  goBack() {
    this.router.navigate(['/orders']);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
