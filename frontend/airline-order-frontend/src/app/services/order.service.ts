import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = `${environment.apiUrl}/orders`;

  constructor(private http: HttpClient) { }

  /**
   * 创建新订单
   * @param orderData 订单数据
   * @returns 创建结果
   */
  createOrder(orderData: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, orderData);
  }

  /**
   * 查询订单列表
   * @param userId 用户ID（可选）
   * @param status 订单状态（可选）
   * @param page 页码
   * @param size 每页大小
   * @returns 订单列表
   */
  getOrders(userId?: number, status?: string, page: number = 0, size: number = 10): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    if (userId) {
      params = params.set('userId', userId.toString());
    }
    
    if (status) {
      params = params.set('status', status);
    }
    
    return this.http.get<any>(this.apiUrl, { params });
  }

  /**
   * 根据ID获取订单详情
   * @param orderId 订单ID
   * @returns 订单详情
   */
  getOrderById(orderId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${orderId}`);
  }

  /**
   * 取消订单
   * @param orderId 订单ID
   * @returns 取消结果
   */
  cancelOrder(orderId: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${orderId}/cancel`, {});
  }

  /**
   * 修改订单状态
   * @param orderId 订单ID
   * @param status 新状态
   * @returns 修改结果
   */
  updateOrderStatus(orderId: number, status: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${orderId}/status`, { status });
  }

  /**
   * 根据订单号查询订单
   * @param orderNumber 订单号
   * @returns 订单信息
   */
  getOrderByNumber(orderNumber: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/by-number/${orderNumber}`);
  }
}