import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatStepperModule } from '@angular/material/stepper';
import { MatRadioModule } from '@angular/material/radio';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { FlightService } from '../../../core/services/flight.service';
import { OrderService } from '../../../core/services/order.service';
import { AuthService } from '../../../core/services/auth.service';
import { Flight, Passenger, BookingRequest, PaymentMethod, SeatClass, SeatClassOption } from '../../../shared/models';

@Component({
  selector: 'app-booking',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatProgressSpinnerModule,
    MatStepperModule,
    MatRadioModule,
    MatCheckboxModule,
    MatDividerModule,
    MatSnackBarModule
  ],
  template: `
    <div class="booking-container p-6 max-w-6xl mx-auto">
      <!-- 返回按钮 -->
      <div class="mb-6">
        <button mat-stroked-button (click)="goBack()">
          <mat-icon>arrow_back</mat-icon>
          返回航班详情
        </button>
      </div>

      <!-- 加载状态 -->
      <div *ngIf="loading" class="text-center py-12">
        <mat-spinner class="mx-auto mb-4"></mat-spinner>
        <p class="text-gray-600">正在加载航班信息...</p>
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

      <!-- 预订流程 -->
      <div *ngIf="flight && !loading && !error">
        <!-- 航班信息摘要 -->
        <mat-card class="mb-6">
          <mat-card-header>
            <mat-card-title>预订航班</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <div class="flex items-center justify-between">
              <div class="flex items-center">
                <img 
                  [src]="getAirlineLogo(flight.airline.logoUrl)" 
                  [alt]="flight.airline.name"
                  class="w-10 h-10 mr-3 rounded"
                  (error)="onImageError($event)"
                >
                <div>
                  <div class="font-bold text-lg">{{ flight.flightNumber }}</div>
                  <div class="text-gray-600">{{ flight.airline.name }}</div>
                </div>
              </div>
              <div class="text-center">
                <div class="text-sm text-gray-600">{{ flight.departureTime | date:'MM月dd日' }}</div>
                <div class="font-semibold">
                  {{ flight.departureAirport.code }} → {{ flight.arrivalAirport.code }}
                </div>
                <div class="text-sm text-gray-600">
                  {{ flight.departureTime | date:'HH:mm' }} - {{ flight.arrivalTime | date:'HH:mm' }}
                </div>
              </div>
              <div class="text-right">
                <div class="text-2xl font-bold text-green-600">¥{{ getCurrentPrice() }}</div>
                <div class="text-sm text-gray-600">{{ getSeatClassLabel() }} - 每人</div>
              </div>
            </div>
          </mat-card-content>
        </mat-card>

        <!-- 预订表单 -->
        <form [formGroup]="bookingForm" (ngSubmit)="onSubmit()">
          <mat-stepper #stepper linear>
            <!-- 步骤1: 座位等级选择 -->
            <mat-step label="座位等级">
              <mat-card>
                <mat-card-header>
                  <mat-card-title>选择座位等级</mat-card-title>
                  <mat-card-subtitle>请选择您偏好的座位等级</mat-card-subtitle>
                </mat-card-header>
                <mat-card-content>
                  <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div 
                      *ngFor="let option of seatClassOptions" 
                      class="border rounded-lg p-4 cursor-pointer transition-all"
                      [class.border-blue-500]="selectedSeatClass === option.value"
                      [class.bg-blue-50]="selectedSeatClass === option.value"
                      [class.border-gray-200]="selectedSeatClass !== option.value"
                      (click)="onSeatClassChange(option.value)"
                    >
                      <div class="text-center">
                        <h3 class="text-lg font-semibold mb-2">{{ option.label }}</h3>
                        <div class="text-2xl font-bold text-green-600 mb-2">
                          ¥{{ getSeatClassPrice(option.priceField) }}
                        </div>
                        <div class="text-sm text-gray-600">每人</div>
                        <mat-radio-button 
                          [value]="option.value" 
                          [checked]="selectedSeatClass === option.value"
                          class="mt-3"
                        >
                          选择{{ option.label }}
                        </mat-radio-button>
                      </div>
                    </div>
                  </div>
                </mat-card-content>
                <mat-card-actions>
                  <button mat-raised-button color="primary" matStepperNext>
                    下一步
                  </button>
                </mat-card-actions>
              </mat-card>
            </mat-step>

            <!-- 步骤2: 乘客信息 -->
            <mat-step [stepControl]="passengersFormArray" label="乘客信息">
              <mat-card>
                <mat-card-header>
                  <mat-card-title>乘客信息</mat-card-title>
                  <mat-card-subtitle>请填写所有乘客的详细信息</mat-card-subtitle>
                </mat-card-header>
                <mat-card-content>
                  <div class="mb-4">
                    <mat-form-field appearance="outline" class="w-full max-w-xs">
                      <mat-label>乘客数量</mat-label>
                      <mat-select [value]="passengerCount" (selectionChange)="updatePassengerCount($event.value)">
                        <mat-option *ngFor="let count of [1,2,3,4,5,6,7,8,9]" [value]="count">
                          {{ count }}人
                        </mat-option>
                      </mat-select>
                    </mat-form-field>
                  </div>

                  <div formArrayName="passengers" class="space-y-6">
                    <div 
                      *ngFor="let passengerForm of passengersFormArray.controls; let i = index" 
                      [formGroupName]="i"
                      class="border border-gray-200 rounded-lg p-6"
                    >
                      <h3 class="text-lg font-semibold mb-4">乘客 {{ i + 1 }}</h3>
                      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        <mat-form-field appearance="outline">
                          <mat-label>姓名</mat-label>
                          <input matInput formControlName="name" placeholder="请输入姓名">
                          <mat-error *ngIf="passengerForm.get('name')?.hasError('required')">
                            姓名不能为空
                          </mat-error>
                        </mat-form-field>

                        <mat-form-field appearance="outline">
                          <mat-label>身份证号</mat-label>
                          <input matInput formControlName="idNumber" placeholder="请输入身份证号">
                          <mat-error *ngIf="passengerForm.get('idNumber')?.hasError('required')">
                            身份证号不能为空
                          </mat-error>
                          <mat-error *ngIf="passengerForm.get('idNumber')?.hasError('pattern')">
                            身份证号格式不正确
                          </mat-error>
                        </mat-form-field>

                        <mat-form-field appearance="outline">
                          <mat-label>手机号</mat-label>
                          <input matInput formControlName="phone" placeholder="请输入手机号">
                          <mat-error *ngIf="passengerForm.get('phone')?.hasError('required')">
                            手机号不能为空
                          </mat-error>
                          <mat-error *ngIf="passengerForm.get('phone')?.hasError('pattern')">
                            手机号格式不正确
                          </mat-error>
                        </mat-form-field>

                        <mat-form-field appearance="outline">
                          <mat-label>邮箱</mat-label>
                          <input matInput formControlName="email" placeholder="请输入邮箱">
                          <mat-error *ngIf="passengerForm.get('email')?.hasError('required')">
                            邮箱不能为空
                          </mat-error>
                          <mat-error *ngIf="passengerForm.get('email')?.hasError('email')">
                            邮箱格式不正确
                          </mat-error>
                        </mat-form-field>

                        <mat-form-field appearance="outline">
                          <mat-label>出生日期</mat-label>
                          <input matInput [matDatepicker]="picker" formControlName="dateOfBirth">
                          <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
                          <mat-datepicker #picker></mat-datepicker>
                          <mat-error *ngIf="passengerForm.get('dateOfBirth')?.hasError('required')">
                            出生日期不能为空
                          </mat-error>
                        </mat-form-field>

                        <mat-form-field appearance="outline">
                          <mat-label>性别</mat-label>
                          <mat-select formControlName="gender">
                            <mat-option value="MALE">男</mat-option>
                            <mat-option value="FEMALE">女</mat-option>
                          </mat-select>
                          <mat-error *ngIf="passengerForm.get('gender')?.hasError('required')">
                            请选择性别
                          </mat-error>
                        </mat-form-field>
                      </div>
                    </div>
                  </div>
                </mat-card-content>
                <mat-card-actions>
                  <button mat-raised-button color="primary" matStepperNext [disabled]="passengersFormArray.invalid">
                    下一步
                  </button>
                </mat-card-actions>
              </mat-card>
            </mat-step>

            <!-- 步骤3: 联系信息 -->
            <mat-step [stepControl]="contactForm" label="联系信息">
              <mat-card>
                <mat-card-header>
                  <mat-card-title>联系信息</mat-card-title>
                  <mat-card-subtitle>请填写联系人信息，用于接收订单通知</mat-card-subtitle>
                </mat-card-header>
                <mat-card-content formGroupName="contact">
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <mat-form-field appearance="outline">
                      <mat-label>联系人姓名</mat-label>
                      <input matInput formControlName="name" placeholder="请输入联系人姓名">
                      <mat-error *ngIf="contactForm.get('name')?.hasError('required')">
                        联系人姓名不能为空
                      </mat-error>
                    </mat-form-field>

                    <mat-form-field appearance="outline">
                      <mat-label>联系电话</mat-label>
                      <input matInput formControlName="phone" placeholder="请输入联系电话">
                      <mat-error *ngIf="contactForm.get('phone')?.hasError('required')">
                        联系电话不能为空
                      </mat-error>
                      <mat-error *ngIf="contactForm.get('phone')?.hasError('pattern')">
                        电话号码格式不正确
                      </mat-error>
                    </mat-form-field>

                    <mat-form-field appearance="outline" class="md:col-span-2">
                      <mat-label>邮箱地址</mat-label>
                      <input matInput formControlName="email" placeholder="请输入邮箱地址">
                      <mat-error *ngIf="contactForm.get('email')?.hasError('required')">
                        邮箱地址不能为空
                      </mat-error>
                      <mat-error *ngIf="contactForm.get('email')?.hasError('email')">
                        邮箱格式不正确
                      </mat-error>
                    </mat-form-field>
                  </div>
                </mat-card-content>
                <mat-card-actions>
                  <button mat-button matStepperPrevious>上一步</button>
                  <button mat-raised-button color="primary" matStepperNext [disabled]="contactForm.invalid">
                    下一步
                  </button>
                </mat-card-actions>
              </mat-card>
            </mat-step>

            <!-- 步骤4: 支付方式 -->
            <mat-step [stepControl]="paymentForm" label="支付方式">
              <mat-card>
                <mat-card-header>
                  <mat-card-title>支付方式</mat-card-title>
                  <mat-card-subtitle>请选择支付方式</mat-card-subtitle>
                </mat-card-header>
                <mat-card-content formGroupName="payment">
                  <mat-radio-group formControlName="method" class="flex flex-col space-y-4">
                    <mat-radio-button value="ALIPAY" class="p-4 border border-gray-200 rounded-lg">
                      <div class="flex items-center ml-4">
                        <mat-icon class="text-blue-600 mr-3">payment</mat-icon>
                        <div>
                          <div class="font-semibold">支付宝</div>
                          <div class="text-sm text-gray-600">使用支付宝安全支付</div>
                        </div>
                      </div>
                    </mat-radio-button>

                    <mat-radio-button value="WECHAT_PAY" class="p-4 border border-gray-200 rounded-lg">
                      <div class="flex items-center ml-4">
                        <mat-icon class="text-green-600 mr-3">payment</mat-icon>
                        <div>
                          <div class="font-semibold">微信支付</div>
                          <div class="text-sm text-gray-600">使用微信支付快捷支付</div>
                        </div>
                      </div>
                    </mat-radio-button>

                    <mat-radio-button value="CREDIT_CARD" class="p-4 border border-gray-200 rounded-lg">
                      <div class="flex items-center ml-4">
                        <mat-icon class="text-purple-600 mr-3">credit_card</mat-icon>
                        <div>
                          <div class="font-semibold">信用卡</div>
                          <div class="text-sm text-gray-600">支持Visa、MasterCard等</div>
                        </div>
                      </div>
                    </mat-radio-button>
                  </mat-radio-group>

                  <mat-divider class="my-6"></mat-divider>

                  <!-- 订单摘要 -->
                  <div class="bg-gray-50 p-4 rounded-lg">
                    <h3 class="text-lg font-semibold mb-4">订单摘要</h3>
                    <div class="space-y-2">
                      <div class="flex justify-between">
                        <span>{{ getSeatClassLabel() }}价格 ({{ passengerCount }}人)</span>
                        <span>¥{{ getCurrentPrice() * passengerCount }}</span>
                      </div>
                      <div class="flex justify-between">
                        <span>税费</span>
                        <span>¥{{ getTaxAmount() }}</span>
                      </div>
                      <div class="flex justify-between">
                        <span>服务费</span>
                        <span>¥{{ getServiceFee() }}</span>
                      </div>
                      <mat-divider></mat-divider>
                      <div class="flex justify-between text-lg font-bold">
                        <span>总计</span>
                        <span class="text-green-600">¥{{ getTotalAmount() }}</span>
                      </div>
                    </div>
                  </div>

                  <div class="mt-4">
                    <mat-checkbox formControlName="agreeTerms">
                      我已阅读并同意 <a href="#" class="text-blue-600">服务条款</a> 和 <a href="#" class="text-blue-600">隐私政策</a>
                    </mat-checkbox>
                    <mat-error *ngIf="paymentForm.get('agreeTerms')?.hasError('required') && paymentForm.get('agreeTerms')?.touched">
                      请同意服务条款和隐私政策
                    </mat-error>
                  </div>
                </mat-card-content>
                <mat-card-actions>
                  <button mat-button matStepperPrevious>上一步</button>
                  <button 
                    mat-raised-button 
                    color="primary" 
                    type="submit"
                    [disabled]="bookingForm.invalid || submitting"
                    class="px-8"
                  >
                    <mat-spinner *ngIf="submitting" diameter="20" class="mr-2"></mat-spinner>
                    {{ submitting ? '处理中...' : '确认预订' }}
                  </button>
                </mat-card-actions>
              </mat-card>
            </mat-step>
          </mat-stepper>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .booking-container {
      min-height: calc(100vh - 64px);
    }
    
    .mat-stepper-horizontal {
      margin-top: 8px;
    }
    
    .mat-radio-button {
      margin-bottom: 16px;
    }
  `]
})
export class BookingComponent implements OnInit, OnDestroy {
  flight: Flight | null = null;
  loading = false;
  error: string | null = null;
  submitting = false;
  flightId: number | null = null;
  passengerCount = 1;
  selectedSeatClass: SeatClass = SeatClass.ECONOMY;
  
  // 搜索参数，用于返回时保持状态
  searchParams: any = {};
  
  bookingForm: FormGroup;
  
  private destroy$ = new Subject<void>();
  
  seatClassOptions: SeatClassOption[] = [
    { value: SeatClass.ECONOMY, label: '经济舱', priceField: 'economyPrice' },
    { value: SeatClass.BUSINESS, label: '商务舱', priceField: 'businessPrice' },
    { value: SeatClass.FIRST, label: '头等舱', priceField: 'firstPrice' }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private flightService: FlightService,
    private orderService: OrderService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {
    this.bookingForm = this.createBookingForm();
  }

  ngOnInit(): void {
    // 获取路由参数
    this.route.params
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        this.flightId = +params['id'];
        if (this.flightId) {
          this.loadFlightDetails();
          // 检查是否有保存的预订信息需要恢复
          this.restorePendingBooking();
        }
      });

    // 获取查询参数，用于返回时保持搜索状态
    this.route.queryParams
      .pipe(takeUntil(this.destroy$))
      .subscribe(queryParams => {
        this.searchParams = {
          from: queryParams['from'],
          to: queryParams['to'],
          date: queryParams['date'],
          passengers: queryParams['passengers'],
          page: queryParams['page'],
          size: queryParams['size']
        };
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private createBookingForm(): FormGroup {
    return this.fb.group({
      passengers: this.fb.array([this.createPassengerForm()]),
      contact: this.fb.group({
        name: ['', Validators.required],
        phone: ['', [Validators.required, Validators.pattern(/^1[3-9]\d{9}$/)]],
        email: ['', [Validators.required, Validators.email]]
      }),
      payment: this.fb.group({
        method: ['', Validators.required],
        agreeTerms: [false, Validators.requiredTrue]
      })
    });
  }

  private createPassengerForm(): FormGroup {
    return this.fb.group({
      name: ['', Validators.required],
      idNumber: ['', [Validators.required, Validators.pattern(/^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/)]],
      phone: ['', [Validators.required, Validators.pattern(/^1[3-9]\d{9}$/)]],
      email: ['', [Validators.required, Validators.email]],
      dateOfBirth: ['', Validators.required],
      gender: ['', Validators.required]
    });
  }

  get passengersFormArray(): FormArray {
    return this.bookingForm.get('passengers') as FormArray;
  }

  get contactForm(): FormGroup {
    return this.bookingForm.get('contact') as FormGroup;
  }

  get paymentForm(): FormGroup {
    return this.bookingForm.get('payment') as FormGroup;
  }

  updatePassengerCount(count: number): void {
    this.passengerCount = count;
    const passengersArray = this.passengersFormArray;
    
    // 清空现有的乘客表单
    while (passengersArray.length !== 0) {
      passengersArray.removeAt(0);
    }
    
    // 添加新的乘客表单
    for (let i = 0; i < count; i++) {
      passengersArray.push(this.createPassengerForm());
    }
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

  getCurrentPrice(): number {
    if (!this.flight) return 0;
    
    switch (this.selectedSeatClass) {
      case SeatClass.ECONOMY:
        return this.flight.economyPrice || 0;
      case SeatClass.BUSINESS:
        return this.flight.businessPrice || 0;
      case SeatClass.FIRST:
        return this.flight.firstPrice || 0;
      default:
        return this.flight.economyPrice || 0;
    }
  }

  getTaxAmount(): number {
    return Math.round(this.getCurrentPrice() * this.passengerCount * 0.1); // 10% 税费
  }

  getServiceFee(): number {
    return 50 * this.passengerCount; // 每人50元服务费
  }

  getTotalAmount(): number {
    return this.getCurrentPrice() * this.passengerCount + this.getTaxAmount() + this.getServiceFee();
  }

  onSeatClassChange(seatClass: SeatClass): void {
    this.selectedSeatClass = seatClass;
  }

  getSeatClassPrice(priceField: string): number {
    if (!this.flight) return 0;
    return (this.flight as any)[priceField] || 0;
  }

  getSeatClassLabel(): string {
    const option = this.seatClassOptions.find(opt => opt.value === this.selectedSeatClass);
    return option ? option.label : '经济舱';
  }

  onSubmit(): void {
    if (this.bookingForm.invalid || !this.flight) {
      this.markFormGroupTouched(this.bookingForm);
      return;
    }

    // 检查用户是否已登录
    if (!this.authService.isLoggedIn()) {
      // 保存当前预订信息到sessionStorage
      const bookingData = {
        flightId: this.flight.id,
        passengerCount: this.passengerCount,
        selectedSeatClass: this.selectedSeatClass,
        formValue: this.bookingForm.value,
        returnUrl: this.router.url
      };
      sessionStorage.setItem('pendingBooking', JSON.stringify(bookingData));
      
      this.snackBar.open('请先登录后再进行预订', '去登录', {
        duration: 5000,
        panelClass: ['warning-snackbar']
      }).onAction().subscribe(() => {
        this.router.navigate(['/auth/login'], { 
          queryParams: { returnUrl: this.router.url } 
        });
      });
      return;
    }

    this.submitting = true;

    const formValue = this.bookingForm.value;
    const bookingRequest: BookingRequest = {
      flightId: this.flight.id,
      passengers: formValue.passengers.map((p: any) => ({
        name: p.name,
        idNumber: p.idNumber,
        phone: p.phone,
        email: p.email,
        dateOfBirth: p.dateOfBirth,
        gender: p.gender
      } as Passenger)),
      contactName: formValue.contact.name,
      contactPhone: formValue.contact.phone,
      contactEmail: formValue.contact.email,
      paymentMethod: formValue.payment.method as PaymentMethod,
      seatClass: this.selectedSeatClass,
      totalAmount: this.getTotalAmount()
    };

    this.orderService.createOrder(bookingRequest)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (order) => {
          this.submitting = false;
          // 清除保存的预订信息
          sessionStorage.removeItem('pendingBooking');
          
          this.snackBar.open('预订成功！正在跳转到支付页面...', '关闭', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          
          // 跳转到支付页面或订单详情页面
          setTimeout(() => {
            this.router.navigate(['/orders', order.id]);
          }, 2000);
        },
        error: (error) => {
          this.submitting = false;
          this.snackBar.open(`预订失败: ${error.message}`, '关闭', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
  }

  private markFormGroupTouched(formGroup: FormGroup | FormArray): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      if (control instanceof FormGroup || control instanceof FormArray) {
        this.markFormGroupTouched(control);
      } else {
        control?.markAsTouched();
      }
    });
  }

  goBack(): void {
    if (this.flight) {
      // 检查关键搜索参数是否完整
      const hasRequiredParams = this.searchParams.from && 
                               this.searchParams.to && 
                               this.searchParams.date;
      
      if (hasRequiredParams) {
        // 参数完整，正常导航到详情页并传递参数
        this.router.navigate(['/flights/details', this.flight.id], {
          queryParams: {
            from: this.searchParams.from,
            to: this.searchParams.to,
            date: this.searchParams.date,
            passengers: this.searchParams.passengers,
            page: this.searchParams.page,
            size: this.searchParams.size
          }
        });
      } else {
        // 参数不完整，直接导航到详情页不传递不完整的参数
        this.router.navigate(['/flights/details', this.flight.id]);
      }
    } else {
      // 没有航班信息，使用浏览器历史记录返回
      window.history.back();
    }
  }

  getAirlineLogo(logoUrl: string | null | undefined): string {
    return logoUrl || '/assets/images/default-airline.png';
  }

  onImageError(event: any): void {
    event.target.src = '/assets/images/default-airline.png';
  }

  private restorePendingBooking(): void {
    const pendingBookingData = sessionStorage.getItem('pendingBooking');
    if (pendingBookingData && this.authService.isLoggedIn()) {
      try {
        const bookingData = JSON.parse(pendingBookingData);
        
        // 检查是否是同一个航班
        if (bookingData.flightId === this.flightId) {
          // 恢复乘客数量
          this.updatePassengerCount(bookingData.passengerCount);
          
          // 恢复座位等级选择
          this.selectedSeatClass = bookingData.selectedSeatClass || SeatClass.ECONOMY;
          
          // 恢复表单数据
          if (bookingData.formValue) {
            this.bookingForm.patchValue(bookingData.formValue);
          }
          
          this.snackBar.open('已恢复您之前的预订信息', '关闭', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
        }
        
        // 清除保存的数据
        sessionStorage.removeItem('pendingBooking');
      } catch (error) {
        console.error('恢复预订信息失败:', error);
        sessionStorage.removeItem('pendingBooking');
      }
    }
  }
}