import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { FlightService } from '../../services/flight.service';

@Component({
  selector: 'app-flight-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './flight-search.component.html',
  styleUrl: './flight-search.component.scss'
})
export class FlightSearchComponent implements OnInit {
  searchMode: 'route' | 'number' | 'date' = 'route';
  isLoading = false;
  error: string | null = null;
  flights: any[] = [];
  
  // 路线搜索表单
  routeSearchForm = {
    departureCode: '',
    arrivalCode: ''
  };
  
  // 航班号搜索表单
  numberSearchForm = {
    keyword: ''
  };
  
  // 日期搜索表单
  dateSearchForm = {
    date: ''
  };

  constructor(
    private flightService: FlightService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // 初始化时加载所有航班
    this.loadAllFlights();
  }

  // 加载所有航班
  loadAllFlights(): void {
    this.isLoading = true;
    this.error = null;
    
    this.flightService.getAllFlights()
      .subscribe({
        next: (response) => {
          this.isLoading = false;
          if (response.success) {
            this.flights = response.data;
          } else {
            this.error = response.message || '获取航班信息失败';
          }
        },
        error: (err) => {
          this.isLoading = false;
          this.error = err.error?.message || '获取航班信息失败，请稍后再试';
        }
      });
  }

  // 切换搜索模式
  setSearchMode(mode: 'route' | 'number' | 'date'): void {
    this.searchMode = mode;
  }

  // 搜索航班
  onSearch(): void {
    this.isLoading = true;
    this.error = null;
    
    switch (this.searchMode) {
      case 'route':
        this.searchByRoute();
        break;
      case 'number':
        this.searchByNumber();
        break;
      case 'date':
        this.searchByDate();
        break;
    }
  }

  // 根据路线搜索
  searchByRoute(): void {
    const { departureCode, arrivalCode } = this.routeSearchForm;
    
    if (!departureCode || !arrivalCode) {
      this.error = '请输入出发地和目的地';
      this.isLoading = false;
      return;
    }
    
    this.flightService.searchFlightsByRoute(departureCode, arrivalCode)
      .subscribe({
        next: (response) => {
          this.isLoading = false;
          if (response.success) {
            this.flights = response.data;
          } else {
            this.error = response.message || '搜索航班失败';
          }
        },
        error: (err) => {
          this.isLoading = false;
          this.error = err.error?.message || '搜索航班失败，请稍后再试';
        }
      });
  }

  // 根据航班号搜索
  searchByNumber(): void {
    const { keyword } = this.numberSearchForm;
    
    if (!keyword) {
      this.error = '请输入航班号关键字';
      this.isLoading = false;
      return;
    }
    
    this.flightService.searchFlightsByNumberKeyword(keyword)
      .subscribe({
        next: (response) => {
          this.isLoading = false;
          if (response.success) {
            this.flights = response.data;
          } else {
            this.error = response.message || '搜索航班失败';
          }
        },
        error: (err) => {
          this.isLoading = false;
          this.error = err.error?.message || '搜索航班失败，请稍后再试';
        }
      });
  }

  // 根据日期搜索
  searchByDate(): void {
    const { date } = this.dateSearchForm;
    
    if (!date) {
      this.error = '请选择日期';
      this.isLoading = false;
      return;
    }
    
    const searchDate = new Date(date);
    
    this.flightService.searchFlightsByDate(searchDate)
      .subscribe({
        next: (response) => {
          this.isLoading = false;
          if (response.success) {
            this.flights = response.data;
          } else {
            this.error = response.message || '搜索航班失败';
          }
        },
        error: (err) => {
          this.isLoading = false;
          this.error = err.error?.message || '搜索航班失败，请稍后再试';
        }
      });
  }

  // 查看航班详情
  viewFlightDetails(flightId: number): void {
    // 这里可以导航到航班详情页面，或者打开一个模态框显示详情
    console.log('查看航班详情:', flightId);
  }

  // 预订航班
  bookFlight(flightId: number): void {
    // 这里可以导航到订单创建页面
    console.log('预订航班:', flightId);
  }
}