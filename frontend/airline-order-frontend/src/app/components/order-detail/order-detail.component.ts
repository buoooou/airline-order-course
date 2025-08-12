import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-detail.component.html',
  styleUrl: './order-detail.component.scss'
})
export class OrderDetailComponent implements OnInit {
  isLoading = false;
  error: string | null = null;
  order: any = null;
  orderId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.orderId = +idParam;
        this.loadOrderDetail();
      } else {
        this.error = '订单ID无效';
      }
    });
  }

  // 加载订单详情
  loadOrderDetail(): void {
    if (!this.orderId) {
      this.error = '订单ID无效';
      return;
    }

    this.isLoading = true;
    this.error = null;
    
    this.orderService.getOrderById(this.orderId)
      .subscribe({
        next: (response) => {
          this.isLoading = false;
          if (response.success) {
            this.order = response.data;
          } else {
            this.error = response.message || '获取订单详情失败';
          }
        },
        error: (err) => {
          this.isLoading = false;
          this.error = err.error?.message || '获取订单详情失败，请稍后再试';
        }
      });
  }

  // 取消订单
  cancelOrder(): void {
    if (!this.orderId) {
      this.error = '订单ID无效';
      return;
    }

    if (confirm('确定要取消该订单吗？')) {
      this.orderService.cancelOrder(this.orderId)
        .subscribe({
          next: (response) => {
            if (response.success) {
              // 刷新订单详情
              this.loadOrderDetail();
            } else {
              this.error = response.message || '取消订单失败';
            }
          },
          error: (err) => {
            this.error = err.error?.message || '取消订单失败，请稍后再试';
          }
        });
    }
  }

  // 返回订单列表
  goBack(): void {
    this.router.navigate(['/orders']);
  }

  // 获取订单状态的中文名称
  getStatusText(status: string): string {
    const statusMap: { [key: string]: string } = {
      'PENDING': '待支付',
      'PAID': '已支付',
      'TICKETING': '出票中',
      'TICKETED': '已出票',
      'CANCELED': '已取消',
      'REFUNDING': '退款中',
      'REFUNDED': '已退款'
    };
    
    return statusMap[status] || status;
  }

  // 获取订单状态的样式类
  getStatusClass(status: string): string {
    const statusClassMap: { [key: string]: string } = {
      'PENDING': 'status-pending',
      'PAID': 'status-paid',
      'TICKETING': 'status-processing',
      'TICKETED': 'status-success',
      'CANCELED': 'status-canceled',
      'REFUNDING': 'status-processing',
      'REFUNDED': 'status-refunded'
    };
    
    return statusClassMap[status] || '';
  }
}