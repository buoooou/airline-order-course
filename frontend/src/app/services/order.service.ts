import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OrderDto, CreateOrderRequest } from '../models/order.model';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = environment.apiUrl || '';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    return headers;
  }

  createOrder(request: CreateOrderRequest): Observable<OrderDto> {
    const url = this.apiUrl ? `${this.apiUrl}/orders` : `/orders`;
    return this.http.post<OrderDto>(url, request, {
      headers: this.getHeaders()
    });
  }

  getAllOrders(): Observable<OrderDto[]> {
    const url = this.apiUrl ? `${this.apiUrl}/orders/my` : `/orders/my`;
    return this.http.get<OrderDto[]>(url, {
      headers: this.getHeaders()
    });
  }

  getOrderById(id: number): Observable<OrderDto> {
    const url = this.apiUrl ? `${this.apiUrl}/orders/${id}` : `/orders/${id}`;
    return this.http.get<OrderDto>(url, {
      headers: this.getHeaders()
    });
  }

  // 订单操作方法
  payOrder(id: number): Observable<string> {
    const url = this.apiUrl ? `${this.apiUrl}/orders/${id}/actions/pay` : `/orders/${id}/actions/pay`;
    return this.http.post<string>(url, {}, {
      headers: this.getHeaders()
    });
  }

  startTicketing(id: number): Observable<string> {
    const url = this.apiUrl ? `${this.apiUrl}/orders/${id}/actions/start-ticketing` : `/orders/${id}/actions/start-ticketing`;
    return this.http.post<string>(url, {}, {
      headers: this.getHeaders()
    });
  }

  completeTicketing(id: number): Observable<string> {
    const url = this.apiUrl ? `${this.apiUrl}/orders/${id}/actions/complete-ticketing` : `/orders/${id}/actions/complete-ticketing`;
    return this.http.post<string>(url, {}, {
      headers: this.getHeaders()
    });
  }

  cancelOrder(id: number): Observable<string> {
    const url = this.apiUrl ? `${this.apiUrl}/orders/${id}/actions/cancel` : `/orders/${id}/actions/cancel`;
    return this.http.post<string>(url, {}, {
      headers: this.getHeaders()
    });
  }

  retryPayment(id: number): Observable<string> {
    const url = this.apiUrl ? `${this.apiUrl}/orders/${id}/actions/retry-payment` : `/orders/${id}/actions/retry-payment`;
    return this.http.post<string>(url, {}, {
      headers: this.getHeaders()
    });
  }

  retryTicketing(id: number): Observable<string> {
    const url = this.apiUrl ? `${this.apiUrl}/orders/${id}/actions/retry-ticketing` : `/orders/${id}/actions/retry-ticketing`;
    return this.http.post<string>(url, {}, {
      headers: this.getHeaders()
    });
  }
}
