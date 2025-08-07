import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { ScheduledTaskService, TaskConfig, SystemHealth, TaskStatistics, TaskExecution } from '../../core/services/scheduled-task.service';
import { AuthService } from '../../core/services/auth';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-scheduled-tasks',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    HttpClientModule,
    MatIconModule
  ],
  template: `
    <div class="scheduled-tasks-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1>定时任务管理中心</h1>
        <p>实时监控和管理ShedLock分布式定时任务</p>
      </div>

      <!-- 系统状态卡片 -->
      <div class="card status-card">
        <div class="card-header">
          <h3>系统状态</h3>
          <div class="refresh-controls">
            <button class="btn btn-primary" (click)="refreshSystemStatus()" [disabled]="loading">
              <span *ngIf="loading">刷新中...</span>
              <span *ngIf="!loading">🔄 刷新状态</span>
            </button>
            <button class="btn btn-secondary" (click)="toggleAutoRefresh()">
              {{ autoRefreshEnabled ? '⏸️ 停止自动刷新' : '▶️ 开启自动刷新' }}
            </button>
            <span class="last-update">最后更新: {{ lastUpdateTime | date:'yyyy-MM-dd HH:mm:ss' }}</span>
          </div>
        </div>
        <div class="card-body">
          <div class="status-grid">
            <div class="status-item">
              <div class="status-label">系统状态</div>
              <div class="status-value" [class]="'status-' + (systemHealth?.status === 'UP' ? 'success' : 'error')">
                {{ systemHealth?.status || 'UNKNOWN' }}
              </div>
            </div>
            <div class="status-item">
              <div class="status-label">定时任务</div>
              <div class="status-value" [class]="'status-' + (systemHealth?.scheduledTasksEnabled ? 'success' : 'error')">
                {{ systemHealth?.scheduledTasksEnabled ? '已启用' : '已禁用' }}
              </div>
            </div>
            <div class="status-item">
              <div class="status-label">数据库连接</div>
              <div class="status-value" [class]="'status-' + (systemHealth?.databaseConnected ? 'success' : 'error')">
                {{ systemHealth?.databaseConnected ? '正常' : '异常' }}
              </div>
            </div>
            <div class="status-item">
              <div class="status-label">ShedLock</div>
              <div class="status-value" [class]="'status-' + (systemHealth?.shedlockEnabled ? 'success' : 'error')">
                {{ systemHealth?.shedlockEnabled ? '已启用' : '已禁用' }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 订单统计卡片 -->
      <div class="card statistics-card">
        <div class="card-header">
          <h3>订单状态统计</h3>
          <span class="stats-subtitle">实时监控各状态订单数量</span>
        </div>
        <div class="card-body">
          <div class="stats-grid">
            <div class="stat-card total">
              <div class="stat-icon">📊</div>
              <div class="stat-content">
                <div class="stat-value">{{ getTotalOrders() }}</div>
                <div class="stat-label">订单总数</div>
                <div class="stat-desc">系统中所有订单</div>
              </div>
            </div>
            
            <div class="stat-card pending" [class.highlight]="getOrderCount('PENDING_PAYMENT') > 0">
              <div class="stat-icon">⏳</div>
              <div class="stat-content">
                <div class="stat-value">{{ getOrderCount('PENDING_PAYMENT') }}</div>
                <div class="stat-label">待支付订单</div>
                <div class="stat-desc">超过30分钟将被自动取消</div>
              </div>
            </div>
            
            <div class="stat-card paid">
              <div class="stat-icon">💰</div>
              <div class="stat-content">
                <div class="stat-value">{{ getOrderCount('PAID') }}</div>
                <div class="stat-label">已支付订单</div>
                <div class="stat-desc">等待开始出票</div>
              </div>
            </div>
            
            <div class="stat-card ticketing" [class.highlight]="getOrderCount('TICKETING_IN_PROGRESS') > 0">
              <div class="stat-icon">🎫</div>
              <div class="stat-content">
                <div class="stat-value">{{ getOrderCount('TICKETING_IN_PROGRESS') }}</div>
                <div class="stat-label">出票处理中</div>
                <div class="stat-desc">超过60分钟将标记为失败</div>
              </div>
            </div>
            
            <div class="stat-card failed" [class.highlight]="getOrderCount('TICKETING_FAILED') > 0">
              <div class="stat-icon">❌</div>
              <div class="stat-content">
                <div class="stat-value">{{ getOrderCount('TICKETING_FAILED') }}</div>
                <div class="stat-label">出票失败</div>
                <div class="stat-desc">超过24小时将被自动取消</div>
              </div>
            </div>
            
            <div class="stat-card success">
              <div class="stat-icon">✅</div>
              <div class="stat-content">
                <div class="stat-value">{{ getOrderCount('TICKETED') }}</div>
                <div class="stat-label">出票成功</div>
                <div class="stat-desc">订单处理完成</div>
              </div>
            </div>
            
            <div class="stat-card cancelled">
              <div class="stat-icon">🚫</div>
              <div class="stat-content">
                <div class="stat-value">{{ getOrderCount('CANCELLED') }}</div>
                <div class="stat-label">已取消订单</div>
                <div class="stat-desc">用户取消或系统自动取消</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 定时任务配置 -->
      <div class="card config-card">
        <div class="card-header">
          <h3>定时任务配置</h3>
        </div>
        <div class="card-body">
          <div class="config-grid">
            <div class="config-item">
              <span class="config-label">待支付订单超时时间:</span>
              <span class="config-value">{{ taskConfig?.paymentTimeoutMinutes || 0 }} 分钟</span>
            </div>
            <div class="config-item">
              <span class="config-label">出票中订单超时时间:</span>
              <span class="config-value">{{ taskConfig?.ticketingTimeoutMinutes || 0 }} 分钟</span>
            </div>
            <div class="config-item">
              <span class="config-label">出票失败订单自动取消时间:</span>
              <span class="config-value">{{ taskConfig?.ticketingFailedTimeoutHours || 0 }} 小时</span>
            </div>
            <div class="config-item">
              <span class="config-label">定时任务状态:</span>
              <span class="config-value" 
                    [class]="'status-' + (taskConfig?.scheduledEnabled ? 'success' : 'error')">
                {{ taskConfig?.scheduledEnabled ? '已启用' : '已禁用' }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 手动执行任务 -->
      <div class="card manual-tasks-card">
        <div class="card-header">
          <h3>手动执行任务</h3>
          <div class="task-info">
            <small>💡 这些是一次性执行的任务，执行完成后状态会重置</small>
          </div>
        </div>
        <div class="card-body">
          <div class="task-buttons">
            <button 
              class="btn btn-primary task-btn" 
              (click)="executeTask('cancel-timeout-payment-orders')"
              [disabled]="taskExecuting['cancel-timeout-payment-orders']">
              <span *ngIf="taskExecuting['cancel-timeout-payment-orders']">⏳ 正在执行...</span>
              <span *ngIf="!taskExecuting['cancel-timeout-payment-orders']">🕐 立即执行：取消超时待支付订单</span>
            </button>
            
            <button 
              class="btn btn-primary task-btn" 
              (click)="executeTask('handle-timeout-ticketing-orders')"
              [disabled]="taskExecuting['handle-timeout-ticketing-orders']">
              <span *ngIf="taskExecuting['handle-timeout-ticketing-orders']">⏳ 正在执行...</span>
              <span *ngIf="!taskExecuting['handle-timeout-ticketing-orders']">📄 立即执行：处理超时出票订单</span>
            </button>
            
            <button 
              class="btn btn-primary task-btn" 
              (click)="executeTask('cancel-long-time-failed-orders')"
              [disabled]="taskExecuting['cancel-long-time-failed-orders']">
              <span *ngIf="taskExecuting['cancel-long-time-failed-orders']">⏳ 正在执行...</span>
              <span *ngIf="!taskExecuting['cancel-long-time-failed-orders']">❌ 立即执行：取消长时间失败订单</span>
            </button>
            
            <button 
              class="btn btn-primary task-btn" 
              (click)="executeTask('daily-maintenance')"
              [disabled]="taskExecuting['daily-maintenance']">
              <span *ngIf="taskExecuting['daily-maintenance']">⏳ 正在执行...</span>
              <span *ngIf="!taskExecuting['daily-maintenance']">🔧 立即执行：每日维护任务</span>
            </button>
          </div>
          
          <div class="task-note">
            <div class="note-content">
              <mat-icon>info</mat-icon>
              <div class="note-text">
                <strong>说明：</strong>
                <ul>
                  <li>这些按钮用于手动触发定时任务，不是启动/停止开关</li>
                  <li>每次点击都会执行一次完整的任务流程</li>
                  <li>执行完成后可以在"任务执行历史"中查看结果</li>
                  <li>建议执行任务后切换到"订单管理"页面查看数据变化</li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 任务执行历史 -->
      <div class="card history-card">
        <div class="card-header">
          <h3>任务执行历史</h3>
        </div>
        <div class="card-body">
          <div class="timeline" *ngIf="taskExecutionHistory.length > 0">
            <div class="timeline-item" *ngFor="let execution of taskExecutionHistory">
              <div class="timeline-marker" [class]="'marker-' + (execution.status === 'completed' ? 'success' : 'error')"></div>
              <div class="timeline-content">
                <div class="timeline-title">{{ getTaskTypeName(execution.taskType) }}</div>
                <div class="timeline-time">{{ execution.executionTime | date:'yyyy-MM-dd HH:mm:ss' }}</div>
                <div class="timeline-status" [class]="'status-' + (execution.status === 'completed' ? 'success' : 'error')">
                  {{ execution.status === 'completed' ? '✅ 成功' : '❌ 失败' }}
                </div>
              </div>
            </div>
          </div>
          
          <div *ngIf="taskExecutionHistory.length === 0" class="no-data">
            <div class="alert alert-info">
              ℹ️ 暂无任务执行记录
            </div>
          </div>
        </div>
      </div>

      <!-- 实时订单监控 -->
      <div class="card monitor-card">
        <div class="card-header">
          <h3>实时订单监控</h3>
        </div>
        <div class="card-body">
          <div class="table-container" *ngIf="!ordersLoading">
            <table class="data-table">
              <thead>
                <tr>
                  <th>订单号</th>
                  <th>状态</th>
                  <th>金额</th>
                  <th>创建时间</th>
                  <th>乘客</th>
                  <th>备注</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let order of recentOrders">
                  <td>{{ order.orderNumber }}</td>
                  <td>
                    <span class="status-tag" [style.background-color]="getOrderStatusColor(order.status)">
                      {{ getOrderStatusName(order.status) }}
                    </span>
                  </td>
                  <td>¥{{ order.amount }}</td>
                  <td>{{ order.creationDate | date:'yyyy-MM-dd HH:mm:ss' }}</td>
                  <td>{{ order.passengerNames }}</td>
                  <td>{{ order.remarks }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <div *ngIf="ordersLoading" class="loading">
            <div class="spinner"></div>
            <span>加载订单数据中...</span>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .scheduled-tasks-container {
      padding: 24px;
      background: #f5f5f5;
      min-height: 100vh;
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    }

    .page-header {
      margin-bottom: 24px;
      text-align: center;
    }

    .page-header h1 {
      margin: 0;
      color: #262626;
      font-size: 28px;
      font-weight: 600;
    }

    .page-header p {
      margin: 8px 0 0 0;
      color: #8c8c8c;
      font-size: 16px;
    }

    .card {
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      margin-bottom: 24px;
      overflow: hidden;
    }

    .card-header {
      padding: 16px 24px;
      border-bottom: 1px solid #f0f0f0;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .card-header h3 {
      margin: 0;
      color: #262626;
      font-size: 18px;
      font-weight: 600;
    }

    .card-body {
      padding: 24px;
    }

    .refresh-controls {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .last-update {
      color: #8c8c8c;
      font-size: 12px;
    }

    .btn {
      padding: 8px 16px;
      border: none;
      border-radius: 6px;
      cursor: pointer;
      font-size: 14px;
      transition: all 0.3s;
    }

    .btn:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .btn-primary {
      background: #1890ff;
      color: white;
    }

    .btn-primary:hover:not(:disabled) {
      background: #40a9ff;
    }

    .btn-secondary {
      background: #f0f0f0;
      color: #262626;
    }

    .btn-secondary:hover:not(:disabled) {
      background: #d9d9d9;
    }

    .status-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 24px;
    }

    .status-item {
      text-align: center;
    }

    .status-label {
      font-size: 14px;
      color: #8c8c8c;
      margin-bottom: 8px;
    }

    .status-value {
      font-size: 20px;
      font-weight: 600;
    }

    .status-success {
      color: #52c41a;
    }

    .status-error {
      color: #ff4d4f;
    }

    .stats-subtitle {
      font-size: 14px;
      color: #8c8c8c;
      font-weight: normal;
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 16px;
    }

    .stat-card {
      background: #fafafa;
      border-radius: 8px;
      padding: 20px;
      display: flex;
      align-items: center;
      gap: 16px;
      transition: all 0.3s;
      border: 2px solid transparent;
    }

    .stat-card.highlight {
      border-color: #ff4d4f;
      background: #fff2f0;
      box-shadow: 0 2px 8px rgba(255, 77, 79, 0.15);
    }

    .stat-card.total {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
    }

    .stat-card.pending {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      color: white;
    }

    .stat-card.paid {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      color: white;
    }

    .stat-card.ticketing {
      background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
      color: #333;
    }

    .stat-card.failed {
      background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
      color: #333;
    }

    .stat-card.success {
      background: linear-gradient(135deg, #a8e6cf 0%, #dcedc1 100%);
      color: #333;
    }

    .stat-card.cancelled {
      background: linear-gradient(135deg, #d3d3d3 0%, #f0f0f0 100%);
      color: #666;
    }

    .stat-icon {
      font-size: 32px;
      opacity: 0.9;
    }

    .stat-content {
      flex: 1;
    }

    .stat-value {
      font-size: 28px;
      font-weight: 700;
      margin-bottom: 4px;
    }

    .stat-label {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 4px;
    }

    .stat-desc {
      font-size: 12px;
      opacity: 0.8;
      line-height: 1.3;
    }

    .config-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 16px;
    }

    .config-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px;
      background: #fafafa;
      border-radius: 6px;
    }

    .config-label {
      font-weight: 600;
      color: #262626;
    }

    .config-value {
      color: #1890ff;
      font-weight: 600;
    }

    .task-buttons {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 16px;
    }

    .task-info {
      color: #8c8c8c;
      font-size: 12px;
    }

    .task-btn {
      padding: 16px;
      font-size: 14px;
      min-height: 60px;
      text-align: center;
      line-height: 1.4;
    }

    .task-note {
      margin-top: 24px;
      padding: 16px;
      background: #f0f8ff;
      border-radius: 8px;
      border-left: 4px solid #1890ff;
    }

    .note-content {
      display: flex;
      gap: 12px;
      align-items: flex-start;
    }

    .note-content mat-icon {
      color: #1890ff;
      font-size: 20px;
      margin-top: 2px;
    }

    .note-text {
      flex: 1;
      color: #262626;
      font-size: 14px;
      line-height: 1.5;
    }

    .note-text strong {
      color: #1890ff;
      margin-bottom: 8px;
      display: block;
    }

    .note-text ul {
      margin: 8px 0 0 0;
      padding-left: 16px;
    }

    .note-text li {
      margin-bottom: 4px;
      color: #595959;
    }

    .timeline {
      position: relative;
      padding-left: 30px;
    }

    .timeline-item {
      position: relative;
      margin-bottom: 24px;
    }

    .timeline-marker {
      position: absolute;
      left: -30px;
      top: 0;
      width: 12px;
      height: 12px;
      border-radius: 50%;
      border: 2px solid #d9d9d9;
      background: white;
    }

    .timeline-marker::before {
      content: '';
      position: absolute;
      left: 5px;
      top: 12px;
      width: 2px;
      height: 40px;
      background: #d9d9d9;
    }

    .timeline-item:last-child .timeline-marker::before {
      display: none;
    }

    .marker-success {
      border-color: #52c41a;
      background: #52c41a;
    }

    .marker-error {
      border-color: #ff4d4f;
      background: #ff4d4f;
    }

    .timeline-content {
      padding-left: 16px;
    }

    .timeline-title {
      font-weight: 600;
      color: #262626;
      margin-bottom: 4px;
    }

    .timeline-time {
      font-size: 12px;
      color: #8c8c8c;
      margin-bottom: 4px;
    }

    .timeline-status {
      font-size: 14px;
    }

    .no-data {
      text-align: center;
      padding: 40px;
    }

    .alert {
      padding: 12px 16px;
      border-radius: 6px;
      border: 1px solid;
    }

    .alert-info {
      background: #e6f7ff;
      border-color: #91d5ff;
      color: #0050b3;
    }

    .table-container {
      overflow-x: auto;
    }

    .data-table {
      width: 100%;
      border-collapse: collapse;
    }

    .data-table th,
    .data-table td {
      padding: 12px;
      text-align: left;
      border-bottom: 1px solid #f0f0f0;
    }

    .data-table th {
      background: #fafafa;
      font-weight: 600;
      color: #262626;
    }

    .status-tag {
      padding: 4px 8px;
      border-radius: 4px;
      color: white;
      font-size: 12px;
      font-weight: 500;
    }

    .loading {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 40px;
      gap: 12px;
    }

    .spinner {
      width: 20px;
      height: 20px;
      border: 2px solid #f0f0f0;
      border-top: 2px solid #1890ff;
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }

    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
  `]
})
export class ScheduledTasksComponent implements OnInit, OnDestroy {
  taskConfig: TaskConfig | null = null;
  systemHealth: SystemHealth | null = null;
  orderStatistics: any = {};
  orderStatisticsArray: Array<{status: string, count: number}> = [];
  taskExecutionHistory: TaskExecution[] = [];
  recentOrders: any[] = [];
  
  loading = false;
  ordersLoading = false;
  taskExecuting: { [key: string]: boolean } = {};
  
  autoRefreshEnabled = false;
  autoRefreshSubscription: Subscription | null = null;
  lastUpdateTime = new Date();

  constructor(
    private scheduledTaskService: ScheduledTaskService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    // 确保认证状态已就绪后再加载数据
    setTimeout(() => {
      this.loadAllData();
    }, 0);
  }

  ngOnDestroy() {
    if (this.autoRefreshSubscription) {
      this.autoRefreshSubscription.unsubscribe();
    }
  }

  async loadAllData() {
    this.loading = true;
    this.cdr.detectChanges(); // 立即触发变更检测
    
    try {
      await Promise.all([
        this.loadTaskConfig(),
        this.loadSystemHealth(),
        this.loadTaskStatistics()
      ]);
      this.lastUpdateTime = new Date();
    } catch (error) {
      console.error('加载数据失败:', error);
      alert('加载数据失败');
    } finally {
      this.loading = false;
      this.cdr.detectChanges(); // 完成后再次触发变更检测
    }
  }

  async loadTaskConfig() {
    try {
      const response = await this.scheduledTaskService.getTaskConfig();
      if (response.success) {
        this.taskConfig = response.data;
        this.cdr.detectChanges(); // 数据更新后触发变更检测
      }
    } catch (error) {
      console.error('加载任务配置失败:', error);
    }
  }

  async loadSystemHealth() {
    try {
      const response = await this.scheduledTaskService.getSystemHealth();
      if (response.success) {
        this.systemHealth = response.data;
        this.cdr.detectChanges(); // 数据更新后触发变更检测
      }
    } catch (error) {
      console.error('加载系统健康状态失败:', error);
    }
  }

  async loadTaskStatistics() {
    try {
      const response = await this.scheduledTaskService.getTaskStatistics();
      if (response.success) {
        this.orderStatistics = response.data.orderStatistics || {};
        this.orderStatisticsArray = Object.entries(this.orderStatistics).map(([status, count]) => ({
          status,
          count: count as number
        }));
        
        // 模拟一些订单数据用于展示
        this.recentOrders = [
          {
            orderNumber: 'ORD202508050001',
            status: 'PENDING_PAYMENT',
            amount: 850.00,
            creationDate: '2025-08-05T19:00:00',
            passengerNames: '测试用户1',
            remarks: '超时待支付订单1'
          },
          {
            orderNumber: 'ORD202508050004',
            status: 'TICKETING_IN_PROGRESS',
            amount: 1350.00,
            creationDate: '2025-08-05T19:30:00',
            passengerNames: '测试用户5',
            remarks: '超时出票中订单1'
          },
          {
            orderNumber: 'ORD202508040001',
            status: 'TICKETING_FAILED',
            amount: 980.00,
            creationDate: '2025-08-04T10:00:00',
            passengerNames: '测试用户8',
            remarks: '长时间出票失败订单1'
          }
        ];
        
        this.cdr.detectChanges(); // 数据更新后触发变更检测
      }
    } catch (error) {
      console.error('加载任务统计失败:', error);
    }
  }

  async executeTask(taskType: string) {
    this.taskExecuting[taskType] = true;
    try {
      const response = await this.scheduledTaskService.executeTask(taskType);
      if (response.success) {
        alert(`任务执行成功: ${this.getTaskTypeName(taskType)}`);
        
        // 添加到执行历史
        this.taskExecutionHistory.unshift({
          executionTime: response.data.executionTime,
          taskType: response.data.taskType,
          status: response.data.status
        });
        
        // 保持历史记录不超过10条
        if (this.taskExecutionHistory.length > 10) {
          this.taskExecutionHistory = this.taskExecutionHistory.slice(0, 10);
        }
        
        // 刷新数据
        setTimeout(() => {
          this.loadTaskStatistics();
        }, 1000);
      } else {
        alert(`任务执行失败: ${response.message}`);
      }
    } catch (error) {
      console.error('执行任务失败:', error);
      alert('执行任务失败');
    } finally {
      this.taskExecuting[taskType] = false;
    }
  }

  refreshSystemStatus() {
    this.loadAllData();
  }

  toggleAutoRefresh() {
    if (this.autoRefreshEnabled) {
      // 停止自动刷新
      if (this.autoRefreshSubscription) {
        this.autoRefreshSubscription.unsubscribe();
        this.autoRefreshSubscription = null;
      }
      this.autoRefreshEnabled = false;
      alert('已停止自动刷新');
    } else {
      // 开启自动刷新（每30秒）
      this.autoRefreshSubscription = interval(30000).subscribe(() => {
        this.loadAllData();
      });
      this.autoRefreshEnabled = true;
      alert('已开启自动刷新（每30秒）');
    }
  }

  getTotalOrders(): number {
    if (!this.orderStatistics || Object.keys(this.orderStatistics).length === 0) {
      return 0;
    }
    return Object.values(this.orderStatistics).reduce((total: number, count: any) => {
      const numCount = typeof count === 'number' ? count : parseInt(count) || 0;
      return total + numCount;
    }, 0);
  }

  getOrderCount(status: string): number {
    return this.orderStatistics[status] || 0;
  }

  getOrderStatusName(status: string): string {
    const statusMap: { [key: string]: string } = {
      'PENDING_PAYMENT': '待支付',
      'PAID': '已支付',
      'TICKETING_IN_PROGRESS': '出票中',
      'TICKETING_FAILED': '出票失败',
      'TICKETED': '已出票',
      'CANCELLED': '已取消'
    };
    return statusMap[status] || status;
  }

  getOrderStatusColor(status: string): string {
    const colorMap: { [key: string]: string } = {
      'PENDING_PAYMENT': '#fa8c16',
      'PAID': '#1890ff',
      'TICKETING_IN_PROGRESS': '#722ed1',
      'TICKETING_FAILED': '#ff4d4f',
      'TICKETED': '#52c41a',
      'CANCELLED': '#8c8c8c'
    };
    return colorMap[status] || '#8c8c8c';
  }

  getTaskTypeName(taskType: string): string {
    const taskMap: { [key: string]: string } = {
      'cancelTimeoutPaymentOrders': '取消超时待支付订单',
      'handleTimeoutTicketingOrders': '处理超时出票订单',
      'cancelLongTimeTicketingFailedOrders': '取消长时间失败订单',
      'dailyMaintenanceTask': '每日维护任务'
    };
    return taskMap[taskType] || taskType;
  }
}
