import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzTagModule } from 'ng-zorro-antd/tag';
import { OrderService } from '../../core/services/order.service';
import { Order } from '../../shared/models/order.model';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { NzPageHeaderModule } from 'ng-zorro-antd/page-header';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NzCardModule, 
    NzTableModule, 
    NzInputModule,
    NzButtonModule,
    NzTagModule,
    NzIconModule,
    NzPageHeaderModule
  ],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.scss'
})
export class OrdersComponent implements OnInit {
  searchValue = '';
  orders: Order[] = [];
  isLoading = true;
  pageTitle = '订单管理';
  
  statusColorMap = {
    'NONE': 'default',
    'PENDING_PAYMENT': 'gold',
    'PAID': 'green',
    'TICKETING_IN_PROGRESS': 'blue',
    'TICKETING_FAILED': 'volcano',
    'TICKETED': 'cyan',
    'CANCELED': 'red'
  };
  
  statusTextMap = {
    'NONE': '未处理',
    'PENDING_PAYMENT': '待支付',
    'PAID': '已支付',
    'TICKETING_IN_PROGRESS': '出票中',
    'TICKETING_FAILED': '出票失败',
    'TICKETED': '已出票',
    'CANCELED': '已取消'
  };

  constructor(
    private orderService: OrderService,
    private message: NzMessageService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders(): void {
    this.isLoading = true;
    this.orderService.getAllOrders()
      .pipe(
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (res) => {
          console.log('loadOrders# response status:' + res.code);
          if (res.code === 200) {
            this.orders = res.data;
          } else {
            this.message.error(`加载订单失败: ${res.message}`);
          }
        },
        error: (err) => {
          console.log('加载订单失败:', err);
          this.message.error('加载订单失败:', err);
        }
      });
  }

  // 支付订单
  payOrder(orderId: number): void {
    this.orderService.payOrder(orderId)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (res) => {
          if (res.code === 200) {
            this.message.success('支付成功');
            this.loadOrders();
          } else {
            this.message.error(`支付失败: ${res.message}`);
          }
        },
        error: (err) => {
          this.message.error('支付请求失败');
        }
      });
  }

  // 取消订单
  cancelOrder(orderId: number): void {
    this.orderService.cancelOrder(orderId)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (res) => {
          if (res.code === 200) {
            this.message.success('订单已取消');
            this.loadOrders();
          } else {
            this.message.error(`取消失败: ${res.message}`);
          }
        },
        error: (err) => {
          this.message.error('取消请求失败');
        }
      });
  }

  logout(): void {
    this.authService.logout();
  }
}
