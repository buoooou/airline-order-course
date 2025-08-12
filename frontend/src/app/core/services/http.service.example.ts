import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpService } from './http.service';
import { Observable } from 'rxjs';

// 示例接口定义
interface User {
  id: number;
  name: string;
  email: string;
  avatar?: string;
}

interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  success: boolean;
}

// 在组件类外部或顶部定义
interface CreateUserRequest {
  name: string;
  email: string;
  age: number;
  // 可根据实际接口需求添加其他字段，如 password、role 等
}

@Component({
  selector: 'app-http-example',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="example-container">
      <h2>HTTP服务使用示例</h2>
      
      <div class="section">
        <h3>1. 获取用户列表</h3>
        <button (click)="getUsers()" [disabled]="loading">获取用户</button>
        <div *ngIf="users$ | async as users" class="users">
          <div *ngFor="let user of users" class="user-card">
            <span>{{ user.name }} ({{ user.email }})</span>
          </div>
        </div>
      </div>

      <div class="section">
        <h3>2. 创建用户</h3>
        <button (click)="createUser()">创建用户</button>
      </div>

      <div class="section">
        <h3>3. 上传文件</h3>
        <input type="file" (change)="onFileSelected($event)" accept="image/*">
        <button (click)="uploadFile()" [disabled]="!selectedFile">上传文件</button>
      </div>
    </div>
  `,
  styles: [`
    .example-container {
      padding: 20px;
    }
    
    .section {
      margin-bottom: 20px;
      padding: 15px;
      border: 1px solid #ddd;
      border-radius: 4px;
    }
    
    .users {
      margin-top: 10px;
    }
    
    .user-card {
      padding: 8px;
      margin: 5px 0;
      background: #f5f5f5;
      border-radius: 4px;
    }
    
    button {
      margin-right: 10px;
      padding: 8px 16px;
      background: #1890ff;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }
    
    button:disabled {
      background: #ccc;
      cursor: not-allowed;
    }
  `]
})
export class HttpExampleComponent implements OnInit {
  users$!: Observable<User[]>;
  loading = false;
  selectedFile: File | null = null;

  constructor(private httpService: HttpService) {}

  ngOnInit(): void {
    // 组件初始化时可以调用API
    // this.getUsers();
  }

  // 示例1: 获取用户列表
  getUsers(): void {
    this.loading = true;
    this.users$ = this.httpService.get<User[]>('/users', {
      params: { page: '1', size: '10' }
    });
    
    this.users$.subscribe({
      next: (users) => {
        console.log('获取用户成功:', users);
        this.loading = false;
      },
      error: (error: Error) => {
        console.error('获取用户失败:', error);
        this.loading = false;
      }
    });
  }

  // 示例2: 创建用户
  createUser(): void {
    const newUser: CreateUserRequest = {
      name: '新用户',
      email: 'newuser@example.com',
      age: 25
    };

    this.httpService.post<User>('/users', newUser).subscribe({
      next: (user) => {
        console.log('用户创建成功:', user);
        this.loading = false;
      },
      error: (error: Error) => {
        console.error('用户创建失败:', error);
        this.loading = false;
      }
    });
  }

  // 示例3: 文件上传
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  uploadFile(): void {
    if (!this.selectedFile) return;

    this.httpService.upload('/upload', this.selectedFile, 'avatar').subscribe({
      next: (result) => {
        console.log('文件上传成功:', result);
        this.selectedFile = null;
      },
      error: (error: Error) => {
        console.error('文件上传失败:', error);
      }
    });
  }

  // 示例4: 更新用户
  updateUser(userId: number): void {
    const updateData = {
      name: '李四',
      email: 'lisi@example.com'
    };

    this.httpService.put<User>(`/users/${userId}`, updateData).subscribe({
      next: (user) => {
        console.log('更新用户成功:', user);
        this.getUsers();
      },
      error: (error) => {
        console.error('更新用户失败:', error);
      }
    });
  }

  // 示例5: 删除用户
  deleteUser(userId: number): void {
    this.httpService.delete<void>(`/users/${userId}`).subscribe({
      next: () => {
        console.log('删除用户成功');
        this.getUsers();
      },
      error: (error: Error) => {
        console.error('删除用户失败:', error);
      }
    });
  }
}