import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { catchError, map, Observable, of } from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  
  // return auth.isLoggedIn() ? true : router.parseUrl('/login');
  return auth.isAuthenticated$.pipe(
    map(isValid => isValid ? true : router.parseUrl('/login')),
    catchError(() => of(router.parseUrl('/login')))
  );
};
