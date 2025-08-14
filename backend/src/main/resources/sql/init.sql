-- =================================================================
--  Docker MySQL 初始化脚本
--  功能:
--  1. 创建 airline_order_db 数据库
--  2. 创建 app_users 表 (用户信息)
--  3. 创建 flight_info 表 (航班信息)
--  4. 创建 orders 表 (订单信息)
--  5. 创建 shedlock 表 (分布式锁)
-- =================================================================

-- 步骤 1: 创建数据库并切换
CREATE DATABASE IF NOT EXISTS `airline_order_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `airline_order_db`;

-- 步骤 2: 创建 app_users 表
-- 用于存储用户信息，对应 User.java 实体
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `flight_info`;
DROP TABLE IF EXISTS `app_users`;
DROP TABLE IF EXISTS `shedlock`;

CREATE TABLE `app_users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL UNIQUE,
  `email` VARCHAR(255) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `role` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 步骤 3: 创建 flight_info 表
-- 用于存储航班信息，对应 FlightInfo.java 实体
CREATE TABLE `flight_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `flight_number` VARCHAR(50) NOT NULL,
  `departure` VARCHAR(100) NOT NULL,
  `destination` VARCHAR(100) NOT NULL,
  `departure_time` DATETIME(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 步骤 4: 创建 orders 表
-- 用于存储订单信息，对应 Order.java 实体
CREATE TABLE `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_number` VARCHAR(255) NOT NULL,
  `status` ENUM('PENDING_PAYMENT', 'PAID', 'TICKETING_IN_PROGRESS', 'TICKETING_FAILED', 'TICKETED', 'CANCELLED') NOT NULL,
  `amount` DECIMAL(19, 2) NOT NULL,
  `creation_date` DATETIME(6) NOT NULL,
  `user_id` BIGINT NOT NULL,
  `flight_info_id` BIGINT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 步骤 5: 创建 shedlock 表
-- 用于ShedLock分布式锁管理
CREATE TABLE `shedlock` (
  `name` VARCHAR(64) NOT NULL,
  `lock_until` TIMESTAMP(3) NOT NULL,
  `locked_at` TIMESTAMP(3) NOT NULL,
  `locked_by` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 注意: 用户和订单数据现在由 Spring Boot 的 DataInitializer 自动创建
-- 不再需要手动插入测试数据