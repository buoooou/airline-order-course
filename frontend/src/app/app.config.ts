import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { routes } from './app.routes';
import { httpInterceptor } from './core/interceptors/http.interceptor.fn';
import { NzConfig, provideNzConfig } from 'ng-zorro-antd/core/config';
import { provideNzI18n, zh_CN } from 'ng-zorro-antd/i18n';

// 定义全局配置
const nzConfig: NzConfig = {
  message: {
    nzDuration: 3000,
    nzTop: 24,
    nzMaxStack: 7,
    nzPauseOnHover: true,
    nzDirection: "ltr"
  }
};

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([httpInterceptor])),
    provideAnimationsAsync(),
    provideNzConfig(nzConfig),
    provideNzI18n(zh_CN)
  ]
};
