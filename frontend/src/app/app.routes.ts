// src/app/app.routes.ts

import { Routes } from '@angular/router';
import { authGuard } from '././core/guards/auth.guard';
import { LoginComponent } from './pages/login/login.component';
import { OrderListComponent } from './pages/order-list/order-list.component';
import { OrderDetailComponent } from './pages/order-detail/order-detail.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'orders',
    component: OrderListComponent,
    canActivate: [authGuard],
  },
  {
    path: 'orders/:id',
    component: OrderDetailComponent,
    canActivate: [authGuard],
  },
  { path: '', redirectTo: '/orders', pathMatch: 'full' },
  { path: '**', redirectTo: '/orders' },
];