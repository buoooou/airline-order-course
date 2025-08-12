import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { FlightSearchComponent } from './components/flight-search/flight-search.component';
import { OrderListComponent } from './components/order-list/order-list.component';
import { OrderDetailComponent } from './components/order-detail/order-detail.component';
import { AuthComponent } from './components/auth/auth.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'flights', component: FlightSearchComponent },
  { path: 'orders', component: OrderListComponent },
  { path: 'orders/:id', component: OrderDetailComponent },
  { path: 'auth', component: AuthComponent },
  { path: '**', redirectTo: '' }
];
