import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators,ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/service/auth.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true,
    imports: [
    ReactiveFormsModule,
    RouterModule
  ]
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string = '';
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', Validators.required]
    });
  }

onSubmit() {
  if (this.loginForm.valid) {
    this.isLoading = true;
    this.authService.login(this.loginForm.value).subscribe({
      next: (success) => {
        this.isLoading = false; 
        if (success) {
          this.router.navigate(['/orders']);
        } else {
          this.errorMessage = '登录失败：用户名或密码错误';
        }
      },
      error: (error) => {
        this.isLoading = false;
        console.error('登录请求失败:', error);
        this.errorMessage = '网络错误，请稍后重试';
      }
    });
  }
}
}