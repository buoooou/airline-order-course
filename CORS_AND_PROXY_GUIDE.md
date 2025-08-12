# 航空订单系统 - 跨域和代理配置详解

## 🌐 跨域问题解析

### 什么是跨域问题？
当前端应用（Angular）和后端API（Spring Boot）部署在不同的域名、端口或协议时，浏览器会阻止跨域请求，这就是CORS（Cross-Origin Resource Sharing）问题。

### 我们的部署场景
```
开发环境:
前端: http://localhost:4200
后端: http://localhost:8080
❌ 跨域问题存在

生产环境:
前端: http://your-ec2-ip (通过Nginx)
后端: http://your-ec2-ip:8080
❌ 仍然存在跨域问题
```

## 🔧 解决方案

### 方案1: Nginx反向代理（推荐）
使用Nginx作为反向代理，统一入口，避免跨域问题。

### 方案2: Spring Boot CORS配置
在后端配置CORS允许跨域请求。

### 方案3: Angular代理配置
在开发环境使用Angular的代理功能。

## 📝 具体配置

### 1. Nginx配置（生产环境）

#### nginx.conf
```nginx
server {
    listen 80;
    server_name your-domain.com;  # 或者使用EC2公网IP
    
    # 前端静态文件
    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
        
        # 添加安全头
        add_header X-Frame-Options "SAMEORIGIN" always;
        add_header X-Content-Type-Options "nosniff" always;
        add_header X-XSS-Protection "1; mode=block" always;
    }
    
    # API代理到后端
    location /api/ {
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # CORS头设置
        add_header Access-Control-Allow-Origin *;
        add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS";
        add_header Access-Control-Allow-Headers "DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization";
        
        # 处理预检请求
        if ($request_method = 'OPTIONS') {
            add_header Access-Control-Allow-Origin *;
            add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS";
            add_header Access-Control-Allow-Headers "DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization";
            add_header Access-Control-Max-Age 1728000;
            add_header Content-Type 'text/plain; charset=utf-8';
            add_header Content-Length 0;
            return 204;
        }
    }
    
    # Swagger UI代理
    location /swagger-ui/ {
        proxy_pass http://localhost:8080/swagger-ui/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    
    # API文档代理
    location /api-docs/ {
        proxy_pass http://localhost:8080/api-docs/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 2. Spring Boot CORS配置

#### WebConfig.java
```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // 生产环境应该指定具体域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

#### SecurityConfig.java（如果使用Spring Security）
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().configurationSource(corsConfigurationSource())
            .and()
            .csrf().disable()
            // 其他安全配置...
            ;
        return http.build();
    }
}
```

### 3. Angular代理配置（开发环境）

#### proxy.conf.json
```json
{
  "/api/*": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "pathRewrite": {
      "^/api": ""
    }
  },
  "/swagger-ui/*": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug"
  },
  "/api-docs/*": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug"
  }
}
```

#### angular.json配置
```json
{
  "serve": {
    "builder": "@angular-devkit/build-angular:dev-server",
    "options": {
      "proxyConfig": "proxy.conf.json"
    }
  }
}
```

#### package.json启动脚本
```json
{
  "scripts": {
    "start": "ng serve --proxy-config proxy.conf.json",
    "start:prod": "ng serve --configuration production --proxy-config proxy.conf.json"
  }
}
```

### 4. 环境配置文件

#### environment.ts（开发环境）
```typescript
export const environment = {
  production: false,
  apiUrl: '/api'  // 使用代理路径
};
```

#### environment.prod.ts（生产环境）
```typescript
export const environment = {
  production: true,
  apiUrl: '/api'  // 通过Nginx代理
};
```

## 🚀 部署配置

### Docker Compose配置
```yaml
version: '3.8'
services:
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/conf.d/default.conf
      - ./frontend/dist:/usr/share/nginx/html
    depends_on:
      - backend
    networks:
      - airline-network

  backend:
    image: ${ECR_URI}:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=${DB_HOST}
      - DB_NAME=airline_order_db
      - DB_USERNAME=admin
      - DB_PASSWORD=${DB_PASSWORD}
    networks:
      - airline-network

networks:
  airline-network:
    driver: bridge
```

## 🔍 测试跨域配置

### 1. 浏览器开发者工具检查
```javascript
// 在浏览器控制台测试API调用
fetch('/api/flights')
  .then(response => response.json())
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));
```

### 2. curl命令测试
```bash
# 测试预检请求
curl -X OPTIONS \
  -H "Origin: http://your-domain.com" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: Content-Type" \
  http://your-ec2-ip/api/flights

# 测试实际请求
curl -X GET \
  -H "Origin: http://your-domain.com" \
  http://your-ec2-ip/api/flights
```

## ⚠️ 常见问题和解决方案

### 问题1: 预检请求失败
```
Access to fetch at 'http://localhost:8080/api/flights' from origin 'http://localhost:4200' has been blocked by CORS policy
```

**解决方案:**
- 确保后端正确处理OPTIONS请求
- 检查CORS配置是否包含所有必要的头信息

### 问题2: 认证头被阻止
```
Request header field authorization is not allowed by Access-Control-Allow-Headers
```

**解决方案:**
- 在CORS配置中添加Authorization头
- 确保预检请求返回正确的允许头列表

### 问题3: Cookie无法发送
```
Credentials flag is 'true', but the 'Access-Control-Allow-Origin' header is '*'
```

**解决方案:**
- 使用具体的域名而不是通配符*
- 设置allowCredentials为true

## 🛡️ 安全最佳实践

### 1. 生产环境CORS配置
```java
// 不要在生产环境使用通配符
.allowedOrigins("https://your-domain.com", "https://www.your-domain.com")
.allowedOriginPatterns("https://*.your-domain.com")
```

### 2. Nginx安全头
```nginx
# 安全头配置
add_header X-Frame-Options "SAMEORIGIN" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-XSS-Protection "1; mode=block" always;
add_header Referrer-Policy "strict-origin-when-cross-origin" always;
add_header Content-Security-Policy "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline';" always;
```

### 3. HTTPS配置
```nginx
# 强制HTTPS
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;
    
    ssl_certificate /path/to/certificate.crt;
    ssl_certificate_key /path/to/private.key;
    
    # SSL配置...
}
```

## 📊 监控和调试

### 1. Nginx访问日志
```nginx
log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                '$status $body_bytes_sent "$http_referer" '
                '"$http_user_agent" "$http_x_forwarded_for"';

access_log /var/log/nginx/access.log main;
error_log /var/log/nginx/error.log warn;
```

### 2. Spring Boot日志配置
```properties
# application-prod.properties
logging.level.org.springframework.web.cors=DEBUG
logging.level.org.springframework.security=DEBUG
```

这个配置指南涵盖了跨域问题的所有方面，从开发到生产环境的完整解决方案！
