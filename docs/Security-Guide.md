# 安全指南

## 概述

本文档详细描述了在线机票预订系统的安全架构、安全措施以及安全最佳实践，确保系统和用户数据的安全性。

## 威胁模型

### 1. 潜在威胁

| 威胁类型 | 描述 | 风险等级 | 缓解措施 |
|----------|------|----------|----------|
| SQL 注入 | 恶意 SQL 代码注入 | 高 | 参数化查询、输入验证 |
| XSS 攻击 | 跨站脚本攻击 | 高 | 输出编码、CSP 策略 |
| CSRF 攻击 | 跨站请求伪造 | 中 | CSRF Token、同源策略 |
| 认证绕过 | 未授权访问 | 高 | JWT 验证、权限检查 |
| 数据泄露 | 敏感信息暴露 | 高 | 数据加密、访问控制 |
| DDoS 攻击 | 拒绝服务攻击 | 中 | 限流、负载均衡 |
| 会话劫持 | 会话令牌被盗用 | 中 | HTTPS、安全 Cookie |

### 2. 资产分类

**关键资产**:
- 用户认证信息（密码、令牌）
- 个人身份信息（身份证、护照）
- 支付信息（信用卡、银行账户）
- 订单数据
- 系统配置信息

**保护级别**:
- **机密级**: 密码、支付信息、身份证号
- **敏感级**: 个人信息、订单数据
- **内部级**: 系统日志、配置文件
- **公开级**: 航班时刻表、机场信息

## 认证与授权

### 1. 认证机制

#### 1.1 JWT (JSON Web Token) 认证
```java
// JWT 配置
@Component
public class JwtTokenProvider {
    
    // 使用 HS512 算法签名
    private final SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInMs);
        
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("id", userPrincipal.getId())
                .claim("authorities", getAuthorities(authentication))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}
```

#### 1.2 密码安全策略
```java
// 密码加密配置
@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用 BCrypt 强哈希算法
        return new BCryptPasswordEncoder(12); // 12轮加密
    }
}

// 密码验证规则
@Entity
public class User {
    
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "密码必须包含大小写字母、数字和特殊字符，长度至少8位"
    )
    private String password;
}
```

### 2. 授权机制

#### 2.1 基于角色的访问控制 (RBAC)
```java
// 权限检查
@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/flights")
public ResponseEntity<FlightDto> createFlight(@RequestBody FlightDto flightDto) {
    // 只有管理员可以创建航班
}

@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@PostMapping("/orders")
public ResponseEntity<OrderDto> createOrder(@RequestBody OrderCreateDto orderDto) {
    // 用户和管理员可以创建订单
}

@PreAuthorize("@orderService.isOrderOwner(#orderId, authentication.name) or hasRole('ADMIN')")
@GetMapping("/orders/{orderId}")
public ResponseEntity<OrderDto> getOrder(@PathVariable Long orderId) {
    // 只有订单所有者或管理员可以查看订单
}
```

#### 2.2 权限矩阵

| 资源 | 游客 | 用户 | 管理员 |
|------|------|------|--------|
| 查看航班 | ✅ | ✅ | ✅ |
| 创建订单 | ❌ | ✅ | ✅ |
| 查看订单 | ❌ | 仅自己 | ✅ |
| 管理航班 | ❌ | ❌ | ✅ |
| 用户管理 | ❌ | 仅自己 | ✅ |

## 数据保护

### 1. 数据加密

#### 1.1 传输加密
```yaml
# 强制 HTTPS
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: airline-booking

# HTTP 安全头
spring:
  security:
    headers:
      frame-options: DENY
      content-type-options: nosniff
      xss-protection: 1; mode=block
```

#### 1.2 数据库加密
```java
// 敏感字段加密
@Entity
public class Passenger {
    
    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "id_card_number")
    private String idCardNumber; // 身份证号加密存储
    
    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "passport_number")
    private String passportNumber; // 护照号加密存储
}

// 自定义加密转换器
@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {
    
    private final AESUtil aesUtil;
    
    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute == null ? null : aesUtil.encrypt(attribute);
    }
    
    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData == null ? null : aesUtil.decrypt(dbData);
    }
}
```

### 2. 数据脱敏

#### 2.1 日志脱敏
```java
// 日志脱敏配置
@Component
public class LogMaskingPatternLayout extends PatternLayout {
    
    private static final Pattern PHONE_PATTERN = Pattern.compile("(\\d{3})\\d{4}(\\d{4})");
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("(\\d{6})\\d{8}(\\d{4})");
    
    @Override
    public String doLayout(ILoggingEvent event) {
        String message = super.doLayout(event);
        
        // 手机号脱敏：138****1234
        message = PHONE_PATTERN.matcher(message).replaceAll("$1****$2");
        
        // 身份证脱敏：123456********1234
        message = ID_CARD_PATTERN.matcher(message).replaceAll("$1********$2");
        
        return message;
    }
}
```

#### 2.2 API 响应脱敏
```java
// 响应数据脱敏
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PassengerDto {
    
    @JsonProperty("idCardNumber")
    @JsonSerialize(using = IdCardMaskSerializer.class)
    private String idCardNumber;
    
    @JsonProperty("phone")
    @JsonSerialize(using = PhoneMaskSerializer.class)
    private String phone;
}

public class IdCardMaskSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) 
            throws IOException {
        if (value != null && value.length() >= 10) {
            String masked = value.substring(0, 6) + "********" + value.substring(value.length() - 4);
            gen.writeString(masked);
        } else {
            gen.writeString(value);
        }
    }
}
```

## 输入验证

### 1. 服务端验证

#### 1.1 Bean Validation
```java
@Entity
public class User {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}

// 自定义验证注解
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IdCardValidator.class)
public @interface ValidIdCard {
    String message() default "身份证号格式不正确";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class IdCardValidator implements ConstraintValidator<ValidIdCard, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // 由 @NotBlank 处理空值
        }
        return isValidIdCard(value);
    }
}
```

#### 1.2 SQL 注入防护
```java
// 使用 JPA Repository，自动防护 SQL 注入
@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    
    // 安全的参数化查询
    @Query("SELECT f FROM Flight f WHERE f.flightNumber = :flightNumber")
    Optional<Flight> findByFlightNumber(@Param("flightNumber") String flightNumber);
    
    // 避免原生 SQL 字符串拼接
    @Query(value = "SELECT * FROM flights_yb WHERE departure_airport_id = ?1 " +
                   "AND arrival_airport_id = ?2 AND DATE(departure_time) = ?3",
           nativeQuery = true)
    List<Flight> findFlightsByRoute(Long departureId, Long arrivalId, LocalDate date);
}
```

### 2. 客户端验证

#### 2.1 Angular 表单验证
```typescript
// 响应式表单验证
@Component({
  selector: 'app-user-registration',
  template: `
    <form [formGroup]="registrationForm" (ngSubmit)="onSubmit()">
      <mat-form-field>
        <input matInput formControlName="username" placeholder="用户名">
        <mat-error *ngIf="registrationForm.get('username')?.hasError('required')">
          用户名不能为空
        </mat-error>
        <mat-error *ngIf="registrationForm.get('username')?.hasError('pattern')">
          用户名只能包含字母、数字和下划线
        </mat-error>
      </mat-form-field>
    </form>
  `
})
export class UserRegistrationComponent {
  
  registrationForm = this.fb.group({
    username: ['', [
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(50),
      Validators.pattern(/^[a-zA-Z0-9_]+$/)
    ]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [
      Validators.required,
      Validators.minLength(8),
      this.strongPasswordValidator
    ]]
  });
  
  strongPasswordValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    const hasNumber = /[0-9]/.test(value);
    const hasUpper = /[A-Z]/.test(value);
    const hasLower = /[a-z]/.test(value);
    const hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(value);
    
    const valid = hasNumber && hasUpper && hasLower && hasSpecial;
    return valid ? null : { strongPassword: true };
  }
}
```

#### 2.2 XSS 防护
```typescript
// DOM 清理
@Injectable({
  providedIn: 'root'
})
export class SanitizationService {
  
  constructor(private sanitizer: DomSanitizer) {}
  
  sanitizeHtml(html: string): SafeHtml {
    return this.sanitizer.sanitize(SecurityContext.HTML, html) || '';
  }
  
  sanitizeUrl(url: string): SafeUrl {
    return this.sanitizer.sanitize(SecurityContext.URL, url) || '';
  }
}

// CSP 策略
export const CSP_POLICY = {
  'default-src': "'self'",
  'script-src': "'self' 'unsafe-inline'",
  'style-src': "'self' 'unsafe-inline' fonts.googleapis.com",
  'font-src': "'self' fonts.gstatic.com",
  'img-src': "'self' data: https:",
  'connect-src': "'self' api.example.com"
};
```

## 会话管理

### 1. JWT 令牌安全

#### 1.1 令牌配置
```yaml
app:
  jwt:
    secret: ${JWT_SECRET} # 至少 256 位的强密钥
    expiration: 1800000    # 30 分钟
    refresh-expiration: 604800000 # 7 天
    issuer: airline-booking-system
    
  security:
    token:
      max-sessions-per-user: 3 # 限制同时在线会话数
      require-explicit-logout: true
```

#### 1.2 令牌验证
```java
@Component
public class JwtTokenProvider {
    
    public boolean validateToken(String authToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(authToken)
                    .getBody();
            
            // 检查令牌是否在黑名单中
            if (tokenBlacklistService.isBlacklisted(authToken)) {
                return false;
            }
            
            // 检查令牌是否过期
            return !claims.getExpiration().before(new Date());
            
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }
}

// 令牌黑名单服务
@Service
public class TokenBlacklistService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    public void blacklistToken(String token) {
        // 将令牌加入黑名单，设置过期时间为令牌的剩余有效期
        long expiration = getTokenExpiration(token);
        redisTemplate.opsForValue().set("blacklist:" + token, "true", 
                Duration.ofMillis(expiration));
    }
    
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token));
    }
}
```

### 2. 会话固化防护

#### 2.1 会话令牌轮换
```java
@Service
public class AuthService {
    
    public AuthResponse login(UserLoginDto loginDto) {
        // 验证用户凭据
        Authentication authentication = authenticateUser(loginDto);
        
        // 生成新的会话令牌
        String accessToken = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);
        
        // 废除旧的会话令牌
        invalidateOldTokens(authentication.getName());
        
        return new AuthResponse(accessToken, refreshToken);
    }
    
    private void invalidateOldTokens(String username) {
        // 将用户的所有旧令牌加入黑名单
        List<String> oldTokens = tokenRepository.findByUsername(username);
        oldTokens.forEach(tokenBlacklistService::blacklistToken);
    }
}
```

## 安全配置

### 1. Spring Security 配置

#### 1.1 核心安全配置
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（API 使用 JWT）
            .csrf(csrf -> csrf.disable())
            
            // 会话管理
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 权限配置
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/flights/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            
            // 自定义过滤器
            .addFilterBefore(jwtAuthenticationFilter, 
                           UsernamePasswordAuthenticationFilter.class)
            
            // 异常处理
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler))
            
            // 安全头
            .headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                .contentTypeOptions(withDefaults())
                .xssProtection(withDefaults())
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubdomains(true)));
        
        return http.build();
    }
}
```

#### 1.2 CORS 配置
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("https://*.airline.com", "http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

### 2. 应用级安全配置

#### 2.1 限流配置
```java
@Configuration
public class RateLimitConfig {
    
    @Bean
    public RedisRateLimiter authRateLimiter() {
        // 登录接口限制：每分钟 5 次
        return new RedisRateLimiter(5, 5, Duration.ofMinutes(1));
    }
    
    @Bean
    public RedisRateLimiter apiRateLimiter() {
        // API 调用限制：每秒 100 次
        return new RedisRateLimiter(100, 100, Duration.ofSeconds(1));
    }
}

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {
        
        String clientIp = getClientIp(request);
        String key = "rate_limit:" + clientIp;
        
        if (isRateLimited(key)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("{\"error\":\"Rate limit exceeded\"}");
            return false;
        }
        
        return true;
    }
}
```

#### 2.2 审计日志
```java
@Aspect
@Component
public class AuditLogAspect {
    
    @Autowired
    private AuditLogService auditLogService;
    
    @AfterReturning(value = "@annotation(Auditable)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();
        
        AuditLog auditLog = AuditLog.builder()
                .action(className + "." + methodName)
                .userId(getCurrentUserId())
                .ipAddress(getCurrentUserIp())
                .timestamp(LocalDateTime.now())
                .success(true)
                .details(buildDetails(args, result))
                .build();
        
        auditLogService.save(auditLog);
    }
    
    @AfterThrowing(value = "@annotation(Auditable)", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Exception exception) {
        // 记录异常操作
    }
}

// 使用示例
@Service
public class OrderService {
    
    @Auditable
    public OrderDto createOrder(OrderCreateDto orderDto) {
        // 创建订单的审计日志会自动记录
    }
}
```

## 监控与检测

### 1. 安全事件监控

#### 1.1 异常检测
```java
@Component
public class SecurityEventDetector {
    
    private final AlertService alertService;
    private final MetricsService metricsService;
    
    @EventListener
    public void handleAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        String ip = getClientIp();
        
        // 记录失败次数
        metricsService.incrementCounter("auth.failure", "username", username, "ip", ip);
        
        // 检查是否达到告警阈值
        if (getFailureCount(username, ip) >= MAX_FAILURE_ATTEMPTS) {
            alertService.sendAlert(AlertType.BRUTE_FORCE_ATTACK, 
                                 "用户 " + username + " 来自 " + ip + " 的登录失败次数过多");
            
            // 临时锁定账户
            userLockService.lockUser(username, Duration.ofMinutes(15));
        }
    }
    
    @EventListener
    public void handleSuspiciousActivity(SuspiciousActivityEvent event) {
        // 处理可疑活动
        if (event.getSeverity() == Severity.HIGH) {
            alertService.sendUrgentAlert(event.getDescription());
        }
    }
}
```

#### 1.2 实时监控面板
```java
@RestController
@RequestMapping("/api/admin/security")
@PreAuthorize("hasRole('ADMIN')")
public class SecurityMonitoringController {
    
    @GetMapping("/dashboard")
    public SecurityDashboard getSecurityDashboard() {
        return SecurityDashboard.builder()
                .activeUsers(userService.getActiveUserCount())
                .failedLogins(getFailedLoginCount(Duration.ofHours(1)))
                .suspiciousActivities(getSuspiciousActivities(Duration.ofHours(24)))
                .blockedIps(ipBlockService.getBlockedIpCount())
                .systemHealth(getSystemHealthStatus())
                .build();
    }
    
    @GetMapping("/alerts")
    public List<SecurityAlert> getSecurityAlerts(
            @RequestParam(defaultValue = "24") int hours) {
        return alertService.getAlerts(Duration.ofHours(hours));
    }
}
```

### 2. 日志分析

#### 2.1 安全日志配置
```xml
<!-- logback-spring.xml -->
<configuration>
    <!-- 安全事件日志 -->
    <appender name="SECURITY_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/security.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/security.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers>
                <timestamp/>
                <logLevel/>
                <loggerName/>
                <message/>
                <mdc/>
                <pattern>
                    <pattern>
                        {
                        "user_id": "%X{userId:-}",
                        "ip_address": "%X{ipAddress:-}",
                        "user_agent": "%X{userAgent:-}",
                        "session_id": "%X{sessionId:-}"
                        }
                    </pattern>
                </pattern>
            </providers>
        </encoder>
    </appender>
    
    <logger name="SECURITY" level="INFO" additivity="false">
        <appender-ref ref="SECURITY_FILE"/>
    </logger>
</configuration>
```

#### 2.2 威胁检测规则
```java
@Component
public class ThreatDetectionService {
    
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void detectThreats() {
        detectBruteForceAttacks();
        detectSqlInjectionAttempts();
        detectUnusualAccessPatterns();
    }
    
    private void detectBruteForceAttacks() {
        // 检测暴力破解攻击
        List<String> suspiciousIps = logAnalysisService.findIpsWithHighFailureRate(
                Duration.ofMinutes(5), 10);
        
        suspiciousIps.forEach(ip -> {
            ipBlockService.blockIp(ip, Duration.ofHours(1));
            alertService.sendAlert(AlertType.BRUTE_FORCE, 
                                 "IP " + ip + " 疑似暴力破解攻击");
        });
    }
    
    private void detectSqlInjectionAttempts() {
        // 检测 SQL 注入尝试
        List<String> suspiciousRequests = logAnalysisService.findSqlInjectionPatterns();
        
        if (!suspiciousRequests.isEmpty()) {
            alertService.sendAlert(AlertType.SQL_INJECTION, 
                                 "检测到 SQL 注入尝试：" + suspiciousRequests.size() + " 次");
        }
    }
}
```

## 事件响应

### 1. 安全事件分类

| 事件类型 | 严重级别 | 响应时间 | 处理措施 |
|----------|----------|----------|----------|
| 数据泄露 | 严重 | 立即 | 停止服务、通知用户、法律报告 |
| 系统入侵 | 严重 | 15分钟 | 隔离系统、取证分析 |
| DDoS 攻击 | 高 | 5分钟 | 启动防护、流量清洗 |
| 暴力破解 | 中 | 30分钟 | 封禁 IP、强化监控 |
| 异常访问 | 低 | 2小时 | 日志分析、用户通知 |

### 2. 应急响应流程

#### 2.1 事件响应自动化
```java
@Component
public class IncidentResponseService {
    
    @EventListener
    @Async
    public void handleSecurityIncident(SecurityIncidentEvent event) {
        IncidentResponse response = IncidentResponse.builder()
                .incidentId(UUID.randomUUID().toString())
                .type(event.getType())
                .severity(event.getSeverity())
                .timestamp(LocalDateTime.now())
                .status(IncidentStatus.OPEN)
                .build();
        
        // 保存事件记录
        incidentRepository.save(response);
        
        // 根据严重级别执行响应措施
        switch (event.getSeverity()) {
            case CRITICAL:
                handleCriticalIncident(event);
                break;
            case HIGH:
                handleHighIncident(event);
                break;
            case MEDIUM:
                handleMediumIncident(event);
                break;
            case LOW:
                handleLowIncident(event);
                break;
        }
    }
    
    private void handleCriticalIncident(SecurityIncidentEvent event) {
        // 严重事件处理
        alertService.sendUrgentAlert(event.getDescription());
        
        if (event.getType() == IncidentType.DATA_BREACH) {
            // 数据泄露应急处理
            systemService.enableMaintenanceMode();
            notificationService.notifyAllUsers("系统正在进行安全维护");
            complianceService.reportDataBreach(event);
        }
    }
}
```

#### 2.2 灾难恢复
```java
@Component
public class DisasterRecoveryService {
    
    public void executeRecoveryPlan(RecoveryType type) {
        switch (type) {
            case DATABASE_CORRUPTION:
                recoverFromDatabaseBackup();
                break;
            case SYSTEM_COMPROMISE:
                recoverFromSystemImage();
                break;
            case SERVICE_UNAVAILABLE:
                switchToBackupService();
                break;
        }
    }
    
    private void recoverFromDatabaseBackup() {
        // 1. 停止应用服务
        applicationService.stop();
        
        // 2. 恢复数据库
        DatabaseBackup latestBackup = backupService.getLatestBackup();
        databaseService.restoreFromBackup(latestBackup);
        
        // 3. 验证数据完整性
        boolean isValid = databaseService.validateIntegrity();
        
        if (isValid) {
            // 4. 重启应用服务
            applicationService.start();
            alertService.sendAlert(AlertType.RECOVERY_SUCCESS, "数据库恢复成功");
        } else {
            alertService.sendAlert(AlertType.RECOVERY_FAILED, "数据库恢复失败");
        }
    }
}
```

## 合规性

### 1. 数据保护法规

#### 1.1 GDPR 合规
```java
@Service
public class GdprComplianceService {
    
    // 数据主体权利：查看个人数据
    public PersonalDataExport exportUserData(Long userId) {
        User user = userService.findById(userId);
        List<Order> orders = orderService.findByUserId(userId);
        List<Passenger> passengers = passengerService.findByUserId(userId);
        
        return PersonalDataExport.builder()
                .personalInfo(user.toExportFormat())
                .orderHistory(orders.stream().map(Order::toExportFormat).collect(toList()))
                .passengerInfo(passengers.stream().map(Passenger::toExportFormat).collect(toList()))
                .exportDate(LocalDateTime.now())
                .build();
    }
    
    // 数据主体权利：删除个人数据
    @Transactional
    public void deleteUserData(Long userId, DataDeletionRequest request) {
        if (request.isDeleteAll()) {
            // 完全删除用户数据
            passengerService.deleteByUserId(userId);
            orderService.anonymizeOrdersByUserId(userId);
            userService.deleteUser(userId);
        } else {
            // 部分删除
            if (request.isDeletePassengers()) {
                passengerService.deleteByUserId(userId);
            }
            if (request.isDeleteOrders()) {
                orderService.anonymizeOrdersByUserId(userId);
            }
        }
        
        // 记录删除操作
        auditLogService.logDataDeletion(userId, request);
    }
}
```

#### 1.2 PCI DSS 合规
```java
// 支付数据处理
@Service
public class PaymentService {
    
    // 不存储敏感的支付信息
    public PaymentResult processPayment(PaymentRequest request) {
        // 使用第三方支付网关，不存储信用卡信息
        PaymentGatewayRequest gatewayRequest = PaymentGatewayRequest.builder()
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .merchantId(paymentConfig.getMerchantId())
                .orderId(request.getOrderId())
                // 不包含敏感的卡片信息
                .build();
        
        return paymentGateway.processPayment(gatewayRequest);
    }
    
    // 支付信息脱敏存储
    @Transactional
    public void savePaymentRecord(PaymentResult result) {
        Payment payment = Payment.builder()
                .orderId(result.getOrderId())
                .amount(result.getAmount())
                .currency(result.getCurrency())
                .paymentMethod(result.getPaymentMethod())
                .transactionId(result.getTransactionId())
                // 只存储脱敏后的卡片信息
                .maskedCardNumber(maskCardNumber(result.getCardNumber()))
                .status(result.getStatus())
                .build();
        
        paymentRepository.save(payment);
    }
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) {
            return "****";
        }
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }
}
```

### 2. 审计要求

#### 2.1 操作审计
```java
@Entity
@Table(name = "audit_logs_yb")
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "action")
    private String action;
    
    @Column(name = "resource")
    private String resource;
    
    @Column(name = "resource_id")
    private String resourceId;
    
    @Column(name = "old_value", columnDefinition = "JSON")
    private String oldValue;
    
    @Column(name = "new_value", columnDefinition = "JSON")
    private String newValue;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
```

## 定期安全检查

### 1. 自动化安全扫描

#### 1.1 依赖漏洞扫描
```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>8.3.1</version>
    <configuration>
        <failBuildOnCVSS>7</failBuildOnCVSS>
        <suppressionFiles>
            <suppressionFile>dependency-check-suppressions.xml</suppressionFile>
        </suppressionFiles>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### 1.2 代码安全扫描
```yaml
# .github/workflows/security-scan.yml
name: Security Scan

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  security:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Run Snyk to check for vulnerabilities
      uses: snyk/actions/maven@master
      env:
        SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}
      with:
        args: --severity-threshold=high
    
    - name: SonarCloud Scan
      uses: SonarSource/sonarcloud-github-action@master
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
```

### 2. 渗透测试

#### 2.1 自动化测试
```bash
#!/bin/bash
# security-test.sh

echo "开始安全测试..."

# 1. 端口扫描
nmap -sS -sV localhost

# 2. Web 应用扫描
nikto -h http://localhost:8080

# 3. SQL 注入测试
sqlmap -u "http://localhost:8080/api/flights/search" --data="departureCode=PEK" --batch

# 4. XSS 测试
python3 xss-scanner.py --url http://localhost:4200

echo "安全测试完成"
```

---

本安全指南将持续更新，确保系统安全性与时俱进。如发现安全问题，请立即联系安全团队。