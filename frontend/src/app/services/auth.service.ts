import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { LoginRequest, RegisterRequest, AuthResponse } from '../models/user.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl || '';
  private tokenSubject = new BehaviorSubject<string | null>(this.getStoredToken());
  public token$ = this.tokenSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(loginRequest: LoginRequest): Observable<AuthResponse> {
    const url = this.apiUrl ? `${this.apiUrl}/api/auth/login` : `/api/auth/login`;
    console.log('Login URL:', url, 'API Base URL:', this.apiUrl);
    return this.http.post<AuthResponse>(url, loginRequest)
      .pipe(
        tap(response => {
          this.setToken(response.token);
        })
      );
  }

  register(registerRequest: RegisterRequest): Observable<AuthResponse> {
    const url = this.apiUrl ? `${this.apiUrl}/api/auth/register` : `/api/auth/register`;
    console.log('Register URL:', url, 'API Base URL:', this.apiUrl);
    return this.http.post<AuthResponse>(url, registerRequest)
      .pipe(
        tap(response => {
          this.setToken(response.token);
        })
      );
  }

  logout(): void {
    localStorage.removeItem('token');
    this.tokenSubject.next(null);
  }

  getToken(): string | null {
    return this.tokenSubject.value;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  private setToken(token: string): void {
    localStorage.setItem('token', token);
    this.tokenSubject.next(token);
  }

  private getStoredToken(): string | null {
    return localStorage.getItem('token');
  }
}
