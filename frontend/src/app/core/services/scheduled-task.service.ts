import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  error?: string;
  pagination?: any;
}

export interface TaskConfig {
  paymentTimeoutMinutes: number;
  ticketingTimeoutMinutes: number;
  ticketingFailedTimeoutHours: number;
  scheduledEnabled: boolean;
  currentTime: string;
  description: string;
}

export interface TaskExecution {
  executionTime: string;
  taskType: string;
  status: string;
}

export interface SystemHealth {
  status: string;
  timestamp: string;
  scheduledTasksEnabled: boolean;
  databaseConnected: boolean;
  shedlockEnabled: boolean;
  version: {
    application: string;
    shedlock: string;
  };
}

export interface TaskStatistics {
  orderStatistics: any;
  currentTime: string;
  taskConfiguration: any;
  systemStatus: string;
}

@Injectable({
  providedIn: 'root'
})
export class ScheduledTaskService {
  private apiUrl = `${environment.apiUrl}/api/admin/scheduled-tasks`;

  constructor(private http: HttpClient) {}

  /**
   * 获取定时任务配置
   */
  getTaskConfig(): Promise<ApiResponse<TaskConfig>> {
    return this.http.get<ApiResponse<TaskConfig>>(`${this.apiUrl}/config`).toPromise() as Promise<ApiResponse<TaskConfig>>;
  }

  /**
   * 获取系统健康状态
   */
  getSystemHealth(): Promise<ApiResponse<SystemHealth>> {
    return this.http.get<ApiResponse<SystemHealth>>(`${this.apiUrl}/health`).toPromise() as Promise<ApiResponse<SystemHealth>>;
  }

  /**
   * 获取任务统计信息
   */
  getTaskStatistics(): Promise<ApiResponse<TaskStatistics>> {
    return this.http.get<ApiResponse<TaskStatistics>>(`${this.apiUrl}/statistics`).toPromise() as Promise<ApiResponse<TaskStatistics>>;
  }

  /**
   * 手动执行任务
   */
  executeTask(taskType: string): Promise<ApiResponse<TaskExecution>> {
    return this.http.post<ApiResponse<TaskExecution>>(`${this.apiUrl}/${taskType}`, {}).toPromise() as Promise<ApiResponse<TaskExecution>>;
  }

  /**
   * 取消超时待支付订单
   */
  cancelTimeoutPaymentOrders(): Promise<ApiResponse<TaskExecution>> {
    return this.executeTask('cancel-timeout-payment-orders');
  }

  /**
   * 处理超时出票订单
   */
  handleTimeoutTicketingOrders(): Promise<ApiResponse<TaskExecution>> {
    return this.executeTask('handle-timeout-ticketing-orders');
  }

  /**
   * 取消长时间失败订单
   */
  cancelLongTimeFailedOrders(): Promise<ApiResponse<TaskExecution>> {
    return this.executeTask('cancel-long-time-failed-orders');
  }

  /**
   * 执行每日维护任务
   */
  dailyMaintenance(): Promise<ApiResponse<TaskExecution>> {
    return this.executeTask('daily-maintenance');
  }
}
