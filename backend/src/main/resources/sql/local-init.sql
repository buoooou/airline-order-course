-- 本地数据库初始化脚本
-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS airline_order_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE airline_order_db;

-- 创建用户表
CREATE TABLE IF NOT EXISTS `app_users_qiaozhe` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建订单表
CREATE TABLE IF NOT EXISTS `orders_qiaozhe` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_number` varchar(255) NOT NULL,
  `status` enum('PENDING_PAYMENT','PAID','TICKETING_IN_PROGRESS','TICKETING_FAILED','TICKETED','CANCELLED') NOT NULL,
  `amount` decimal(19,2) NOT NULL,
  `creation_date` datetime(6) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_orders_user_id` (`user_id`),
  CONSTRAINT `fk_orders_user_new` FOREIGN KEY (`user_id`) REFERENCES `app_users_qiaozhe` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建航班信息表
CREATE TABLE IF NOT EXISTS `flight_info_qiaozhe` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `flight_number` varchar(20) NOT NULL COMMENT '航班号',
  `airline` varchar(100) NOT NULL COMMENT '航空公司',
  `departure_airport` varchar(10) NOT NULL COMMENT '出发机场代码',
  `arrival_airport` varchar(10) NOT NULL COMMENT '到达机场代码',
  `departure_time` datetime NOT NULL COMMENT '出发时间',
  `arrival_time` datetime NOT NULL COMMENT '到达时间',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `available_seats` int NOT NULL DEFAULT '0' COMMENT '可用座位数',
  `aircraft_type` varchar(50) DEFAULT NULL COMMENT '机型',
  `status` enum('SCHEDULED','DELAYED','CANCELLED','DEPARTED','ARRIVED') NOT NULL DEFAULT 'SCHEDULED' COMMENT '航班状态',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_flight_number_date` (`flight_number`,`departure_time`),
  KEY `idx_departure_airport` (`departure_airport`),
  KEY `idx_arrival_airport` (`arrival_airport`),
  KEY `idx_departure_time` (`departure_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='航班信息表';

-- 插入测试用户数据
INSERT IGNORE INTO `app_users_qiaozhe` (`username`, `password`, `role`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'ADMIN'),  -- 密码: admin123
('user', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER');    -- 密码: user123

-- 插入测试航班数据
INSERT IGNORE INTO `flight_info_qiaozhe` (`flight_number`, `airline`, `departure_airport`, `arrival_airport`, `departure_time`, `arrival_time`, `price`, `available_seats`, `aircraft_type`, `status`) VALUES
('CA1234', '中国国际航空', 'PEK', 'SHA', '2024-12-20 08:00:00', '2024-12-20 10:30:00', 1200.00, 150, 'Boeing 737', 'SCHEDULED'),
('MU5678', '中国东方航空', 'SHA', 'CAN', '2024-12-20 14:00:00', '2024-12-20 16:45:00', 980.00, 180, 'Airbus A320', 'SCHEDULED'),
('CZ9012', '中国南方航空', 'CAN', 'PEK', '2024-12-20 19:00:00', '2024-12-20 22:15:00', 1350.00, 200, 'Boeing 777', 'SCHEDULED'),
('3U8888', '四川航空', 'CTU', 'PVG', '2024-12-21 09:30:00', '2024-12-21 12:00:00', 850.00, 120, 'Airbus A319', 'SCHEDULED'),
('HU7777', '海南航空', 'HAK', 'PEK', '2024-12-21 16:20:00', '2024-12-21 19:45:00', 1680.00, 160, 'Boeing 787', 'SCHEDULED');

-- 插入测试订单数据
INSERT IGNORE INTO `orders_qiaozhe` (`order_number`, `status`, `amount`, `creation_date`, `user_id`) VALUES
('ORD20241220001', 'PAID', 1200.00, '2024-12-20 10:30:00.000000', 2),
('ORD20241220002', 'TICKETED', 980.00, '2024-12-20 11:15:00.000000', 2),
('ORD20241220003', 'PENDING_PAYMENT', 1350.00, '2024-12-20 15:20:00.000000', 2);

-- 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders_qiaozhe(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders_qiaozhe(status);
CREATE INDEX IF NOT EXISTS idx_orders_creation_date ON orders_qiaozhe(creation_date);

-- 显示创建结果
SELECT 'Database and tables created successfully!' as result;
