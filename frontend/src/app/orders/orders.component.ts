import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { DatePipe, CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Order {
  id: number;
  name: string;
  status: string;
  createdAt: string;
  totalAmount: number;
  orderNumber: string;
}

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css'],
  imports: [DatePipe, FormsModule, CommonModule]
})
export class OrdersComponent implements OnInit {
  orders: Order[] = [];
  newOrder: Partial<Order> = {};
  isEditing = false;
  currentOrderId: number | null = null;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.http.get<Order[]>(`${environment.apiUrl}/orders`).subscribe({
      next: (data) => this.orders = data,
      error: (error) => {
        console.error('加载订单失败:', error);
        alert('加载订单失败，请稍后重试或联系管理员。');
      }
    });
  }

  addOrder() {
    this.http.post(`${environment.apiUrl}/orders`, this.newOrder).subscribe({
      next: () => {
        this.loadOrders();
        this.newOrder = {};
      },
      error: (error) => {
        console.error('添加订单失败:', error);
        alert('添加订单失败，请检查输入或联系管理员。');
      }
    });
  }

  editOrder(order: Order) {
    this.isEditing = true;
    this.currentOrderId = order.id;
    this.newOrder = { ...order };
  }

  updateOrder() {
    if (!this.currentOrderId) return;
    this.http.put(`${environment.apiUrl}/orders/${this.currentOrderId}`, this.newOrder).subscribe({
      next: () => {
        this.loadOrders();
        this.cancelEdit();
      },
      error: (error) => console.error('更新订单失败:', error)
    });
  }

  deleteOrder(id: number) {
    this.http.delete(`${environment.apiUrl}/orders/${id}`).subscribe({
      next: () => this.loadOrders(),
      error: (error) => console.error('删除订单失败:', error)
    });
  }

  cancelEdit() {
    this.isEditing = false;
    this.currentOrderId = null;
    this.newOrder = {};
  }
}