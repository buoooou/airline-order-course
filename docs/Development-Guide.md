# 开发指南

## 项目架构

### 总体架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端 (Angular) │───▶│  后端 (Spring)   │───▶│   数据库 (MySQL) │
│                 │    │                 │    │                 │
│ - 用户界面       │    │ - REST API      │    │ - 数据存储       │
│ - 状态管理       │    │ - 业务逻辑       │    │ - 数据持久化     │
│ - 路由导航       │    │ - 安全认证       │    │ - 事务管理       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 后端架构 (Spring Boot)

```
┌─────────────────────────────────────────────────────────────┐
│                        Presentation Layer                   │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ Controller  │ │ RestController│ │ Exception   │           │
│  │             │ │              │ │ Handler     │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
                               │
┌─────────────────────────────────────────────────────────────┐
│                        Business Layer                       │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ Service     │ │ DTO         │ │ Mapper      │           │
│  │             │ │             │ │             │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
                               │
┌─────────────────────────────────────────────────────────────┐
│                        Persistence Layer                    │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ Repository  │ │ Entity      │ │ Configuration│           │
│  │             │ │             │ │             │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
```

### 前端架构 (Angular)

```
┌─────────────────────────────────────────────────────────────┐
│                           Components                         │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ Feature     │ │ Shared      │ │ Layout      │           │
│  │ Components  │ │ Components  │ │ Components  │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
                               │
┌─────────────────────────────────────────────────────────────┐
│                           Services                           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ HTTP        │ │ Auth        │ │ State       │           │
│  │ Services    │ │ Services    │ │ Management  │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
                               │
┌─────────────────────────────────────────────────────────────┐
│                        Core & Shared                        │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ Guards      │ │ Interceptors│ │ Models      │           │
│  │             │ │             │ │             │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
```

## 开发环境设置

### 1. IDE 配置

#### IntelliJ IDEA (推荐)

**插件安装**:
- Lombok
- MapStruct Support
- Spring Boot Assistant
- Angular Language Service

**代码格式化设置**:
```xml
<!-- .editorconfig -->
root = true

[*]
charset = utf-8
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true

[*.java]
indent_style = space
indent_size = 4

[*.{ts,js,json}]
indent_style = space
indent_size = 2
```

#### VS Code

**推荐扩展**:
- Extension Pack for Java
- Spring Boot Extension Pack
- Angular Language Service
- Prettier - Code formatter
- ESLint

### 2. Git 配置

#### Git Hooks 设置
```bash
# pre-commit hook
#!/bin/sh
# 运行代码格式化
mvn spotless:apply -f backend/pom.xml
npm run lint --fix --prefix frontend

# 运行测试
mvn test -f backend/pom.xml
npm test --prefix frontend --watch=false
```

#### .gitignore 配置
```gitignore
# 编译输出
target/
dist/
*.class

# 依赖
node_modules/
.m2/

# IDE
.idea/
.vscode/
*.iml

# 操作系统
.DS_Store
Thumbs.db

# 日志
*.log
logs/

# 环境变量
.env
.env.local
```

## 编码规范

### 1. Java 编码规范

#### 1.1 命名规范
```java
// 类名：大驼峰命名
public class FlightService {}

// 方法名：小驼峰命名
public FlightDto createFlight() {}

// 常量：大写下划线
private static final String DEFAULT_CURRENCY = "CNY";

// 包名：小写，点分隔
package com.airline.service.impl;
```

#### 1.2 注释规范
```java
/**
 * 航班服务接口
 * 
 * @author 开发团队
 * @version 1.0
 * @since 2024-08-13
 */
public interface FlightService {
    
    /**
     * 创建新的航班
     * 
     * @param flightDto 航班信息
     * @return 创建的航班信息
     * @throws ValidationException 当输入数据无效时
     */
    FlightDto createFlight(FlightDto flightDto);
}
```

#### 1.3 异常处理
```java
// 自定义异常
public class FlightBookingException extends RuntimeException {
    private final String errorCode;
    
    public FlightBookingException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

// 异常处理
@Service
public class FlightServiceImpl implements FlightService {
    
    public FlightDto createFlight(FlightDto flightDto) {
        try {
            validateFlightData(flightDto);
            return saveFlightData(flightDto);
        } catch (DataIntegrityViolationException e) {
            throw new FlightBookingException("航班数据保存失败", "DATA_SAVE_ERROR");
        }
    }
}
```

### 2. TypeScript/Angular 编码规范

#### 2.1 命名规范
```typescript
// 类名：大驼峰
export class FlightSearchComponent {}

// 方法名：小驼峰
public searchFlights(): void {}

// 属性名：小驼峰
private isLoading = false;

// 常量：大写下划线
private static readonly MAX_PASSENGERS = 9;

// 接口：大驼峰，以 I 开头（可选）
export interface IFlightData {}
```

#### 2.2 组件结构
```typescript
@Component({
  selector: 'app-flight-search',
  templateUrl: './flight-search.component.html',
  styleUrls: ['./flight-search.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FlightSearchComponent implements OnInit, OnDestroy {
  // 1. 公共属性
  public searchForm: FormGroup;
  public flights$ = new BehaviorSubject<Flight[]>([]);
  
  // 2. 私有属性
  private destroy$ = new Subject<void>();
  
  // 3. 构造函数
  constructor(
    private fb: FormBuilder,
    private flightService: FlightService,
    private cdr: ChangeDetectorRef
  ) {
    this.initForm();
  }
  
  // 4. 生命周期方法
  ngOnInit(): void {
    this.loadInitialData();
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
  
  // 5. 公共方法
  public searchFlights(): void {}
  
  // 6. 私有方法
  private initForm(): void {}
  private loadInitialData(): void {}
}
```

#### 2.3 错误处理
```typescript
// 服务中的错误处理
@Injectable({
  providedIn: 'root'
})
export class FlightService {
  
  searchFlights(criteria: SearchCriteria): Observable<Flight[]> {
    return this.http.post<ApiResponse<Flight[]>>('/api/flights/search', criteria)
      .pipe(
        map(response => response.data),
        catchError(this.handleError),
        retry(2),
        timeout(10000)
      );
  }
  
  private handleError = (error: HttpErrorResponse): Observable<never> => {
    let errorMessage = '未知错误';
    
    if (error.error instanceof ErrorEvent) {
      // 客户端错误
      errorMessage = error.error.message;
    } else {
      // 服务器错误
      errorMessage = error.error?.message || `服务器错误: ${error.status}`;
    }
    
    console.error('API 错误:', errorMessage);
    return throwError(() => new Error(errorMessage));
  };
}
```

## 数据库设计规范

### 1. 表命名规范
```sql
-- 表名：小写，下划线分隔，必须以 _yb 结尾
CREATE TABLE flight_orders_yb;
CREATE TABLE passenger_info_yb;

-- 字段名：小写，下划线分隔
CREATE TABLE flights_yb (
    id BIGINT PRIMARY KEY,
    flight_number VARCHAR(20),
    departure_time DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2. 索引设计
```sql
-- 主键索引（自动创建）
ALTER TABLE flights_yb ADD PRIMARY KEY (id);

-- 唯一索引
ALTER TABLE flights_yb ADD UNIQUE KEY uk_flight_number (flight_number);

-- 复合索引（查询优化）
ALTER TABLE flights_yb ADD INDEX idx_route_time (departure_airport_id, arrival_airport_id, departure_time);

-- 外键索引
ALTER TABLE order_items_yb ADD INDEX idx_order_id (order_id);
```

### 3. 数据迁移
```sql
-- 使用 Flyway 进行版本管理
-- V1__Create_initial_tables.sql
-- V2__Add_payment_table.sql
-- V3__Update_flight_status_enum.sql

-- 示例迁移脚本
-- V2__Add_payment_table.sql
CREATE TABLE payments_yb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('CREDIT_CARD', 'ALIPAY', 'WECHAT'),
    status ENUM('PENDING', 'SUCCESS', 'FAILED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders_yb(id)
);
```

## 测试规范

### 1. 单元测试

#### Java 单元测试
```java
@ExtendWith(MockitoExtension.class)
class FlightServiceTest {
    
    @Mock
    private FlightRepository flightRepository;
    
    @Mock
    private FlightMapper flightMapper;
    
    @InjectMocks
    private FlightServiceImpl flightService;
    
    @Test
    @DisplayName("应该成功创建航班")
    void shouldCreateFlightSuccessfully() {
        // Given
        FlightDto inputDto = createValidFlightDto();
        Flight entity = createFlightEntity();
        Flight savedEntity = createSavedFlightEntity();
        FlightDto expectedDto = createExpectedFlightDto();
        
        when(flightMapper.toEntity(inputDto)).thenReturn(entity);
        when(flightRepository.save(entity)).thenReturn(savedEntity);
        when(flightMapper.toDto(savedEntity)).thenReturn(expectedDto);
        
        // When
        FlightDto result = flightService.createFlight(inputDto);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFlightNumber()).isEqualTo(expectedDto.getFlightNumber());
        verify(flightRepository).save(entity);
    }
    
    @Test
    @DisplayName("当航班号重复时应该抛出异常")
    void shouldThrowExceptionWhenFlightNumberDuplicated() {
        // Given
        FlightDto inputDto = createValidFlightDto();
        when(flightRepository.existsByFlightNumber(anyString())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> flightService.createFlight(inputDto))
            .isInstanceOf(ValidationException.class)
            .hasMessage("航班号已存在");
    }
}
```

#### Angular 单元测试
```typescript
describe('FlightSearchComponent', () => {
  let component: FlightSearchComponent;
  let fixture: ComponentFixture<FlightSearchComponent>;
  let flightService: jasmine.SpyObj<FlightService>;

  beforeEach(async () => {
    const flightServiceSpy = jasmine.createSpyObj('FlightService', ['searchFlights']);

    await TestBed.configureTestingModule({
      declarations: [FlightSearchComponent],
      imports: [ReactiveFormsModule, HttpClientTestingModule],
      providers: [
        { provide: FlightService, useValue: flightServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FlightSearchComponent);
    component = fixture.componentInstance;
    flightService = TestBed.inject(FlightService) as jasmine.SpyObj<FlightService>;
  });

  it('应该创建组件', () => {
    expect(component).toBeTruthy();
  });

  it('应该在有效表单提交时调用搜索服务', () => {
    // Given
    const mockFlights = [createMockFlight()];
    flightService.searchFlights.and.returnValue(of(mockFlights));
    
    component.searchForm.patchValue({
      departureCity: 'PEK',
      arrivalCity: 'SHA',
      departureDate: new Date()
    });

    // When
    component.searchFlights();

    // Then
    expect(flightService.searchFlights).toHaveBeenCalled();
  });
});
```

### 2. 集成测试

#### Spring Boot 集成测试
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class FlightControllerIntegrationTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private FlightRepository flightRepository;
    
    @Test
    @DisplayName("应该成功搜索航班")
    void shouldSearchFlightsSuccessfully() {
        // Given
        createTestFlightData();
        FlightSearchDto searchDto = createSearchDto();
        
        // When
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
            "/api/flights/search", 
            searchDto, 
            ApiResponse.class
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isSuccess()).isTrue();
    }
}
```

### 3. E2E 测试

#### Cypress E2E 测试
```typescript
// cypress/integration/flight-booking.spec.ts
describe('航班预订流程', () => {
  beforeEach(() => {
    cy.visit('/');
    cy.login('test@example.com', 'password');
  });

  it('应该完成完整的航班预订流程', () => {
    // 搜索航班
    cy.get('[data-cy=departure-city]').select('PEK');
    cy.get('[data-cy=arrival-city]').select('SHA');
    cy.get('[data-cy=departure-date]').type('2024-12-25');
    cy.get('[data-cy=search-button]').click();

    // 选择航班
    cy.get('[data-cy=flight-card]').first().click();
    cy.get('[data-cy=select-flight]').click();

    // 填写旅客信息
    cy.get('[data-cy=passenger-name]').type('张三');
    cy.get('[data-cy=passenger-id]').type('110101199001011234');

    // 确认订单
    cy.get('[data-cy=confirm-order]').click();

    // 验证订单创建成功
    cy.url().should('include', '/orders/');
    cy.get('[data-cy=order-status]').should('contain', '已创建');
  });
});
```

## 性能优化

### 1. 后端性能优化

#### 1.1 数据库优化
```java
// 使用 @Query 优化查询
@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    
    @Query("SELECT f FROM Flight f " +
           "JOIN FETCH f.airline " +
           "JOIN FETCH f.departureAirport " +
           "JOIN FETCH f.arrivalAirport " +
           "WHERE f.departureAirport.code = :departureCode " +
           "AND f.arrivalAirport.code = :arrivalCode " +
           "AND DATE(f.departureTime) = DATE(:departureDate)")
    List<Flight> findFlightsWithDetails(@Param("departureCode") String departureCode,
                                       @Param("arrivalCode") String arrivalCode,
                                       @Param("departureDate") LocalDateTime departureDate);
}

// 使用缓存
@Service
public class FlightServiceImpl implements FlightService {
    
    @Cacheable(value = "flights", key = "#departureCode + #arrivalCode + #departureDate")
    public List<FlightDto> searchFlights(String departureCode, String arrivalCode, LocalDate departureDate) {
        // 实现搜索逻辑
    }
    
    @CacheEvict(value = "flights", allEntries = true)
    public FlightDto createFlight(FlightDto flightDto) {
        // 创建航班后清除缓存
    }
}
```

#### 1.2 异步处理
```java
@Service
public class NotificationService {
    
    @Async("taskExecutor")
    public CompletableFuture<Void> sendBookingConfirmation(String email, OrderDto order) {
        // 异步发送邮件通知
        emailService.sendEmail(email, "订单确认", buildEmailContent(order));
        return CompletableFuture.completedFuture(null);
    }
}

@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean("taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}
```

### 2. 前端性能优化

#### 2.1 懒加载和代码分割
```typescript
// 路由懒加载
const routes: Routes = [
  {
    path: 'flights',
    loadChildren: () => import('./features/flights/flights.module').then(m => m.FlightsModule)
  }
];

// 组件懒加载
@Component({
  template: `
    <ng-container *ngIf="showHeavyComponent">
      <app-heavy-component></app-heavy-component>
    </ng-container>
  `
})
export class ParentComponent {
  showHeavyComponent = false;
  
  loadHeavyComponent() {
    import('./heavy-component/heavy-component.component').then(({ HeavyComponent }) => {
      // 动态加载组件
    });
  }
}
```

#### 2.2 变更检测优化
```typescript
@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div *ngFor="let flight of flights$ | async; trackBy: trackByFlightId">
      {{ flight.flightNumber }}
    </div>
  `
})
export class FlightListComponent {
  flights$ = this.flightService.getFlights();
  
  trackByFlightId(index: number, flight: Flight): number {
    return flight.id;
  }
}
```

## 安全最佳实践

### 1. 后端安全

#### 1.1 输入验证
```java
@RestController
@Validated
public class FlightController {
    
    @PostMapping("/flights")
    public ResponseEntity<ApiResponse<FlightDto>> createFlight(
            @Valid @RequestBody FlightDto flightDto) {
        // @Valid 注解会自动验证输入数据
    }
}

@Entity
public class Flight {
    
    @NotBlank(message = "航班号不能为空")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{3,4}$", message = "航班号格式不正确")
    private String flightNumber;
    
    @NotNull(message = "出发时间不能为空")
    @Future(message = "出发时间必须是将来时间")
    private LocalDateTime departureTime;
}
```

#### 1.2 SQL 注入防护
```java
// 使用 JPA Query，自动防护 SQL 注入
@Query("SELECT f FROM Flight f WHERE f.flightNumber = :flightNumber")
Flight findByFlightNumber(@Param("flightNumber") String flightNumber);

// 避免字符串拼接
// 错误示例
@Query(value = "SELECT * FROM flights WHERE flight_number = '" + flightNumber + "'", nativeQuery = true)

// 正确示例
@Query(value = "SELECT * FROM flights WHERE flight_number = ?1", nativeQuery = true)
```

### 2. 前端安全

#### 2.1 XSS 防护
```typescript
// 使用 Angular 内置的 DomSanitizer
@Component({
  template: `
    <div [innerHTML]="sanitizedHtml"></div>
  `
})
export class SafeComponent {
  constructor(private sanitizer: DomSanitizer) {}
  
  get sanitizedHtml() {
    return this.sanitizer.sanitize(SecurityContext.HTML, this.userInput);
  }
}
```

#### 2.2 CSRF 防护
```typescript
// 在 HTTP 拦截器中添加 CSRF token
@Injectable()
export class CsrfInterceptor implements HttpInterceptor {
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.method === 'POST' || req.method === 'PUT' || req.method === 'DELETE') {
      const csrfToken = this.getCsrfToken();
      if (csrfToken) {
        req = req.clone({
          setHeaders: {
            'X-CSRF-TOKEN': csrfToken
          }
        });
      }
    }
    
    return next.handle(req);
  }
}
```

## 代码审查清单

### 1. 通用检查项
- [ ] 代码遵循项目编码规范
- [ ] 所有公共方法都有适当的注释
- [ ] 异常处理适当且完整
- [ ] 无硬编码的值，使用配置文件
- [ ] 敏感信息（密码、密钥）未硬编码
- [ ] 单元测试覆盖率达到 80% 以上

### 2. 后端检查项
- [ ] Controller 只负责请求处理，业务逻辑在 Service 层
- [ ] 使用 DTO 进行数据传输，不直接暴露 Entity
- [ ] 数据库操作使用事务管理
- [ ] 输入参数进行了验证
- [ ] SQL 查询使用了参数化查询
- [ ] 缓存策略合理

### 3. 前端检查项
- [ ] 组件职责单一，可复用性强
- [ ] 使用 OnPush 变更检测策略优化性能
- [ ] HTTP 请求包含适当的错误处理
- [ ] 表单包含验证逻辑
- [ ] 用户输入进行了清理和验证
- [ ] 路由包含适当的守卫

---

遵循以上开发指南可以确保代码质量、系统性能和安全性。如有疑问，请参考相关文档或咨询团队成员。