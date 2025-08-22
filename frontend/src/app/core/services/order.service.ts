import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order } from '../models/order.model';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
    private apiUrl = `${environment.apiUrl}/api/orders`;

    constructor(private http: HttpClient) { }

    getOrders(): Observable<ApiResponse<Order[]>> {
        return this.http.get<ApiResponse<Order[]>>(this.apiUrl);
    }

    getOrderById(id: number): Observable<ApiResponse<Order>> {
        return this.http.get<ApiResponse<Order>>(`${this.apiUrl}/${id}`);
    }

    payOrder(id: number): Observable<ApiResponse<Order>> {
        return this.http.post<ApiResponse<Order>>(`${this.apiUrl}/${id}/pay`, {});
    }

    cancelOrder(id: number): Observable<ApiResponse<Order>> {
        return this.http.post<ApiResponse<Order>>(`${this.apiUrl}/${id}/cancel`, {});
    }

    retryTicketing(id: number): Observable<ApiResponse<Order>> {
        return this.http.post<ApiResponse<Order>>(`${this.apiUrl}/${id}/retry-ticketing`, {});
    }
}