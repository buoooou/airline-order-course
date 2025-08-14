import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'flights',
    loadChildren: () => import('./features/flights/flights.routes').then(m => m.flightRoutes)
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.authRoutes)
  },
  {
    path: 'profile',
    canActivate: [AuthGuard],
    loadChildren: () => import('./features/profile/profile.routes').then(m => m.profileRoutes)
  },
  {
    path: 'admin',
    canActivate: [AuthGuard],
    loadChildren: () => import('./features/admin/admin.routes').then(m => m.adminRoutes)
  },
  {
    path: '**',
    redirectTo: '/home'
  }
];