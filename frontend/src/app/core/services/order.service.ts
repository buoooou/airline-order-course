import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { ApiResponseDTO } from '../../shared/models/api-response.model';
import { Order } from '../../shared/models/order.model';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private readonly apiUrl = '/api/orders';

  constructor(private http: HttpClient, private authService: AuthService) {}

  // 获取所有订单
  getAllOrders(): Observable<ApiResponseDTO<Order[]>> {
    const userId = this.authService.getCurrentUserId();
    return this.http.get<ApiResponseDTO<Order[]>>(
      `${this.apiUrl}/all?userid=${userId}`
    );
  }

  // 根据订单号获取订单
  getOrderByOrderNumber(
    orderNumber: string
  ): Observable<ApiResponseDTO<Order>> {
    return this.http.get<ApiResponseDTO<Order>>(
      `${this.apiUrl}/${orderNumber}`
    );
  }

  // 支付订单
  payOrder(orderId: number): Observable<ApiResponseDTO<Order>> {
    return this.http.put<ApiResponseDTO<Order>>(
      `${this.apiUrl}/${orderId}/pay`,
      null
    );
  }

  // 取消订单
  cancelOrder(orderId: number): Observable<ApiResponseDTO<Order>> {
    return this.http.put<ApiResponseDTO<Order>>(
      `${this.apiUrl}/${orderId}/cancel`,
      null
    );
  }

  // 创建订单
  createOrder(orderData: Order): Observable<ApiResponseDTO<Order>> {
    return this.http.post<ApiResponseDTO<Order>>(this.apiUrl, orderData);
  }
}
