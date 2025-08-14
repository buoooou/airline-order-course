import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, takeUntil, debounceTime, distinctUntilChanged, switchMap, startWith } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { FlightService } from '../../core/services/flight.service';
import { Airport } from '../../shared/models';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatFormFieldModule,
    MatAutocompleteModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  template: `
    <div class="home-container">
      <!-- Background Decoration -->
      <div class="background-decoration">
      </div>
      
      <!-- Hero Section -->
      <section class="hero-section">
        <div class="hero-content">
          <h1 class="hero-title">发现您的下一次旅程</h1>
          <p class="hero-subtitle">搜索并预订全球最优惠的航班</p>
        </div>
      </section>

      <!-- Flight Search Form -->
      <section class="search-section py-12 bg-white">
        <div class="container mx-auto px-4 lg:px-8 xl:px-12">
          <mat-card class="search-form max-w-4xl lg:max-w-5xl xl:max-w-6xl mx-auto">
            
            
            <mat-card-content>
              <form [formGroup]="searchForm" (ngSubmit)="searchFlights()" class="grid grid-cols-1 md:grid-cols-1 lg:grid-cols-1 gap-1 lg:gap-1 xl:gap-1">
                <mat-form-field appearance="outline">
                  <mat-label>出发城市</mat-label>
                  <input 
                    matInput 
                    formControlName="departureCity" 
                    [matAutocomplete]="departureAuto"
                    placeholder="请输入城市或机场代码"
                    required>
                  <mat-autocomplete #departureAuto="matAutocomplete" [displayWith]="displayAirport">
                    <mat-option *ngFor="let airport of filteredDepartureAirports" [value]="airport">
                      <div class="flex items-center">
                        <mat-icon class="mr-2 text-blue-600">flight_takeoff</mat-icon>
                        <div>
                          <div class="font-semibold">{{ airport.name }}</div>
                          <div class="text-sm text-gray-600">{{ airport.code }} · {{ airport.city }}</div>
                        </div>
                      </div>
                    </mat-option>
                  </mat-autocomplete>
                  <mat-icon matSuffix>flight_takeoff</mat-icon>
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>到达城市</mat-label>
                  <input 
                    matInput 
                    formControlName="arrivalCity" 
                    [matAutocomplete]="arrivalAuto"
                    placeholder="请输入城市或机场代码"
                    required>
                  <mat-autocomplete #arrivalAuto="matAutocomplete" [displayWith]="displayAirport">
                    <mat-option *ngFor="let airport of filteredArrivalAirports" [value]="airport">
                      <div class="flex items-center">
                        <mat-icon class="mr-2 text-purple-600">flight_land</mat-icon>
                        <div>
                          <div class="font-semibold">{{ airport.name }}</div>
                          <div class="text-sm text-gray-600">{{ airport.code }} · {{ airport.city }}</div>
                        </div>
                      </div>
                    </mat-option>
                  </mat-autocomplete>
                  <mat-icon matSuffix>flight_land</mat-icon>
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>出发日期</mat-label>
                  <input matInput [matDatepicker]="departurePicker" formControlName="departureDate" required>
                  <mat-datepicker-toggle matSuffix [for]="departurePicker"></mat-datepicker-toggle>
                  <mat-datepicker #departurePicker></mat-datepicker>
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>乘客数量</mat-label>
                  <mat-select formControlName="passengers">
                    <mat-option value="1">1 乘客</mat-option>
                    <mat-option value="2">2 乘客</mat-option>
                    <mat-option value="3">3 乘客</mat-option>
                    <mat-option value="4">4 乘客</mat-option>
                    <mat-option value="5">5+ 乘客</mat-option>
                  </mat-select>
                  <mat-icon matSuffix>group</mat-icon>
                </mat-form-field>

                <div class="flex mt-4">
                  <button 
                    mat-raised-button 
                    color="primary" 
                    type="submit" 
                    [disabled]="searching"
                    class="search-button">
                    <span class="button-content">
                      <mat-icon *ngIf="searching" class="button-icon spinning">hourglass_empty</mat-icon>
                      <mat-icon *ngIf="!searching" class="button-icon">search</mat-icon>
                      <span class="button-text">{{ searching ? '搜索中...' : '搜索航班' }}</span>
                    </span>
                    <div class="button-shine"></div>
                  </button>
                </div>
              </form>
            </mat-card-content>
          </mat-card>
        </div>
      </section>

      <!-- Features Section -->
      <section class="features-section py-16 lg:py-20 xl:py-24 bg-gray-50">
        <div class="container mx-auto px-4 lg:px-8 xl:px-12">
          <h2 class="text-3xl lg:text-4xl xl:text-5xl font-bold text-center mb-12 lg:mb-16 xl:mb-20">为什么选择我们</h2>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6 lg:gap-8 xl:gap-12 max-w-6xl mx-auto">
            <mat-card class="text-center hover:shadow-lg transition-shadow duration-300">
              <div class="flex justify-center mb-4">
                <mat-icon class="text-5xl text-blue-600" style="width: 48px; height: 48px; font-size: 48px;">verified_user</mat-icon>
              </div>
              <h3 class="text-xl font-semibold mb-3 text-gray-800">安全可靠</h3>
              <p class="text-gray-600 text-sm leading-relaxed">采用银行级安全加密，保障您的个人信息和支付安全</p>
            </mat-card>

            <mat-card class="text-center p-8 hover:shadow-lg transition-shadow duration-300">
              <div class="flex justify-center mb-4">
                <mat-icon class="text-5xl text-green-600" style="width: 48px; height: 48px; font-size: 48px;">access_time</mat-icon>
              </div>
              <h3 class="text-xl font-semibold mb-3 text-gray-800">24/7服务</h3>
              <p class="text-gray-600 text-sm leading-relaxed">全天候客服支持，随时为您解决旅行中的问题</p>
            </mat-card>

            <mat-card class="text-center p-8 hover:shadow-lg transition-shadow duration-300">
              <div class="flex justify-center mb-4">
                <mat-icon class="text-5xl text-purple-600" style="width: 48px; height: 48px; font-size: 48px;">local_offer</mat-icon>
              </div>
              <h3 class="text-xl font-semibold mb-3 text-gray-800">优惠价格</h3>
              <p class="text-gray-600 text-sm leading-relaxed">与多家航空公司合作，为您提供最优惠的机票价格</p>
            </mat-card>
          </div>
        </div>
      </section>
    </div>
  `,
  styles: [`
    .home-container {
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
      position: relative;
      overflow: hidden;
      animation: backgroundShift 15s ease-in-out infinite;
    }

    @keyframes backgroundShift {
      0%, 100% {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
      }
      33% {
        background: linear-gradient(135deg, #5a67d8 0%, #6b46c1 50%, #8b5cf6 100%);
      }
      66% {
        background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #667eea 100%);
      }
    }

    .background-decoration {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      pointer-events: none;
      z-index: 1;
    }

    .floating-shape {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.15);
      animation: float 8s ease-in-out infinite;
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.2);
    }

    .shape-1 {
      width: 120px;
      height: 120px;
      top: 10%;
      left: 5%;
      animation-delay: 0s;
    }

    .shape-2 {
      width: 180px;
      height: 180px;
      top: 70%;
      right: 8%;
      animation-delay: 2.5s;
    }

    .shape-3 {
      width: 90px;
      height: 90px;
      bottom: 15%;
      left: 10%;
      animation-delay: 5s;
    }

    .shape-4 {
      width: 70px;
      height: 70px;
      top: 25%;
      right: 20%;
      animation-delay: 1.5s;
    }

    .shape-5 {
      width: 140px;
      height: 140px;
      bottom: 40%;
      right: 35%;
      animation-delay: 3.5s;
    }

    @keyframes float {
      0%, 100% {
        transform: translateY(0px) rotate(0deg) scale(1);
      }
      25% {
        transform: translateY(-15px) rotate(90deg) scale(1.1);
      }
      50% {
        transform: translateY(-30px) rotate(180deg) scale(0.9);
      }
      75% {
        transform: translateY(-15px) rotate(270deg) scale(1.05);
      }
    }

    .hero-section {
      position: relative;
      z-index: 2;
      color: white;
      padding: 4rem 0 4rem;
      text-align: center;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .hero-content {
      max-width: 1000px;
      margin: 0 auto;
      padding: 0 2rem;
      animation: heroFadeIn 1.2s ease-out;
    }

    /* 大屏幕优化 */
    @media (min-width: 1200px) {
      .hero-section {
        padding: 4rem 0 4rem;
      }
      
      .hero-content {
        max-width: 1200px;
        padding: 0 3rem;
      }
    }

    @media (min-width: 1600px) {
      .hero-section {
        padding: 4rem 0 4rem;
      }
      
      .hero-content {
        max-width: 1400px;
        padding: 0 4rem;
      }
    }

    @keyframes heroFadeIn {
      0% {
        opacity: 0;
        transform: translateY(30px);
      }
      100% {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .hero-title {
      font-size: 2.8rem;
      font-weight: 800;
      margin-bottom: 2rem;
      line-height: 1.2;
      color: #ffffff;
      text-shadow: 
        0 2px 4px rgba(0, 0, 0, 0.5),
        0 4px 8px rgba(0, 0, 0, 0.3),
        0 0 20px rgba(255, 255, 255, 0.3);
      animation: titleGlow 3s ease-in-out infinite alternate;
    }

    /* 响应式标题字体 */
    @media (min-width: 768px) {
      .hero-title {
      }
    }

    @media (min-width: 1200px) {
      .hero-title {
        margin-bottom: 2.5rem;
      }
    }

    @media (min-width: 1600px) {
      .hero-title {
        margin-bottom: 3rem;
      }
    }

    @keyframes titleGlow {
      0% {
        text-shadow: 
          0 2px 4px rgba(0, 0, 0, 0.5),
          0 4px 8px rgba(0, 0, 0, 0.3),
          0 0 20px rgba(255, 255, 255, 0.3);
      }
      100% {
        text-shadow: 
          0 2px 4px rgba(0, 0, 0, 0.5),
          0 4px 8px rgba(0, 0, 0, 0.3),
          0 0 30px rgba(255, 255, 255, 0.5),
          0 0 40px rgba(255, 255, 255, 0.2);
      }
    }

    .hero-subtitle {
      font-size: 1.4rem;
      color: #ffffff;
      margin-bottom: 4rem;
      font-weight: 500;
      text-shadow: 
        0 2px 4px rgba(0, 0, 0, 0.5),
        0 4px 8px rgba(0, 0, 0, 0.3),
        0 0 15px rgba(255, 255, 255, 0.2);
      animation: subtitleSlide 1.5s ease-out 0.3s both;
    }

    /* 响应式副标题字体 */
    @media (min-width: 768px) {
      .hero-subtitle {
        font-size: 1.6rem;
      }
    }

    @media (min-width: 1200px) {
      .hero-subtitle {
        font-size: 1.5rem;
        margin-bottom: 5rem;
      }
    }

    @media (min-width: 1600px) {
      .hero-subtitle {
        font-size: 2rem;
        margin-bottom: 6rem;
      }
    }

    @keyframes subtitleSlide {
      0% {
        opacity: 0;
        transform: translateY(20px);
      }
      100% {
        opacity: 1;
        transform: translateY(0);
      }
    }
    
    .search-form {
      background: rgba(255, 255, 255, 0.98);
      backdrop-filter: blur(20px);
      border-radius: 20px;
      padding: 2.5rem;
      box-shadow: 
        0 20px 40px rgba(0, 0, 0, 0.1),
        0 8px 16px rgba(0, 0, 0, 0.06),
        inset 0 1px 0 rgba(255, 255, 255, 0.8);
      max-width: 850px;
      margin: -80px auto 0;
      border: 1px solid rgba(255, 255, 255, 0.3);
      position: relative;
      z-index: 10;
      transition: all 0.3s ease;
      animation: formSlideIn 1s ease-out 0.6s both;
    }

    /* 搜索表单响应式设计 */
    @media (min-width: 768px) {
      .search-form {
        padding: 3rem;
        margin: -100px auto 0;
      }
    }

    @media (min-width: 1200px) {
      .search-form {
        padding: 3rem;
        margin: -100px auto 0;
      }
    }

    @media (min-width: 1600px) {
      .search-form {
        padding: 3rem;
        margin: -100px auto 0;
      }
    }

    @keyframes formSlideIn {
      0% {
        opacity: 0;
        transform: translateY(40px) scale(0.95);
      }
      100% {
        opacity: 1;
        transform: translateY(0) scale(1);
      }
    }

    .search-form:hover {
      transform: translateY(-2px);
      box-shadow: 
        0 25px 50px rgba(0, 0, 0, 0.15),
        0 12px 20px rgba(0, 0, 0, 0.08),
        inset 0 1px 0 rgba(255, 255, 255, 0.9);
    }
    
    .features-section {
      padding: 6rem 0;
      background: rgba(0, 0, 0, 0.2);
      backdrop-filter: blur(15px);
      position: relative;
      z-index: 2;
      border-top: 1px solid rgba(255, 255, 255, 0.15);
    }

    /* Features区域响应式设计 */
    @media (min-width: 1200px) {
      .features-section {
        padding: 8rem 0;
      }
    }

    @media (min-width: 1600px) {
      .features-section {
        padding: 10rem 0;
      }
    }

    .features-section h2 {
      text-align: center;
      font-size: 2.8rem;
      font-weight: 800;
      margin-bottom: 4rem;
      color: #ffffff;
      text-shadow: 
        0 2px 4px rgba(0, 0, 0, 0.6),
        0 4px 8px rgba(0, 0, 0, 0.4),
        0 0 20px rgba(255, 255, 255, 0.3);
      animation: sectionTitleFade 1.5s ease-out 1s both;
    }

    /* Features标题响应式设计 */
    @media (min-width: 1200px) {
      .features-section h2 {
        font-size: 3.2rem;
        margin-bottom: 5rem;
      }
    }

    @media (min-width: 1600px) {
      .features-section h2 {
        font-size: 3.6rem;
        margin-bottom: 6rem;
      }
    }

    @keyframes sectionTitleFade {
      0% {
        opacity: 0;
        transform: translateY(30px);
      }
      100% {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .features-section .features-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
      gap: 2.5rem;
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 1rem;
    }

    .features-section mat-card {
      background: rgba(255, 255, 255, 0.98);
      backdrop-filter: blur(20px);
      border-radius: 16px;
      border: 1px solid rgba(255, 255, 255, 0.3);
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      box-shadow: 
        0 8px 25px rgba(0, 0, 0, 0.15),
        0 4px 12px rgba(0, 0, 0, 0.1);
      animation: cardFadeIn 1s ease-out var(--delay, 1.2s) both;
      padding: 2rem;
    }

    /* Features卡片响应式设计 */
    @media (min-width: 768px) {
      .features-section mat-card {
        padding: 2.5rem;
      }
    }

    @media (min-width: 1200px) {
      .features-section mat-card {
        padding: 3rem;
        border-radius: 20px;
      }
    }

    @media (min-width: 1600px) {
      .features-section mat-card {
        padding: 2.8rem;
        border-radius: 24px;
      }
    }

    @keyframes cardFadeIn {
      0% {
        opacity: 0;
        transform: translateY(30px) scale(0.95);
      }
      100% {
        opacity: 1;
        transform: translateY(0) scale(1);
      }
    }
    
    .features-section mat-card:nth-child(1) { --delay: 1.2s; }
    .features-section mat-card:nth-child(2) { --delay: 1.4s; }
    .features-section mat-card:nth-child(3) { --delay: 1.6s; }
    
    .features-section mat-card:hover {
      transform: translateY(-10px) scale(1.02);
      box-shadow: 
        0 25px 40px rgba(0, 0, 0, 0.2),
        0 12px 20px rgba(0, 0, 0, 0.15);
      background: rgba(255, 255, 255, 1);
    }
    
    .features-section mat-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      color: #667eea;
      transition: all 0.3s ease;
    }

    .features-section mat-card:hover mat-icon {
      transform: scale(1.1) rotate(5deg);
      color: #764ba2;
    }

    /* 搜索按钮样式 */
    .search-button {
      width: 100%;
      height: 56px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      border-radius: 16px;
      color: white;
      font-size: 16px;
      font-weight: 700;
      letter-spacing: 0.5px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
      margin-top: -15px;
    }

    .search-button:not(:disabled):hover {
      transform: translateY(-3px) scale(1.02);
      box-shadow: 0 15px 35px rgba(102, 126, 234, 0.5);
      background: linear-gradient(135deg, #5a67d8 0%, #6b46c1 100%);
    }

    .search-button:not(:disabled):active {
      transform: translateY(-1px) scale(1.01);
      box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
    }

    .search-button:disabled {
      opacity: 0.6;
      cursor: not-allowed;
      transform: none;
      box-shadow: 0 4px 15px rgba(102, 126, 234, 0.2);
    }

    .button-content {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      position: relative;
      z-index: 2;
      background: transparent;
    }

    .button-icon {
      font-size: 20px;
      transition: transform 0.3s ease;
      color: white;
      background: transparent;
      border: none;
    }

    .button-icon.spinning {
      animation: spin 1s linear infinite;
    }

    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }

    .button-text {
      font-weight: 700;
      letter-spacing: 0.5px;
    }

    .button-shine {
      display: none;
    }

    .search-button:hover .button-icon {
      transform: scale(1.1);
    }
  `]
})
export class HomeComponent implements OnInit, OnDestroy {
  searchForm: FormGroup;
  searching = false;
  airports: Airport[] = [];
  filteredDepartureAirports: Airport[] = [];
  filteredArrivalAirports: Airport[] = [];
  
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private flightService: FlightService,
    private snackBar: MatSnackBar
  ) {
    this.searchForm = this.fb.group({
      departureCity: ['', Validators.required],
      arrivalCity: ['', Validators.required],
      departureDate: ['', Validators.required],
      passengers: ['1', Validators.required]
    });
  }

  ngOnInit(): void {
    // Set default departure date to tomorrow
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    this.searchForm.patchValue({
      departureDate: tomorrow
    });

    // Load airports and set default values
    this.loadAirports();
    
    // Setup autocomplete for departure city
    this.searchForm.get('departureCity')?.valueChanges
      .pipe(
        startWith(''),
        debounceTime(300),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe(value => {
        this.filteredDepartureAirports = this.filterAirports(value);
      });

    // Setup autocomplete for arrival city
    this.searchForm.get('arrivalCity')?.valueChanges
      .pipe(
        startWith(''),
        debounceTime(300),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe(value => {
        this.filteredArrivalAirports = this.filterAirports(value);
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadAirports(): void {
    this.flightService.searchAirports('')
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (airports) => {
          this.airports = airports;
          this.filteredDepartureAirports = airports;
          this.filteredArrivalAirports = airports;
          this.setDefaultValues();
        },
        error: (error) => {
          console.error('Failed to load airports:', error);
          // Fallback to default airports if API fails
          this.airports = this.getDefaultAirports();
          this.filteredDepartureAirports = this.airports;
          this.filteredArrivalAirports = this.airports;
          this.setDefaultValues();
        }
      });
  }

  private getDefaultAirports(): Airport[] {
    return [
      { id: 1, code: 'PEK', name: '北京首都国际机场', city: '北京', country: '中国' },
      { id: 2, code: 'SHA', name: '上海虹桥国际机场', city: '上海', country: '中国' },
      { id: 3, code: 'PVG', name: '上海浦东国际机场', city: '上海', country: '中国' },
      { id: 4, code: 'CAN', name: '广州白云国际机场', city: '广州', country: '中国' },
      { id: 5, code: 'SZX', name: '深圳宝安国际机场', city: '深圳', country: '中国' },
      { id: 6, code: 'CTU', name: '成都双流国际机场', city: '成都', country: '中国' },
      { id: 7, code: 'XIY', name: '西安咸阳国际机场', city: '西安', country: '中国' }
    ];
  }

  private setDefaultValues(): void {
    // Find Beijing (PEK) airport for departure
    const beijingAirport = this.airports.find(airport => airport.code === 'PEK');
    // Find Pudong (PVG) airport for arrival
    const pudongAirport = this.airports.find(airport => airport.code === 'PVG');
    
    if (beijingAirport && pudongAirport) {
      this.searchForm.patchValue({
        departureCity: beijingAirport,
        arrivalCity: pudongAirport,
        passengers: '1'
      });
    }
  }

  private filterAirports(value: string | Airport): Airport[] {
    if (!value) {
      return this.airports.slice(0, 10); // Show top 10 airports by default
    }

    if (typeof value === 'object') {
      return this.airports;
    }

    const filterValue = value.toLowerCase();
    return this.airports.filter(airport => 
      airport.name.toLowerCase().includes(filterValue) ||
      airport.city.toLowerCase().includes(filterValue) ||
      airport.code.toLowerCase().includes(filterValue)
    ).slice(0, 10);
  }

  displayAirport(airport: Airport): string {
    return airport ? `${airport.name} (${airport.code})` : '';
  }

  private formatDateToLocal(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  searchFlights(): void {
    if (this.searchForm.invalid) {
      this.markFormGroupTouched(this.searchForm);
      this.snackBar.open('请填写所有必填字段', '关闭', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      return;
    }

    const formValue = this.searchForm.value;
    const departureAirport = formValue.departureCity as Airport;
    const arrivalAirport = formValue.arrivalCity as Airport;

    // Validate that airports are selected (not just text input)
    if (!departureAirport?.id || !arrivalAirport?.id) {
      this.snackBar.open('请从下拉列表中选择有效的机场', '关闭', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      return;
    }

    if (departureAirport.id === arrivalAirport.id) {
      this.snackBar.open('出发地和目的地不能相同', '关闭', {
        duration: 3000,
        panelClass: ['error-snackbar']
      });
      return;
    }

    this.searching = true;

    // Navigate to search results with query parameters
    const queryParams = {
      from: departureAirport.code,
      to: arrivalAirport.code,
      date: this.formatDateToLocal(formValue.departureDate),
      passengers: formValue.passengers
    };

    this.router.navigate(['/flights/search'], { queryParams })
      .then(() => {
        this.searching = false;
      })
      .catch((error) => {
        console.error('路由跳转失败:', error);
        this.searching = false;
        this.snackBar.open('跳转失败，请重试', '关闭', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      });
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
      
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }
}