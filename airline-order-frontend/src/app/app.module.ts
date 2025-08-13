import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BrowserModule } from '@angular/platform-browser';
import { ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { MatBadgeModule } from '@angular/material/badge';
import { TranslateModule, TranslateLoader, TranslateService } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { HttpClient, HttpClientModule, provideHttpClient, withFetch } from '@angular/common/http';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { AppComponent } from './app.component';
import { DatePipe } from '@angular/common';
import { OrderStatus } from './pages/order-status/order-status';
import { OrderInfoComponent } from './pages/order-info/order-info.component';
import { OrderList } from './pages/order-list/order-list';
import { OrderDetail } from './pages/order-detail/order-detail';
import { StatusTextPipe } from './pipes/status-text.pipe';
import { AuthGuard } from './auth.guard';

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader();
}

@NgModule({
  declarations: [
    OrderInfoComponent,
    AppComponent,
    StatusTextPipe,
    OrderDetail,
    OrderList,
    OrderStatus
  ],
  imports: [
    FontAwesomeModule,
    CommonModule,
    RouterModule.forRoot([]),
    BrowserModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatPaginatorModule,
    MatTableModule,
    MatBadgeModule,
    HttpClientModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    })
  ],
  providers: [AuthGuard, DatePipe, TranslateService, provideHttpClient(withFetch())],
  bootstrap: [AppComponent]
})
export class AppModule { }