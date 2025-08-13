import { Injectable } from '@angular/core';
import { Observable, of, from } from 'rxjs';
import { ApiService } from './api.service';
import { tap, map } from 'rxjs/operators';
import { Order } from '../models/order.model';
import { CacheService } from './cache.service';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  constructor(private cacheService: CacheService, private apiService: ApiService) {}

  private maskPhoneNumber(phone: string | undefined): string {
    if (!phone) return '';
    return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
  }
  private cacheKey = 'orders_cache';
  private cacheExpiry = 300000; // 5 minutes in milliseconds
  private orders: Order[] = [];

  getOrders(page: number = 1, pageSize: number = 10, bypassCache: boolean = false): Observable<{ orders: Order[], total: number }> {
    const cacheKey = `${this.cacheKey}_page${page}_size${pageSize}`;
    if (!bypassCache) {
      const cachedData = this.cacheService.get(cacheKey);
      if (cachedData) {
        console.log('[OrderService] 缓存命中，直接返回缓存数据');
        return of({
          orders: cachedData.content || [],
          total: cachedData.totalElements || 0
        });
      }
    }
    console.log(`[OrderService] 从接口获取订单数据，页码: ${page}, 每页大小: ${pageSize}`);
    return this.apiService.getOrdersWithPagination(page - 1, pageSize).pipe(
      map((result: any) => {
        console.log('[OrderService] 接口原始数据:', result);
        return {
          orders: result?.orders || [],
          total: result?.total || 0
        };
      }),
      tap((normalizedResult) => {
        console.log('[OrderService] 格式化后的数据:', normalizedResult);
        this.cacheService.set(cacheKey, {
          content: normalizedResult.orders,
          totalElements: normalizedResult.total
        }, this.cacheExpiry);
      })
    );
  }
  rebookOrder(id: string): Observable<void> {
    return this.apiService.rebookOrder(id);
  }

  searchOrder(orderId: string): Observable<Order | null> {
    return this.apiService.searchOrder(orderId).pipe(
      map((order: Order | null) => {
        if (order) {
          order.passengerPhone = this.maskPhoneNumber(order.passengerPhone);
        }
        return order;
      })
    );
  }

  getOrderById(id: string): Observable<Order | undefined> {
    return this.apiService.getOrderById(id).pipe(
      map((order: Order | undefined) => {
        if (order) {
          order.passengerPhone = this.maskPhoneNumber(order.passengerPhone);
        }
        return order;
      })
    );
  }

  updateOrderStatus(orderId: string, status: Order['status']): Observable<Order> {
    return this.apiService.updateOrderStatus(orderId, status).pipe(
      map((order: Order) => {
        order.passengerPhone = this.maskPhoneNumber(order.passengerPhone);
        return order;
      })
    );
  }

  cancelOrder(id: string): Observable<void> {
    return this.apiService.cancelOrder(id);
  }

  searchOrders(keyword: string): Observable<Order[]> {
    return this.apiService.searchOrders(keyword).pipe(
      map((orders: Order[]) => orders.map((order: Order) => ({
        ...order,
        passengerPhone: this.maskPhoneNumber(order.passengerPhone)
      })))
    );
  }
}