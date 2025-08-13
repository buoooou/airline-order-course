import { Component, OnInit, AfterViewInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DatePipe } from '@angular/common';
import { OrderService } from '../../services/order.service';
import { Order } from '../../models/order.model';
import { Subscription } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';

/**
 * 订单列表页面组件
 * 功能：展示订单列表，支持分页、筛选和操作
 */
@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.html',
  styleUrl: './order-list.scss',
  imports: [CommonModule, MatIconModule, MatPaginatorModule]
})
export class OrderList implements OnInit, OnDestroy {
  allOrders: Order[] = [];
  orders: Order[] = [];
  total = 0;
  pageSize = 10;
  private subscription = new Subscription();

  constructor(
    private orderService: OrderService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadOrders(0);
  }

  onPageChange(event: PageEvent): void {
    console.log('分页事件触发:', event);
    this.pageSize = event.pageSize;
    this.loadOrders(event.pageIndex);
  }

  loadOrders(page: number): void {
    // 统一页码逻辑：后端接口期望从 1 开始，前端从 0 开始
    const apiPage = page + 1;
    this.subscription.add(
      this.orderService.getOrders(apiPage, this.pageSize).subscribe({
        next: (res:any) => { console.log("loadOrders:",res);
          this.orders = res.orders || [];
          this.total = res.total || 0;
          this.cdr.detectChanges();
          console.log('强制刷新分页器状态');
          console.log('赋值后:', this.orders);
        },
        error: (err) => {
          console.error('加载订单失败:', err);
          // 这里可以添加用户通知逻辑，例如：
          // this.notification.error('加载订单失败，请稍后重试');
        }
      })
    );
  }

  viewDetails(order: Order): void {
    // 导航到订单详情页面
    window.location.href = `/api/orders/${order.orderId}`;
  }

  private debounceTimer: any;

applyFilter(event: Event): void {
  const filterValue = (event.target as HTMLInputElement).value.trim().toLowerCase();
  
  if (!filterValue) {
    this.loadOrders(0);
    return;
  }
  
  this.subscription.add(
    this.orderService.getOrders(1, 1000).subscribe({
      next: ({orders}) => {
        this.allOrders = orders;
        this.orders = this.allOrders
          .filter(order => 
            order.orderId.toLowerCase().includes(filterValue) || 
            (order.passengerName && order.passengerName.toLowerCase().includes(filterValue))
          )
          .slice(0, this.pageSize);
        this.total = this.orders.length;
      },
      error: (err) => console.error('加载订单失败:', err)
    })
  );
}

  getStatusIcon(status: 'PAID' | 'TICKETING_IN_PROGRESS' | 'TICKETED' | 'PENDING_PAYMENT' | 'PAYMENT_FAILED' | 'TICKETING_FAILED' | 'CANCELLED'): string {
    switch (status) {
      case 'PAID': return 'flight';
      case 'TICKETING_IN_PROGRESS': return 'timelapse';
      case 'TICKETED': return 'confirmation_number';
      case 'PENDING_PAYMENT': return 'credit_card';
      case 'PAYMENT_FAILED': return 'warning';
      case 'TICKETING_FAILED': return 'dangerous';
      case 'CANCELLED': return 'no_flights';
      default: return 'help';
    }
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'PAID': return 'status-paid';
      case 'TICKETING_IN_PROGRESS': return 'status-in-progress';
      case 'TICKETED': return 'status-ticketed';
      default: return 'status-default';
    }
  }

  updateStatus(orderId: string, status: 'PAID' | 'TICKETING_IN_PROGRESS' | 'TICKETED' | 'PENDING_PAYMENT' | 'PAYMENT_FAILED' | 'TICKETING_FAILED' | 'CANCELLED'): void {
    this.orderService.updateOrderStatus(orderId, status).subscribe({
      next: (updatedOrder) => {
        this.orders = this.orders.map(order => 
          order.orderId === updatedOrder.orderId ? updatedOrder : order
        );
      },
      error: (error) => {
        console.error(`[OrderList] 订单 ${orderId} 状态更新失败:`, error);
      }
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}