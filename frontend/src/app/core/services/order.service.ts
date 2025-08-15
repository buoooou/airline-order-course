import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import {
  Order,
  BookingRequest,
  PaymentRequest,
  PaymentMethod,
  ApiResponse,
  PaginationParams
} from '../../shared/models';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  /**
   * 创建订单
   */
  createOrder(bookingRequest: BookingRequest): Observable<Order> {
    return this.http.post<ApiResponse<Order>>(`${this.apiUrl}/orders`, bookingRequest)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  /**
   * 获取用户订单列表
   */
  getUserOrders(pagination: PaginationParams = { page: 0, size: 10 }): Observable<{ content: Order[], totalElements: number }> {
    let params = new HttpParams()
      .set('page', pagination.page.toString())
      .set('size', pagination.size.toString());

    return this.http.get<ApiResponse<{ content: Order[], totalElements: number }>>(`${this.apiUrl}/orders`, { params })
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  /**
   * 根据ID获取订单详情
   */
  getOrderById(id: number): Observable<Order> {
    return this.http.get<ApiResponse<Order>>(`${this.apiUrl}/orders/${id}`)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  /**
   * 支付订单
   */
  payOrder(orderId: number, paymentMethod: PaymentMethod): Observable<Order> {
    const paymentRequest: PaymentRequest = {
      orderId,
      paymentMethod,
      amount: 0 // 后端会计算实际金额
    };

    return this.http.post<ApiResponse<Order>>(`${this.apiUrl}/orders/${orderId}/pay`, paymentRequest)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  /**
   * 取消订单
   */
  cancelOrder(orderId: number): Observable<Order> {
    return this.http.put<ApiResponse<Order>>(`${this.apiUrl}/orders/${orderId}/cancel`, {})
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  /**
   * 获取订单统计信息
   */
  getOrderStats(): Observable<any> {
    return this.http.get<ApiResponse<any>>(`${this.apiUrl}/orders/stats`)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  /**
   * 错误处理
   */
  private handleError(error: any): Observable<never> {
    console.error('OrderService Error:', error);
    
    let errorMessage = '服务器错误，请稍后重试';
    
    if (error.error?.message) {
      errorMessage = error.error.message;
    } else if (error.status === 0) {
      errorMessage = '网络连接失败，请检查网络设置';
    } else if (error.status === 401) {
      errorMessage = '请先登录';
    } else if (error.status === 403) {
      errorMessage = '权限不足';
    } else if (error.status === 404) {
      errorMessage = '订单不存在';
    } else if (error.status === 500) {
      errorMessage = '服务器内部错误';
    }
    
    return throwError(() => new Error(errorMessage));
  }
}