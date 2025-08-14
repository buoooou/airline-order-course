-- 在线机票系统数据库表结构设计
-- 复用表：app_users_yb, orders_yb
-- 所有表名必须以 _yb 结尾
-- 优化版本：使用IF NOT EXISTS避免重复建表，保护现有数据

-- 1. 用户表 (复用)
CREATE TABLE IF NOT EXISTS app_users_yb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    full_name VARCHAR(100),
    role ENUM('GUEST', 'USER', 'ADMIN') DEFAULT 'USER',
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

-- 2. 航空公司表
CREATE TABLE IF NOT EXISTS airlines_yb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL COMMENT '航空公司代码',
    name VARCHAR(100) NOT NULL COMMENT '航空公司名称',
    logo_url VARCHAR(255) COMMENT '航空公司logo',
    country VARCHAR(50) COMMENT '所属国家',
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. 机场表
CREATE TABLE IF NOT EXISTS airports_yb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL COMMENT '机场代码',
    name VARCHAR(100) NOT NULL COMMENT '机场名称',
    city VARCHAR(50) NOT NULL COMMENT '所在城市',
    country VARCHAR(50) NOT NULL COMMENT '所在国家',
    timezone VARCHAR(50) COMMENT '时区',
    latitude DECIMAL(10, 8) COMMENT '纬度',
    longitude DECIMAL(11, 8) COMMENT '经度',
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 4. 航班表
CREATE TABLE IF NOT EXISTS flights_yb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(20) NOT NULL COMMENT '航班号',
    airline_id BIGINT NOT NULL COMMENT '航空公司ID',
    departure_airport_id BIGINT NOT NULL COMMENT '出发机场ID',
    arrival_airport_id BIGINT NOT NULL COMMENT '到达机场ID',
    departure_time DATETIME NOT NULL COMMENT '出发时间',
    arrival_time DATETIME NOT NULL COMMENT '到达时间',
    aircraft_type VARCHAR(50) COMMENT '机型',
    total_seats INT NOT NULL COMMENT '总座位数',
    available_seats INT NOT NULL COMMENT '可用座位数',
    economy_price DECIMAL(10, 2) COMMENT '经济舱价格',
    business_price DECIMAL(10, 2) COMMENT '商务舱价格',
    first_price DECIMAL(10, 2) COMMENT '头等舱价格',
    status ENUM('SCHEDULED', 'BOARDING', 'DEPARTED', 'ARRIVED', 'CANCELLED', 'DELAYED') DEFAULT 'SCHEDULED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (airline_id) REFERENCES airlines_yb(id),
    FOREIGN KEY (departure_airport_id) REFERENCES airports_yb(id),
    FOREIGN KEY (arrival_airport_id) REFERENCES airports_yb(id),
    INDEX idx_flight_number (flight_number),
    INDEX idx_departure_time (departure_time),
    INDEX idx_airports (departure_airport_id, arrival_airport_id)
);

-- 5. 旅客表
CREATE TABLE IF NOT EXISTS passengers_yb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT COMMENT '关联用户ID(注册用户)',
    first_name VARCHAR(50) NOT NULL COMMENT '名',
    last_name VARCHAR(50) NOT NULL COMMENT '姓',
    gender ENUM('M', 'F', 'OTHER') COMMENT '性别',
    date_of_birth DATE COMMENT '出生日期',
    nationality VARCHAR(50) COMMENT '国籍',
    passport_number VARCHAR(50) COMMENT '护照号',
    id_card_number VARCHAR(50) COMMENT '身份证号',
    phone VARCHAR(20) COMMENT '电话',
    email VARCHAR(100) COMMENT '邮箱',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_users_yb(id) ON DELETE SET NULL,
    INDEX idx_passport (passport_number),
    INDEX idx_id_card (id_card_number)
);

-- 6. 订单表 (复用)
CREATE TABLE IF NOT EXISTS orders_yb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL COMMENT '订单号',
    user_id BIGINT COMMENT '用户ID',
    total_amount DECIMAL(10, 2) NOT NULL COMMENT '总金额',
    currency VARCHAR(10) DEFAULT 'CNY' COMMENT '货币',
    status ENUM('PENDING', 'CONFIRMED', 'PAID', 'CANCELLED', 'REFUNDED') DEFAULT 'PENDING',
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '预订日期',
    payment_method ENUM('CREDIT_CARD', 'DEBIT_CARD', 'ALIPAY', 'WECHAT', 'BANK_TRANSFER') COMMENT '支付方式',
    payment_status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    payment_time TIMESTAMP NULL COMMENT '支付时间',
    contact_name VARCHAR(100) COMMENT '联系人姓名',
    contact_phone VARCHAR(20) COMMENT '联系人电话',
    contact_email VARCHAR(100) COMMENT '联系人邮箱',
    notes TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_users_yb(id) ON DELETE SET NULL,
    INDEX idx_order_number (order_number),
    INDEX idx_booking_date (booking_date)
);

-- 7. 订单明细表
CREATE TABLE IF NOT EXISTS order_items_yb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    flight_id BIGINT NOT NULL COMMENT '航班ID',
    passenger_id BIGINT NOT NULL COMMENT '旅客ID',
    seat_class ENUM('ECONOMY', 'BUSINESS', 'FIRST') NOT NULL COMMENT '舱位等级',
    seat_number VARCHAR(10) COMMENT '座位号',
    ticket_price DECIMAL(10, 2) NOT NULL COMMENT '票价',
    taxes_fees DECIMAL(10, 2) DEFAULT 0 COMMENT '税费',
    total_price DECIMAL(10, 2) NOT NULL COMMENT '总价',
    ticket_status ENUM('BOOKED', 'CHECKED_IN', 'BOARDED', 'NO_SHOW', 'CANCELLED') DEFAULT 'BOOKED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders_yb(id) ON DELETE CASCADE,
    FOREIGN KEY (flight_id) REFERENCES flights_yb(id),
    FOREIGN KEY (passenger_id) REFERENCES passengers_yb(id),
    INDEX idx_order_flight (order_id, flight_id)
);

-- 8. 支付记录表
CREATE TABLE IF NOT EXISTS payments_yb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    payment_method ENUM('CREDIT_CARD', 'DEBIT_CARD', 'ALIPAY', 'WECHAT', 'BANK_TRANSFER') NOT NULL,
    amount DECIMAL(10, 2) NOT NULL COMMENT '支付金额',
    currency VARCHAR(10) DEFAULT 'CNY',
    payment_gateway VARCHAR(50) COMMENT '支付网关',
    transaction_id VARCHAR(100) COMMENT '交易ID',
    gateway_response TEXT COMMENT '网关响应',
    status ENUM('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED') DEFAULT 'PENDING',
    paid_at TIMESTAMP NULL COMMENT '支付时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders_yb(id) ON DELETE CASCADE,
    INDEX idx_transaction_id (transaction_id)
);

-- 9. 权限表
CREATE TABLE IF NOT EXISTS permissions_yb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL COMMENT '权限名称',
    description VARCHAR(255) COMMENT '权限描述',
    resource VARCHAR(100) NOT NULL COMMENT '资源',
    action VARCHAR(50) NOT NULL COMMENT '操作',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10. 角色权限关联表
CREATE TABLE IF NOT EXISTS role_permissions_yb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(20) NOT NULL COMMENT '角色',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (permission_id) REFERENCES permissions_yb(id) ON DELETE CASCADE,
    UNIQUE KEY uk_role_permission (role, permission_id)
);

-- 11. 系统配置表
CREATE TABLE IF NOT EXISTS system_configs_yb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) UNIQUE NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    description VARCHAR(255) COMMENT '配置描述',
    config_type ENUM('STRING', 'NUMBER', 'BOOLEAN', 'JSON') DEFAULT 'STRING',
    is_public BOOLEAN DEFAULT FALSE COMMENT '是否公开配置',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 12. 操作日志表
CREATE TABLE IF NOT EXISTS audit_logs_yb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT COMMENT '操作用户ID',
    action VARCHAR(100) NOT NULL COMMENT '操作类型',
    resource VARCHAR(100) COMMENT '操作资源',
    resource_id VARCHAR(100) COMMENT '资源ID',
    old_value JSON COMMENT '旧值',
    new_value JSON COMMENT '新值',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_users_yb(id) ON DELETE SET NULL,
    INDEX idx_user_action (user_id, action),
    INDEX idx_created_at (created_at)
);

-- 插入基础数据
-- 使用INSERT IGNORE避免重复插入时出错

-- 插入默认权限
INSERT IGNORE INTO permissions_yb (name, description, resource, action) VALUES
('USER_READ', '查看用户信息', 'USER', 'READ'),
('USER_WRITE', '编辑用户信息', 'USER', 'WRITE'),
('USER_DELETE', '删除用户', 'USER', 'DELETE'),
('FLIGHT_READ', '查看航班信息', 'FLIGHT', 'READ'),
('FLIGHT_WRITE', '编辑航班信息', 'FLIGHT', 'WRITE'),
('FLIGHT_DELETE', '删除航班', 'FLIGHT', 'DELETE'),
('ORDER_READ', '查看订单', 'ORDER', 'READ'),
('ORDER_WRITE', '编辑订单', 'ORDER', 'WRITE'),
('ORDER_DELETE', '删除订单', 'ORDER', 'DELETE'),
('PASSENGER_READ', '查看旅客信息', 'PASSENGER', 'READ'),
('PASSENGER_WRITE', '编辑旅客信息', 'PASSENGER', 'WRITE'),
('SYSTEM_CONFIG', '系统配置管理', 'SYSTEM', 'CONFIG'),
('AUDIT_LOG', '查看审计日志', 'AUDIT', 'READ');

-- 分配角色权限
-- 游客权限 (GUEST)
INSERT IGNORE INTO role_permissions_yb (role, permission_id) 
SELECT 'GUEST', id FROM permissions_yb WHERE name IN ('FLIGHT_READ');

-- 普通用户权限 (USER)
INSERT IGNORE INTO role_permissions_yb (role, permission_id) 
SELECT 'USER', id FROM permissions_yb WHERE name IN ('FLIGHT_READ', 'ORDER_READ', 'ORDER_WRITE', 'PASSENGER_READ', 'PASSENGER_WRITE');

-- 管理员权限 (ADMIN) - 所有权限
INSERT IGNORE INTO role_permissions_yb (role, permission_id) 
SELECT 'ADMIN', id FROM permissions_yb;

-- 插入默认系统配置
INSERT IGNORE INTO system_configs_yb (config_key, config_value, description, config_type, is_public) VALUES
('SITE_NAME', '天空机票预订系统', '网站名称', 'STRING', TRUE),
('SITE_LOGO', '/assets/images/logo.png', '网站logo', 'STRING', TRUE),
('BOOKING_ADVANCE_DAYS', '365', '最大提前预订天数', 'NUMBER', TRUE),
('REFUND_POLICY', '免费退票需在起飞前24小时', '退票政策', 'STRING', TRUE),
('CUSTOMER_SERVICE_PHONE', '400-123-4567', '客服电话', 'STRING', TRUE),
('CUSTOMER_SERVICE_EMAIL', 'service@airline.com', '客服邮箱', 'STRING', TRUE);

-- 插入示例数据

-- 插入航空公司
INSERT IGNORE INTO airlines_yb (code, name, logo_url, country) VALUES
('CA', '中国国际航空', '/assets/images/airlines/ca.png', 'CN'),
('CZ', '中国南方航空', '/assets/images/airlines/cz.png', 'CN'),
('MU', '中国东方航空', '/assets/images/airlines/mu.png', 'CN'),
('HU', '海南航空', '/assets/images/airlines/hu.png', 'CN'),
('3U', '四川航空', '/assets/images/airlines/3u.png', 'CN'),
('9C', '春秋航空', '/assets/images/airlines/9c.png', 'CN'),
('GJ', '长龙航空', '/assets/images/airlines/gj.png', 'CN'),
('JD', '首都航空', '/assets/images/airlines/jd.png', 'CN'),
('OQ', '青岛航空', '/assets/images/airlines/oq.png', 'CN'),
('EU', '成都航空', '/assets/images/airlines/eu.png', 'CN');

-- 插入机场
INSERT IGNORE INTO airports_yb (code, name, city, country, timezone) VALUES
('PEK', '北京首都国际机场', '北京', 'CN', 'Asia/Shanghai'),
('SHA', '上海虹桥国际机场', '上海', 'CN', 'Asia/Shanghai'),
('PVG', '上海浦东国际机场', '上海', 'CN', 'Asia/Shanghai'),
('CAN', '广州白云国际机场', '广州', 'CN', 'Asia/Shanghai'),
('SZX', '深圳宝安国际机场', '深圳', 'CN', 'Asia/Shanghai'),
('CTU', '成都双流国际机场', '成都', 'CN', 'Asia/Shanghai'),
('XIY', '西安咸阳国际机场', '西安', 'CN', 'Asia/Shanghai'),
('KMG', '昆明长水国际机场', '昆明', 'CN', 'Asia/Shanghai'),
('URC', '乌鲁木齐地窝堡国际机场', '乌鲁木齐', 'CN', 'Asia/Shanghai'),
('HGH', '杭州萧山国际机场', '杭州', 'CN', 'Asia/Shanghai'),
('NKG', '南京禄口国际机场', '南京', 'CN', 'Asia/Shanghai'),
('WUH', '武汉天河国际机场', '武汉', 'CN', 'Asia/Shanghai'),
('CSX', '长沙黄花国际机场', '长沙', 'CN', 'Asia/Shanghai'),
('NNG', '南宁吴圩国际机场', '南宁', 'CN', 'Asia/Shanghai'),
('HAK', '海口美兰国际机场', '海口', 'CN', 'Asia/Shanghai'),
('SYX', '三亚凤凰国际机场', '三亚', 'CN', 'Asia/Shanghai');

-- 插入示例用户
INSERT IGNORE INTO app_users_yb (username, email, password, phone, full_name, role, status) VALUES
('admin', 'admin@airline.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLqlfBUNEc2C', '13800138000', '系统管理员', 'ADMIN', 'ACTIVE'),
('testuser1', 'user1@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLqlfBUNEc2C', '13800138001', '张三', 'USER', 'ACTIVE'),
('testuser2', 'user2@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLqlfBUNEc2C', '13800138002', '李四', 'USER', 'ACTIVE'),
('testuser3', 'user3@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLqlfBUNEc2C', '13800138003', '王五', 'USER', 'ACTIVE'),
('testuser4', 'user4@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLqlfBUNEc2C', '13800138004', '赵六', 'USER', 'ACTIVE'),
('testuser5', 'user5@test.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLqlfBUNEc2C', '13800138005', '孙七', 'USER', 'ACTIVE');

-- 插入航班数据
INSERT IGNORE INTO flights_yb (flight_number, airline_id, departure_airport_id, arrival_airport_id, departure_time, arrival_time, aircraft_type, total_seats, available_seats, economy_price, business_price, first_price, status) VALUES
-- 北京到上海航线 (PEK=1, SHA=2, PVG=3)
('CA1501', 1, 1, 3, '2025-08-15 08:00:00', '2025-08-15 10:30:00', 'A320', 180, 150, 800.00, 2400.00, 4800.00, 'SCHEDULED'),
('MU5101', 3, 1, 2, '2025-08-15 09:30:00', '2025-08-15 12:00:00', 'B737', 160, 120, 750.00, 2250.00, 4500.00, 'SCHEDULED'),
('CZ3201', 2, 1, 3, '2025-08-15 14:00:00', '2025-08-15 16:30:00', 'A321', 200, 180, 820.00, 2460.00, 4920.00, 'SCHEDULED'),
-- 添加更多PEK到PVG的航班
('CA1503', 1, 1, 3, '2025-08-15 16:00:00', '2025-08-15 18:30:00', 'A320', 180, 150, 850.00, 2550.00, 5100.00, 'SCHEDULED'),
('MU5103', 3, 1, 3, '2025-08-15 18:30:00', '2025-08-15 21:00:00', 'B737', 160, 120, 780.00, 2340.00, 4680.00, 'SCHEDULED'),
-- 上海到广州航线
('CZ3301', 2, 3, 4, '2025-08-16 10:00:00', '2025-08-16 12:45:00', 'B777', 300, 250, 1200.00, 3600.00, 7200.00, 'SCHEDULED'),
('MU5201', 3, 2, 4, '2025-08-16 15:30:00', '2025-08-16 18:15:00', 'A330', 280, 220, 1150.00, 3450.00, 6900.00, 'SCHEDULED'),
-- 北京到成都航线
('CA4101', 1, 1, 6, '2025-08-17 07:30:00', '2025-08-17 10:15:00', 'A321', 200, 170, 1300.00, 3900.00, 7800.00, 'SCHEDULED'),
('3U8701', 5, 1, 6, '2025-08-17 13:00:00', '2025-08-17 15:45:00', 'A320', 180, 160, 1250.00, 3750.00, 7500.00, 'SCHEDULED'),
-- 广州到深圳航线
('CZ3401', 2, 4, 5, '2025-08-18 08:30:00', '2025-08-18 09:15:00', 'B737', 160, 140, 300.00, 900.00, 1800.00, 'SCHEDULED'),
('HU7201', 4, 4, 5, '2025-08-18 16:00:00', '2025-08-18 16:45:00', 'A320', 180, 150, 320.00, 960.00, 1920.00, 'SCHEDULED'),
-- 成都到昆明航线
('3U8801', 5, 6, 8, '2025-08-19 11:00:00', '2025-08-19 12:30:00', 'A319', 140, 120, 600.00, 1800.00, 3600.00, 'SCHEDULED'),
('MU5301', 3, 6, 8, '2025-08-19 17:30:00', '2025-08-19 19:00:00', 'B737', 160, 130, 580.00, 1740.00, 3480.00, 'SCHEDULED'),
-- 西安到乌鲁木齐航线
('CZ6901', 2, 7, 9, '2025-08-20 09:00:00', '2025-08-20 12:30:00', 'A330', 280, 200, 1800.00, 5400.00, 10800.00, 'SCHEDULED'),
-- 杭州到南京航线
('MU5401', 3, 10, 11, '2025-08-21 14:30:00', '2025-08-21 15:45:00', 'A320', 180, 160, 400.00, 1200.00, 2400.00, 'SCHEDULED'),
-- 武汉到长沙航线
('CZ3501', 2, 12, 13, '2025-08-22 10:30:00', '2025-08-22 11:45:00', 'B737', 160, 140, 350.00, 1050.00, 2100.00, 'SCHEDULED'),
-- 海口到三亚航线
('HU7301', 4, 15, 16, '2025-08-23 12:00:00', '2025-08-23 12:50:00', 'A319', 140, 120, 280.00, 840.00, 1680.00, 'SCHEDULED');

-- 插入旅客数据
INSERT IGNORE INTO passengers_yb (user_id, first_name, last_name, gender, date_of_birth, nationality, passport_number, id_card_number, phone, email) VALUES
(2, '三', '张', 'M', '1990-05-15', '中国', 'G12345678', '110101199005151234', '13800138001', 'user1@test.com'),
(3, '四', '李', 'F', '1985-08-20', '中国', 'G23456789', '110101198508201234', '13800138002', 'user2@test.com'),
(4, '五', '王', 'M', '1992-12-10', '中国', 'G34567890', '110101199212101234', '13800138003', 'user3@test.com'),
(5, '六', '赵', 'F', '1988-03-25', '中国', 'G45678901', '110101198803251234', '13800138004', 'user4@test.com'),
(6, '七', '孙', 'M', '1995-07-08', '中国', 'G56789012', '110101199507081234', '13800138005', 'user5@test.com');

-- 插入订单数据
INSERT IGNORE INTO orders_yb (order_number, user_id, total_amount, currency, status, booking_date, payment_method, payment_status, contact_name, contact_phone, contact_email) VALUES
('ORD202501200001', 2, 800.00, 'CNY', 'PAID', '2025-01-19 10:30:00', 'ALIPAY', 'COMPLETED', '张三', '13800138001', 'user1@test.com'),
('ORD202501200002', 3, 1200.00, 'CNY', 'CONFIRMED', '2025-01-19 14:20:00', 'WECHAT', 'COMPLETED', '李四', '13800138002', 'user2@test.com'),
('ORD202501200003', 4, 1300.00, 'CNY', 'PENDING', '2025-01-19 16:45:00', 'CREDIT_CARD', 'PENDING', '王五', '13800138003', 'user3@test.com');

-- 插入订单明细数据
INSERT IGNORE INTO order_items_yb (order_id, flight_id, passenger_id, seat_class, seat_number, ticket_price, taxes_fees, total_price, ticket_status) VALUES
(1, 1, 1, 'ECONOMY', '12A', 800.00, 0.00, 800.00, 'BOOKED'),
(2, 4, 2, 'ECONOMY', '15C', 1200.00, 0.00, 1200.00, 'BOOKED'),
(3, 6, 3, 'ECONOMY', '8F', 1300.00, 0.00, 1300.00, 'BOOKED');

-- 插入支付记录数据
INSERT IGNORE INTO payments_yb (order_id, payment_method, amount, currency, payment_gateway, transaction_id, status, paid_at) VALUES
(1, 'ALIPAY', 800.00, 'CNY', 'alipay', 'ALI202501190001', 'SUCCESS', '2025-01-19 10:35:00'),
(2, 'WECHAT', 1200.00, 'CNY', 'wechat', 'WX202501190001', 'SUCCESS', '2025-01-19 14:25:00'),
(3, 'CREDIT_CARD', 1300.00, 'CNY', 'stripe', 'STRIPE202501190001', 'PENDING', NULL);}]}}}