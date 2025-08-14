// src/app/app.routes.ts

import { Routes } from '@angular/router';
import { HelloWorld } from './pages/hello-world/hello-world';
import { LoginComponent } from './pages/login/login.component';
import { OrdersComponent } from './pages/orders/orders.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { 
    path: 'orders', 
    component: OrdersComponent,
    canActivate: [authGuard]
  },
  // {
  //   path: 'hello-world',
  //   component: HelloWorld,
  // },
  // 默认路由
  { path: '', redirectTo: '/orders', pathMatch: 'full' },
  { path: '**', redirectTo: '/orders' }
];
