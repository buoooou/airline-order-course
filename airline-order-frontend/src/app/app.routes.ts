// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { OrderListComponent } from './pages/orders/order-list.component';
import { OrderDetailComponent } from './pages/orders/order-detail.component';
import { AuthGuard } from '../core/guard/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { 
    path: 'orders', 
    component: OrderListComponent,
    canActivate: [AuthGuard] // 只有认证用户才能访问
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'order-detail/:id', component: OrderDetailComponent }
];