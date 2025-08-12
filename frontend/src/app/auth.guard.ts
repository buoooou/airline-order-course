import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(): boolean {
    const token = typeof window !== 'undefined' && window.localStorage ? localStorage.getItem('token') : null;
    if (!token) {
      this.router.navigate(['/login']);
      return false;
    }
    return true;
  }
}