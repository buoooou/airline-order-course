import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FlightService {
  private apiUrl = `${environment.apiUrl}/flights`;

  constructor(private http: HttpClient) { }

  /**
   * 获取所有航班信息
   * @returns 航班信息列表
   */
  getAllFlights(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }

  /**
   * 根据ID获取航班信息
   * @param id 航班ID
   * @returns 航班信息
   */
  getFlightById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  /**
   * 根据航班号获取航班信息
   * @param flightNumber 航班号
   * @returns 航班信息
   */
  getFlightByNumber(flightNumber: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/number/${flightNumber}`);
  }

  /**
   * 根据出发和到达机场代码查询航班
   * @param departureCode 出发机场代码
   * @param arrivalCode 到达机场代码
   * @returns 航班信息列表
   */
  searchFlightsByRoute(departureCode: string, arrivalCode: string): Observable<any> {
    let params = new HttpParams()
      .set('departureCode', departureCode)
      .set('arrivalCode', arrivalCode);
    
    return this.http.get<any>(`${this.apiUrl}/search/route`, { params });
  }

  /**
   * 根据出发日期查询航班
   * @param date 出发日期
   * @returns 航班信息列表
   */
  searchFlightsByDate(date: Date): Observable<any> {
    let params = new HttpParams()
      .set('date', date.toISOString());
    
    return this.http.get<any>(`${this.apiUrl}/search/date`, { params });
  }

  /**
   * 根据航班号关键字模糊查询
   * @param keyword 航班号关键字
   * @returns 航班信息列表
   */
  searchFlightsByNumberKeyword(keyword: string): Observable<any> {
    let params = new HttpParams()
      .set('keyword', keyword);
    
    return this.http.get<any>(`${this.apiUrl}/search/number`, { params });
  }

  /**
   * 根据飞行时长范围查询航班
   * @param minDuration 最小飞行时长（分钟）
   * @param maxDuration 最大飞行时长（分钟）
   * @returns 航班信息列表
   */
  searchFlightsByDuration(minDuration: number, maxDuration: number): Observable<any> {
    let params = new HttpParams()
      .set('minDuration', minDuration.toString())
      .set('maxDuration', maxDuration.toString());
    
    return this.http.get<any>(`${this.apiUrl}/search/duration`, { params });
  }
}