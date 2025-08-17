import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZonelessChangeDetection, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';

import { appRoutes } from './app.routes';
import { en_US, provideNzI18n } from 'ng-zorro-antd/i18n';
import { DatePipe, registerLocaleData } from '@angular/common';
import en from '@angular/common/locales/en';
import { FormsModule } from '@angular/forms';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptors, withInterceptorsFromDi } from '@angular/common/http';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';

registerLocaleData(en);

export const appConfig: ApplicationConfig = {
  providers: [
    DatePipe,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(appRoutes),
    provideNzI18n(en_US),
    importProvidersFrom(FormsModule),
    provideAnimationsAsync(),
    provideHttpClient(withInterceptorsFromDi()),
  ],
};
