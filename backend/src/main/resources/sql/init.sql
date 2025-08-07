-- =================================================================
--  航空订单管理系统 - 数据库初始化脚本
--  功能:
--  1. 创建 airline_order_db 数据库
--  2. 创建 app_users_qiaozhe 表 (用户信息)
--  3. 创建 flight_info_qiaozhe 表 (航班信息)
--  4. 创建 orders_qiaozhe 表 (订单信息)
--  5. 建立表之间的外键关系
--  6. 插入基础测试数据
--  
--  注意: 此脚本适用于Docker容器初始化或手动数据库初始化
-- =================================================================

-- 步骤 1: 创建数据库并切换
CREATE DATABASE IF NOT EXISTS `airline_order_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `airline_order_db`;

-- 删除已存在的表（按依赖关系顺序删除）
DROP TABLE IF EXISTS `orders_qiaozhe`;
DROP TABLE IF EXISTS `flight_info_qiaozhe`;
DROP TABLE IF EXISTS `app_users_qiaozhe`;

-- =================================================================
-- 步骤 2: 创建用户表 (app_users_qiaozhe)
-- =================================================================
CREATE TABLE `app_users_qiaozhe` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(255) NOT NULL COMMENT '用户名，唯一',
  `password` varchar(255) NOT NULL COMMENT 'BCrypt加密后的密码',
  `role` varchar(50) NOT NULL COMMENT '用户角色：ADMIN/USER',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`) COMMENT '用户名唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';

-- =================================================================
-- 步骤 3: 创建航班信息表 (flight_info_qiaozhe)
-- =================================================================
CREATE TABLE `flight_info_qiaozhe` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `flight_number` varchar(20) NOT NULL COMMENT '航班号，如 CA1234',
  `airline` varchar(100) NOT NULL COMMENT '航空公司名称',
  `departure_airport` varchar(10) NOT NULL COMMENT '出发机场代码，如 PEK',
  `arrival_airport` varchar(10) NOT NULL COMMENT '到达机场代码，如 SHA',
  `departure_time` datetime(6) NOT NULL COMMENT '出发时间',
  `arrival_time` datetime(6) NOT NULL COMMENT '到达时间',
  `aircraft_type` varchar(50) COMMENT '机型，如 Boeing 737',
  `price` decimal(19,2) NOT NULL COMMENT '票价',
  `available_seats` int NOT NULL DEFAULT 0 COMMENT '可用座位数',
  `total_seats` int NOT NULL DEFAULT 0 COMMENT '总座位数',
  `status` enum('ACTIVE','CANCELLED','DELAYED') NOT NULL DEFAULT 'ACTIVE' COMMENT '航班状态：正常/取消/延误',
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_flight_number_date` (`flight_number`, `departure_time`) COMMENT '同一航班号在同一时间唯一',
  KEY `idx_departure_arrival` (`departure_airport`, `arrival_airport`) COMMENT '出发到达机场索引',
  KEY `idx_departure_time` (`departure_time`) COMMENT '出发时间索引',
  KEY `idx_status` (`status`) COMMENT '航班状态索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='航班信息表';

-- =================================================================
-- 步骤 4: 创建订单表 (orders_qiaozhe)
-- =================================================================
CREATE TABLE `orders_qiaozhe` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_number` varchar(255) NOT NULL COMMENT '订单号，系统生成',
  `status` enum('PENDING_PAYMENT','PAID','TICKETING_IN_PROGRESS','TICKETING_FAILED','TICKETED','CANCELLED') NOT NULL COMMENT '订单状态',
  `amount` decimal(19,2) NOT NULL COMMENT '订单金额',
  `creation_date` datetime(6) NOT NULL COMMENT '创建时间',
  `last_updated` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '最后更新时间',
  `user_id` bigint NOT NULL COMMENT '用户ID，外键关联用户表',
  `flight_info_id` bigint NOT NULL COMMENT '航班信息ID，外键关联航班表',
  `passenger_count` int NOT NULL DEFAULT 1 COMMENT '乘客数量',
  `passenger_names` varchar(500) COMMENT '乘客姓名列表，逗号分隔',
  `contact_phone` varchar(11) COMMENT '联系电话',
  `contact_email` varchar(100) COMMENT '联系邮箱',
  `payment_time` datetime(6) COMMENT '支付时间',
  `ticketing_time` datetime(6) COMMENT '出票时间',
  `ticketing_start_time` datetime(6) COMMENT '开始出票时间',
  `ticketing_completion_time` datetime(6) COMMENT '出票完成时间',
  `ticketing_failure_reason` varchar(500) COMMENT '出票失败原因',
  `cancellation_time` datetime(6) COMMENT '取消时间',
  `cancellation_reason` varchar(500) COMMENT '取消原因',
  `remarks` varchar(1000) COMMENT '备注信息',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_number` (`order_number`) COMMENT '订单号唯一索引',
  KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引',
  KEY `idx_flight_info_id` (`flight_info_id`) COMMENT '航班信息ID索引',
  KEY `idx_status` (`status`) COMMENT '订单状态索引',
  KEY `idx_creation_date` (`creation_date`) COMMENT '创建时间索引',
  CONSTRAINT `fk_orders_user_new` FOREIGN KEY (`user_id`) REFERENCES `app_users_qiaozhe` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_orders_flight_new` FOREIGN KEY (`flight_info_id`) REFERENCES `flight_info_qiaozhe` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单信息表';

-- =================================================================
-- 步骤 5: 插入基础测试数据
-- =================================================================

-- 插入测试用户 (密码原文均为 'admin123' 和 'user123')
-- BCrypt加密后的密码哈希值
INSERT INTO `app_users_qiaozhe` (`username`, `password`, `role`) VALUES
('admin', '$2a$10$hJ/pfq0k2alfmFB.E5L5JOoEr.bDRpBEK20DFMLs73yGrwzHNDR/S', 'ADMIN'),
('user1', '$2a$10$hJ/pfq0k2alfmFB.E5L5JOoEr.bDRpBEK20DFMLs73yGrwzHNDR/S', 'USER'),
('user2', '$2a$10$hJ/pfq0k2alfmFB.E5L5JOoEr.bDRpBEK20DFMLs73yGrwzHNDR/S', 'USER');

-- 插入基础航班数据
INSERT INTO `flight_info_qiaozhe` (
  `flight_number`, `airline`, `departure_airport`, `arrival_airport`, 
  `departure_time`, `arrival_time`, `aircraft_type`, `price`, 
  `available_seats`, `total_seats`, `status`
) VALUES
-- 热门航线航班
('CA1234', '中国国际航空', 'PEK', 'PVG', '2025-07-30 08:00:00', '2025-07-30 10:30:00', 'A320', 800.00, 150, 180, 'ACTIVE'),
('MU5678', '中国东方航空', 'PEK', 'PVG', '2025-07-30 14:00:00', '2025-07-30 16:30:00', 'A320', 750.00, 120, 160, 'ACTIVE'),
('CZ9012', '中国南方航空', 'PVG', 'CAN', '2025-07-31 09:30:00', '2025-07-31 12:00:00', 'B777', 980.00, 200, 250, 'ACTIVE');

-- 插入基础订单数据（覆盖主要状态）
INSERT INTO `orders_qiaozhe` (
  `order_number`, `status`, `amount`, `creation_date`, `user_id`, `flight_info_id`,
  `passenger_count`, `passenger_names`, `contact_phone`, `contact_email`, 
  `payment_time`, `ticketing_time`, `remarks`
) VALUES
-- 管理员测试订单
('ORD202507280001', 'PAID', 800.00, NOW() - INTERVAL 1 DAY, 1, 1, 1, '张三', '13800138001', 'admin@example.com', NOW() - INTERVAL 23 HOUR, NULL, '管理员测试订单'),
('ORD202507280002', 'TICKETED', 1500.00, NOW() - INTERVAL 2 DAY, 1, 2, 2, '李四,王五', '13800138002', 'admin@example.com', NOW() - INTERVAL 47 HOUR, NOW() - INTERVAL 46 HOUR, '已出票订单'),

-- 普通用户订单
('ORD202507280003', 'PENDING_PAYMENT', 750.00, NOW() - INTERVAL 5 MINUTE, 2, 2, 1, '钱七', '13800138004', 'user1@example.com', NULL, NULL, '待支付订单'),
('ORD202507280004', 'CANCELLED', 980.00, NOW() - INTERVAL 1 HOUR, 3, 3, 1, '孙八', '13800138005', 'user2@example.com', NULL, NULL, '用户取消订单');

-- =================================================================
-- 步骤 6: 数据验证和统计
-- =================================================================

-- 打印初始化成功信息
SELECT '=== 航空订单管理系统数据库初始化成功！===' AS '状态';

-- 统计数据
SELECT 
  (SELECT COUNT(*) FROM `app_users_qiaozhe`) AS '用户总数',
  (SELECT COUNT(*) FROM `flight_info_qiaozhe`) AS '航班总数',
  (SELECT COUNT(*) FROM `orders_qiaozhe`) AS '订单总数';

-- 按状态统计
SELECT '用户角色分布:' AS '统计类别';
SELECT role AS '角色', COUNT(*) AS '数量' FROM `app_users_qiaozhe` GROUP BY role;

SELECT '航班状态分布:' AS '统计类别';
SELECT status AS '状态', COUNT(*) AS '数量' FROM `flight_info_qiaozhe` GROUP BY status;

SELECT '订单状态分布:' AS '统计类别';
SELECT status AS '状态', COUNT(*) AS '数量' FROM `orders_qiaozhe` GROUP BY status;

SELECT '=== 初始化完成，系统可以正常启动！===' AS '完成状态';
