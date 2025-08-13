import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { OrderList } from './pages/order-list/order-list';
import { OrderDetail } from './pages/order-detail/order-detail';
import { OrderStatus } from './pages/order-status/order-status';
import { OrderInfoComponent } from './pages/order-info/order-info.component';
import { PassengerComponent } from './pages/passenger/passenger.component';
import { PassengerListComponent } from './pages/passenger/passenger-list/passenger-list.component';
import { PassengerDetailComponent } from './pages/passenger/passenger-detail/passenger-detail.component';
import { ReportsComponent } from './pages/reports/reports.component';
import { AuthGuard } from './auth.guard';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  {
    path: 'order',
    children: [
      { path: '', component: OrderList , canActivate: [AuthGuard]},
      { path: ':id', component: OrderDetail , canActivate: [AuthGuard]},
    ],
  },
  {
    path: 'passenger',
    component: PassengerComponent,
    children: [
      { path: '', component: PassengerListComponent , canActivate: [AuthGuard]},
      { path: ':id', component: PassengerDetailComponent , canActivate: [AuthGuard]},
    ],
  },
  { path: 'reports', component: ReportsComponent , canActivate: [AuthGuard]},
  { path: 'order-status', component: OrderStatus , canActivate: [AuthGuard]},
  { path: 'order-info/:id', component: OrderInfoComponent , canActivate: [AuthGuard]},
{ path: '', redirectTo: '/login', pathMatch: 'full' },
];