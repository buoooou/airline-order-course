import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

interface Order {
  id: string;
  passengerName: string;
  status: string;
  flightNumber: string;
  departureTime: string;
}

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.scss']
})
export class OrderListComponent implements OnInit {
  orders: Order[] = [];
  isLoading: boolean = true;
  error: string | null = null;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.fetchOrders();
  }

  fetchOrders() {
    this.isLoading = true;
    this.error = null;
    this.http.get<Order[]>(`${environment.apiUrl}/orders/`).subscribe({
      next: (data) => {
        this.orders = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = '获取订单数据失败，请稍后重试';
        this.isLoading = false;
        console.error('API请求失败:', err);
      }
    });
  }
}