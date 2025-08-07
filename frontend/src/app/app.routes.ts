import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { OrderList } from './pages/order-list/order-list';
import { OrderDetail } from './pages/order-detail/order-detail';
import { FlightManagement } from './pages/flight-management/flight-management';
import { ScheduledTasksComponent } from './pages/scheduled-tasks/scheduled-tasks.component';
import { AuthGuard } from './core/guards/auth-guard';
import { AdminGuard } from './core/guards/admin-guard';

export const routes: Routes = [
  // 默认路由重定向到订单列表
  { path: '', redirectTo: '/orders', pathMatch: 'full' },
  
  // 登录页面（无需认证）
  { path: 'login', component: Login },
  
  // 注册页面（无需认证）
  { path: 'register', component: Register },
  
  // 订单相关路由（需要认证）
  { 
    path: 'orders', 
    component: OrderList, 
    canActivate: [AuthGuard] 
  },
  { 
    path: 'orders/:id', 
    component: OrderDetail, 
    canActivate: [AuthGuard] 
  },
  
  // 航班管理（需要管理员权限）
  { 
    path: 'flights', 
    component: FlightManagement, 
    canActivate: [AuthGuard, AdminGuard] 
  },
  
  // 定时任务管理（需要管理员权限）
  { 
    path: 'scheduled-tasks', 
    component: ScheduledTasksComponent, 
    canActivate: [AuthGuard, AdminGuard] 
  },
  
  // 404页面
  { path: '**', redirectTo: '/orders' }
];
