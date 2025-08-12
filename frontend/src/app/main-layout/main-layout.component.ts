import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.css'],
  imports: [RouterModule]
})
export class MainLayoutComponent {
  constructor(private router: Router, private http: HttpClient) {}

  logout() {
    this.http.post(`${environment.apiUrl}/auth/logout`, {}).subscribe({
      next: () => {
        if (typeof window !== 'undefined' && window.localStorage) {
          localStorage.removeItem('token');
        }
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error('注销失败:', error);
      }
    });
  }
}