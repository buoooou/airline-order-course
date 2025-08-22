import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login';
import { OrderComponent } from './pages/order/order';
import { OrderDetailComponent } from './pages/order-detail/order-detail';
import { AuthGuard } from './core/guards/auth.guard';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'orders', component: OrderComponent, canActivate: [AuthGuard] },
  { path: 'orders/:id', component: OrderDetailComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '/login' }
];

export const appRoutes = routes;