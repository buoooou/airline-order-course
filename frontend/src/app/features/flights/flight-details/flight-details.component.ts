import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatTabsModule } from '@angular/material/tabs';
import { FlightService } from '../../../core/services/flight.service';
import { Flight, FlightStatus } from '../../../shared/models';

@Component({
  selector: 'app-flight-details',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatDividerModule,
    MatTabsModule
  ],
  template: `
    <div class="flight-details-container p-6">
      <!-- 返回按钮 -->
      <div class="mb-6">
        <button mat-stroked-button (click)="goBack()">
          <mat-icon>arrow_back</mat-icon>
          返回搜索结果
        </button>
      </div>

      <!-- 加载状态 -->
      <div *ngIf="loading" class="text-center py-12">
        <mat-spinner class="mx-auto mb-4"></mat-spinner>
        <p class="text-gray-600">正在加载航班详情...</p>
      </div>

      <!-- 错误状态 -->
      <div *ngIf="error && !loading" class="text-center py-12">
        <mat-icon class="text-red-500 text-6xl mb-4">error_outline</mat-icon>
        <h3 class="text-xl font-semibold mb-2">加载失败</h3>
        <p class="text-gray-600 mb-4">{{ error }}</p>
        <button mat-raised-button color="primary" (click)="loadFlightDetails()">
          <mat-icon>refresh</mat-icon>
          重新加载
        </button>
      </div>

      <!-- 航班详情 -->
      <div *ngIf="flight && !loading && !error">
        <!-- 航班基本信息 -->
        <mat-card class="mb-6">
          <mat-card-header>
            <mat-card-title class="flex items-center">
              <img 
                [src]="getAirlineLogo(flight.airline.logoUrl)" 
                [alt]="flight.airline.name"
                class="w-12 h-12 mr-4 rounded"
                (error)="onImageError($event)"
              >
              <div>
                <div class="text-2xl font-bold">{{ flight.flightNumber }}</div>
                <div class="text-lg text-gray-600">{{ flight.airline.name }}</div>
              </div>
            </mat-card-title>
            <div class="ml-auto">
              <mat-chip-set>
                <mat-chip [class]="getStatusClass(flight.status)">
                  {{ getStatusText(flight.status) }}
                </mat-chip>
              </mat-chip-set>
            </div>
          </mat-card-header>
          
          <mat-card-content>
            <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 mt-6">
              <!-- 出发信息 -->
              <div class="text-center">
                <mat-icon class="text-blue-600 text-4xl mb-2">flight_takeoff</mat-icon>
                <h3 class="text-xl font-semibold mb-2">出发</h3>
                <div class="text-3xl font-bold mb-2">{{ flight.departureTime | date:'HH:mm' }}</div>
                <div class="text-lg text-gray-700 mb-1">{{ flight.departureAirport.name }}</div>
                <div class="text-sm text-gray-600">{{ flight.departureAirport.code }} · {{ flight.departureAirport.city }}</div>
                <div class="text-sm text-gray-500 mt-2">{{ flight.departureTime | date:'yyyy年MM月dd日 EEEE' }}</div>
              </div>

              <!-- 飞行信息 -->
              <div class="text-center">
                <mat-icon class="text-green-600 text-4xl mb-2">schedule</mat-icon>
                <h3 class="text-xl font-semibold mb-2">飞行时间</h3>
                <div class="text-2xl font-bold mb-2">{{ formatDuration(flight.duration) }}</div>
                <div class="text-sm text-gray-600 mb-2">{{ flight.aircraftType || '未知机型' }}</div>
                <div class="flex items-center justify-center mt-4">
                  <div class="flex-1 h-px bg-gray-300"></div>
                  <mat-icon class="mx-4 text-gray-400">flight</mat-icon>
                  <div class="flex-1 h-px bg-gray-300"></div>
                </div>
              </div>

              <!-- 到达信息 -->
              <div class="text-center">
                <mat-icon class="text-purple-600 text-4xl mb-2">flight_land</mat-icon>
                <h3 class="text-xl font-semibold mb-2">到达</h3>
                <div class="text-3xl font-bold mb-2">{{ flight.arrivalTime | date:'HH:mm' }}</div>
                <div class="text-lg text-gray-700 mb-1">{{ flight.arrivalAirport.name }}</div>
                <div class="text-sm text-gray-600">{{ flight.arrivalAirport.code }} · {{ flight.arrivalAirport.city }}</div>
                <div class="text-sm text-gray-500 mt-2">{{ flight.arrivalTime | date:'yyyy年MM月dd日 EEEE' }}</div>
              </div>
            </div>
          </mat-card-content>
        </mat-card>

        <!-- 详细信息标签页 -->
        <mat-card>
          <mat-tab-group>
            <!-- 座位和价格信息 -->
            <mat-tab label="座位信息">
              <div class="p-6">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div class="bg-blue-50 p-6 rounded-lg">
                    <div class="flex items-center mb-4">
                      <mat-icon class="text-blue-600 mr-2">airline_seat_recline_normal</mat-icon>
                      <h3 class="text-lg font-semibold">座位信息</h3>
                    </div>
                    <div class="space-y-3">
                      <div class="flex justify-between">
                        <span class="text-gray-600">总座位数:</span>
                        <span class="font-semibold">{{ flight.totalSeats }}</span>
                      </div>
                      <div class="flex justify-between">
                        <span class="text-gray-600">可用座位:</span>
                        <span class="font-semibold text-green-600">{{ flight.availableSeats }}</span>
                      </div>
                      <div class="flex justify-between">
                        <span class="text-gray-600">已预订:</span>
                        <span class="font-semibold text-orange-600">{{ flight.totalSeats - flight.availableSeats }}</span>
                      </div>
                      <mat-divider></mat-divider>
                      <div class="flex justify-between">
                        <span class="text-gray-600">座位利用率:</span>
                        <span class="font-semibold">{{ getSeatUtilization() }}%</span>
                      </div>
                    </div>
                  </div>

                  <div class="bg-green-50 p-6 rounded-lg">
                    <div class="flex items-center mb-4">
                      <mat-icon class="text-green-600 mr-2">attach_money</mat-icon>
                      <h3 class="text-lg font-semibold">价格信息</h3>
                    </div>
                    <div class="space-y-3">
                      <div class="text-center">
                        <div class="text-4xl font-bold text-green-600 mb-2">¥{{ flight.economyPrice || 0 | number:'1.0-0' }}</div>
                        <div class="text-sm text-gray-600">起价/人</div>
                      </div>
                      <mat-divider></mat-divider>
                      <div class="text-xs text-gray-500 text-center">
                        * 价格可能因座位类型和预订时间而有所不同
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </mat-tab>

            <!-- 航班政策 -->
            <mat-tab label="航班政策">
              <div class="p-6">
                <div class="space-y-6">
                  <div>
                    <h3 class="text-lg font-semibold mb-3 flex items-center">
                      <mat-icon class="text-blue-600 mr-2">policy</mat-icon>
                      退改签政策
                    </h3>
                    <div class="bg-gray-50 p-4 rounded-lg">
                      <ul class="space-y-2 text-sm">
                        <li class="flex items-start">
                          <mat-icon class="text-green-600 mr-2 text-sm">check_circle</mat-icon>
                          <span>起飞前24小时以上：免费退改签</span>
                        </li>
                        <li class="flex items-start">
                          <mat-icon class="text-orange-600 mr-2 text-sm">warning</mat-icon>
                          <span>起飞前2-24小时：收取20%手续费</span>
                        </li>
                        <li class="flex items-start">
                          <mat-icon class="text-red-600 mr-2 text-sm">cancel</mat-icon>
                          <span>起飞前2小时内：不可退改签</span>
                        </li>
                      </ul>
                    </div>
                  </div>

                  <div>
                    <h3 class="text-lg font-semibold mb-3 flex items-center">
                      <mat-icon class="text-purple-600 mr-2">luggage</mat-icon>
                      行李政策
                    </h3>
                    <div class="bg-gray-50 p-4 rounded-lg">
                      <ul class="space-y-2 text-sm">
                        <li class="flex items-start">
                          <mat-icon class="text-blue-600 mr-2 text-sm">work</mat-icon>
                          <span>随身行李：7kg以内，尺寸不超过55×40×20cm</span>
                        </li>
                        <li class="flex items-start">
                          <mat-icon class="text-green-600 mr-2 text-sm">luggage</mat-icon>
                          <span>托运行李：经济舱20kg，商务舱30kg</span>
                        </li>
                        <li class="flex items-start">
                          <mat-icon class="text-orange-600 mr-2 text-sm">info</mat-icon>
                          <span>超重行李：按每公斤收取超重费用</span>
                        </li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>
            </mat-tab>

            <!-- 服务设施 -->
            <mat-tab label="服务设施">
              <div class="p-6">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <h3 class="text-lg font-semibold mb-3 flex items-center">
                      <mat-icon class="text-blue-600 mr-2">wifi</mat-icon>
                      机上服务
                    </h3>
                    <div class="space-y-2">
                      <div class="flex items-center">
                        <mat-icon class="text-green-600 mr-2">check</mat-icon>
                        <span>免费WiFi</span>
                      </div>
                      <div class="flex items-center">
                        <mat-icon class="text-green-600 mr-2">check</mat-icon>
                        <span>机上娱乐系统</span>
                      </div>
                      <div class="flex items-center">
                        <mat-icon class="text-green-600 mr-2">check</mat-icon>
                        <span>免费餐食</span>
                      </div>
                      <div class="flex items-center">
                        <mat-icon class="text-green-600 mr-2">check</mat-icon>
                        <span>USB充电接口</span>
                      </div>
                    </div>
                  </div>

                  <div>
                    <h3 class="text-lg font-semibold mb-3 flex items-center">
                      <mat-icon class="text-purple-600 mr-2">support_agent</mat-icon>
                      客户服务
                    </h3>
                    <div class="space-y-2">
                      <div class="flex items-center">
                        <mat-icon class="text-green-600 mr-2">check</mat-icon>
                        <span>24小时客服热线</span>
                      </div>
                      <div class="flex items-center">
                        <mat-icon class="text-green-600 mr-2">check</mat-icon>
                        <span>在线客服支持</span>
                      </div>
                      <div class="flex items-center">
                        <mat-icon class="text-green-600 mr-2">check</mat-icon>
                        <span>多语言服务</span>
                      </div>
                      <div class="flex items-center">
                        <mat-icon class="text-green-600 mr-2">check</mat-icon>
                        <span>特殊需求协助</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </mat-tab>
          </mat-tab-group>
        </mat-card>

        <!-- 预订按钮 -->
        <div class="mt-6 text-center" *ngIf="flight.status === 'SCHEDULED' && flight.availableSeats > 0">
          <button 
            mat-raised-button 
            color="primary" 
            class="px-8 py-3 text-lg"
            (click)="bookFlight()">
            <mat-icon>flight_takeoff</mat-icon>
            立即预订
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
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
export class FlightDetailsComponent implements OnInit, OnDestroy {
  flight: Flight | null = null;
  loading = false;
  error: string | null = null;
  flightId: number | null = null;
  
  // 搜索参数，用于返回时保持状态
  searchParams: any = {};
  // 原始返回路径
  returnPath: string = '/flights/search';
  
  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private flightService: FlightService
  ) {}

  ngOnInit(): void {
    // 获取航班ID
    this.route.params
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        this.flightId = +params['id'];
        if (this.flightId) {
          this.loadFlightDetails();
        }
      });
    
    // 获取搜索参数，用于返回时保持状态
    this.route.queryParams
      .pipe(takeUntil(this.destroy$))
      .subscribe(queryParams => {
        this.searchParams = queryParams;
        // 保存原始返回路径
        if (queryParams['returnPath']) {
          this.returnPath = queryParams['returnPath'];
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadFlightDetails(): void {
    if (!this.flightId) return;

    this.loading = true;
    this.error = null;

    this.flightService.getFlightById(this.flightId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (flight) => {
          this.flight = flight;
          this.loading = false;
        },
        error: (error) => {
          this.error = error.message;
          this.loading = false;
        }
      });
  }

  bookFlight(): void {
    if (this.flight) {
      this.router.navigate(['/flights/booking', this.flight.id], {
        queryParams: {
          from: this.searchParams.from,
          to: this.searchParams.to,
          date: this.searchParams.date,
          passengers: this.searchParams.passengers,
          page: this.searchParams.page,
          size: this.searchParams.size
        }
      });
    }
  }

  goBack(): void {
    // 如果有搜索参数，返回到搜索结果页并保持状态
    if (this.searchParams && Object.keys(this.searchParams).length > 0) {
      // 构建查询参数，排除returnPath
      const queryParams = { ...this.searchParams };
      delete queryParams['returnPath'];
      
      // 使用保存的原始路径进行导航
      this.router.navigate([this.returnPath], {
        queryParams: queryParams
      });
    } else {
      // 如果没有搜索参数，返回首页
      this.router.navigate(['/']);
    }
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

  getSeatUtilization(): number {
    if (!this.flight) return 0;
    const utilized = this.flight.totalSeats - this.flight.availableSeats;
    return Math.round((utilized / this.flight.totalSeats) * 100);
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

  getAirlineLogo(logoUrl: string | null | undefined): string {
    return logoUrl || '/assets/images/default-airline.png';
  }

  onImageError(event: any): void {
    event.target.src = '/assets/images/default-airline.png';
  }
}