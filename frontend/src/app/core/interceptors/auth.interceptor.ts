import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { catchError, switchMap, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  // Clone the request and add the authorization header if token exists
  if (token && authService.isLoggedIn()) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req).pipe(
    catchError(error => {
      // If we get a 401 error, try to refresh the token
      if (error.status === 401 && authService.isLoggedIn()) {
        return authService.refreshToken().pipe(
          switchMap(() => {
            // Retry the original request with new token
            const newToken = authService.getToken();
            if (newToken) {
              const newReq = req.clone({
                setHeaders: {
                  Authorization: `Bearer ${newToken}`
                }
              });
              return next(newReq);
            }
            return throwError(() => error);
          }),
          catchError(() => {
            // Refresh failed, logout user
            authService.logout();
            return throwError(() => error);
          })
        );
      }
      
      return throwError(() => error);
    })
  );
};