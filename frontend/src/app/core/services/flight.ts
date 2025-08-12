import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface FlightInfo {
  id?: number;
  flightNumber: string;
  airline: string;
  departureAirport: string;
  arrivalAirport: string;
  departureTime: string;
  arrivalTime: string;
  price: number;
  availableSeats: number;
  totalSeats: number;
  status: 'SCHEDULED' | 'BOARDING' | 'DEPARTED' | 'ARRIVED' | 'CANCELLED' | 'DELAYED';
  aircraftType?: string;
  gate?: string;
  terminal?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  error?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class FlightService {
  private readonly API_URL = `${environment.apiUrl}/api/flights`;

  constructor(private http: HttpClient) { }

  /**
   * 获取所有航班
   */
  getAllFlights(): Observable<ApiResponse<FlightInfo[]>> {
    return this.http.get<ApiResponse<FlightInfo[]>>(this.API_URL);
  }

  /**
   * 根据ID获取航班详情
   */
  getFlightById(id: number): Observable<ApiResponse<FlightInfo>> {
    return this.http.get<ApiResponse<FlightInfo>>(`${this.API_URL}/${id}`);
  }

  /**
   * 根据航班号搜索
   */
  searchByFlightNumber(flightNumber: string): Observable<ApiResponse<FlightInfo[]>> {
    return this.http.get<ApiResponse<FlightInfo[]>>(`${this.API_URL}/search/number/${flightNumber}`);
  }

  /**
   * 根据航线搜索
   */
  searchByRoute(departureAirport: string, arrivalAirport: string): Observable<ApiResponse<FlightInfo[]>> {
    const params = new HttpParams()
      .set('departureAirport', departureAirport)
      .set('arrivalAirport', arrivalAirport);
    
    return this.http.get<ApiResponse<FlightInfo[]>>(`${this.API_URL}/search/route`, { params });
  }

  /**
   * 综合搜索航班
   */
  searchFlights(
    departureAirport?: string,
    arrivalAirport?: string,
    startTime?: string,
    endTime?: string,
    page: number = 0,
    size: number = 10,
    sort: string = 'departureTime'
  ): Observable<ApiResponse<PageResponse<FlightInfo>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    if (departureAirport) {
      params = params.set('departureAirport', departureAirport);
    }
    if (arrivalAirport) {
      params = params.set('arrivalAirport', arrivalAirport);
    }
    if (startTime) {
      params = params.set('startTime', startTime);
    }
    if (endTime) {
      params = params.set('endTime', endTime);
    }

    return this.http.get<ApiResponse<PageResponse<FlightInfo>>>(`${this.API_URL}/search`, { params });
  }

  /**
   * 获取可预订航班
   */
  getBookableFlights(): Observable<ApiResponse<FlightInfo[]>> {
    return this.http.get<ApiResponse<FlightInfo[]>>(`${this.API_URL}/bookable`);
  }

  /**
   * 根据价格范围搜索
   */
  searchByPriceRange(minPrice: number, maxPrice: number): Observable<ApiResponse<FlightInfo[]>> {
    const params = new HttpParams()
      .set('minPrice', minPrice.toString())
      .set('maxPrice', maxPrice.toString());
    
    return this.http.get<ApiResponse<FlightInfo[]>>(`${this.API_URL}/search/price`, { params });
  }

  /**
   * 创建航班（管理员功能）
   */
  createFlight(flight: FlightInfo): Observable<ApiResponse<FlightInfo>> {
    return this.http.post<ApiResponse<FlightInfo>>(this.API_URL, flight);
  }

  /**
   * 更新航班（管理员功能）
   */
  updateFlight(id: number, flight: FlightInfo): Observable<ApiResponse<FlightInfo>> {
    return this.http.put<ApiResponse<FlightInfo>>(`${this.API_URL}/${id}`, flight);
  }

  /**
   * 删除航班（管理员功能）
   */
  deleteFlight(id: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.API_URL}/${id}`);
  }
}
