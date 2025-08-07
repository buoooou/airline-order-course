-- =================================================================
--  航空订单管理系统 - 数据库表创建脚本
--  功能:
--  1. 创建 app_users_qiaozhe 表 (用户信息)
--  2. 创建 flight_info_qiaozhe 表 (航班信息)
--  3. 创建 orders_qiaozhe 表 (订单信息)
--  4. 建立表之间的外键关系
--  5. 插入测试数据
-- =================================================================

USE `airline_order_db`;

-- 删除已存在的表（按依赖关系顺序删除）
DROP TABLE IF EXISTS `orders_qiaozhe`;
DROP TABLE IF EXISTS `flight_info_qiaozhe`;
DROP TABLE IF EXISTS `app_users_qiaozhe`;
DROP TABLE IF EXISTS `shedlock`;

-- =================================================================
-- 1. 创建用户表 (app_users_qiaozhe)
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
-- 2. 创建航班信息表 (flight_info_qiaozhe)
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
-- 3. 创建订单表 (orders_qiaozhe)
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
-- 4. 创建ShedLock分布式锁表
-- =================================================================
CREATE TABLE `shedlock` (
  `name` varchar(64) NOT NULL COMMENT '锁名称',
  `lock_until` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '锁定到期时间',
  `locked_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '锁定时间',
  `locked_by` varchar(255) NOT NULL COMMENT '锁定者标识',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ShedLock分布式锁表';

-- =================================================================
-- 5. 插入测试数据
-- =================================================================

-- 插入测试用户
INSERT INTO `app_users_qiaozhe` (`username`, `password`, `role`) VALUES
('admin', '$2a$10$hJ/pfq0k2alfmFB.E5L5JOoEr.bDRpBEK20DFMLs73yGrwzHNDR/S', 'ADMIN'),
('user1', '$2a$10$hJ/pfq0k2alfmFB.E5L5JOoEr.bDRpBEK20DFMLs73yGrwzHNDR/S', 'USER'),
('user2', '$2a$10$hJ/pfq0k2alfmFB.E5L5JOoEr.bDRpBEK20DFMLs73yGrwzHNDR/S', 'USER');

-- 插入测试航班数据
INSERT INTO `flight_info_qiaozhe` (
  `flight_number`, `airline`, `departure_airport`, `arrival_airport`, 
  `departure_time`, `arrival_time`, `aircraft_type`, `price`, 
  `available_seats`, `total_seats`, `status`
) VALUES
-- 北京到上海的航班
('CA1234', '中国国际航空', 'PEK', 'PVG', '2025-07-30 08:00:00', '2025-07-30 10:30:00', 'A320', 800.00, 150, 180, 'ACTIVE'),
('MU5678', '中国东方航空', 'PEK', 'PVG', '2025-07-30 14:00:00', '2025-07-30 16:30:00', 'A320', 750.00, 120, 160, 'ACTIVE'),

-- 上海到广州的航班
('CZ9012', '中国南方航空', 'PVG', 'CAN', '2025-07-31 09:30:00', '2025-07-31 12:00:00', 'B777', 980.00, 200, 250, 'ACTIVE'),
('CA3456', '中国国际航空', 'PVG', 'CAN', '2025-07-31 15:45:00', '2025-07-31 18:15:00', 'A330', 1050.00, 180, 220, 'ACTIVE'),

-- 北京到深圳的航班
('ZH7890', '深圳航空', 'PEK', 'SZX', '2025-08-01 07:15:00', '2025-08-01 10:45:00', 'B737', 1200.00, 100, 140, 'ACTIVE'),
('CZ1357', '中国南方航空', 'PEK', 'SZX', '2025-08-01 19:20:00', '2025-08-01 22:50:00', 'A321', 1150.00, 160, 200, 'ACTIVE'),

-- 延误和取消的航班示例
('MU2468', '中国东方航空', 'PVG', 'CTU', '2025-08-02 11:00:00', '2025-08-02 13:30:00', 'B737', 890.00, 0, 150, 'DELAYED'),
('CA9999', '中国国际航空', 'PEK', 'XIY', '2025-08-02 16:00:00', '2025-08-02 18:20:00', 'A320', 750.00, 0, 160, 'CANCELLED');

-- 插入测试订单数据（增加更多各种状态的订单用于测试定时任务）
INSERT INTO `orders_qiaozhe` (
  `order_number`, `status`, `amount`, `creation_date`, `user_id`, `flight_info_id`,
  `passenger_count`, `passenger_names`, `contact_phone`, `contact_email`, 
  `payment_time`, `ticketing_time`, `ticketing_start_time`, `ticketing_completion_time`, 
  `ticketing_failure_reason`, `cancellation_time`, `cancellation_reason`, `remarks`
) VALUES
-- 管理员的订单
('ORD202507280001', 'PAID', 800.00, '2025-07-28 10:00:00', 1, 1, 1, '张三', '13800138001', 'zhangsan@example.com', '2025-07-28 10:05:00', NULL, NULL, NULL, NULL, NULL, NULL, '管理员测试订单'),
('ORD202507280002', 'TICKETED', 1500.00, '2025-07-28 11:00:00', 1, 2, 2, '李四,王五', '13800138002', 'lisi@example.com', '2025-07-28 11:05:00', '2025-07-28 11:10:00', '2025-07-28 11:06:00', '2025-07-28 11:10:00', NULL, NULL, NULL, '已出票订单'),
('ORD202507280003', 'TICKETING_FAILED', 980.00, '2025-07-28 12:00:00', 1, 3, 1, '赵六', '13800138003', 'zhaoliu@example.com', '2025-07-28 12:05:00', NULL, '2025-07-28 12:06:00', NULL, '航班座位不足', NULL, NULL, '出票失败订单'),

-- 普通用户的订单
('ORD202507280004', 'PENDING_PAYMENT', 750.00, '2025-07-28 13:00:00', 2, 2, 1, '钱七', '13800138004', 'qianqi@example.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '待支付订单'),
('ORD202507280005', 'CANCELLED', 1200.00, '2025-07-28 14:00:00', 2, 5, 1, '孙八', '13800138005', 'sunba@example.com', NULL, NULL, NULL, NULL, NULL, '2025-07-28 14:30:00', '用户主动取消', '用户取消订单'),
('ORD202507280006', 'TICKETING_IN_PROGRESS', 2100.00, '2025-07-28 15:00:00', 3, 6, 2, '周九,吴十', '13800138006', 'zhoujiu@example.com', '2025-07-28 15:05:00', NULL, '2025-07-28 15:06:00', NULL, NULL, NULL, NULL, '出票中订单'),

-- 更多测试数据 - 超时待支付订单（用于测试定时任务）
('ORD202508050001', 'PENDING_PAYMENT', 850.00, '2025-08-05 19:00:00', 2, 1, 1, '测试用户1', '13900139001', 'test1@example.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '超时待支付订单1'),
('ORD202508050002', 'PENDING_PAYMENT', 920.00, '2025-08-05 19:10:00', 3, 2, 1, '测试用户2', '13900139002', 'test2@example.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '超时待支付订单2'),
('ORD202508050003', 'PENDING_PAYMENT', 1100.00, '2025-08-05 19:15:00', 2, 3, 2, '测试用户3,测试用户4', '13900139003', 'test3@example.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '超时待支付订单3'),

-- 超时出票中订单（用于测试定时任务）
('ORD202508050004', 'TICKETING_IN_PROGRESS', 1350.00, '2025-08-05 19:30:00', 2, 4, 1, '测试用户5', '13900139004', 'test4@example.com', '2025-08-05 19:35:00', NULL, '2025-08-05 19:36:00', NULL, NULL, NULL, NULL, '超时出票中订单1'),
('ORD202508050005', 'TICKETING_IN_PROGRESS', 1680.00, '2025-08-05 19:40:00', 3, 5, 2, '测试用户6,测试用户7', '13900139005', 'test5@example.com', '2025-08-05 19:45:00', NULL, '2025-08-05 19:46:00', NULL, NULL, NULL, NULL, '超时出票中订单2'),

-- 长时间出票失败订单（用于测试定时任务）
('ORD202508040001', 'TICKETING_FAILED', 980.00, '2025-08-04 10:00:00', 2, 1, 1, '测试用户8', '13900139006', 'test6@example.com', '2025-08-04 10:05:00', NULL, '2025-08-04 10:06:00', NULL, '系统异常导致出票失败', NULL, NULL, '长时间出票失败订单1'),
('ORD202508040002', 'TICKETING_FAILED', 1250.00, '2025-08-04 11:00:00', 3, 2, 1, '测试用户9', '13900139007', 'test7@example.com', '2025-08-04 11:05:00', NULL, '2025-08-04 11:06:00', NULL, '航班取消导致出票失败', NULL, NULL, '长时间出票失败订单2'),

-- 正常流程的订单
('ORD202508050007', 'PAID', 1580.00, '2025-08-05 20:00:00', 2, 3, 1, '正常用户1', '13900139008', 'normal1@example.com', '2025-08-05 20:05:00', NULL, NULL, NULL, NULL, NULL, NULL, '正常已支付订单'),
('ORD202508050008', 'TICKETED', 2200.00, '2025-08-05 20:10:00', 3, 4, 2, '正常用户2,正常用户3', '13900139009', 'normal2@example.com', '2025-08-05 20:15:00', '2025-08-05 20:25:00', '2025-08-05 20:16:00', '2025-08-05 20:25:00', NULL, NULL, NULL, '正常已出票订单'),

-- 最近取消的订单
('ORD202508050009', 'CANCELLED', 890.00, '2025-08-05 20:20:00', 2, 1, 1, '取消用户1', '13900139010', 'cancel1@example.com', NULL, NULL, NULL, NULL, NULL, '2025-08-05 20:25:00', '用户改变行程', '最近取消的订单'),
('ORD202508050010', 'CANCELLED', 1450.00, '2025-08-05 20:30:00', 3, 2, 1, '取消用户2', '13900139011', 'cancel2@example.com', '2025-08-05 20:35:00', NULL, NULL, NULL, NULL, '2025-08-05 20:40:00', '支付超时自动取消', '支付后取消的订单');

-- =================================================================
-- 6. 数据验证和统计
-- =================================================================

-- 打印成功信息
SELECT '=== 数据库表创建成功！===' AS '状态';

-- 统计用户数据
SELECT '用户统计:' AS '类别';
SELECT role AS '角色', COUNT(*) AS '用户数量' FROM `app_users_qiaozhe` GROUP BY role;

-- 统计航班数据
SELECT '航班统计:' AS '类别';
SELECT status AS '航班状态', COUNT(*) AS '航班数量' FROM `flight_info_qiaozhe` GROUP BY status;

-- 统计订单数据
SELECT '订单统计:' AS '类别';
SELECT status AS '订单状态', COUNT(*) AS '订单数量' FROM `orders_qiaozhe` GROUP BY status;

-- 显示总体统计
SELECT 
  (SELECT COUNT(*) FROM `app_users_qiaozhe`) AS '用户总数',
  (SELECT COUNT(*) FROM `flight_info_qiaozhe`) AS '航班总数',
  (SELECT COUNT(*) FROM `orders_qiaozhe`) AS '订单总数';

SELECT '=== 所有表创建完成，测试数据插入成功！===' AS '完成状态';
