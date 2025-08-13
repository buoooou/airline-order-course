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
    return this.http.post<OrderDto>(`/api/orders`, request, {
      headers: this.getHeaders()
    });
  }

  getAllOrders(): Observable<OrderDto[]> {
    return this.http.get<OrderDto[]>(`/api/orders/my`, {
      headers: this.getHeaders()
    });
  }

  getOrderById(id: number): Observable<OrderDto> {
    return this.http.get<OrderDto>(`/api/orders/${id}`, {
      headers: this.getHeaders()
    });
  }

  // 订单操作方法
  payOrder(id: number): Observable<string> {
    return this.http.post<string>(`/api/orders/${id}/actions/pay`, {}, {
      headers: this.getHeaders()
    });
  }

  startTicketing(id: number): Observable<string> {
    return this.http.post<string>(`/api/orders/${id}/actions/start-ticketing`, {}, {
      headers: this.getHeaders()
    });
  }

  completeTicketing(id: number): Observable<string> {
    return this.http.post<string>(`/api/orders/${id}/actions/complete-ticketing`, {}, {
      headers: this.getHeaders()
    });
  }

  cancelOrder(id: number): Observable<string> {
    return this.http.post<string>(`/api/orders/${id}/actions/cancel`, {}, {
      headers: this.getHeaders()
    });
  }

  retryPayment(id: number): Observable<string> {
    return this.http.post<string>(`/api/orders/${id}/actions/retry-payment`, {}, {
      headers: this.getHeaders()
    });
  }

  retryTicketing(id: number): Observable<string> {
    return this.http.post<string>(`/api/orders/${id}/actions/retry-ticketing`, {}, {
      headers: this.getHeaders()
    });
  }
}
