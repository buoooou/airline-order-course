import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Login } from '../login/login';
import { ApiService } from '../../services/api';

describe('LoginComponent', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let mockApiService: jasmine.SpyObj<ApiService>;
  let mockRouter: jasmine.SpyObj<Router>;
  let mockLocalStorage: {
    setItem: jasmine.Spy;
  };

  beforeEach(() => {
    mockApiService = jasmine.createSpyObj('ApiService', ['login']);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockLocalStorage = {
      setItem: jasmine.createSpy('setItem')
    };

    TestBed.configureTestingModule({
      declarations: [Login],
      providers: [
        { provide: ApiService, useValue: mockApiService },
        { provide: Router, useValue: mockRouter },
        { provide: 'localStorage', useValue: mockLocalStorage }
      ]
    });

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
  });

  it('should call api.login with username and password', () => {
    component.loginForm.setValue({ username: 'testUser', password: 'testPass' });
    mockApiService.login.and.returnValue(Promise.resolve({ token: 'mockToken' }));

    component.login();

    expect(mockApiService.login).toHaveBeenCalledWith('testUser', 'testPass');
  });

  const TEST_TOKEN = 'mockToken';
const TEST_ERROR_MSG = '登录失败: 用户名或密码错误';

  it('should store JWT and navigate to home on successful login', () => {
    mockApiService.login.and.returnValue(Promise.resolve({ token: TEST_TOKEN }));

    component.login();

    expect(mockLocalStorage.setItem).toHaveBeenCalledWith('jwt', TEST_TOKEN);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should log error on failed login', () => {
    const consoleSpy = spyOn(console, 'error');
    mockApiService.login.and.returnValue(Promise.reject(new Error(TEST_ERROR_MSG)));

    component.login();

    expect(consoleSpy).toHaveBeenCalledWith(TEST_ERROR_MSG);
  });

  it('should handle empty username or password', () => {
    component.loginForm.setValue({ username: '', password: '' });
    const consoleSpy = spyOn(console, 'error');

    component.login();

    expect(consoleSpy).toHaveBeenCalledWith('登录失败: 用户名或密码不能为空');
  });
});