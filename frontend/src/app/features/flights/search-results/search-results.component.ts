import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { FlightService } from '../../../core/services/flight.service';
import {
  Flight,
  FlightSearchCriteria,
  FlightSearchResult,
  FlightStatus
} from '../../../shared/models';

@Component({
  selector: 'app-search-results',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatPaginatorModule,
    MatChipsModule,
    MatDividerModule
  ],
  template: `
    <div class="search-results-container p-6">
      <!-- 搜索条件显示 -->
      <mat-card class="search-criteria mb-6">
        <mat-card-content>
          <div class="flex items-center justify-between">
            <div class="flex items-center space-x-4">
              <div class="flex items-center">
                <mat-icon class="text-blue-600 mr-2">flight_takeoff</mat-icon>
                <span class="font-medium">{{ searchCriteria?.departureAirportCode }}</span>
              </div>
              <mat-icon class="text-gray-400">arrow_forward</mat-icon>
              <div class="flex items-center">
                <mat-icon class="text-green-600 mr-2">flight_land</mat-icon>
                <span class="font-medium">{{ searchCriteria?.arrivalAirportCode }}</span>
              </div>
              <mat-divider [vertical]="true" class="h-6"></mat-divider>
              <div class="flex items-center">
                <mat-icon class="text-purple-600 mr-2">date_range</mat-icon>
                <span>{{ searchCriteria?.departureDate | date:'yyyy-MM-dd' }}</span>
              </div>
              <div class="flex items-center">
                <mat-icon class="text-orange-600 mr-2">group</mat-icon>
                <span>{{ searchCriteria?.passengers }} 乘客</span>
              </div>
            </div>
            <button mat-raised-button color="primary" (click)="modifySearch()">
              <mat-icon>edit</mat-icon>
              修改搜索
            </button>
          </div>
        </mat-card-content>
      </mat-card>

      <!-- 加载状态 -->
      <div *ngIf="loading" class="text-center py-12">
        <mat-spinner class="mx-auto mb-4"></mat-spinner>
        <p class="text-gray-600">正在搜索航班...</p>
      </div>

      <!-- 错误状态 -->
      <div *ngIf="error && !loading" class="text-center py-12">
        <mat-icon class="text-red-500 text-6xl mb-4">error_outline</mat-icon>
        <h3 class="text-xl font-semibold mb-2">搜索失败</h3>
        <p class="text-gray-600 mb-4">{{ error }}</p>
        <button mat-raised-button color="primary" (click)="searchFlights()">
          <mat-icon>refresh</mat-icon>
          重新搜索
        </button>
      </div>

      <!-- 无结果状态 -->
      <div *ngIf="!loading && !error && searchResult && searchResult.content && searchResult.content.length === 0" class="text-center py-12">
        <mat-icon class="text-gray-400 text-6xl mb-4">flight_takeoff</mat-icon>
        <h3 class="text-xl font-semibold mb-2">未找到航班</h3>
        <p class="text-gray-600 mb-4">请尝试修改搜索条件</p>
        <button mat-raised-button color="primary" (click)="modifySearch()">
          <mat-icon>search</mat-icon>
          重新搜索
        </button>
      </div>

      <!-- 搜索结果 -->
      <div *ngIf="!loading && !error && searchResult && searchResult.content && searchResult.content.length > 0">
        <!-- 结果统计 -->
        <div class="mb-4">
          <p class="text-gray-600">
            找到 <span class="font-semibold text-blue-600">{{ searchResult?.totalElements || 0 }}</span> 个航班
          </p>
        </div>

        <!-- 航班列表 -->
        <div class="space-y-4 mb-6">
          <mat-card *ngFor="let flight of searchResult?.content || []" class="flight-card hover:shadow-lg transition-shadow">
            <mat-card-content class="p-6">
              <div class="grid grid-cols-1 lg:grid-cols-4 gap-6 items-center">
                <!-- 航班信息 -->
                <div class="lg:col-span-1">
                  <div class="flex items-center mb-2">
                    <img 
                  [src]="getAirlineLogo(flight.airline.logoUrl)" 
                  [alt]="flight.airline.name"
                  class="w-8 h-8 mr-3 rounded"
                  (error)="onImageError($event)"
                >
                    <div>
                      <div class="font-semibold text-lg">{{ flight.flightNumber }}</div>
                      <div class="text-sm text-gray-600">{{ flight.airline.name }}</div>
                    </div>
                  </div>
                  <div class="text-xs text-gray-500">{{ flight.aircraftType }}</div>
                </div>

                <!-- 时间信息 -->
                <div class="lg:col-span-2">
                  <div class="flex items-center justify-between">
                    <!-- 出发 -->
                    <div class="text-center">
                      <div class="text-2xl font-bold">{{ flight.departureTime | date:'HH:mm' }}</div>
                      <div class="text-sm text-gray-600">{{ flight.departureAirport.code }}</div>
                      <div class="text-xs text-gray-500">{{ flight.departureAirport.name }}</div>
                    </div>

                    <!-- 飞行时间 -->
                    <div class="flex-1 mx-4">
                      <div class="flex items-center justify-center mb-1">
                        <div class="flex-1 h-px bg-gray-300"></div>
                        <mat-icon class="mx-2 text-gray-400">flight</mat-icon>
                        <div class="flex-1 h-px bg-gray-300"></div>
                      </div>
                      <div class="text-center text-sm text-gray-600">
                        {{ formatDuration(flight.duration) }}
                      </div>
                    </div>

                    <!-- 到达 -->
                    <div class="text-center">
                      <div class="text-2xl font-bold">{{ flight.arrivalTime | date:'HH:mm' }}</div>
                      <div class="text-sm text-gray-600">{{ flight.arrivalAirport.code }}</div>
                      <div class="text-xs text-gray-500">{{ flight.arrivalAirport.name }}</div>
                    </div>
                  </div>

                  <!-- 航班状态 -->
                  <div class="mt-3 flex justify-center">
                    <mat-chip-set>
                      <mat-chip [class]="getStatusClass(flight.status)">
                        {{ getStatusText(flight.status) }}
                      </mat-chip>
                    </mat-chip-set>
                  </div>
                </div>

                <!-- 价格和预订 -->
                <div class="lg:col-span-1 text-center lg:text-right">
                  <div class="mb-4">
                    <div class="text-3xl font-bold text-blue-600">¥{{ flight.economyPrice || 0 | number:'1.0-0' }}</div>
                    <div class="text-sm text-gray-600">起/人</div>
                  </div>
                  <div class="mb-3">
                    <div class="text-sm text-gray-600">
                      剩余座位: <span class="font-semibold">{{ flight.availableSeats }}</span>
                    </div>
                  </div>
                  <div class="space-y-2">
                    <button 
                      mat-raised-button 
                      color="primary" 
                      class="w-full"
                      [disabled]="flight.availableSeats === 0 || flight.status !== 'SCHEDULED'"
                      (click)="selectFlight(flight)">
                      <mat-icon>flight_takeoff</mat-icon>
                      选择航班
                    </button>
                    <button 
                      mat-stroked-button 
                      class="w-full"
                      (click)="viewDetails(flight)">
                      <mat-icon>info</mat-icon>
                      查看详情
                    </button>
                  </div>
                </div>
              </div>
            </mat-card-content>
          </mat-card>
        </div>

        <!-- 分页 -->
        <mat-paginator
          [length]="searchResult?.totalElements || 0"
          [pageSize]="pageSize"
          [pageIndex]="currentPage"
          [pageSizeOptions]="[5, 10, 20]"
          (page)="onPageChange($event)"
          showFirstLastButtons>
        </mat-paginator>
      </div>
    </div>
  `,
  styles: [`
    .flight-card {
      border-left: 4px solid #2196F3;
    }
    
    .flight-card:hover {
      border-left-color: #1976D2;
    }
    
    .status-scheduled {
      background-color: #E8F5E8;
      color: #2E7D32;
    }
    
    .status-delayed {
      background-color: #FFF3E0;
      color: #F57C00;
    }
    
    .status-cancelled {
      background-color: #FFEBEE;
      color: #D32F2F;
    }
    
    .status-boarding {
      background-color: #E3F2FD;
      color: #1976D2;
    }
  `]
})
export class SearchResultsComponent implements OnInit, OnDestroy {
  searchResult: FlightSearchResult | null = null;
  searchCriteria: FlightSearchCriteria | null = null;
  loading = false;
  error: string | null = null;
  currentPage = 0;
  pageSize = 10;
  
  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private flightService: FlightService
  ) {}

  ngOnInit(): void {
    this.route.queryParams
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        if (params['from'] && params['to'] && params['date']) {
          this.searchCriteria = {
            departureAirportCode: params['from'],
            arrivalAirportCode: params['to'],
            departureDate: params['date'],
            passengers: parseInt(params['passengers']) || 1
          };
          this.searchFlights();
        } else {
          // 如果没有搜索参数，重定向到首页
          this.router.navigate(['/']);
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  searchFlights(): void {
    if (!this.searchCriteria) return;

    this.loading = true;
    this.error = null;

    this.flightService.searchFlights(
      this.searchCriteria,
      { page: this.currentPage, size: this.pageSize }
    ).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: (result) => {
        this.searchResult = result;
        this.loading = false;
      },
      error: (error) => {
        this.error = error.message;
        this.loading = false;
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.searchFlights();
  }

  selectFlight(flight: Flight): void {
    // 获取当前路由路径
    const currentPath = this.router.url.split('?')[0]; // 去掉查询参数，只保留路径
    
    this.router.navigate(['/flights/booking', flight.id], {
      queryParams: {
        from: this.searchCriteria?.departureAirportCode,
        to: this.searchCriteria?.arrivalAirportCode,
        date: this.searchCriteria?.departureDate,
        passengers: this.searchCriteria?.passengers,
        page: this.currentPage,
        size: this.pageSize,
        returnPath: currentPath // 添加返回路径信息
      }
    });
  }

  viewDetails(flight: Flight): void {
    // 获取当前路由路径
    const currentPath = this.router.url.split('?')[0]; // 去掉查询参数，只保留路径
    
    this.router.navigate(['/flights/details', flight.id], {
      queryParams: {
        from: this.searchCriteria?.departureAirportCode,
        to: this.searchCriteria?.arrivalAirportCode,
        date: this.searchCriteria?.departureDate,
        passengers: this.searchCriteria?.passengers,
        page: this.currentPage,
        size: this.pageSize,
        returnPath: currentPath // 添加返回路径信息
      }
    });
  }

  modifySearch(): void {
    this.router.navigate(['/']);
  }

  formatDuration(minutes: number): string {
    // 检查参数是否为有效数字
    if (minutes == null || isNaN(minutes) || minutes < 0) {
      return '未知';
    }
    
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours}小时${mins}分钟`;
  }

  getAirlineLogo(logoUrl: string | null | undefined): string {
    return logoUrl || '/assets/images/default-airline.png';
  }

  onImageError(event: any): void {
    // 防止无限循环，只设置一次默认图片
    if (event.target.src !== '/assets/images/default-airline.png') {
      event.target.src = '/assets/images/default-airline.png';
    }
  }

  getStatusText(status: FlightStatus): string {
    const statusMap = {
      [FlightStatus.SCHEDULED]: '准时',
      [FlightStatus.BOARDING]: '登机中',
      [FlightStatus.DEPARTED]: '已起飞',
      [FlightStatus.ARRIVED]: '已到达',
      [FlightStatus.DELAYED]: '延误',
      [FlightStatus.CANCELLED]: '取消'
    };
    return statusMap[status] || status;
  }

  getStatusClass(status: FlightStatus): string {
    const classMap = {
      [FlightStatus.SCHEDULED]: 'status-scheduled',
      [FlightStatus.BOARDING]: 'status-boarding',
      [FlightStatus.DEPARTED]: 'status-scheduled',
      [FlightStatus.ARRIVED]: 'status-scheduled',
      [FlightStatus.DELAYED]: 'status-delayed',
      [FlightStatus.CANCELLED]: 'status-cancelled'
    };
    return classMap[status] || '';
  }
}