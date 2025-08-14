import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Order } from '../../shared/models/order.model';
import { toNumber } from 'ng-zorro-antd/core/util';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  // private apiUrl = '/api/orders';
  private apiUrl = '/api/order';

  constructor(private http: HttpClient) {}

  getOrders(): Observable<Order[]> {
    return this.http
      .post<{
        success: boolean;
        code: number;
        message: string;
        data: Order[];
      }>(`${this.apiUrl}/getOfUser`, null)
      .pipe(map((res) => res.data));
    // return this.http.get<Order[]>(this.apiUrl);
  }

  getOrderById(id: string): Observable<Order> {
    // return this.http.get<Order>(`${this.apiUrl}/${id}`);
    return this.http
      .post<{
        success: boolean;
        code: number;
        message: string;
        data: Order;
      }>(`${this.apiUrl}/getById`, { orderId: toNumber(id) })
      .pipe(map((res) => res.data));
  }

  pay(id: string): Observable<Order> {
    // return this.http.post<Order>(`${this.apiUrl}/${id}/pay`, {});
    return this.http
      .post<{
        success: boolean;
        code: number;
        message: string;
        data: Order;
      }>(`${this.apiUrl}/pay`, { orderId: toNumber(id) })
      .pipe(map((res) => res.data));
  }

  cancel(id: string): Observable<Order> {
    // return this.http.post<Order>(`${this.apiUrl}/${id}/cancel`, {});
    return this.http
      .post<{
        success: boolean;
        code: number;
        message: string;
        data: Order;
      }>(`${this.apiUrl}/cancel`, { orderId: toNumber(id) })
      .pipe(map((res) => res.data));
  }

  retryTicketing(id: string): Observable<void> {
    // 这个请求会立即返回 202, 后端在后台处理
    // return this.http.post<void>(`${this.apiUrl}/${id}/retry-ticketing`, {});
    return this.http.post<void>(`${this.apiUrl}/retry`, {
      orderId: toNumber(id),
    });
  }
}
