import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface Order {
  id: number;
  orderNumber: string;
  status: OrderStatus;
  amount: number;
  creationDate: string;
  user: {
    id: number;
    username: string;
  };
  flightNumber: string;
  departureCity: string;
  arrivalCity: string;
  departureTime: string;
  arrivalTime: string;
}

export interface OrderRequest {
  flightNumber: string;
  departureCity: string;
  arrivalCity: string;
  departureTime: string;
  arrivalTime: string;
  amount: number;
}

export enum OrderStatus {
  PENDING_PAYMENT = 'PENDING_PAYMENT',
  PAID = 'PAID',
  TICKETING_IN_PROGRESS = 'TICKETING_IN_PROGRESS',
  TICKETING_FAILED = 'TICKETING_FAILED',
  TICKETED = 'TICKETED',
  CANCELLED = 'CANCELLED'
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  createOrder(orderRequest: OrderRequest): Observable<ApiResponse<Order>> {
    return this.http.post<ApiResponse<Order>>(`${this.apiUrl}/orders`, orderRequest, {
      headers: this.getHeaders()
    });
  }

  getUserOrders(): Observable<ApiResponse<Order[]>> {
    console.log('[OrderService] getUserOrders 请求发起');
    return this.http.get<ApiResponse<Order[]>>(`${this.apiUrl}/orders`, {
      headers: this.getHeaders()
    }).pipe(
      tap({
        next: (res) => console.log('[OrderService] getUserOrders 响应:', res),
        error: (err) => console.error('[OrderService] getUserOrders 错误:', err)
      })
    );
  }

  getOrderById(orderId: number): Observable<ApiResponse<Order>> {
    return this.http.get<ApiResponse<Order>>(`${this.apiUrl}/orders/${orderId}`, {
      headers: this.getHeaders()
    });
  }

  payOrder(orderId: number): Observable<ApiResponse<Order>> {
    console.log('[OrderService] payOrder 请求发起, orderId:', orderId);
    return this.http.post<ApiResponse<Order>>(`${this.apiUrl}/orders/${orderId}/pay`, {}, {
      headers: this.getHeaders()
    }).pipe(
      tap({
        next: (res) => console.log('[OrderService] payOrder 响应:', res),
        error: (err) => console.error('[OrderService] payOrder 错误:', err)
      })
    );
  }

  cancelOrder(orderId: number): Observable<ApiResponse<Order>> {
    return this.http.post<ApiResponse<Order>>(`${this.apiUrl}/orders/${orderId}/cancel`, {}, {
      headers: this.getHeaders()
    });
  }

  retryTicketing(orderId: number): Observable<ApiResponse<Order>> {
    return this.http.post<ApiResponse<Order>>(`${this.apiUrl}/orders/${orderId}/retry-ticketing`, {}, {
      headers: this.getHeaders()
    });
  }

  getStatusDisplayName(status: OrderStatus): string {
    const statusMap: { [key in OrderStatus]: string } = {
      [OrderStatus.PENDING_PAYMENT]: '待支付',
      [OrderStatus.PAID]: '已支付',
      [OrderStatus.TICKETING_IN_PROGRESS]: '出票中',
      [OrderStatus.TICKETING_FAILED]: '出票失败',
      [OrderStatus.TICKETED]: '已出票',
      [OrderStatus.CANCELLED]: '已取消'
    };
    return statusMap[status] || status;
  }

  getStatusColor(status: OrderStatus): string {
    const colorMap: { [key in OrderStatus]: string } = {
      [OrderStatus.PENDING_PAYMENT]: 'warning',
      [OrderStatus.PAID]: 'info',
      [OrderStatus.TICKETING_IN_PROGRESS]: 'primary',
      [OrderStatus.TICKETING_FAILED]: 'danger',
      [OrderStatus.TICKETED]: 'success',
      [OrderStatus.CANCELLED]: 'secondary'
    };
    return colorMap[status] || 'secondary';
  }
} 