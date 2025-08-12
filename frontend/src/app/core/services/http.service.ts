import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { HTTP_CONFIG } from './http.config';

export interface ApiResponse<T = any> {
  /** 响应结果状态: SUCCESS - 成功, ERROR - 错误 */
  result: 'SUCCESS' | 'ERROR';
  /** 响应消息，通常用于描述响应结果或错误信息 */
  message: string;
  /** 响应数据，泛型字段，可以存储任意类型的响应数据 */
  data: T;
}

export interface HttpOptions {
  headers?: HttpHeaders | { [header: string]: string | string[] };
  params?: HttpParams | { [param: string]: string | string[] };
  observe?: 'body';
  responseType?: 'json';
  withCredentials?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class HttpService {
  private readonly baseUrl = HTTP_CONFIG.api.baseUrl;

  constructor(private http: HttpClient) {}

  /**
   * GET请求
   */
  get<T>(url: string, options?: HttpOptions): Observable<T> {
    return this.http.get<ApiResponse<T>>(`${this.baseUrl}${url}`, options)
      .pipe(
        map(response => this.handleResponse(response)),
        catchError(error => this.handleError(error))
      );
  }

  /**
   * POST请求
   */
  post<T>(url: string, body?: any, options?: HttpOptions): Observable<T> {
    return this.http.post<ApiResponse<T>>(`${this.baseUrl}${url}`, body, options)
      .pipe(
        map(response => this.handleResponse(response)),
        catchError(error => this.handleError(error))
      );
  }

  /**
   * PUT请求
   */
  put<T>(url: string, body?: any, options?: HttpOptions): Observable<T> {
    return this.http.put<ApiResponse<T>>(`${this.baseUrl}${url}`, body, options)
      .pipe(
        map(response => this.handleResponse(response)),
        catchError(error => this.handleError(error))
      );
  }

  /**
   * DELETE请求
   */
  delete<T>(url: string, options?: HttpOptions): Observable<T> {
    return this.http.delete<ApiResponse<T>>(`${this.baseUrl}${url}`, options)
      .pipe(
        map(response => this.handleResponse(response)),
        catchError(error => this.handleError(error))
      );
  }

  /**
   * PATCH请求
   */
  patch<T>(url: string, body?: any, options?: HttpOptions): Observable<T> {
    return this.http.patch<ApiResponse<T>>(`${this.baseUrl}${url}`, body, options)
      .pipe(
        map(response => this.handleResponse(response)),
        catchError(error => this.handleError(error))
      );
  }

  /**
   * 上传文件
   */
  upload<T>(url: string, file: File, fieldName = 'file', additionalData?: any): Observable<T> {
    const formData = new FormData();
    formData.append(fieldName, file);
    
    if (additionalData) {
      Object.keys(additionalData).forEach(key => {
        formData.append(key, additionalData[key]);
      });
    }

    return this.http.post<ApiResponse<T>>(`${this.baseUrl}${url}`, formData, {
      headers: new HttpHeaders({
        'Accept': 'application/json'
      })
    }).pipe(
      map(response => this.handleResponse(response)),
      catchError(error => this.handleError(error))
    );
  }

  /**
   * 下载文件
   */
  download(url: string, options?: HttpOptions): Observable<Blob> {
    return this.http.get(`${this.baseUrl}${url}`, {
      ...options,
      // responseType: 'blob' as 'json'
      responseType: 'blob'
    }).pipe(
      catchError(error => this.handleError(error))
    ) as Observable<Blob>;
  }

  /**
   * 处理响应数据
   */
  private handleResponse<T>(response: ApiResponse<T>): T {
    if (response.result === 'SUCCESS') {
      return response.data;
    } else {
      throw new Error(response.message || '请求失败');
    }
  }

  /**
   * 处理错误
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    const errorMessage = HTTP_CONFIG.errorCodes[error.status] || 
                        (error.error instanceof ErrorEvent 
                          ? `错误: ${error.error.message}`
                          : `服务器错误: ${error.status}`);

    console.error('HTTP请求错误:', errorMessage, error);
    return throwError(() => new Error(errorMessage));
  }

  /**
   * 创建标准请求头
   */
  createHeaders(contentType = 'application/json'): HttpHeaders {
    let headers = new HttpHeaders();
    headers = headers.set('Content-Type', contentType);
    
    // 可以在这里添加认证token
    const token = localStorage.getItem('token');
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }

    return headers;
  }

  /**
   * 创建查询参数
   */
  createParams(params: { [key: string]: any }): HttpParams {
    let httpParams = new HttpParams();
    Object.keys(params).forEach(key => {
      if (params[key] !== null && params[key] !== undefined) {
        httpParams = httpParams.set(key, params[key].toString());
      }
    });
    return httpParams;
  }
}