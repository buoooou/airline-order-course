import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError, retry, timeout } from 'rxjs/operators';
import {
  Flight,
  FlightSearchCriteria,
  FlightSearchResult,
  ApiResponse,
  PaginationParams,
  Airport,
  Airline
} from '../../shared/models';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FlightService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  /**
   * 搜索航班
   */
  searchFlights(
    criteria: FlightSearchCriteria,
    pagination: PaginationParams = { page: 0, size: 10 }
  ): Observable<FlightSearchResult> {
    const searchDto = {
      departureAirportCode: criteria.departureAirportCode,
      arrivalAirportCode: criteria.arrivalAirportCode,
      departureDate: criteria.departureDate
    };

    let params = new HttpParams()
      .set('page', pagination.page.toString())
      .set('size', pagination.size.toString());

    if (pagination.sortBy) {
      params = params.set('sortBy', pagination.sortBy);
    }
    if (pagination.sortDir) {
      params = params.set('sortDir', pagination.sortDir);
    }

    return this.http.post<ApiResponse<FlightSearchResult>>(
      `${this.apiUrl}/flights/search`,
      searchDto,
      { params }
    ).pipe(
      map(response => response.data),
      catchError(this.handleError),
      retry(2),
      timeout(10000)
    );
  }

  /**
   * 根据ID获取航班详情
   */
  getFlightById(id: number): Observable<Flight> {
    return this.http.get<ApiResponse<Flight>>(`${this.apiUrl}/flights/${id}`)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  /**
   * 根据航班号获取航班
   */
  getFlightByNumber(flightNumber: string): Observable<Flight> {
    return this.http.get<ApiResponse<Flight>>(`${this.apiUrl}/flights/number/${flightNumber}`)
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  /**
   * 获取所有航班（分页）
   */
  getAllFlights(pagination: PaginationParams = { page: 0, size: 10 }): Observable<FlightSearchResult> {
    let params = new HttpParams()
      .set('page', pagination.page.toString())
      .set('size', pagination.size.toString())
      .set('sortBy', pagination.sortBy || 'departureTime')
      .set('sortDir', pagination.sortDir || 'asc');

    return this.http.get<ApiResponse<FlightSearchResult>>(`${this.apiUrl}/flights`, { params })
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  /**
   * 获取可用航班
   */
  getAvailableFlights(
    departureCode: string,
    arrivalCode: string,
    startDate: string,
    endDate: string
  ): Observable<Flight[]> {
    let params = new HttpParams()
      .set('departureAirportCode', departureCode)
      .set('arrivalAirportCode', arrivalCode)
      .set('startDate', startDate)
      .set('endDate', endDate);

    return this.http.get<ApiResponse<Flight[]>>(`${this.apiUrl}/flights/available`, { params })
      .pipe(
        map(response => response.data),
        catchError(this.handleError)
      );
  }

  /**
   * 搜索机场
   */
  searchAirports(keyword: string, pagination: PaginationParams = { page: 0, size: 20 }): Observable<Airport[]> {
    let params = new HttpParams()
      .set('keyword', keyword)
      .set('page', pagination.page.toString())
      .set('size', pagination.size.toString());

    return this.http.get<ApiResponse<{ content: Airport[] }>>(`${this.apiUrl}/airports/search`, { params })
      .pipe(
        map(response => response.data.content),
        catchError(this.handleError)
      );
  }

  /**
   * 搜索航空公司
   */
  searchAirlines(keyword: string, pagination: PaginationParams = { page: 0, size: 20 }): Observable<Airline[]> {
    let params = new HttpParams()
      .set('keyword', keyword)
      .set('page', pagination.page.toString())
      .set('size', pagination.size.toString());

    return this.http.get<ApiResponse<{ content: Airline[] }>>(`${this.apiUrl}/airlines/search`, { params })
      .pipe(
        map(response => response.data.content),
        catchError(this.handleError)
      );
  }

  /**
   * 更新座位可用性
   */
  updateSeatAvailability(flightId: number, seatChange: number): Observable<void> {
    let params = new HttpParams().set('seatChange', seatChange.toString());
    
    return this.http.post<ApiResponse<void>>(
      `${this.apiUrl}/flights/${flightId}/seats/update`,
      null,
      { params }
    ).pipe(
      map(() => void 0),
      catchError(this.handleError)
    );
  }

  /**
   * 错误处理
   */
  private handleError(error: any): Observable<never> {
    console.error('FlightService Error:', error);
    
    let errorMessage = '服务器错误，请稍后重试';
    
    if (error.error?.message) {
      errorMessage = error.error.message;
    } else if (error.status === 0) {
      errorMessage = '网络连接失败，请检查网络设置';
    } else if (error.status === 404) {
      errorMessage = '请求的资源不存在';
    } else if (error.status === 500) {
      errorMessage = '服务器内部错误';
    }
    
    return throwError(() => new Error(errorMessage));
  }
}