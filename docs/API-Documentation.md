# API 文档

## 概述

在线机票预订系统 API 文档，基于 RESTful 架构设计。

- **Base URL**: `http://localhost:8080/api`
- **API 版本**: v1.0
- **认证方式**: JWT Bearer Token
- **响应格式**: JSON

## 通用响应格式

所有 API 响应都遵循统一的格式：

```json
{
  "success": true|false,
  "message": "操作结果描述",
  "data": {}, // 响应数据
  "error": "错误信息" // 仅在失败时存在
}
```

## 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 201 | 创建成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 认证接口

### 1. 用户注册

**POST** `/auth/register`

**请求体**:
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "fullName": "string",
  "phone": "string"
}
```

**响应**:
```json
{
  "success": true,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "fullName": "测试用户",
    "role": "USER",
    "status": "ACTIVE"
  }
}
```

### 2. 用户登录

**POST** `/auth/login`

**请求体**:
```json
{
  "usernameOrEmail": "string",
  "password": "string"
}
```

**响应**:
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000
  }
}
```

### 3. 刷新令牌

**POST** `/auth/refresh`

**请求参数**:
- `refreshToken`: string (query parameter)

### 4. 退出登录

**POST** `/auth/logout`

**请求头**:
- `Authorization`: Bearer {token}

## 用户管理接口

### 1. 获取当前用户信息

**GET** `/users/me`

**请求头**:
- `Authorization`: Bearer {token}

**响应**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "fullName": "测试用户",
    "phone": "13800138000",
    "role": "USER",
    "status": "ACTIVE",
    "createdAt": "2024-01-01T10:00:00",
    "lastLogin": "2024-01-01T10:00:00"
  }
}
```

### 2. 获取用户列表 (管理员)

**GET** `/users`

**请求参数**:
- `page`: int (default: 0)
- `size`: int (default: 10)
- `sortBy`: string (default: "createdAt")
- `sortDir`: string (default: "desc")

**权限**: ADMIN

### 3. 更新用户信息

**PUT** `/users/{id}`

**权限**: 用户本人或管理员

## 航班管理接口

### 1. 搜索航班

**POST** `/flights/search`

**请求体**:
```json
{
  "departureAirportCode": "PEK",
  "arrivalAirportCode": "SHA",
  "departureDate": "2024-12-25",
  "passengers": 1,
  "seatClass": "ECONOMY"
}
```

**响应**:
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "flightNumber": "CA1234",
        "airline": {
          "code": "CA",
          "name": "中国国际航空"
        },
        "departureAirport": {
          "code": "PEK",
          "name": "北京首都国际机场",
          "city": "北京"
        },
        "arrivalAirport": {
          "code": "SHA",
          "name": "上海虹桥国际机场",
          "city": "上海"
        },
        "departureTime": "2024-12-25T08:00:00",
        "arrivalTime": "2024-12-25T10:30:00",
        "aircraftType": "A320",
        "availableSeats": 150,
        "economyPrice": 800.00,
        "businessPrice": 2400.00,
        "status": "SCHEDULED"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0
  }
}
```

### 2. 获取航班详情

**GET** `/flights/{id}`

### 3. 创建航班 (管理员)

**POST** `/flights`

**权限**: ADMIN

### 4. 更新航班信息 (管理员)

**PUT** `/flights/{id}`

**权限**: ADMIN

## 订单管理接口

### 1. 创建订单

**POST** `/orders`

**请求体**:
```json
{
  "contactName": "张三",
  "contactPhone": "13800138000",
  "contactEmail": "zhangsan@example.com",
  "notes": "靠窗座位",
  "orderItems": [
    {
      "flightId": 1,
      "passengerId": 1,
      "seatClass": "ECONOMY"
    }
  ]
}
```

**权限**: USER, ADMIN

### 2. 获取我的订单

**GET** `/orders/my`

**请求参数**:
- `page`: int
- `size`: int

**权限**: USER, ADMIN

### 3. 获取订单详情

**GET** `/orders/{id}`

**权限**: 订单所有者或管理员

### 4. 支付订单

**POST** `/orders/{id}/pay`

**请求参数**:
- `paymentMethod`: enum (CREDIT_CARD, DEBIT_CARD, ALIPAY, WECHAT, BANK_TRANSFER)

### 5. 取消订单

**PUT** `/orders/{id}/cancel`

## 旅客管理接口

### 1. 创建旅客信息

**POST** `/passengers`

**请求体**:
```json
{
  "firstName": "三",
  "lastName": "张",
  "gender": "M",
  "dateOfBirth": "1990-01-01",
  "nationality": "中国",
  "passportNumber": "E12345678",
  "idCardNumber": "110101199001011234",
  "phone": "13800138000",
  "email": "zhangsan@example.com"
}
```

### 2. 获取我的旅客信息

**GET** `/passengers/my`

### 3. 更新旅客信息

**PUT** `/passengers/{id}`

### 4. 删除旅客信息

**DELETE** `/passengers/{id}`

## 机场接口

### 1. 获取机场列表

**GET** `/airports`

**请求参数**:
- `page`: int
- `size`: int
- `sortBy`: string
- `sortDir`: string

### 2. 搜索机场

**GET** `/airports/search`

**请求参数**:
- `keyword`: string

### 3. 获取国家列表

**GET** `/airports/countries`

### 4. 获取城市列表

**GET** `/airports/cities`

**请求参数**:
- `country`: string

## 航空公司接口

### 1. 获取航空公司列表

**GET** `/airlines`

### 2. 搜索航空公司

**GET** `/airlines/search`

**请求参数**:
- `keyword`: string

## 错误处理

### 常见错误响应

```json
{
  "success": false,
  "message": "用户名或密码错误",
  "error": "INVALID_CREDENTIALS"
}
```

### 验证错误响应

```json
{
  "success": false,
  "message": "输入数据验证失败",
  "error": "{\"username\":\"用户名不能为空\",\"email\":\"邮箱格式不正确\"}"
}
```

## 限流和安全

- 登录接口限制：每分钟最多 5 次尝试
- API 调用限制：每秒最多 100 次请求
- Token 有效期：24 小时
- 刷新 Token 有效期：7 天

## 示例代码

### JavaScript/Axios

```javascript
// 登录
const loginResponse = await axios.post('/api/auth/login', {
  usernameOrEmail: 'admin@airline.com',
  password: 'admin123'
});

const token = loginResponse.data.data.accessToken;

// 搜索航班
const flightsResponse = await axios.post('/api/flights/search', {
  departureAirportCode: 'PEK',
  arrivalAirportCode: 'SHA',
  departureDate: '2024-12-25',
  passengers: 1
}, {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
```

### cURL

```bash
# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin@airline.com","password":"admin123"}'

# 搜索航班
curl -X POST http://localhost:8080/api/flights/search \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"departureAirportCode":"PEK","arrivalAirportCode":"SHA","departureDate":"2024-12-25","passengers":1}'
```

## 更新日志

### v1.0.0 (2024-08-13)
- 初始版本发布
- 完整的用户认证系统
- 航班搜索和预订功能
- 订单管理功能
- 旅客信息管理

---

更多详细信息请访问 Swagger 文档：http://localhost:8080/swagger-ui.html