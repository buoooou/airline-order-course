import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-placeholder',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule],
  template: `
    <div class="placeholder-container p-8 text-center">
      <mat-card class="max-w-md mx-auto">
        <mat-card-content class="py-12">
          <mat-icon class="text-6xl text-gray-400 mb-4">construction</mat-icon>
          <h2 class="text-2xl font-semibold text-gray-700 mb-2">{{ title || '功能开发中' }}</h2>
          <p class="text-gray-500">{{ message || '该功能正在开发中，敬请期待！' }}</p>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .placeholder-container {
      min-height: 400px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  `]
})
export class PlaceholderComponent {
  @Input() title?: string;
  @Input() message?: string;
}