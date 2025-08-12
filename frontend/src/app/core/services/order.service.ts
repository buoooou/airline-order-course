import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OrderDto, ApiResponse } from '../models/order.model';
import { HTTP_CONFIG } from './http.config';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  constructor(private http: HttpClient) {}

  getUserOrders(): Observable<ApiResponse<OrderDto[]>> {
    return this.http.get<ApiResponse<OrderDto[]>>(`${HTTP_CONFIG.api.baseUrl}/user/orders`);
  }
}