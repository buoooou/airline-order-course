// src/main.ts
import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

// 直接使用 appConfig 中的配置，不要重复定义 providers
bootstrapApplication(AppComponent, appConfig)
  .catch(err => console.error(err));