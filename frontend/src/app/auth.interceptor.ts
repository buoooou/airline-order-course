import { HttpInterceptorFn } from '@angular/common/http';
import { HttpRequest } from '@angular/common/http';
import { HttpHandlerFn } from '@angular/common/http';
import { HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (
  request: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {
  const token = typeof window !== 'undefined' && window.localStorage ? localStorage.getItem('token') : null;
  if (token) {
    request = request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }
  return next(request);
};