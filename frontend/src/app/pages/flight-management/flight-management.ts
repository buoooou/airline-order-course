import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';

import { FlightService, FlightInfo } from '../../core/services/flight';
import { AuthService } from '../../core/services/auth';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-flight-management',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatTableModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatTooltipModule,
    MatPaginatorModule
  ],
  templateUrl: './flight-management.html',
  styleUrl: './flight-management.scss'
})
export class FlightManagement implements OnInit, OnDestroy {
  // 表格显示的列
  displayedColumns: string[] = [
    'flightNumber', 
    'airline', 
    'route', 
    'departureTime', 
    'arrivalTime', 
    'price', 
    'seats', 
    'status', 
    'actions'
  ];

  // 数据
  flights: FlightInfo[] = [];
  filteredFlights: FlightInfo[] = [];
  loading = false;
  
  // 分页
  totalElements = 0;
  pageSize = 10;
  currentPage = 0;

  // 搜索表单
  searchForm!: FormGroup;
  
  // 航班状态选项
  statusOptions = [
    { value: 'SCHEDULED', label: '已安排', color: 'primary' },
    { value: 'BOARDING', label: '登机中', color: 'accent' },
    { value: 'DEPARTED', label: '已起飞', color: 'warn' },
    { value: 'ARRIVED', label: '已到达', color: 'primary' },
    { value: 'CANCELLED', label: '已取消', color: 'warn' },
    { value: 'DELAYED', label: '延误', color: 'accent' }
  ];

  // 订阅
  private subscriptions: Subscription[] = [];

  constructor(
    private flightService: FlightService,
    private authService: AuthService,
    private formBuilder: FormBuilder,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.initializeSearchForm();
    this.loadFlights();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  /**
   * 初始化搜索表单
   */
  private initializeSearchForm(): void {
    this.searchForm = this.formBuilder.group({
      flightNumber: [''],
      departureAirport: [''],
      arrivalAirport: [''],
      startDate: [''],
      endDate: [''],
      minPrice: [''],
      maxPrice: [''],
      status: ['']
    });
  }

  /**
   * 加载航班数据
   */
  loadFlights(): void {
    this.loading = true;
    
    const sub = this.flightService.getAllFlights().subscribe({
      next: (response) => {
        if (response.success) {
          this.flights = response.data;
          this.filteredFlights = [...this.flights];
          this.totalElements = this.flights.length;
          this.showSuccess('航班数据加载成功');
        } else {
          this.showError('加载航班数据失败: ' + response.message);
        }
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('加载航班数据失败:', error);
        this.showError('加载航班数据失败，请稍后重试');
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
    
    this.subscriptions.push(sub);
  }

  /**
   * 搜索航班
   */
  onSearch(): void {
    const formValue = this.searchForm.value;
    
    // 如果所有搜索条件都为空，显示所有航班
    if (this.isSearchFormEmpty(formValue)) {
      this.filteredFlights = [...this.flights];
      this.totalElements = this.flights.length;
      this.currentPage = 0;
      this.cdr.detectChanges();
      return;
    }

    this.loading = true;
    
    // 根据不同的搜索条件调用不同的API
    if (formValue.flightNumber) {
      this.searchByFlightNumber(formValue.flightNumber);
    } else if (formValue.departureAirport && formValue.arrivalAirport) {
      this.searchByRoute(formValue.departureAirport, formValue.arrivalAirport);
    } else if (formValue.minPrice && formValue.maxPrice) {
      this.searchByPriceRange(formValue.minPrice, formValue.maxPrice);
    } else {
      // 综合搜索
      this.performComprehensiveSearch(formValue);
    }
  }

  /**
   * 检查搜索表单是否为空
   */
  private isSearchFormEmpty(formValue: any): boolean {
    return Object.values(formValue).every(value => !value || value === '');
  }

  /**
   * 根据航班号搜索
   */
  private searchByFlightNumber(flightNumber: string): void {
    const sub = this.flightService.searchByFlightNumber(flightNumber).subscribe({
      next: (response) => {
        this.handleSearchResponse(response);
      },
      error: (error) => {
        this.handleSearchError(error);
      }
    });
    
    this.subscriptions.push(sub);
  }

  /**
   * 根据航线搜索
   */
  private searchByRoute(departureAirport: string, arrivalAirport: string): void {
    const sub = this.flightService.searchByRoute(departureAirport, arrivalAirport).subscribe({
      next: (response) => {
        this.handleSearchResponse(response);
      },
      error: (error) => {
        this.handleSearchError(error);
      }
    });
    
    this.subscriptions.push(sub);
  }

  /**
   * 根据价格范围搜索
   */
  private searchByPriceRange(minPrice: number, maxPrice: number): void {
    const sub = this.flightService.searchByPriceRange(minPrice, maxPrice).subscribe({
      next: (response) => {
        this.handleSearchResponse(response);
      },
      error: (error) => {
        this.handleSearchError(error);
      }
    });
    
    this.subscriptions.push(sub);
  }

  /**
   * 综合搜索
   */
  private performComprehensiveSearch(formValue: any): void {
    const startTime = formValue.startDate ? new Date(formValue.startDate).toISOString() : undefined;
    const endTime = formValue.endDate ? new Date(formValue.endDate).toISOString() : undefined;
    
    const sub = this.flightService.searchFlights(
      formValue.departureAirport,
      formValue.arrivalAirport,
      startTime,
      endTime,
      0,
      100
    ).subscribe({
      next: (response) => {
        if (response.success) {
          let results = response.data.content;
          
          // 客户端过滤状态
          if (formValue.status) {
            results = results.filter(flight => flight.status === formValue.status);
          }
          
          this.filteredFlights = results;
          this.totalElements = results.length;
          this.currentPage = 0;
          this.showSuccess(`找到 ${results.length} 个航班`);
        } else {
          this.showError('搜索失败: ' + response.message);
        }
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.handleSearchError(error);
      }
    });
    
    this.subscriptions.push(sub);
  }

  /**
   * 处理搜索响应
   */
  private handleSearchResponse(response: any): void {
    if (response.success) {
      this.filteredFlights = response.data;
      this.totalElements = response.data.length;
      this.currentPage = 0;
      this.showSuccess(`找到 ${response.data.length} 个航班`);
    } else {
      this.showError('搜索失败: ' + response.message);
    }
    this.loading = false;
    this.cdr.detectChanges();
  }

  /**
   * 处理搜索错误
   */
  private handleSearchError(error: any): void {
    console.error('搜索失败:', error);
    this.showError('搜索失败，请稍后重试');
    this.loading = false;
    this.cdr.detectChanges();
  }

  /**
   * 重置搜索
   */
  onResetSearch(): void {
    this.searchForm.reset();
    this.filteredFlights = [...this.flights];
    this.totalElements = this.flights.length;
    this.currentPage = 0;
    this.cdr.detectChanges();
  }

  /**
   * 分页事件处理
   */
  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    // 这里可以实现服务端分页
    this.cdr.detectChanges();
  }

  /**
   * 获取当前页的数据
   */
  getCurrentPageData(): FlightInfo[] {
    const startIndex = this.currentPage * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    return this.filteredFlights.slice(startIndex, endIndex);
  }

  /**
   * 获取状态显示信息
   */
  getStatusInfo(status: string) {
    return this.statusOptions.find(option => option.value === status) || 
           { value: status, label: status, color: 'primary' };
  }

  /**
   * 格式化日期时间
   */
  formatDateTime(dateTime: string): string {
    if (!dateTime) return '-';
    return new Date(dateTime).toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  /**
   * 格式化价格
   */
  formatPrice(price: number): string {
    return `¥${price.toLocaleString()}`;
  }

  /**
   * 格式化座位信息
   */
  formatSeats(available: number, total: number): string {
    return `${available}/${total}`;
  }

  /**
   * 检查是否为管理员
   */
  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  /**
   * 编辑航班
   */
  editFlight(flight: FlightInfo): void {
    // TODO: 实现编辑航班对话框
    this.showInfo('编辑航班功能开发中...');
  }

  /**
   * 删除航班
   */
  deleteFlight(flight: FlightInfo): void {
    if (!flight.id) return;
    
    if (confirm(`确定要删除航班 ${flight.flightNumber} 吗？`)) {
      this.loading = true;
      
      const sub = this.flightService.deleteFlight(flight.id).subscribe({
        next: (response) => {
          if (response.success) {
            this.showSuccess('航班删除成功');
            this.loadFlights(); // 重新加载数据
          } else {
            this.showError('删除失败: ' + response.message);
          }
          this.loading = false;
        },
        error: (error) => {
          console.error('删除航班失败:', error);
          this.showError('删除失败，请稍后重试');
          this.loading = false;
        }
      });
      
      this.subscriptions.push(sub);
    }
  }

  /**
   * 添加新航班
   */
  addFlight(): void {
    // TODO: 实现添加航班对话框
    this.showInfo('添加航班功能开发中...');
  }

  /**
   * 刷新数据
   */
  refreshData(): void {
    this.loadFlights();
  }

  /**
   * 显示成功消息
   */
  private showSuccess(message: string): void {
    this.snackBar.open(message, '关闭', {
      duration: 3000,
      panelClass: ['success-snackbar']
    });
  }

  /**
   * 显示错误消息
   */
  private showError(message: string): void {
    this.snackBar.open(message, '关闭', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
  }

  /**
   * 显示信息消息
   */
  private showInfo(message: string): void {
    this.snackBar.open(message, '关闭', {
      duration: 3000,
      panelClass: ['info-snackbar']
    });
  }
}
