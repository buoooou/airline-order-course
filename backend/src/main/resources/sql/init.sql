-- =================================================================
--  Docker PostgreSQL 初始化脚本
--  功能:
--  1. 创建 airline_order_db 数据库
--  2. 创建 app_users 表 (用户信息)
--  3. 创建 orders 表 (订单信息)
--  4. 插入测试用户和覆盖所有状态的测试订单
-- =================================================================

-- 步骤 1: 创建数据库
CREATE DATABASE "airline_order_db" WITH TEMPLATE template0 ENCODING 'UTF8';

-- 步骤 2: 创建 app_users 表
-- 用于存储用户信息，对应 User.java 实体
DROP TABLE IF EXISTS "orders";
DROP TABLE IF EXISTS "app_users";

CREATE TABLE "app_users" (
  "id" BIGSERIAL PRIMARY KEY,
  "username" VARCHAR(255) NOT NULL UNIQUE,
  "password" VARCHAR(255) NOT NULL,
  "role" VARCHAR(50) NOT NULL,
  "creation_date" TIMESTAMP NOT NULL,
  "update_date" TIMESTAMP NOT NULL
);

-- 步骤 3: 创建 orders 表
-- 用于存储订单信息，对应 Order.java 实体
CREATE TABLE "orders" (
  "id" BIGSERIAL PRIMARY KEY,
  "order_number" VARCHAR(255) NOT NULL,
  "status" VARCHAR(50) NOT NULL CHECK ("status" IN ('PENDING_PAYMENT', 'PAID', 'TICKETING_IN_PROGRESS', 'TICKETING_FAILED', 'TICKETED', 'CANCELLED')),
  "amount" DECIMAL(19, 2) NOT NULL,
  "creation_date" TIMESTAMP NOT NULL,
  "update_date" TIMESTAMP NOT NULL,
  "user_id" BIGINT NOT NULL,
  CONSTRAINT "fk_orders_user_id" FOREIGN KEY ("user_id") REFERENCES "app_users" ("id")
);


-- 步骤 4: 插入测试数据

-- 插入用户 (密码原文均为 'password')
-- 注意: 这里的哈希值是 BCrypt 加密后的示例，您的 Spring 应用可以识别
INSERT INTO "app_users" ("id", "username", "password", "role", "creation_date", "update_date") VALUES
(1, 'admin', '$2a$10$hJ/pfq0k2alfmFB.E5L5JOoEr.bDRpBEK20DFMLs73yGrwzHNDR/S', 'ADMIN', NOW(), NOW()),
(2, 'user', '$2a$10$hJ/pfq0k2alfmFB.E5L5JOoEr.bDRpBEK20DFMLs73yGrwzHNDR/S', 'USER', NOW(), NOW());

-- 插入覆盖所有场景的订单数据
INSERT INTO "orders" ("order_number", "status", "amount", "creation_date", "update_date", "user_id") VALUES
-- 订单 1 (admin): 已支付 -> 用于测试异步出票
('PAI-1A2B3C4D', 'PAID', 1250.75, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day', 1),

-- 订单 2 (admin): 已出票 (最终成功状态)
('TIC-2B3C4D5E', 'TICKETED', 3400.00, NOW() - INTERVAL '5 DAY', NOW() - INTERVAL '5 DAY', 1),

-- 订单 3 (admin): 出票失败 -> 用于测试“重试出票”
('TIC-3C4D5E6F', 'TICKETING_FAILED', 980.50, NOW() - INTERVAL '2 hour', NOW() - INTERVAL '2 hour', 1),

-- 订单 4 (admin): 支付超时 -> 用于测试定时任务自动取消 (30分钟前创建)
('PEN-4D5E6F7G', 'PENDING_PAYMENT', 550.00, NOW() - INTERVAL '30 minute', NOW() - INTERVAL '30 minute', 1),

-- 订单 5 (user): 待支付 (正常) -> 用于测试“立即支付” (5分钟前创建)
('PEN-5E6F7G8H', 'PENDING_PAYMENT', 888.00, NOW() - INTERVAL '5 minute', NOW() - INTERVAL '5 minute', 2),

-- 订单 6 (user): 已取消 (最终失败状态)
('CAN-6F7G8H9I', 'CANCELLED', 1100.20, NOW() - INTERVAL '2 day', NOW() - INTERVAL '2 day', 2),

-- 订单 7 (user): 出票中 -> 模拟中间状态，测试UI展示
('TIC-7G8H9I0J', 'TICKETING_IN_PROGRESS', 4321.00, NOW() - INTERVAL '10 minute', NOW() - INTERVAL '10 minute', 2);


-- 打印成功信息
SELECT '数据库和测试数据初始化成功！' AS "状态";
SELECT COUNT(*) AS "用户总数" FROM "app_users";
SELECT status, COUNT(*) AS "订单数量" FROM "orders" GROUP BY status;

SELECT * FROM "app_users";
SELECT * FROM "orders";