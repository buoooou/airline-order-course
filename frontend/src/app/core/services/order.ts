import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order, OrderStatus, OrderStatusUpdateRequest, OrderQueryParams } from '../models/order.model';
import { ApiResponse } from '../models/user.model';
import { AuthService } from './auth';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private readonly API_URL = 'http://localhost:8080/api/orders';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  /**
   * 获取订单列表
   * 根据用户角色决定调用不同的API端点
   */
  getOrders(params?: OrderQueryParams): Observable<ApiResponse<Order[]>> {
    let httpParams = new HttpParams();
    
    if (params) {
      if (params.page !== undefined) {
        httpParams = httpParams.set('page', params.page.toString());
      }
      if (params.size !== undefined) {
        httpParams = httpParams.set('size', params.size.toString());
      }
      if (params.status) {
        httpParams = httpParams.set('status', params.status);
      }
      if (params.orderNumber) {
        httpParams = httpParams.set('orderNumber', params.orderNumber);
      }
    }

    // 根据用户角色决定API端点
    const isAdmin = this.authService.isAdmin();
    const endpoint = isAdmin ? this.API_URL : `${this.API_URL}/my`;

    return this.http.get<ApiResponse<Order[]>>(endpoint, { params: httpParams });
  }

  /**
   * 根据ID获取订单详情
   */
  getOrderById(id: number): Observable<ApiResponse<Order>> {
    return this.http.get<ApiResponse<Order>>(`${this.API_URL}/${id}`);
  }

  /**
   * 更新订单状态
   */
  updateOrderStatus(id: number, statusUpdate: OrderStatusUpdateRequest): Observable<ApiResponse<Order>> {
    return this.http.put<ApiResponse<Order>>(`${this.API_URL}/${id}/status`, statusUpdate);
  }

  /**
   * 创建新订单
   */
  createOrder(order: Partial<Order>): Observable<ApiResponse<Order>> {
    return this.http.post<ApiResponse<Order>>(this.API_URL, order);
  }

  /**
   * 删除订单
   */
  deleteOrder(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/${id}`);
  }

  /**
   * 支付订单
   */
  payOrder(id: number): Observable<ApiResponse<Order>> {
    return this.http.put<ApiResponse<Order>>(`${this.API_URL}/${id}/pay`, {});
  }

  /**
   * 取消订单
   */
  cancelOrder(id: number, reason?: string): Observable<ApiResponse<Order>> {
    let httpParams = new HttpParams();
    if (reason) {
      httpParams = httpParams.set('reason', reason);
    }
    return this.http.put<ApiResponse<Order>>(`${this.API_URL}/${id}/cancel`, {}, { params: httpParams });
  }

  /**
   * 获取订单状态流转选项
   * 根据当前状态返回可以转换到的状态列表
   */
  getAvailableStatusTransitions(currentStatus: OrderStatus): OrderStatus[] {
    switch (currentStatus) {
      case OrderStatus.PENDING_PAYMENT:
        return [OrderStatus.PAID, OrderStatus.CANCELLED];
      case OrderStatus.PAID:
        return [OrderStatus.TICKETING_IN_PROGRESS, OrderStatus.CANCELLED];
      case OrderStatus.TICKETING_IN_PROGRESS:
        return [OrderStatus.TICKETED, OrderStatus.TICKETING_FAILED];
      case OrderStatus.TICKETING_FAILED:
        return [OrderStatus.TICKETING_IN_PROGRESS, OrderStatus.CANCELLED];
      case OrderStatus.TICKETED:
        return [OrderStatus.CANCELLED];
      case OrderStatus.CANCELLED:
        return []; // 已取消的订单不能再转换状态
      default:
        return [];
    }
  }

  /**
   * 检查状态转换是否有效
   */
  isValidStatusTransition(currentStatus: OrderStatus, newStatus: OrderStatus): boolean {
    const availableTransitions = this.getAvailableStatusTransitions(currentStatus);
    return availableTransitions.includes(newStatus);
  }
}
