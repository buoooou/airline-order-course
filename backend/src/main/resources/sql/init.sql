-- =================================================================
--  Docker MySQL 初始化脚本
--  功能:
--  1. 创建 airline_order_db 数据库
--  2. 创建所有表 (app_users, flight_info, seat_detail, orders)
--  3. 插入完整的测试数据
-- =================================================================

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

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
(1, 'admin', '$2a$10$VCSwbfVcFUvgxvzndqJ5MutD8GoF06606MrBjQrNhs8x/VJrEB9aa', 'ADMIN'),
(2, 'user1', '$2a$10$VCSwbfVcFUvgxvzndqJ5MutD8GoF06606MrBjQrNhs8x/VJrEB9aa', 'USER'),
(3, 'user2', '$2a$10$VCSwbfVcFUvgxvzndqJ5MutD8GoF06606MrBjQrNhs8x/VJrEB9aa', 'USER'),
(4, 'manager', '$2a$10$VCSwbfVcFUvgxvzndqJ5MutD8GoF06606MrBjQrNhs8x/VJrEB9aa', 'ADMIN'),
(5, 'customer1', '$2a$10$VCSwbfVcFUvgxvzndqJ5MutD8GoF06606MrBjQrNhs8x/VJrEB9aa', 'USER'),
(6, 'customer2', '$2a$10$VCSwbfVcFUvgxvzndqJ5MutD8GoF06606MrBjQrNhs8x/VJrEB9aa', 'USER'),
(7, 'vip_user', '$2a$10$VCSwbfVcFUvgxvzndqJ5MutD8GoF06606MrBjQrNhs8x/VJrEB9aa', 'USER'),
(8, 'test_user', '$2a$10$VCSwbfVcFUvgxvzndqJ5MutD8GoF06606MrBjQrNhs8x/VJrEB9aa', 'USER');

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

-- 3. 插入座位详情测试数据
-- 为每个航班创建不同类型和价格的座位
-- 国内航班座位 (航班1-5和11-15)
-- 航班1: 北京-上海 (CA1234)
INSERT INTO `seat_detail` (`flight_id`, `seat_number`, `seat_type`, `seat_status`, `price`) VALUES
-- 经济舱
(1, '12A', 'ECONOMY', 'OCCUPIED', 1280.00),
(1, '12B', 'ECONOMY', 'OCCUPIED', 1280.00),
(1, '12C', 'ECONOMY', 'OCCUPIED', 1280.00),
(1, '12D', 'ECONOMY', 'OCCUPIED', 1280.00),
(1, '12E', 'ECONOMY', 'OCCUPIED', 1280.00),
(1, '12F', 'ECONOMY', 'OCCUPIED', 1280.00),
(1, '13A', 'ECONOMY', 'AVAILABLE', 1280.00),
(1, '13B', 'ECONOMY', 'AVAILABLE', 1280.00),
(1, '13C', 'ECONOMY', 'AVAILABLE', 1280.00),
-- 商务舱
(1, '5A', 'BUSINESS', 'AVAILABLE', 2580.00),
(1, '5B', 'BUSINESS', 'AVAILABLE', 2580.00),
(1, '5C', 'BUSINESS', 'AVAILABLE', 2580.00),
-- 头等舱
(1, '1A', 'FIRST_CLASS', 'AVAILABLE', 4800.00),
(1, '1B', 'FIRST_CLASS', 'AVAILABLE', 4800.00);

-- 航班2: 上海-广州 (MU5678)
INSERT INTO `seat_detail` (`flight_id`, `seat_number`, `seat_type`, `seat_status`, `price`) VALUES
-- 经济舱
(2, '8C', 'ECONOMY', 'OCCUPIED', 890.50),
(2, '8D', 'ECONOMY', 'OCCUPIED', 890.50),
(2, '8E', 'ECONOMY', 'OCCUPIED', 890.50),
(2, '9A', 'ECONOMY', 'AVAILABLE', 890.50),
(2, '9B', 'ECONOMY', 'AVAILABLE', 890.50),
(2, '9C', 'ECONOMY', 'AVAILABLE', 890.50),
-- 商务舱
(2, '4A', 'BUSINESS', 'AVAILABLE', 1780.00),
(2, '4B', 'BUSINESS', 'AVAILABLE', 1780.00),
-- 头等舱
(2, '1C', 'FIRST_CLASS', 'AVAILABLE', 3560.00);

-- 航班3: 广州-深圳 (CZ9876)
INSERT INTO `seat_detail` (`flight_id`, `seat_number`, `seat_type`, `seat_status`, `price`) VALUES
-- 经济舱
(3, '20D', 'ECONOMY', 'OCCUPIED', 550.00),
(3, '20E', 'ECONOMY', 'OCCUPIED', 550.00),
(3, '21A', 'ECONOMY', 'AVAILABLE', 550.00),
(3, '21B', 'ECONOMY', 'AVAILABLE', 550.00),
-- 商务舱
(3, '10A', 'BUSINESS', 'AVAILABLE', 1100.00),
(3, '10B', 'BUSINESS', 'AVAILABLE', 1100.00),
-- 头等舱
(3, '2A', 'FIRST_CLASS', 'AVAILABLE', 1650.00);

-- 航班4: 成都-北京 (HU1357)
INSERT INTO `seat_detail` (`flight_id`, `seat_number`, `seat_type`, `seat_status`, `price`) VALUES
-- 经济舱
(4, '18F', 'ECONOMY', 'OCCUPIED', 980.00),
(4, '18G', 'ECONOMY', 'OCCUPIED', 980.00),
(4, '19A', 'ECONOMY', 'AVAILABLE', 980.00),
(4, '19B', 'ECONOMY', 'AVAILABLE', 980.00),
-- 商务舱
(4, '8A', 'BUSINESS', 'AVAILABLE', 1960.00),
(4, '8B', 'BUSINESS', 'AVAILABLE', 1960.00),
-- 头等舱
(4, '2B', 'FIRST_CLASS', 'AVAILABLE', 2940.00);

-- 航班5: 西安-昆明 (SC2468)
INSERT INTO `seat_detail` (`flight_id`, `seat_number`, `seat_type`, `seat_status`, `price`) VALUES
-- 经济舱
(5, '15A', 'ECONOMY', 'AVAILABLE', 720.00),
(5, '15B', 'ECONOMY', 'AVAILABLE', 720.00),
-- 商务舱
(5, '6A', 'BUSINESS', 'AVAILABLE', 1440.00),
-- 头等舱
(5, '1D', 'FIRST_CLASS', 'AVAILABLE', 2160.00);

-- 国际航班座位 (航班6-10)
-- 航班6: 北京-洛杉矶 (CA981)
INSERT INTO `seat_detail` (`flight_id`, `seat_number`, `seat_type`, `seat_status`, `price`) VALUES
-- 经济舱
(6, '3F', 'ECONOMY', 'OCCUPIED', 2150.00),
(6, '3G', 'ECONOMY', 'OCCUPIED', 2150.00),
(6, '3H', 'ECONOMY', 'OCCUPIED', 2150.00),
(6, '4D', 'ECONOMY', 'AVAILABLE', 2150.00),
(6, '4E', 'ECONOMY', 'AVAILABLE', 2150.00),
-- 商务舱
(6, '2A', 'BUSINESS', 'AVAILABLE', 5800.00),
(6, '2B', 'BUSINESS', 'AVAILABLE', 5800.00),
-- 头等舱
(6, '1C', 'FIRST_CLASS', 'AVAILABLE', 9200.00);

-- 航班7: 上海-东京 (MU587)
INSERT INTO `seat_detail` (`flight_id`, `seat_number`, `seat_type`, `seat_status`, `price`) VALUES
-- 经济舱
(7, '5A', 'ECONOMY', 'OCCUPIED', 1600.00),
(7, '5B', 'ECONOMY', 'OCCUPIED', 1600.00),
(7, '6A', 'ECONOMY', 'AVAILABLE', 1600.00),
(7, '6B', 'ECONOMY', 'AVAILABLE', 1600.00),
-- 商务舱
(7, '3A', 'BUSINESS', 'AVAILABLE', 3200.00),
(7, '3B', 'BUSINESS', 'AVAILABLE', 3200.00),
-- 头等舱
(7, '1D', 'FIRST_CLASS', 'AVAILABLE', 6400.00);

-- 航班8: 广州-曼谷 (CZ303)
INSERT INTO `seat_detail` (`flight_id`, `seat_number`, `seat_type`, `seat_status`, `price`) VALUES
-- 经济舱
(8, '7B', 'ECONOMY', 'OCCUPIED', 1400.00),
(8, '7C', 'ECONOMY', 'OCCUPIED', 1400.00),
(8, '8D', 'ECONOMY', 'AVAILABLE', 1400.00),
(8, '8E', 'ECONOMY', 'AVAILABLE', 1400.00),
-- 商务舱
(8, '4C', 'BUSINESS', 'AVAILABLE', 2800.00),
(8, '4D', 'BUSINESS', 'AVAILABLE', 2800.00),
-- 头等舱
(8, '1E', 'FIRST_CLASS', 'AVAILABLE', 5600.00);

-- 航班9: 北京-巴黎 (HU7931)
INSERT INTO `seat_detail` (`flight_id`, `seat_number`, `seat_type`, `seat_status`, `price`) VALUES
-- 经济舱
(9, '1A', 'ECONOMY', 'OCCUPIED', 2900.00),
(9, '1B', 'ECONOMY', 'OCCUPIED', 2900.00),
(9, '2C', 'ECONOMY', 'AVAILABLE', 2900.00),
(9, '2D', 'ECONOMY', 'AVAILABLE', 2900.00),
-- 商务舱
(9, '1F', 'BUSINESS', 'AVAILABLE', 5800.00),
(9, '1G', 'BUSINESS', 'AVAILABLE', 5800.00),
-- 头等舱
(9, '1K', 'FIRST_CLASS', 'AVAILABLE', 11600.00);

-- 航班10: 北京-法兰克福 (CA125)
INSERT INTO `seat_detail` (`flight_id`, `seat_number`, `seat_type`, `seat_status`, `price`) VALUES
-- 经济舱
(10, '2C', 'ECONOMY', 'OCCUPIED', 2100.00),
(10, '2D', 'ECONOMY', 'OCCUPIED', 2100.00),
(10, '3E', 'ECONOMY', 'AVAILABLE', 2100.00),
(10, '3F', 'ECONOMY', 'AVAILABLE', 2100.00),
-- 商务舱
(10, '2G', 'BUSINESS', 'AVAILABLE', 4200.00),
(10, '2H', 'BUSINESS', 'AVAILABLE', 4200.00),
-- 头等舱
(10, '1J', 'FIRST_CLASS', 'AVAILABLE', 8400.00);

-- 短途国内航班座位 (航班11-15)
-- 航班11: 上海-南京 (FM9201)
INSERT INTO `seat_detail` (`flight_id`, `seat_number`, `seat_type`, `seat_status`, `price`) VALUES
-- 经济舱
(11, '15B', 'ECONOMY', 'OCCUPIED', 450.00),
(11, '16A', 'ECONOMY', 'AVAILABLE', 450.00),
(11, '16B', 'ECONOMY', 'AVAILABLE', 450.00),
-- 商务舱
(11, '5C', 'BUSINESS', 'AVAILABLE', 900.00);

-- 航班12: 北京-天津 (JD5432)
INSERT INTO `seat_detail` (`flight_id`, `seat_number`, `seat_type`, `seat_status`, `price`) VALUES
-- 经济舱
(12, '25A', 'ECONOMY', 'OCCUPIED', 380.00),
(12, '25B', 'ECONOMY', 'AVAILABLE', 380.00),
-- 商务舱
(12, '10A', 'BUSINESS', 'AVAILABLE', 760.00);

-- 航班13: 广州-海口 (GS7890)
INSERT INTO `seat_detail` (`flight_id`, `seat_number`, `seat_type`, `seat_status`, `price`) VALUES
-- 经济舱
(13, '22A', 'ECONOMY', 'OCCUPIED', 750.00),
(13, '22B', 'ECONOMY', 'OCCUPIED', 750.00),
(13, '23A', 'ECONOMY', 'AVAILABLE', 750.00),
-- 商务舱
(13, '8C', 'BUSINESS', 'AVAILABLE', 1500.00);

-- 航班14: 成都-重庆 (EU2345)
INSERT INTO `seat_detail` (`flight_id`, `seat_number`, `seat_type`, `seat_status`, `price`) VALUES
-- 经济舱
(14, '18A', 'ECONOMY', 'AVAILABLE', 420.00),
(14, '18B', 'ECONOMY', 'AVAILABLE', 420.00),
-- 商务舱
(14, '6D', 'BUSINESS', 'AVAILABLE', 840.00);

-- 航班15: 昆明-丽江 (OQ6789)
INSERT INTO `seat_detail` (`flight_id`, `seat_number`, `seat_type`, `seat_status`, `price`) VALUES
-- 经济舱
(15, '16C', 'ECONOMY', 'OCCUPIED', 520.00),
(15, '17A', 'ECONOMY', 'AVAILABLE', 520.00),
-- 商务舱
(15, '7E', 'BUSINESS', 'AVAILABLE', 1040.00);

-- 4. 插入订单测试数据（覆盖所有状态和场景）
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