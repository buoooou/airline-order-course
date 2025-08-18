import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Order } from '../../shared/models/order.model';

// 定义API响应接口
interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp?: number;
}

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private apiUrl = '/api/orders';

  constructor(private http: HttpClient) {}

  // 获取订单列表
  getOrders(): Observable<Order[]> {
    return this.http.get<ApiResponse<Order[]>>(this.apiUrl).pipe(
      map((response) => response.data) // 提取data字段
    );
  }

  // 获取单个订单
  getOrderById(id: string): Observable<Order> {
    return this.http
      .get<ApiResponse<Order>>(`${this.apiUrl}/${id}`)
      .pipe(map((response) => response.data));
  }

  // 支付订单
  pay(id: string): Observable<Order> {
    return this.http
      .post<ApiResponse<Order>>(`${this.apiUrl}/${id}/pay`, {})
      .pipe(map((response) => response.data));
  }

  // 取消订单
  cancel(id: string): Observable<Order> {
    return this.http
      .post<ApiResponse<Order>>(`${this.apiUrl}/${id}/cancel`, {})
      .pipe(map((response) => response.data));
  }

  // 重试出票
  retryTicketing(id: string): Observable<void> {
    return this.http
      .post<ApiResponse<void>>(`${this.apiUrl}/${id}/retry-ticketing`, {})
      .pipe(
        map((response) => undefined) // 因为返回的是void
      );
  }
}
