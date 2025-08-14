import { HttpClient } from "@angular/common/http";
import { Observable, tap } from "rxjs";
import { Order } from "../../shared/models/order.model";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private apiUrl = '/api/orders';

  constructor(private http: HttpClient) {}

  getOrders(): Observable<any> {
    return this.http.get(this.apiUrl);
  }

  getOrderById(id: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  pay(id: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/${id}/pay`, {});
  }

  cancel(id: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/${id}/cancel`, {});
  }

  retryTicketing(id: string): Observable<void> {
    // 这个请求会立即返回 202，后端在后台处理
    return this.http.post<void>(`${this.apiUrl}/${id}/retry-ticketing`, {});
  }
}
