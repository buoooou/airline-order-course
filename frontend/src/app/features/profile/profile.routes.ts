import { Routes } from '@angular/router';
import { Component } from '@angular/core';

@Component({
  selector: 'app-placeholder',
  template: '<div class="p-8 text-center"><h2>功能开发中...</h2></div>',
  standalone: true
})
export class PlaceholderComponent {}

export const profileRoutes: Routes = [
  {
    path: '',
    component: PlaceholderComponent
  },
  {
    path: 'orders',
    component: PlaceholderComponent
  },
  {
    path: 'orders/:orderId',
    component: PlaceholderComponent
  },
  {
    path: 'passengers',
    component: PlaceholderComponent
  },
  {
    path: 'settings',
    component: PlaceholderComponent
  }
];