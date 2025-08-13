import { Component, OnInit,ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { OrderService } from '../../../core/services/service/order.service';
import { Order } from '../../../core/model/order.model';

@Component({
  selector: 'app-order-detail',
  templateUrl: './order-detail.component.html',
  styleUrls: ['./order-detail.component.scss'],
   imports: [CommonModule, RouterModule]
})
export class OrderDetailComponent implements OnInit {
  order: Order | null = null;
  isLoading = true;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadOrderDetails();
  }

  private loadOrderDetails(): void {
    const orderNumber = this.route.snapshot.paramMap.get('id');
    console.log("参数id==="+orderNumber)
    
    if (orderNumber) {
      this.orderService.getOrderById(orderNumber).subscribe({
        next: (data) => {
          this.order = data;
          this.isLoading = false;
          // 手动触发变更检测
        this.cdr.detectChanges();
        },
        error: (error) => {
          this.errorMessage = '获取订单信息失败';
          this.isLoading = false;
          console.error('Error order:', error);
        this.cdr.detectChanges();
        }
      });
    } else {
      this.errorMessage = '无效的订单号';
      this.isLoading = false;
      this.cdr.detectChanges();
    }
  }

  // 在 OrderDetailComponent 类中添加
getOrderStatusText(status: string): string {
  const statusMap: { [key: string]: string } = {
    'PAID': '已支付',
    'PENDING': '待支付',
    'CANCELLED': '已取消',
    'REFUNDED': '已退款',
    'SHIPPED': '已发货',
    'DELIVERED': '已送达'
  };
  return statusMap[status] || status;
}

// 返回上一页
goBack(): void {
  window.history.back();
}
}
