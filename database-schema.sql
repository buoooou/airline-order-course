-- 在线机票系统数据库表结构设计
-- 复用表：app_users_yb, orders_yb
-- 所有表名必须以 _yb 结尾

-- 1. 用户表 (复用)
DROP TABLE IF EXISTS app_users_yb;
CREATE TABLE app_users_yb (
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
DROP TABLE IF EXISTS airlines_yb;
CREATE TABLE airlines_yb (
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
DROP TABLE IF EXISTS airports_yb;
CREATE TABLE airports_yb (
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
DROP TABLE IF EXISTS flights_yb;
CREATE TABLE flights_yb (
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
DROP TABLE IF EXISTS passengers_yb;
CREATE TABLE passengers_yb (
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
DROP TABLE IF EXISTS orders_yb;
CREATE TABLE orders_yb (
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
DROP TABLE IF EXISTS order_items_yb;
CREATE TABLE order_items_yb (
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
DROP TABLE IF EXISTS payments_yb;
CREATE TABLE payments_yb (
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
DROP TABLE IF EXISTS permissions_yb;
CREATE TABLE permissions_yb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL COMMENT '权限名称',
    description VARCHAR(255) COMMENT '权限描述',
    resource VARCHAR(100) NOT NULL COMMENT '资源',
    action VARCHAR(50) NOT NULL COMMENT '操作',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10. 角色权限关联表
DROP TABLE IF EXISTS role_permissions_yb;
CREATE TABLE role_permissions_yb (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(20) NOT NULL COMMENT '角色',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (permission_id) REFERENCES permissions_yb(id) ON DELETE CASCADE,
    UNIQUE KEY uk_role_permission (role, permission_id)
);

-- 11. 系统配置表
DROP TABLE IF EXISTS system_configs_yb;
CREATE TABLE system_configs_yb (
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
DROP TABLE IF EXISTS audit_logs_yb;
CREATE TABLE audit_logs_yb (
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

-- 插入默认权限
INSERT INTO permissions_yb (name, description, resource, action) VALUES
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
INSERT INTO role_permissions_yb (role, permission_id) 
SELECT 'GUEST', id FROM permissions_yb WHERE name IN ('FLIGHT_READ');

-- 普通用户权限 (USER)
INSERT INTO role_permissions_yb (role, permission_id) 
SELECT 'USER', id FROM permissions_yb WHERE name IN ('FLIGHT_READ', 'ORDER_READ', 'ORDER_WRITE', 'PASSENGER_READ', 'PASSENGER_WRITE');

-- 管理员权限 (ADMIN) - 所有权限
INSERT INTO role_permissions_yb (role, permission_id) 
SELECT 'ADMIN', id FROM permissions_yb;

-- 插入默认系统配置
INSERT INTO system_configs_yb (config_key, config_value, description, config_type, is_public) VALUES
('SITE_NAME', '天空机票预订系统', '网站名称', 'STRING', TRUE),
('SITE_LOGO', '/assets/images/logo.png', '网站logo', 'STRING', TRUE),
('BOOKING_ADVANCE_DAYS', '365', '最大提前预订天数', 'NUMBER', TRUE),
('REFUND_POLICY', '免费退票需在起飞前24小时', '退票政策', 'STRING', TRUE),
('CUSTOMER_SERVICE_PHONE', '400-123-4567', '客服电话', 'STRING', TRUE),
('CUSTOMER_SERVICE_EMAIL', 'service@airline.com', '客服邮箱', 'STRING', TRUE);

-- 插入示例数据

-- 插入航空公司
INSERT INTO airlines_yb (code, name, logo_url, country) VALUES
('CA', '中国国际航空', '/assets/images/airlines/ca.png', 'CN'),
('CZ', '中国南方航空', '/assets/images/airlines/cz.png', 'CN'),
('MU', '中国东方航空', '/assets/images/airlines/mu.png', 'CN'),
('HU', '海南航空', '/assets/images/airlines/hu.png', 'CN');

-- 插入机场
INSERT INTO airports_yb (code, name, city, country, timezone) VALUES
('PEK', '北京首都国际机场', '北京', 'CN', 'Asia/Shanghai'),
('SHA', '上海虹桥国际机场', '上海', 'CN', 'Asia/Shanghai'),
('PVG', '上海浦东国际机场', '上海', 'CN', 'Asia/Shanghai'),
('CAN', '广州白云国际机场', '广州', 'CN', 'Asia/Shanghai'),
('SZX', '深圳宝安国际机场', '深圳', 'CN', 'Asia/Shanghai'),
('CTU', '成都双流国际机场', '成都', 'CN', 'Asia/Shanghai'),
('XIY', '西安咸阳国际机场', '西安', 'CN', 'Asia/Shanghai');

-- 插入示例管理员用户
INSERT INTO app_users_yb (username, email, password, phone, full_name, role, status) VALUES
('admin', 'admin@airline.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLqlfBUNEc2C', '13800138000', '系统管理员', 'ADMIN', 'ACTIVE');