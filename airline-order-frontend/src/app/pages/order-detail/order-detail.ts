import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { TranslateModule } from '@ngx-translate/core';
import { ActivatedRoute } from '@angular/router';
import { OrderService } from '../../services/order.service';
import { Order } from '../../models/order.model';
import { Subject, takeUntil } from 'rxjs';

/**
 * 订单详情页面组件
 * 功能：展示订单的完整信息，支持打印和导出
 */
@Component({
  selector: 'app-order-detail',
  templateUrl: './order-detail.html',
  styleUrl: './order-detail.scss',
  imports: [CommonModule, MatProgressSpinnerModule, MatIconModule, TranslateModule]
})
export class OrderDetail implements OnInit, OnDestroy {
  private translate = inject(TranslateService);
  calculateDuration(departure: string, arrival: string): string {
    const depTime = new Date(departure);
    const arrTime = new Date(arrival);
    const durationMs = arrTime.getTime() - depTime.getTime();
    
    const hours = Math.floor(durationMs / (1000 * 60 * 60));
    const minutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));
    
    return `${hours}h${minutes}m`;
  }
  order: Order | null = null;
  loading = false;
  private destroy$ = new Subject<void>();

  getStatusClass(status: string): string {
    switch (status) {
      case 'PENDING_PAYMENT':
        return 'status-pending';
      case 'PAID':
        return 'status-paid';
      case 'PAYMENT_FAILED':
        return 'status-failed';
      case 'TICKETING_IN_PROGRESS':
        return 'status-in-progress';
      case 'TICKETING_FAILED':
        return 'status-failed';
      case 'TICKETED':
        return 'status-ticketed';
      case 'CANCELLED':
        return 'status-cancelled';
      default:
        return 'status-default';
    }
  }

  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService
  ) {}

  /**
   * 初始化方法
   * 根据路由参数加载订单详情
   */
  ngOnInit(): void {
    console.error('订单详情页面');
    this.loading = true;
    const orderId = this.route.snapshot.paramMap.get('id');
    if (orderId) {
      this.orderService.getOrderById(orderId).pipe(
        takeUntil(this.destroy$)
      ).subscribe({
        next: (order : any) => {
          this.order = order;
          this.loading = false;
        },
        error: (error) => {
          console.error('加载订单详情失败:', error);
          this.loading = false;
        }
      });
    }
  }

  /**
   * 打印订单
   */
  printOrder(): void {
    window.print();
  }

  /**
   * 导出订单为PDF
   */
  exportToPDF(): void {
    // 调用导出服务
    console.log('导出订单为PDF:', this.order?.orderId);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
