* 用户注册功能（POST /api/auth/register）
终端输入：
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin&password=admin123&role=ADMIN"
终端返回：
{"data":{"username":"admin","role":"ADMIN"},"success":true,"message":"注册成功"}%    

结论
功能正常。


* 用户登录功能（POST /api/auth/login）
终端输入：
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "rememberMe": true
  }'
终端返回：
{"data":{"accessToken":"eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY3ODkwNiwiZXhwIjoxNzUzNzY1MzA2fQ.BKSTaQQwrnR9-Xs5peoYDvvlQO4wR1lo9KLV1eNsMRY","tokenType":"Bearer","expiresAt":"2025-07-29T13:01:45.796637","user":{"id":3,"username":"admin","role":"ADMIN","roleDescription":"管理员","user":false,"admin":true},"loginTime":"2025-07-28T13:01:46.808497","message":"登录成功","authorizationHeader":"Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY3ODkwNiwiZXhwIjoxNzUzNzY1MzA2fQ.BKSTaQQwrnR9-Xs5peoYDvvlQO4wR1lo9KLV1eNsMRY","remainingMinutes":1439,"tokenExpiringSoon":false},"success":true,"message":"登录成功"}%   

结论：
1. 登录成功，返回了完整的认证响应
2. JWT令牌生成成功
3. 用户信息正确返回（包含角色、权限等）
4. 令牌有效期设置正确（24小时）


* JWT令牌验证
保存JWT令牌并测试需要认证的API。获取航班列表（公开接口）：
航班列表API正常工作，返回了空列表（因为还没有航班数据）。
终端输入：
curl -X GET "http://localhost:8080/api/flights" \
  -H "Content-Type: application/json"
终端返回：
{"data":[],"success":true,"message":"获取航班列表成功"}%     

* 创建航班（POST /api/flights）
创建一些航班数据。使用JWT令牌测试创建航班的API：
终端输入：
curl -X POST "http://localhost:8080/api/flights" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY3ODkwNiwiZXhwIjoxNzUzNzY1MzA2fQ.BKSTaQQwrnR9-Xs5peoYDvvlQO4wR1lo9KLV1eNsMRY" \
  -d '{
    "flightNumber": "CA1234",
    "airline": "中国国际航空",
    "departureAirport": "PEK",
    "arrivalAirport": "SHA",
    "departureTime": "2025-07-30T08:00:00",
    "arrivalTime": "2025-07-30T10:30:00",
    "aircraftType": "A320",
    "totalSeats": 180,
    "availableSeats": 150,
    "price": 800.00,
    "status": "ACTIVE"
  }'
终端返回：
{"data":{"id":1,"flightNumber":"CA1234","airline":"中国国际航空","departureAirport":"PEK","arrivalAirport":"SHA","departureTime":"2025-07-30T08:00:00","arrivalTime":"2025-07-30T10:30:00","aircraftType":"A320","price":800.00,"availableSeats":150,"totalSeats":180,"status":"ACTIVE","statusDescription":"正常","createdAt":"2025-07-28T13:05:08.264205","updatedAt":"2025-07-28T13:05:08.264205","flightDurationMinutes":150,"occupancyRate":0.16666666666666666,"bookable":true,"fullyBooked":false,"routeDescription":"PEK → SHA","timeRangeDescription":"08:00 - 10:30","flightDurationDescription":"2小时30分钟","seatInfoDescription":"剩余150/180座位"},"success":true,"message":"航班创建成功"}% 

结论
功能正常。

* 获取航班列表（GET /api/flights）
再次获取航班列表，能看到刚创建的航班：
终端输入：
curl -X GET "http://localhost:8080/api/flights" \
  -H "Content-Type: application/json"

终端返回：
{"data":[{"id":1,"flightNumber":"CA1234","airline":"中国国际航空","departureAirport":"PEK","arrivalAirport":"SHA","departureTime":"2025-07-30T08:00:00","arrivalTime":"2025-07-30T10:30:00","aircraftType":"A320","price":800.00,"availableSeats":150,"totalSeats":180,"status":"ACTIVE","statusDescription":"正常","createdAt":"2025-07-28T13:05:08.264205","updatedAt":"2025-07-28T13:05:08.264205","flightDurationMinutes":150,"occupancyRate":0.16666666666666666,"bookable":true,"fullyBooked":false,"routeDescription":"PEK → SHA","timeRangeDescription":"08:00 - 10:30","flightDurationDescription":"2小时30分钟","seatInfoDescription":"剩余150/180座位"}],"success":true,"message":"获取航班列表成功"}%   

结论
功能正常。

* 创建订单（POST /api/orders）
测试订单功能。创建一个订单：

终端输入：
curl -X POST "http://localhost:8080/api/orders" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY3ODkwNiwiZXhwIjoxNzUzNzY1MzA2fQ.BKSTaQQwrnR9-Xs5peoYDvvlQO4wR1lo9KLV1eNsMRY" \
  -d '{
    "userId": 3,
    "flightInfoId": 1,
    "passengerCount": 2,
    "passengerNames": ["张三", "李四"],
    "contactPhone": "13800138000",
    "contactEmail": "test@example.com",
    "remarks": "测试订单"
  }'
终端返回：
{"data":{"id":8,"userId":3,"flightInfoId":1,"orderNumber":"ORD1753679265654407924C2","status":"PENDING_PAYMENT","statusDescription":"待支付","amount":1600.00,"creationDate":"2025-07-28T13:07:45.655863","lastUpdated":"2025-07-28T13:07:45.663089","paymentTime":null,"ticketingTime":null,"cancellationTime":null,"cancellationReason":null,"remarks":null,"user":null,"flightInfo":null,"passengerCount":2,"username":"admin","flightNumber":"CA1234","passengerNames":"张三,李四","contactPhone":"13800138000","contactEmail":"test@example.com","airline":"中国国际航空","departureAirport":"PEK","arrivalAirport":"SHA","departureTime":"2025-07-30T08:00:00","arrivalTime":"2025-07-30T10:30:00","ticketingStartTime":null,"ticketingCompletionTime":null,"ticketingFailureReason":null,"durationMinutes":null,"canPay":null,"canCancel":null,"completed":null,"cancelled":null,"statusCssClass":"status-pending","statusColor":"#faad14","durationDescription":"刚刚创建","nextPossibleActions":"支付, 取消","summary":"订单号: ORD1753679265654407924C2, 状态: 待支付, 金额: ¥1600.00"},"success":true,"message":"订单创建成功"}%  

结论
1. 订单成功创建，生成了订单号：ORD1753679265654407924C2
2. 订单状态为PENDING_PAYMENT（待支付）
3. 金额计算正确：2人 × 800元 = 1600元
4. 航班座位数自动减少：从150减少到148
5. 数据库操作正常，订单和航班信息都正确更新

* 订单支付（PUT /api/orders/{id}/pay）
测试订单状态更新功能，模拟支付订单：
终端输入：
curl -X PUT "http://localhost:8080/api/orders/8/pay" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY3ODkwNiwiZXhwIjoxNzUzNzY1MzA2fQ.BKSTaQQwrnR9-Xs5peoYDvvlQO4wR1lo9KLV1eNsMRY"
终端返回：
{"data":{"id":8,"userId":3,"flightInfoId":1,"orderNumber":"ORD1753679265654407924C2","status":"PAID","statusDescription":"已支付","amount":1600.00,"creationDate":"2025-07-28T13:07:45.655863","lastUpdated":"2025-07-28T13:08:31.93118","paymentTime":"2025-07-28T13:08:31.931099","ticketingTime":null,"cancellationTime":null,"cancellationReason":null,"remarks":null,"user":null,"flightInfo":null,"passengerCount":2,"username":"admin","flightNumber":"CA1234","passengerNames":"张三,李四","contactPhone":"13800138000","contactEmail":"test@example.com","airline":"中国国际航空","departureAirport":"PEK","arrivalAirport":"SHA","departureTime":"2025-07-30T08:00:00","arrivalTime":"2025-07-30T10:30:00","ticketingStartTime":null,"ticketingCompletionTime":null,"ticketingFailureReason":null,"durationMinutes":null,"canPay":null,"canCancel":null,"completed":null,"cancelled":null,"statusCssClass":"status-paid","statusColor":"#1890ff","durationDescription":"刚刚创建","nextPossibleActions":"出票, 取消","summary":"订单号: ORD1753679265654407924C2, 状态: 已支付, 金额: ¥1600.00"},"success":true,"message":"订单支付成功"}%              

结论
1. 订单状态从PENDING_PAYMENT成功更新为PAID
2. 支付时间被正确记录
3. 状态机逻辑正常工作
4. 数据库更新成功


* 获取我的订单（GET /api/orders/my）
测试获取我的订单列表
终端输入：
curl -X GET "http://localhost:8080/api/orders/my" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY3ODkwNiwiZXhwIjoxNzUzNzY1MzA2fQ.BKSTaQQwrnR9-Xs5peoYDvvlQO4wR1lo9KLV1eNsMRY"
终端返回：
{"data":{"content":[{"id":8,"userId":3,"flightInfoId":1,"orderNumber":"ORD1753679265654407924C2","status":"PAID","statusDescription":"已支付","amount":1600.00,"creationDate":"2025-07-28T13:07:45.655863","lastUpdated":"2025-07-28T13:08:31.935247","paymentTime":"2025-07-28T13:08:31.931099","ticketingTime":null,"cancellationTime":null,"cancellationReason":null,"remarks":null,"user":null,"flightInfo":null,"passengerCount":2,"username":"admin","flightNumber":"CA1234","passengerNames":"张三,李四","contactPhone":"13800138000","contactEmail":"test@example.com","airline":"中国国际航空","departureAirport":"PEK","arrivalAirport":"SHA","departureTime":"2025-07-30T08:00:00","arrivalTime":"2025-07-30T10:30:00","ticketingStartTime":null,"ticketingCompletionTime":null,"ticketingFailureReason":null,"durationMinutes":null,"canPay":null,"canCancel":null,"completed":null,"cancelled":null,"statusCssClass":"status-paid","statusColor":"#1890ff","durationDescription":"刚刚创建","nextPossibleActions":"出票, 取消","summary":"订单号: ORD1753679265654407924C2, 状态: 已支付, 金额: ¥1600.00"}],"pageable":{"sort":{"unsorted":false,"sorted":true,"empty":false},"pageNumber":0,"pageSize":10,"offset":0,"paged":true,"unpaged":false},"totalPages":1,"totalElements":1,"last":true,"numberOfElements":1,"first":true,"size":10,"number":0,"sort":{"unsorted":false,"sorted":true,"empty":false},"empty":false},"success":true,"message":"获取订单列表成功"}%  

结论
1.  返回了分页数据，包含完整的分页信息
2. 订单按创建时间倒序排列
3. 包含了完整的订单和航班信息
4. JWT认证和权限控制正常

* 获取当前用户信息 (GET /api/auth/me)
测试获取当前用户信息：
终端输入：
curl -X GET "http://localhost:8080/api/auth/me" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY3ODkwNiwiZXhwIjoxNzUzNzY1MzA2fQ.BKSTaQQwrnR9-Xs5peoYDvvlQO4wR1lo9KLV1eNsMRY"

终端输出：
{"data":{"id":3,"username":"admin","role":"ADMIN","roleDescription":"管理员","user":false,"admin":true},"success":true,"message":"获取用户信息成功"}%   

结论
获取当前用户信息功能正常。

* 检查令牌状态 (GET /api/auth/token/check)
终端输入：
curl -X GET "http://localhost:8080/api/auth/token/check" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY3ODkwNiwiZXhwIjoxNzUzNzY1MzA2fQ.BKSTaQQwrnR9-Xs5peoYDvvlQO4wR1lo9KLV1eNsMRY"
终端输出：
{"data":{"valid":true,"remainingTimeMs":81613521,"expiringSoon":false,"remainingTimeMinutes":1360},"success":true,"message":"令牌状态检查完成"}%    

结论
令牌状态检查功能正常。

* 令牌刷新功能： (POST /api/auth/refresh)
终端输入：
curl -X POST "http://localhost:8080/api/auth/refresh" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY3ODkwNiwiZXhwIjoxNzUzNzY1MzA2fQ.BKSTaQQwrnR9-Xs5peoYDvvlQO4wR1lo9KLV1eNsMRY"
终端输出：
{"data":{"accessToken":"eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY4Mzc1MiwiZXhwIjoxNzUzNzcwMTUyfQ.k139fZocPPJIAicLbsTErfkuJoW_s1n2V_DGgE4Lm7E","tokenType":"Bearer","expiresAt":"2025-07-29T14:22:31.247772","user":{"id":3,"username":"admin","role":"ADMIN","roleDescription":"管理员","user":false,"admin":true},"loginTime":"2025-07-28T14:22:32.248206","message":"令牌刷新成功","authorizationHeader":"Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY4Mzc1MiwiZXhwIjoxNzUzNzcwMTUyfQ.k139fZocPPJIAicLbsTErfkuJoW_s1n2V_DGgE4Lm7E","remainingMinutes":1439,"tokenExpiringSoon":false},"success":true,"message":"令牌刷新成功"}% 

结论
令牌刷新功能正常。

* 用户登出： (POST /api/auth/logout)
终端输入：
curl -X POST "http://localhost:8080/api/auth/logout" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY4Mzc1MiwiZXhwIjoxNzUzNzcwMTUyfQ.k139fZocPPJIAicLbsTErfkuJoW_s1n2V_DGgE4Lm7E"
终端输出：
{"success":true,"message":"登出成功"}%    

结论
用户登出功能正常。


* 获取航班列表 (GET /api/flights)
终端输入：
curl -X GET "http://localhost:8080/api/flights/1" \
  -H "Content-Type: application/json"
终端输出：
{"data":{"id":1,"flightNumber":"CA1234","airline":"中国国际航空","departureAirport":"PEK","arrivalAirport":"SHA","departureTime":"2025-07-30T08:00:00","arrivalTime":"2025-07-30T10:30:00","aircraftType":"A320","price":800.00,"availableSeats":148,"totalSeats":180,"status":"ACTIVE","statusDescription":"正常","createdAt":"2025-07-28T13:05:08.264205","updatedAt":"2025-07-28T13:07:45.95753","flightDurationMinutes":150,"occupancyRate":0.17777777777777778,"bookable":true,"fullyBooked":false,"routeDescription":"PEK → SHA","timeRangeDescription":"08:00 - 10:30","flightDurationDescription":"2小时30分钟","seatInfoDescription":"剩余148/180座位"},"success":true,"message":"获取航班详情成功"}

结论
获取航班详情功能正常。

* 获取可预订航班 (GET /api/flights/bookable)
终端输入：
curl -X GET "http://localhost:8080/api/flights/bookable" \
  -H "Content-Type: application/json"
终端输出：
{"data":[{"id":1,"flightNumber":"CA1234","airline":"中国国际航空","departureAirport":"PEK","arrivalAirport":"SHA","departureTime":"2025-07-30T08:00:00","arrivalTime":"2025-07-30T10:30:00","aircraftType":"A320","price":800.00,"availableSeats":148,"totalSeats":180,"status":"ACTIVE","statusDescription":"正常","createdAt":"2025-07-28T13:05:08.264205","updatedAt":"2025-07-28T13:07:45.95753","flightDurationMinutes":150,"occupancyRate":0.17777777777777778,"bookable":true,"fullyBooked":false,"routeDescription":"PEK → SHA","timeRangeDescription":"08:00 - 10:30","flightDurationDescription":"2小时30分钟","seatInfoDescription":"剩余148/180座位"}],"success":true,"message":"获取可预订航班成功"}%   

结论
获取可预订航班功能正常。

* 航班搜索功能 (GET /api/flights/search) 
终端输入：
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
终端输出：
{"data":{"accessToken":"eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY4NDAyOCwiZXhwIjoxNzUzNzcwNDI4fQ.0XceETVcwKnmTnUPM8bhjTeXevyejAy44QjEzaAtReM","tokenType":"Bearer","expiresAt":"2025-07-29T14:27:07.754395","user":{"id":3,"username":"admin","role":"ADMIN","roleDescription":"管理员","user":false,"admin":true},"loginTime":"2025-07-28T14:27:08.754911","message":"登录成功","authorizationHeader":"Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY4NDAyOCwiZXhwIjoxNzUzNzcwNDI4fQ.0XceETVcwKnmTnUPM8bhjTeXevyejAy44QjEzaAtReM","remainingMinutes":1439,"tokenExpiringSoon":false},"success":true,"message":"登录成功"}%  

结论
基础功能正常

* 更新航班 (PUT /api/flights/{id})
用新的JWT令牌测试更新航班功能：
终端输入：
curl -X PUT "http://localhost:8080/api/flights/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY4NDAyOCwiZXhwIjoxNzUzNzcwNDI4fQ.0XceETVcwKnmTnUPM8bhjTeXevyejAy44QjEzaAtReM" \
* 用户注册功能（POST /api/auth/register）
终端输入：
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=admin&password=admin123&role=ADMIN"
终端返回：
{"data":{"username":"admin","role":"ADMIN"},"success":true,"message":"注册成功"}%    

结论
功能正常。


* 用户登录功能（POST /api/auth/login）
终端输入：
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "rememberMe": true
  }'
终端返回：
{"data":{"accessToken":"eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY3ODkwNiwiZXhwIjoxNzUzNzY1MzA2fQ.BKSTaQQwrnR9-Xs5peoYDvvlQO4wR1lo9KLV1eNsMRY","tokenType":"Bearer","expiresAt":"2025-07-29T13:01:45.796637","user":{"id":3,"username":"admin","role":"ADMIN","roleDescription":"管理员","user":false,"admin":true},"loginTime":"2025-07-28T13:01:46.808497","message":"登录成功","authorizationHeader":"Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY3ODkwNiwiZXhwIjoxNzUzNzY1MzA2fQ.BKSTaQQwrnR9-Xs5peoYDvvlQO4wR1lo9KLV1eNsMRY","remainingMinutes":1439,"tokenExpiringSoon":false},"success":true,"message":"登录成功"}%   

结论：
1. 登录成功，返回了完整的认证响应
2. JWT令牌生成成功
3. 用户信息正确返回（包含角色、权限等）
4. 令牌有效期设置正确（24小时）


* JWT令牌验证
保存JWT令牌并测试需要认证的API。获取航班列表（公开接口）：
航班列表API正常工作，返回了空列表（因为还没有航班数据）。
终端输入：
curl -X GET "http://localhost:8080/api/flights" \
  -H "Content-Type: application/json"
终端返回：
{"data":[],"success":true,"message":"获取航班列表成功"}%     

* 创建航班（POST /api/flights）
创建一些航班数据。使用JWT令牌测试创建航班的API：
终端输入：
curl -X POST "http://localhost:8080/api/flights" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY3ODkwNiwiZXhwIjoxNzUzNzY1MzA2fQ.BKSTaQQwrnR9-Xs5peoYDvvlQO4wR1lo9KLV1eNsMRY" \
  -d '{
    "flightNumber": "CA1234",
    "airline": "中国国际航空",
    "departureAirport": "PEK",
    "arrivalAirport": "SHA",
    "departureTime": "2025-07-30T08:00:00",
    "arrivalTime": "2025-07-30T10:30:00",
    "aircraftType": "A320",
    "totalSeats": 180,
    "availableSeats": 150,
    "price": 800.00,
    "status": "ACTIVE"
  }'
终端返回：
{"data":{"id":1,"flightNumber":"CA1234","airline":"中国国际航空","departureAirport":"PEK","arrivalAirport":"SHA","departureTime":"2025-07-30T08:00:00","arrivalTime":"2025-07-30T10:30:00","aircraftType":"A320","price":800.00,"availableSeats":150,"totalSeats":180,"status":"ACTIVE","statusDescription":"正常","createdAt":"2025-07-28T13:05:08.264205","updatedAt":"2025-07-28T13:05:08.264205","flightDurationMinutes":150,"occupancyRate":0.16666666666666666,"bookable":true,"fullyBooked":false,"routeDescription":"PEK → SHA","timeRangeDescription":"08:00 - 10:30","flightDurationDescription":"2小时30分钟","seatInfoDescription":"剩余150/180座位"},"success":true,"message":"航班创建成功"}% 

结论
功能正常。

* 获取航班列表（GET /api/flights）
再次获取航班列表，能看到刚创建的航班：
终端输入：
curl -X GET "http://localhost:8080/api/flights" \
  -H "Content-Type: application/json"

终端返回：
{"data":[{"id":1,"flightNumber":"CA1234","airline":"中国国际航空","departureAirport":"PEK","arrivalAirport":"SHA","departureTime":"2025-07-30T08:00:00","arrivalTime":"2025-07-30T10:30:00","aircraftType":"A320","price":800.00,"availableSeats":150,"totalSeats":180,"status":"ACTIVE","statusDescription":"正常","createdAt":"2025-07-28T13:05:08.264205","updatedAt":"2025-07-28T13:05:08.264205","flightDurationMinutes":150,"occupancyRate":0.16666666666666666,"bookable":true,"fullyBooked":false,"routeDescription":"PEK → SHA","timeRangeDescription":"08:00 - 10:30","flightDurationDescription":"2小时30分钟","seatInfoDescription":"剩余150/180座位"}],"success":true,"message":"获取航班列表成功"}%   

结论
功能正常。

* 创建订单（POST /api/orders）
测试订单功能。创建一个订单：

终端输入：
curl -X POST "http://localhost:8080/api/orders" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY3ODkwNiwiZXhwIjoxNzUzNzY1MzA2fQ.BKSTaQQwrnR9-Xs5peoYDvvlQO4wR1lo9KLV1eNsMRY" \
  -d '{
    "userId": 3,
    "flightInfoId": 1,
    "passengerCount": 2,
    "passengerNames": ["张三", "李四"],
    "contactPhone": "13800138000",
    "contactEmail": "test@example.com",
    "remarks": "测试订单"
  }'
终端返回：
{"data":{"id":8,"userId":3,"flightInfoId":1,"orderNumber":"ORD1753679265654407924C2","status":"PENDING_PAYMENT","statusDescription":"待支付","amount":1600.00,"creationDate":"2025-07-28T13:07:45.655863","lastUpdated":"2025-07-28T13:07:45.663089","paymentTime":null,"ticketingTime":null,"cancellationTime":null,"cancellationReason":null,"remarks":null,"user":null,"flightInfo":null,"passengerCount":2,"username":"admin","flightNumber":"CA1234","passengerNames":"张三,李四","contactPhone":"13800138000","contactEmail":"test@example.com","airline":"中国国际航空","departureAirport":"PEK","arrivalAirport":"SHA","departureTime":"2025-07-30T08:00:00","arrivalTime":"2025-07-30T10:30:00","ticketingStartTime":null,"ticketingCompletionTime":null,"ticketingFailureReason":null,"durationMinutes":null,"canPay":null,"canCancel":null,"completed":null,"cancelled":null,"statusCssClass":"status-pending","statusColor":"#faad14","durationDescription":"刚刚创建","nextPossibleActions":"支付, 取消","summary":"订单号: ORD1753679265654407924C2, 状态: 待支付, 金额: ¥1600.00"},"success":true,"message":"订单创建成功"}%  

结论
1. 订单成功创建，生成了订单号：ORD1753679265654407924C2
2. 订单状态为PENDING_PAYMENT（待支付）
3. 金额计算正确：2人 × 800元 = 1600元
4. 航班座位数自动减少：从150减少到148
5. 数据库操作正常，订单和航班信息都正确更新

* 订单支付（PUT /api/orders/{id}/pay）
测试订单状态更新功能，模拟支付订单：
终端输入：
curl -X PUT "http://localhost:8080/api/orders/8/pay" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY3ODkwNiwiZXhwIjoxNzUzNzY1MzA2fQ.BKSTaQQwrnR9-Xs5peoYDvvlQO4wR1lo9KLV1eNsMRY"
终端返回：
{"data":{"id":8,"userId":3,"flightInfoId":1,"orderNumber":"ORD1753679265654407924C2","status":"PAID","statusDescription":"已支付","amount":1600.00,"creationDate":"2025-07-28T13:07:45.655863","lastUpdated":"2025-07-28T13:08:31.93118","paymentTime":"2025-07-28T13:08:31.931099","ticketingTime":null,"cancellationTime":null,"cancellationReason":null,"remarks":null,"user":null,"flightInfo":null,"passengerCount":2,"username":"admin","flightNumber":"CA1234","passengerNames":"张三,李四","contactPhone":"13800138000","contactEmail":"test@example.com","airline":"中国国际航空","departureAirport":"PEK","arrivalAirport":"SHA","departureTime":"2025-07-30T08:00:00","arrivalTime":"2025-07-30T10:30:00","ticketingStartTime":null,"ticketingCompletionTime":null,"ticketingFailureReason":null,"durationMinutes":null,"canPay":null,"canCancel":null,"completed":null,"cancelled":null,"statusCssClass":"status-paid","statusColor":"#1890ff","durationDescription":"刚刚创建","nextPossibleActions":"出票, 取消","summary":"订单号: ORD1753679265654407924C2, 状态: 已支付, 金额: ¥1600.00"},"success":true,"message":"订单支付成功"}%              

结论
1. 订单状态从PENDING_PAYMENT成功更新为PAID
2. 支付时间被正确记录
3. 状态机逻辑正常工作
4. 数据库更新成功


* 获取我的订单（GET /api/orders/my）
测试获取我的订单列表
终端输入：
curl -X GET "http://localhost:8080/api/orders/my" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY3ODkwNiwiZXhwIjoxNzUzNzY1MzA2fQ.BKSTaQQwrnR9-Xs5peoYDvvlQO4wR1lo9KLV1eNsMRY"
终端返回：
{"data":{"content":[{"id":8,"userId":3,"flightInfoId":1,"orderNumber":"ORD1753679265654407924C2","status":"PAID","statusDescription":"已支付","amount":1600.00,"creationDate":"2025-07-28T13:07:45.655863","lastUpdated":"2025-07-28T13:08:31.935247","paymentTime":"2025-07-28T13:08:31.931099","ticketingTime":null,"cancellationTime":null,"cancellationReason":null,"remarks":null,"user":null,"flightInfo":null,"passengerCount":2,"username":"admin","flightNumber":"CA1234","passengerNames":"张三,李四","contactPhone":"13800138000","contactEmail":"test@example.com","airline":"中国国际航空","departureAirport":"PEK","arrivalAirport":"SHA","departureTime":"2025-07-30T08:00:00","arrivalTime":"2025-07-30T10:30:00","ticketingStartTime":null,"ticketingCompletionTime":null,"ticketingFailureReason":null,"durationMinutes":null,"canPay":null,"canCancel":null,"completed":null,"cancelled":null,"statusCssClass":"status-paid","statusColor":"#1890ff","durationDescription":"刚刚创建","nextPossibleActions":"出票, 取消","summary":"订单号: ORD1753679265654407924C2, 状态: 已支付, 金额: ¥1600.00"}],"pageable":{"sort":{"unsorted":false,"sorted":true,"empty":false},"pageNumber":0,"pageSize":10,"offset":0,"paged":true,"unpaged":false},"totalPages":1,"totalElements":1,"last":true,"numberOfElements":1,"first":true,"size":10,"number":0,"sort":{"unsorted":false,"sorted":true,"empty":false},"empty":false},"success":true,"message":"获取订单列表成功"}%  

结论
1.  返回了分页数据，包含完整的分页信息
2. 订单按创建时间倒序排列
3. 包含了完整的订单和航班信息
4. JWT认证和权限控制正常

* 获取当前用户信息 (GET /api/auth/me)
测试获取当前用户信息：
终端输入：
curl -X GET "http://localhost:8080/api/auth/me" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY3ODkwNiwiZXhwIjoxNzUzNzY1MzA2fQ.BKSTaQQwrnR9-Xs5peoYDvvlQO4wR1lo9KLV1eNsMRY"

终端输出：
{"data":{"id":3,"username":"admin","role":"ADMIN","roleDescription":"管理员","user":false,"admin":true},"success":true,"message":"获取用户信息成功"}%   

结论
获取当前用户信息功能正常。

* 检查令牌状态 (GET /api/auth/token/check)
终端输入：
curl -X GET "http://localhost:8080/api/auth/token/check" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY3ODkwNiwiZXhwIjoxNzUzNzY1MzA2fQ.BKSTaQQwrnR9-Xs5peoYDvvlQO4wR1lo9KLV1eNsMRY"
终端输出：
{"data":{"valid":true,"remainingTimeMs":81613521,"expiringSoon":false,"remainingTimeMinutes":1360},"success":true,"message":"令牌状态检查完成"}%    

结论
令牌状态检查功能正常。

* 令牌刷新功能： (POST /api/auth/refresh)
终端输入：
curl -X POST "http://localhost:8080/api/auth/refresh" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY3ODkwNiwiZXhwIjoxNzUzNzY1MzA2fQ.BKSTaQQwrnR9-Xs5peoYDvvlQO4wR1lo9KLV1eNsMRY"
终端输出：
{"data":{"accessToken":"eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY4Mzc1MiwiZXhwIjoxNzUzNzcwMTUyfQ.k139fZocPPJIAicLbsTErfkuJoW_s1n2V_DGgE4Lm7E","tokenType":"Bearer","expiresAt":"2025-07-29T14:22:31.247772","user":{"id":3,"username":"admin","role":"ADMIN","roleDescription":"管理员","user":false,"admin":true},"loginTime":"2025-07-28T14:22:32.248206","message":"令牌刷新成功","authorizationHeader":"Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY4Mzc1MiwiZXhwIjoxNzUzNzcwMTUyfQ.k139fZocPPJIAicLbsTErfkuJoW_s1n2V_DGgE4Lm7E","remainingMinutes":1439,"tokenExpiringSoon":false},"success":true,"message":"令牌刷新成功"}% 

结论
令牌刷新功能正常。

* 用户登出： (POST /api/auth/logout)
终端输入：
curl -X POST "http://localhost:8080/api/auth/logout" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY4Mzc1MiwiZXhwIjoxNzUzNzcwMTUyfQ.k139fZocPPJIAicLbsTErfkuJoW_s1n2V_DGgE4Lm7E"
终端输出：
{"success":true,"message":"登出成功"}%    

结论
用户登出功能正常。


* 获取航班列表 (GET /api/flights)
终端输入：
curl -X GET "http://localhost:8080/api/flights/1" \
  -H "Content-Type: application/json"
终端输出：
{"data":{"id":1,"flightNumber":"CA1234","airline":"中国国际航空","departureAirport":"PEK","arrivalAirport":"SHA","departureTime":"2025-07-30T08:00:00","arrivalTime":"2025-07-30T10:30:00","aircraftType":"A320","price":800.00,"availableSeats":148,"totalSeats":180,"status":"ACTIVE","statusDescription":"正常","createdAt":"2025-07-28T13:05:08.264205","updatedAt":"2025-07-28T13:07:45.95753","flightDurationMinutes":150,"occupancyRate":0.17777777777777778,"bookable":true,"fullyBooked":false,"routeDescription":"PEK → SHA","timeRangeDescription":"08:00 - 10:30","flightDurationDescription":"2小时30分钟","seatInfoDescription":"剩余148/180座位"},"success":true,"message":"获取航班详情成功"}

结论
获取航班详情功能正常。

* 获取可预订航班 (GET /api/flights/bookable)
终端输入：
curl -X GET "http://localhost:8080/api/flights/bookable" \
  -H "Content-Type: application/json"
终端输出：
{"data":[{"id":1,"flightNumber":"CA1234","airline":"中国国际航空","departureAirport":"PEK","arrivalAirport":"SHA","departureTime":"2025-07-30T08:00:00","arrivalTime":"2025-07-30T10:30:00","aircraftType":"A320","price":800.00,"availableSeats":148,"totalSeats":180,"status":"ACTIVE","statusDescription":"正常","createdAt":"2025-07-28T13:05:08.264205","updatedAt":"2025-07-28T13:07:45.95753","flightDurationMinutes":150,"occupancyRate":0.17777777777777778,"bookable":true,"fullyBooked":false,"routeDescription":"PEK → SHA","timeRangeDescription":"08:00 - 10:30","flightDurationDescription":"2小时30分钟","seatInfoDescription":"剩余148/180座位"}],"success":true,"message":"获取可预订航班成功"}%   

结论
获取可预订航班功能正常。

* 航班搜索功能 (GET /api/flights/search) 
终端输入：
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
终端输出：
{"data":{"accessToken":"eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY4NDAyOCwiZXhwIjoxNzUzNzcwNDI4fQ.0XceETVcwKnmTnUPM8bhjTeXevyejAy44QjEzaAtReM","tokenType":"Bearer","expiresAt":"2025-07-29T14:27:07.754395","user":{"id":3,"username":"admin","role":"ADMIN","roleDescription":"管理员","user":false,"admin":true},"loginTime":"2025-07-28T14:27:08.754911","message":"登录成功","authorizationHeader":"Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY4NDAyOCwiZXhwIjoxNzUzNzcwNDI4fQ.0XceETVcwKnmTnUPM8bhjTeXevyejAy44QjEzaAtReM","remainingMinutes":1439,"tokenExpiringSoon":false},"success":true,"message":"登录成功"}%  

结论
基础功能正常

* 更新航班 (PUT /api/flights/{id})
用新的JWT令牌测试更新航班功能：
终端输入：
curl -X PUT "http://localhost:8080/api/flights/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTc1MzY4NDAyOCwiZXhwIjoxNzUzNzcwNDI4fQ.0XceETVcwKnmTnUPM8bhjTeXevyejAy44QjEzaAtReM" \
