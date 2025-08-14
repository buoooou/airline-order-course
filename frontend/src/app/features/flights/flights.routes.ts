import { Routes } from '@angular/router';
import { Component } from '@angular/core';

@Component({
  selector: 'app-placeholder',
  template: '<div class="p-8 text-center"><h2>功能开发中...</h2></div>',
  standalone: true
})
export class PlaceholderComponent {}

export const flightRoutes: Routes = [
  {
    path: '',
    component: PlaceholderComponent
  },
  {
    path: 'search',
    component: PlaceholderComponent
  },
  {
    path: 'results',
    component: PlaceholderComponent
  },
  {
    path: 'booking/:flightId',
    component: PlaceholderComponent
  }
];