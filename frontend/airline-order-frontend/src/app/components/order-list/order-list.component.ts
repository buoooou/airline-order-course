import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './order-list.component.html',
  styleUrl: './order-list.component.scss'
})
export class OrderListComponent implements OnInit {
  isLoading = false;
  error: string | null = null;
  orders: any[] = [];
  
  // 分页信息
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;
  
  // 筛选条件
  statusFilter: string | null = null;

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  // 加载订单列表
  loadOrders(): void {
    this.isLoading = true;
    this.error = null;
    
    const userInfo = this.authService.getUserInfo();
    const userId = userInfo ? userInfo.id : null;
    
    if (!userId) {
      this.error = '请先登录';
      this.isLoading = false;
      return;
    }
    
    this.orderService.getOrders(userId, this.statusFilter || undefined, this.currentPage, this.pageSize)
      .subscribe({
        next: (response) => {
          this.isLoading = false;
          if (response.success) {
            const pageData = response.data;
            this.orders = pageData.orders;
            this.totalPages = pageData.totalPages;
            this.totalElements = pageData.totalElements;
          } else {
            this.error = response.message || '获取订单列表失败';
          }
        },
        error: (err) => {
          this.isLoading = false;
          this.error = err.error?.message || '获取订单列表失败，请稍后再试';
        }
      });
  }

  // 查看订单详情
  viewOrderDetail(orderId: number): void {
    this.router.navigate(['/orders', orderId]);
  }

  // 取消订单
  cancelOrder(orderId: number, event: Event): void {
    event.stopPropagation();
    
    if (confirm('确定要取消该订单吗？')) {
      this.orderService.cancelOrder(orderId)
        .subscribe({
          next: (response) => {
            if (response.success) {
              // 刷新订单列表
              this.loadOrders();
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

  // 筛选订单
  filterByStatus(status: string | null): void {
    this.statusFilter = status;
    this.currentPage = 0;
    this.loadOrders();
  }

  // 翻页
  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadOrders();
    }
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