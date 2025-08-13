import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError, InternalAxiosRequestConfig } from 'axios';
import { Injectable } from '@angular/core';
import { Observable, from } from 'rxjs';
import { map } from 'rxjs/operators';
import { Order } from '../models/order.model';

const API_BASE_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // 请求拦截器 - 添加JWT Token
    this.api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
      const token = localStorage.getItem('token');
      
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    }, (error: AxiosError) => {
      return Promise.reject(error);
    });
  }

  // 将 axios 的 Promise 转换为 Observable
  private toObservable<T>(promise: Promise<T>): Observable<T> {
    return from(promise);
  }

  // Order Service
  getOrdersWithPagination(page: number = 1, pageSize: number = 10): Observable<{ orders: Order[], total: number }> {
    return this.toObservable(
      this.api.get('/api/orders', {
        params: { page, pageSize }

      }).then((response: AxiosResponse) => {console.log('API Response:', response.data); return response.data; })
    );
  }

  rebookOrder(id: string): Observable<void> {
    return this.toObservable(
      this.api.patch(`/api/orders/${id}/rebook`, {}).then(() => {})
    );
  }

  searchOrder(orderId: string): Observable<Order | null> {
    return this.toObservable(
      this.api.get(`/api/orders/${orderId}`)
        .then((response: AxiosResponse) => response.data)
        .catch(() => null)
    );
  }

  getOrderById(id: string): Observable<Order | undefined> {
    return this.toObservable(
      this.api.get(`/api/orders/${id}`)
        .then((response: AxiosResponse) => response.data)
        .catch(() => undefined)
    );
  }

  updateOrderStatus(orderId: string, status: Order['status']): Observable<Order> {
    return this.toObservable(
      this.api.put(`/api/orders/${orderId}/status`, { status })
        .then((response: AxiosResponse) => response.data)
    );
  }

  cancelOrder(id: string): Observable<void> {
    return this.toObservable(
      this.api.post(`/api/orders/${id}/cancel`, {}).then(() => { console.log("helloooooo")})
    );
  }

  searchOrders(keyword: string): Observable<Order[]> {
    return this.toObservable(
      this.api.get('/api/orders/search', {
        params: { keyword }
      }).then((response: AxiosResponse) => response.data)
    );
  }

  payOrder(id: string): Observable<any> {
    return this.toObservable(
      this.api.post(`/api/orders/${id}/pay`)
        .then((response: AxiosResponse) => response.data)
        .catch((error: AxiosError) => {
          if (error.response?.status === 400) {
            throw new Error('订单已支付或已取消');
          } else if (error.response?.status === 402) {
            throw new Error('支付失败，余额不足');
          }
          throw new Error('支付失败，请稍后重试');
        })
    );
  }

  retryTicketing(id: string): Observable<any> {
    return this.toObservable(
      this.api.post(`/api/orders/${id}/retry-ticketing`)
        .then((response: AxiosResponse) => response.data)
        .catch((error: AxiosError) => {
          if (error.response?.status === 400) {
            throw new Error('订单状态不支持重新出票');
          } else if (error.response?.status === 409) {
            throw new Error('正在处理中，请勿重复操作');
          }
          throw new Error('重新出票失败，请稍后重试');
        })
    );
  }
}