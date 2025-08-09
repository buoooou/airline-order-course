import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent) },
  { path: 'login', loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./pages/register/register.component').then(m => m.RegisterComponent) },
  { 
    path: 'orders', 
    loadComponent: () => import('./pages/orders/orders.component').then(m => m.OrdersComponent),
    canActivate: [AuthGuard]
  },
  { 
    path: 'orders/create', 
    loadComponent: () => import('./pages/create-order/create-order.component').then(m => m.CreateOrderComponent),
    canActivate: [AuthGuard]
  },
  { 
    path: 'orders/:id', 
    loadComponent: () => import('./pages/order-detail/order-detail.component').then(m => m.OrderDetailComponent),
    canActivate: [AuthGuard]
  },
  { path: '**', redirectTo: '' }
];
