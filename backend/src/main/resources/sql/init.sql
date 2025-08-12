-- =================================================================
--  Docker MySQL 初始化脚本
--  功能:
--  1. 创建 airline_order_db 数据库
--  2. 创建所有表 (app_users, flight_info, seat_detail, orders)
--  3. 插入完整的测试数据
-- =================================================================

-- 步骤 1: 创建数据库并切换
CREATE DATABASE IF NOT EXISTS `airline_order_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `airline_order_db`;

-- 步骤 2: 创建表
-- 为确保幂等性，先删除已存在的表（注意外键依赖顺序）
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `seat_detail`;
DROP TABLE IF EXISTS `flight_info`;
DROP TABLE IF EXISTS `app_users`;

-- 创建 app_users 表
CREATE TABLE `app_users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `role` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建 flight_info 表
CREATE TABLE `flight_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `flight_number` VARCHAR(20),
  `departure_airport_code` VARCHAR(10),
  `departure_airport_name` VARCHAR(100),
  `arrival_airport_code` VARCHAR(10),
  `arrival_airport_name` VARCHAR(100),
  `departure_time` DATETIME,
  `arrival_time` DATETIME,
  `flight_duration` INT
);

-- 创建 seat_detail 表
CREATE TABLE `seat_detail` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `flight_id` BIGINT NOT NULL,
  `seat_number` VARCHAR(10) NOT NULL,
  `seat_type` ENUM('ECONOMY', 'BUSINESS', 'FIRST_CLASS') NOT NULL,
  `seat_status` ENUM('AVAILABLE', 'RESERVED', 'OCCUPIED') NOT NULL DEFAULT 'AVAILABLE',
  `price` DECIMAL(10, 2) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_seat_detail_flight_id` FOREIGN KEY (`flight_id`) REFERENCES `flight_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建 orders 表
CREATE TABLE `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_number` VARCHAR(255) NOT NULL,
  `status` ENUM('PENDING_PAYMENT', 'PAID', 'TICKETING_IN_PROGRESS', 'TICKETING_FAILED', 'TICKETED', 'CANCELLED') NOT NULL,
  `amount` DECIMAL(19, 2) NOT NULL,
  `creation_date` DATETIME(6) NOT NULL,
  `user_id` BIGINT NOT NULL,
  `flight_id` BIGINT NOT NULL,
  `seat_number` Varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_orders_user_id` FOREIGN KEY (`user_id`) REFERENCES `app_users` (`id`),
  CONSTRAINT `fk_orders_flight_id` FOREIGN KEY (`flight_id`) REFERENCES `flight_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =================================================================
-- 步骤 3: 插入测试数据
-- =================================================================

-- 1. 插入用户测试数据
-- 密码原文均为 'password'，BCrypt加密后的哈希值
INSERT INTO `app_users` (`id`, `username`, `password`, `role`) VALUES
(1, 'admin', '$2a$10$hJ/pfq0k2alfmFB.E5L5JOoEr.bDRpBEK20DFMLs73yGrwzHNDR/S', 'ADMIN'),
(2, 'user1', '$2a$10$hJ/pfq0k2alfmFB.E5L5JOoEr.bDRpBEK20DFMLs73yGrwzHNDR/S', 'USER'),
(3, 'user2', '$2a$10$hJ/pfq0k2alfmFB.E5L5JOoEr.bDRpBEK20DFMLs73yGrwzHNDR/S', 'USER'),
(4, 'manager', '$2a$10$hJ/pfq0k2alfmFB.E5L5JOoEr.bDRpBEK20DFMLs73yGrwzHNDR/S', 'ADMIN'),
(5, 'customer1', '$2a$10$hJ/pfq0k2alfmFB.E5L5JOoEr.bDRpBEK20DFMLs73yGrwzHNDR/S', 'USER'),
(6, 'customer2', '$2a$10$hJ/pfq0k2alfmFB.E5L5JOoEr.bDRpBEK20DFMLs73yGrwzHNDR/S', 'USER'),
(7, 'vip_user', '$2a$10$hJ/pfq0k2alfmFB.E5L5JOoEr.bDRpBEK20DFMLs73yGrwzHNDR/S', 'USER'),
(8, 'test_user', '$2a$10$hJ/pfq0k2alfmFB.E5L5JOoEr.bDRpBEK20DFMLs73yGrwzHNDR/S', 'USER');

-- 2. 插入航班信息测试数据
INSERT INTO `flight_info` (`id`, `flight_number`, `departure_airport_code`, `departure_airport_name`, `arrival_airport_code`, `arrival_airport_name`, `departure_time`, `arrival_time`, `flight_duration`) VALUES
-- 国内航班
(1, 'CA1234', 'PEK', '北京首都国际机场', 'SHA', '上海虹桥国际机场', '2024-02-01 08:00:00', '2024-02-01 10:30:00', 150),
(2, 'MU5678', 'SHA', '上海浦东国际机场', 'CAN', '广州白云国际机场', '2024-02-01 14:00:00', '2024-02-01 16:45:00', 165),
(3, 'CZ9876', 'CAN', '广州白云国际机场', 'SZX', '深圳宝安国际机场', '2024-02-01 18:00:00', '2024-02-01 19:15:00', 75),
(4, 'HU1357', 'CTU', '成都双流国际机场', 'PEK', '北京首都国际机场', '2024-02-02 09:30:00', '2024-02-02 12:45:00', 195),
(5, 'SC2468', 'XIY', '西安咸阳国际机场', 'KMG', '昆明长水国际机场', '2024-02-02 15:20:00', '2024-02-02 17:50:00', 150),
-- 国际航班
(6, 'CA981', 'PEK', '北京首都国际机场', 'LAX', '洛杉矶国际机场', '2024-02-03 13:30:00', '2024-02-03 09:45:00', 780),
(7, 'MU587', 'PVG', '上海浦东国际机场', 'NRT', '东京成田国际机场', '2024-02-03 10:15:00', '2024-02-03 14:30:00', 195),
(8, 'CZ303', 'CAN', '广州白云国际机场', 'BKK', '曼谷素万那普国际机场', '2024-02-04 08:45:00', '2024-02-04 11:30:00', 165),
(9, 'HU7931', 'PEK', '北京首都国际机场', 'CDG', '巴黎戴高乐机场', '2024-02-04 23:50:00', '2024-02-05 06:15:00', 665),
(10, 'CA125', 'PEK', '北京首都国际机场', 'FRA', '法兰克福机场', '2024-02-05 02:20:00', '2024-02-05 07:30:00', 610),
-- 更多国内短途航班
(11, 'FM9201', 'SHA', '上海虹桥国际机场', 'NKG', '南京禄口国际机场', '2024-02-05 07:00:00', '2024-02-05 08:20:00', 80),
(12, 'JD5432', 'PEK', '北京首都国际机场', 'TSN', '天津滨海国际机场', '2024-02-05 16:30:00', '2024-02-05 17:15:00', 45),
(13, 'GS7890', 'CAN', '广州白云国际机场', 'HAK', '海口美兰国际机场', '2024-02-06 11:00:00', '2024-02-06 12:30:00', 90),
(14, 'EU2345', 'CTU', '成都双流国际机场', 'CKG', '重庆江北国际机场', '2024-02-06 19:45:00', '2024-02-06 20:50:00', 65),
(15, 'OQ6789', 'KMG', '昆明长水国际机场', 'LJG', '丽江三义机场', '2024-02-07 13:15:00', '2024-02-07 14:05:00', 50);

-- 3. 插入订单测试数据（覆盖所有状态和场景）
INSERT INTO `orders` (`order_number`, `status`, `amount`, `creation_date`, `user_id`, `flight_id`, `seat_number`) VALUES
-- PENDING_PAYMENT 状态订单
('ORD-2024020100001', 'PENDING_PAYMENT', 1280.00, NOW() - INTERVAL 5 MINUTE, 2, 1, '12A'),
('ORD-2024020100002', 'PENDING_PAYMENT', 890.50, NOW() - INTERVAL 15 MINUTE, 3, 2, '8C'),
('ORD-2024020100003', 'PENDING_PAYMENT', 2150.00, NOW() - INTERVAL 25 MINUTE, 5, 6, '3F'),
('ORD-2024020100004', 'PENDING_PAYMENT', 450.00, NOW() - INTERVAL 35 MINUTE, 6, 11, '15B'),
-- PAID 状态订单
('ORD-2024020100005', 'PAID', 1280.00, NOW() - INTERVAL 2 HOUR, 2, 1, '12B'),
('ORD-2024020100006', 'PAID', 3200.00, NOW() - INTERVAL 4 HOUR, 4, 7, '5A'),
('ORD-2024020100007', 'PAID', 1650.00, NOW() - INTERVAL 6 HOUR, 7, 3, '20D'),
('ORD-2024020100008', 'PAID', 5800.00, NOW() - INTERVAL 1 DAY, 1, 9, '1A'),
-- TICKETING_IN_PROGRESS 状态订单
('ORD-2024020100009', 'TICKETING_IN_PROGRESS', 1280.00, NOW() - INTERVAL 30 MINUTE, 3, 1, '12C'),
('ORD-2024020100010', 'TICKETING_IN_PROGRESS', 2800.00, NOW() - INTERVAL 45 MINUTE, 5, 8, '7B'),
('ORD-2024020100011', 'TICKETING_IN_PROGRESS', 980.00, NOW() - INTERVAL 1 HOUR, 6, 4, '18F'),
-- TICKETING_FAILED 状态订单
('ORD-2024020100012', 'TICKETING_FAILED', 1280.00, NOW() - INTERVAL 2 HOUR, 2, 1, '12D'),
('ORD-2024020100013', 'TICKETING_FAILED', 4200.00, NOW() - INTERVAL 3 HOUR, 7, 10, '2C'),
('ORD-2024020100014', 'TICKETING_FAILED', 750.00, NOW() - INTERVAL 4 HOUR, 8, 13, '22A'),
-- TICKETED 状态订单（成功出票）
('ORD-2024020100015', 'TICKETED', 1280.00, NOW() - INTERVAL 1 DAY, 2, 1, '12E'),
('ORD-2024020100016', 'TICKETED', 890.50, NOW() - INTERVAL 2 DAY, 3, 2, '8D'),
('ORD-2024020100017', 'TICKETED', 3200.00, NOW() - INTERVAL 3 DAY, 4, 7, '5B'),
('ORD-2024020100018', 'TICKETED', 2150.00, NOW() - INTERVAL 4 DAY, 5, 6, '3G'),
('ORD-2024020100019', 'TICKETED', 1650.00, NOW() - INTERVAL 5 DAY, 6, 3, '20E'),
('ORD-2024020100020', 'TICKETED', 5800.00, NOW() - INTERVAL 6 DAY, 7, 9, '1B'),
('ORD-2024020100021', 'TICKETED', 2800.00, NOW() - INTERVAL 7 DAY, 8, 8, '7C'),
('ORD-2024020100022', 'TICKETED', 980.00, NOW() - INTERVAL 8 DAY, 1, 4, '18G'),
('ORD-2024020100023', 'TICKETED', 4200.00, NOW() - INTERVAL 9 DAY, 2, 10, '2D'),
('ORD-2024020100024', 'TICKETED', 380.00, NOW() - INTERVAL 10 DAY, 3, 12, '25A'),
-- CANCELLED 状态订单
('ORD-2024020100025', 'CANCELLED', 1280.00, NOW() - INTERVAL 2 DAY, 4, 1, '12F'),
('ORD-2024020100026', 'CANCELLED', 890.50, NOW() - INTERVAL 3 DAY, 5, 2, '8E'),
('ORD-2024020100027', 'CANCELLED', 2150.00, NOW() - INTERVAL 4 DAY, 6, 6, '3H'),
('ORD-2024020100028', 'CANCELLED', 750.00, NOW() - INTERVAL 5 DAY, 7, 13, '22B'),
('ORD-2024020100029', 'CANCELLED', 520.00, NOW() - INTERVAL 6 DAY, 8, 15, '16C');

-- =================================================================
-- 步骤 4: 数据验证查询
-- =================================================================

SELECT '=== 数据库和测试数据初始化成功 ===' AS '状态';

SELECT '用户数据统计' AS '类别', role AS '角色', COUNT(*) AS '数量' 
FROM `app_users` 
GROUP BY role;

SELECT '航班数据统计' AS '类别', 
       CASE 
           WHEN departure_airport_code IN ('PEK', 'SHA', 'CAN', 'CTU', 'XIY', 'KMG', 'NKG', 'TSN', 'HAK', 'CKG', 'LJG') 
                AND arrival_airport_code IN ('PEK', 'SHA', 'CAN', 'CTU', 'XIY', 'KMG', 'NKG', 'TSN', 'HAK', 'CKG', 'LJG', 'SZX')
           THEN '国内航班'
           ELSE '国际航班'
       END AS '航班类型',
       COUNT(*) AS '数量'
FROM `flight_info`
GROUP BY CASE 
           WHEN departure_airport_code IN ('PEK', 'SHA', 'CAN', 'CTU', 'XIY', 'KMG', 'NKG', 'TSN', 'HAK', 'CKG', 'LJG') 
                AND arrival_airport_code IN ('PEK', 'SHA', 'CAN', 'CTU', 'XIY', 'KMG', 'NKG', 'TSN', 'HAK', 'CKG', 'LJG', 'SZX')
           THEN '国内航班'
           ELSE '国际航班'
       END;

SELECT '订单数据统计' AS '类别', status AS '订单状态', COUNT(*) AS '数量', 
       CONCAT(FORMAT(SUM(amount), 2), ' 元') AS '总金额'
FROM `orders` 
GROUP BY status 
ORDER BY 
    CASE status
        WHEN 'PENDING_PAYMENT' THEN 1
        WHEN 'PAID' THEN 2
        WHEN 'TICKETING_IN_PROGRESS' THEN 3
        WHEN 'TICKETING_FAILED' THEN 4
        WHEN 'TICKETED' THEN 5
        WHEN 'CANCELLED' THEN 6
    END;

SELECT '总体统计' AS '类别',
       (SELECT COUNT(*) FROM `app_users`) AS '用户总数',
       (SELECT COUNT(*) FROM `flight_info`) AS '航班总数',
       (SELECT COUNT(*) FROM `orders`) AS '订单总数',
       CONCAT(FORMAT((SELECT SUM(amount) FROM `orders` WHERE status IN ('PAID', 'TICKETING_IN_PROGRESS', 'TICKETED')), 2), ' 元') AS '有效订单总金额';

-- 显示一些示例数据
SELECT '=== 示例数据预览 ===' AS '信息';

SELECT '最新5个订单' AS '类别';
SELECT o.order_number AS '订单号', o.status AS '状态', o.amount AS '金额', 
       u.username AS '用户', f.flight_number AS '航班号', o.seat_number AS '座位号'
FROM `orders` o
JOIN `app_users` u ON o.user_id = u.id
JOIN `flight_info` f ON o.flight_id = f.id
ORDER BY o.creation_date DESC
LIMIT 5;