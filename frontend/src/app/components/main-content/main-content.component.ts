import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService } from '../../core/services/order.service';
import { OrderDto } from '../../core/models/order.model';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzSpinModule } from 'ng-zorro-antd/spin';
import { NzEmptyModule } from 'ng-zorro-antd/empty';
import { NzMessageService } from 'ng-zorro-antd/message';
// nz-tag
import { NzTagComponent } from 'ng-zorro-antd/tag'; // 导入 Tag 组件

@Component({
  selector: 'app-main-content',
  standalone: true,
  imports: [
    CommonModule,
    NzTableModule,
    NzCardModule,
    NzSpinModule,
    NzEmptyModule,
    NzTagComponent
  ],
  templateUrl: './main-content.component.html',
  styleUrls: ['./main-content.component.css']
})
export class MainContentComponent implements OnInit {
  orders: OrderDto[] = [];
  loading = true;

  constructor(
    private orderService: OrderService,
    private message: NzMessageService
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading = true;
    this.orderService.getUserOrders().subscribe({
      next: (response) => {
        if (response.result === 'SUCCESS') {
          this.orders = response.data;
        } else {
          this.message.error(response.message || '获取订单失败');
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('获取订单失败:', error);
        this.message.error('获取订单失败，请稍后重试');
        this.loading = false;
      }
    });
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatAmount(amount: number): string {
    return `¥${amount.toFixed(2)}`;
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'PENDING':
        return 'orange';
      case 'CONFIRMED':
        return 'blue';
      case 'COMPLETED':
        return 'green';
      case 'CANCELLED':
        return 'red';
      default:
        return 'default';
    }
  }
}