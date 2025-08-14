import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';

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
    MatFormFieldModule
  ],
  template: `
    <div class="home-container">
      <!-- Hero Section -->
      <section class="hero-section bg-gradient-to-r from-blue-600 to-purple-600 text-white py-20">
        <div class="container mx-auto px-4 text-center">
          <h1 class="text-4xl md:text-6xl font-bold mb-6">
            发现您的下一个目的地
          </h1>
          <p class="text-xl md:text-2xl mb-8 opacity-90">
            搜索和预订全球航班，享受最优质的服务
          </p>
        </div>
      </section>

      <!-- Flight Search Form -->
      <section class="search-section py-12 bg-white">
        <div class="container mx-auto px-4">
          <mat-card class="search-form max-w-4xl mx-auto">
            <mat-card-header>
              <mat-card-title class="flex items-center">
                <mat-icon class="mr-2">search</mat-icon>
                搜索航班
              </mat-card-title>
            </mat-card-header>
            
            <mat-card-content>
              <form [formGroup]="searchForm" (ngSubmit)="searchFlights()" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
                <mat-form-field appearance="outline">
                  <mat-label>出发城市</mat-label>
                  <mat-select formControlName="departureCity" required>
                    <mat-option value="PEK">北京 (PEK)</mat-option>
                    <mat-option value="SHA">上海虹桥 (SHA)</mat-option>
                    <mat-option value="PVG">上海浦东 (PVG)</mat-option>
                    <mat-option value="CAN">广州 (CAN)</mat-option>
                    <mat-option value="SZX">深圳 (SZX)</mat-option>
                    <mat-option value="CTU">成都 (CTU)</mat-option>
                    <mat-option value="XIY">西安 (XIY)</mat-option>
                  </mat-select>
                  <mat-icon matSuffix>flight_takeoff</mat-icon>
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>到达城市</mat-label>
                  <mat-select formControlName="arrivalCity" required>
                    <mat-option value="PEK">北京 (PEK)</mat-option>
                    <mat-option value="SHA">上海虹桥 (SHA)</mat-option>
                    <mat-option value="PVG">上海浦东 (PVG)</mat-option>
                    <mat-option value="CAN">广州 (CAN)</mat-option>
                    <mat-option value="SZX">深圳 (SZX)</mat-option>
                    <mat-option value="CTU">成都 (CTU)</mat-option>
                    <mat-option value="XIY">西安 (XIY)</mat-option>
                  </mat-select>
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

                <div class="flex items-end">
                  <button 
                    mat-raised-button 
                    color="primary" 
                    type="submit" 
                    [disabled]="searchForm.invalid"
                    class="w-full py-3">
                    <mat-icon>search</mat-icon>
                    搜索航班
                  </button>
                </div>
              </form>
            </mat-card-content>
          </mat-card>
        </div>
      </section>

      <!-- Features Section -->
      <section class="features-section py-16 bg-gray-50">
        <div class="container mx-auto px-4">
          <h2 class="text-3xl font-bold text-center mb-12">为什么选择我们</h2>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
            <mat-card class="text-center p-6">
              <mat-icon class="text-4xl text-blue-600 mb-4">verified_user</mat-icon>
              <h3 class="text-xl font-semibold mb-2">安全可靠</h3>
              <p class="text-gray-600">采用银行级安全加密，保障您的个人信息和支付安全</p>
            </mat-card>

            <mat-card class="text-center p-6">
              <mat-icon class="text-4xl text-green-600 mb-4">access_time</mat-icon>
              <h3 class="text-xl font-semibold mb-2">24/7服务</h3>
              <p class="text-gray-600">全天候客服支持，随时为您解决旅行中的问题</p>
            </mat-card>

            <mat-card class="text-center p-6">
              <mat-icon class="text-4xl text-purple-600 mb-4">local_offer</mat-icon>
              <h3 class="text-xl font-semibold mb-2">优惠价格</h3>
              <p class="text-gray-600">与多家航空公司合作，为您提供最优惠的机票价格</p>
            </mat-card>
          </div>
        </div>
      </section>
    </div>
  `,
  styles: [`
    .hero-section {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    
    .search-form {
      margin-top: -60px;
      position: relative;
      z-index: 10;
    }
    
    .features-section mat-card {
      transition: transform 0.3s ease;
    }
    
    .features-section mat-card:hover {
      transform: translateY(-5px);
    }
  `]
})
export class HomeComponent implements OnInit {
  searchForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private router: Router
  ) {
    this.searchForm = this.fb.group({
      departureCity: ['', Validators.required],
      arrivalCity: ['', Validators.required],
      departureDate: ['', Validators.required],
      passengers: [1, Validators.required]
    });
  }

  ngOnInit(): void {
    // Set default departure date to tomorrow
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    this.searchForm.patchValue({
      departureDate: tomorrow
    });
  }

  searchFlights(): void {
    if (this.searchForm.valid) {
      const searchParams = this.searchForm.value;
      this.router.navigate(['/flights/search'], { 
        queryParams: {
          from: searchParams.departureCity,
          to: searchParams.arrivalCity,
          date: searchParams.departureDate.toISOString().split('T')[0],
          passengers: searchParams.passengers
        }
      });
    }
  }
}