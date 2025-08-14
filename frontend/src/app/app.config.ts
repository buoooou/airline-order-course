import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZonelessChangeDetection, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { en_US, provideNzI18n } from 'ng-zorro-antd/i18n';
import { registerLocaleData } from '@angular/common';
import en from '@angular/common/locales/en';
import { FormsModule } from '@angular/forms';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptors } from '@angular/common/http';
import { NZ_CONFIG, NzConfig } from 'ng-zorro-antd/core/config';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';
registerLocaleData(en);

const ngZorroConfig: NzConfig = {
  // theme: { primaryColor: '#1890ff' }
};

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideNzI18n(en_US),
    importProvidersFrom(FormsModule),
    provideAnimationsAsync(),
    provideHttpClient(),
    { provide: NZ_CONFIG, useValue: ngZorroConfig },
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}
  ],
};
