
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
  "payment_method" VARCHAR(20),
  "payment_status" VARCHAR(20) DEFAULT 'UNPAID',
  "payment_time" TIMESTAMP,
  CONSTRAINT "fk_orders_user_id" FOREIGN KEY ("user_id") REFERENCES "app_users" ("id")
);
-- 步骤 4: 插入测试数据

-- 插入用户 (密码原文均为 'password')
-- 注意: 这里的哈希值是 BCrypt 加密后的示例，您的 Spring 应用可以识别
INSERT INTO "app_users" ("id", "username", "password", "role", "creation_date", "update_date") VALUES
(1, 'admin', '$2a$10$hJ/pfq0k2alfmFB.E5L5JOoEr.bDRpBEK20DFMLs73yGrwzHNDR/S', 'ADMIN', NOW(), NOW()),
(2, 'user', '$2a$10$hJ/pfq0k2alfmFB.E5L5JOoEr.bDRpBEK20DFMLs73yGrwzHNDR/S', 'USER', NOW(), NOW());

-- 插入20条覆盖所有场景的订单数据
INSERT INTO "orders" ("order_number", "status", "amount", "creation_date", "update_date", "user_id", "payment_method", "payment_status", "payment_time") VALUES
-- 订单 1-5 (admin): 不同状态的订单
('PAI-1A2B3C4D', 'PAID', 1250.75, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day', 1, 'CREDIT_CARD', 'PAID', NOW() - INTERVAL '1 day'),
('TIC-2B3C4D5E', 'TICKETED', 3400.00, NOW() - INTERVAL '5 DAY', NOW() - INTERVAL '5 DAY', 1, 'CREDIT_CARD', 'PAID', NOW() - INTERVAL '5 DAY'),
('TIC-3C4D5E6F', 'TICKETING_FAILED', 980.50, NOW() - INTERVAL '2 hour', NOW() - INTERVAL '2 hour', 1, 'CREDIT_CARD', 'FAILED', NULL),
('PEN-4D5E6F7G', 'PENDING_PAYMENT', 550.00, NOW() - INTERVAL '30 minute', NOW() - INTERVAL '30 minute', 1, NULL, 'UNPAID', NULL),
('CAN-5E6F7G8H', 'CANCELLED', 1200.00, NOW() - INTERVAL '3 day', NOW() - INTERVAL '3 day', 1, 'CREDIT_CARD', 'REFUNDED', NOW() - INTERVAL '3 day'),

-- 订单 6-10 (user): 不同状态的订单
('PEN-6F7G8H9I', 'PENDING_PAYMENT', 888.00, NOW() - INTERVAL '5 minute', NOW() - INTERVAL '5 minute', 2, NULL, 'UNPAID', NULL),
('TIC-7G8H9I0J', 'TICKETING_IN_PROGRESS', 4321.00, NOW() - INTERVAL '10 minute', NOW() - INTERVAL '10 minute', 2, 'CREDIT_CARD', 'PAID', NOW() - INTERVAL '10 minute'),
('PAI-8H9I0J1K', 'PAID', 2100.00, NOW() - INTERVAL '1 hour', NOW() - INTERVAL '1 hour', 2, 'CREDIT_CARD', 'PAID', NOW() - INTERVAL '1 hour'),
('TIC-9I0J1K2L', 'TICKETED', 1500.00, NOW() - INTERVAL '2 day', NOW() - INTERVAL '2 day', 2, 'CREDIT_CARD', 'PAID', NOW() - INTERVAL '2 day'),
('CAN-0J1K2L3M', 'CANCELLED', 950.00, NOW() - INTERVAL '4 day', NOW() - INTERVAL '4 day', 2, 'CREDIT_CARD', 'REFUNDED', NOW() - INTERVAL '4 day'),

-- 订单 11-15 (admin): 更多测试数据
('PAI-1K2L3M4N', 'PAID', 1800.00, NOW() - INTERVAL '6 hour', NOW() - INTERVAL '6 hour', 1, 'CREDIT_CARD', 'PAID', NOW() - INTERVAL '6 hour'),
('TIC-2L3M4N5O', 'TICKETED', 2900.00, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day', 1, 'CREDIT_CARD', 'PAID', NOW() - INTERVAL '1 day'),
('PEN-3M4N5O6P', 'PENDING_PAYMENT', 750.00, NOW() - INTERVAL '15 minute', NOW() - INTERVAL '15 minute', 1, NULL, 'UNPAID', NULL),
('TIC-4N5O6P7Q', 'TICKETING_IN_PROGRESS', 3200.00, NOW() - INTERVAL '20 minute', NOW() - INTERVAL '20 minute', 1, 'CREDIT_CARD', 'PAID', NOW() - INTERVAL '20 minute'),
('CAN-5O6P7Q8R', 'CANCELLED', 1100.00, NOW() - INTERVAL '3 day', NOW() - INTERVAL '3 day', 1, 'CREDIT_CARD', 'REFUNDED', NOW() - INTERVAL '3 day'),

-- 订单 16-20 (user): 更多测试数据
('PAI-6P7Q8R9S', 'PAID', 2400.00, NOW() - INTERVAL '2 hour', NOW() - INTERVAL '2 hour', 2, 'CREDIT_CARD', 'PAID', NOW() - INTERVAL '2 hour'),
('TIC-7Q8R9S0T', 'TICKETED', 1700.00, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day', 2, 'CREDIT_CARD', 'PAID', NOW() - INTERVAL '1 day'),
('PEN-8R9S0T1U', 'PENDING_PAYMENT', 850.00, NOW() - INTERVAL '25 minute', NOW() - INTERVAL '25 minute', 2, NULL, 'UNPAID', NULL),
('TIC-9S0T1U2V', 'TICKETING_IN_PROGRESS', 4100.00, NOW() - INTERVAL '30 minute', NOW() - INTERVAL '30 minute', 2, 'CREDIT_CARD', 'PAID', NOW() - INTERVAL '30 minute'),
('CAN-0T1U2V3W', 'CANCELLED', 1300.00, NOW() - INTERVAL '5 day', NOW() - INTERVAL '5 day', 2, 'CREDIT_CARD', 'REFUNDED', NOW() - INTERVAL '5 day');

-- 为每个订单添加订单项（每个订单1-3个订单项）
INSERT INTO "order_items" ("order_id", "flight_id", "passenger_id", "price", "quantity", "creation_date", "update_date") VALUES
(1, 1, 1, 1250.75, 1, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
(2, 1, 2, 1700.00, 2, NOW() - INTERVAL '5 DAY', NOW() - INTERVAL '5 DAY'),
(3, 1, 3, 980.50, 1, NOW() - INTERVAL '2 hour', NOW() - INTERVAL '2 hour'),
(4, 1, 4, 550.00, 1, NOW() - INTERVAL '30 minute', NOW() - INTERVAL '30 minute'),
(5, 1, 5, 1200.00, 1, NOW() - INTERVAL '3 day', NOW() - INTERVAL '3 day'),
(6, 1, 6, 888.00, 1, NOW() - INTERVAL '5 minute', NOW() - INTERVAL '5 minute'),
(7, 1, 7, 2160.50, 2, NOW() - INTERVAL '10 minute', NOW() - INTERVAL '10 minute'),
(8, 1, 8, 2100.00, 1, NOW() - INTERVAL '1 hour', NOW() - INTERVAL '1 hour'),
(9, 1, 9, 1500.00, 1, NOW() - INTERVAL '2 day', NOW() - INTERVAL '2 day'),
(10, 1, 10, 950.00, 1, NOW() - INTERVAL '4 day', NOW() - INTERVAL '4 day'),
(11, 1, 1, 1800.00, 1, NOW() - INTERVAL '6 hour', NOW() - INTERVAL '6 hour'),
(12, 1, 2, 1450.00, 2, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
(13, 1, 3, 750.00, 1, NOW() - INTERVAL '15 minute', NOW() - INTERVAL '15 minute'),
(14, 1, 4, 1600.00, 2, NOW() - INTERVAL '20 minute', NOW() - INTERVAL '20 minute'),
(15, 1, 5, 1100.00, 1, NOW() - INTERVAL '3 day', NOW() - INTERVAL '3 day'),
(16, 1, 6, 2400.00, 1, NOW() - INTERVAL '2 hour', NOW() - INTERVAL '2 hour'),
(17, 1, 7, 1700.00, 1, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
(18, 1, 8, 850.00, 1, NOW() - INTERVAL '25 minute', NOW() - INTERVAL '25 minute'),
(19, 1, 9, 2050.00, 2, NOW() - INTERVAL '30 minute', NOW() - INTERVAL '30 minute'),
(20, 1, 10, 1300.00, 1, NOW() - INTERVAL '5 day', NOW() - INTERVAL '5 day');

-- 补充更多航班数据（共5条航班）
INSERT INTO "flights" ("id", "flight_number", "departure_airport", "arrival_airport", "departure_time", "arrival_time", "airline_code", "status", "creation_date", "update_date") VALUES
(2, 'MU5678', 'SHA', 'PEK', NOW() + INTERVAL '2 day', NOW() + INTERVAL '2 day 2 hours', 'MU', 'SCHEDULED', NOW(), NOW()),
(3, 'CA4567', 'PEK', 'CAN', NOW() + INTERVAL '3 day', NOW() + INTERVAL '3 day 3 hours', 'CA', 'SCHEDULED', NOW(), NOW()),
(4, 'MU3456', 'CAN', 'SHA', NOW() + INTERVAL '4 day', NOW() + INTERVAL '4 day 1 hour', 'MU', 'SCHEDULED', NOW(), NOW()),
(5, 'CA2345', 'SHA', 'CAN', NOW() + INTERVAL '5 day', NOW() + INTERVAL '5 day 2 hours', 'CA', 'SCHEDULED', NOW(), NOW());

-- 补充更多乘客数据（共10条）
INSERT INTO "passengers" ("id", "user_id", "name", "id_type", "id_number", "phone", "email", "creation_date", "update_date") VALUES
(3, 1, '王五', '身份证', '110101199002021234', '13700137000', 'wangwu@example.com', NOW(), NOW()),
(4, 1, '赵六', '护照', 'E87654321', '13600136000', 'zhaoliu@example.com', NOW(), NOW()),
(5, 2, '钱七', '身份证', '110101199003031234', '13500135000', 'qianqi@example.com', NOW(), NOW()),
(6, 2, '孙八', '护照', 'E11223344', '13400134000', 'sunba@example.com', NOW(), NOW()),
(7, 1, '周九', '身份证', '110101199004041234', '13300133000', 'zhoujiu@example.com', NOW(), NOW()),
(8, 1, '吴十', '护照', 'E55667788', '13200132000', 'wushi@example.com', NOW(), NOW()),
(9, 2, '郑十一', '身份证', '110101199005051234', '13100131000', 'zhengshiyi@example.com', NOW(), NOW()),
(10, 2, '王十二', '护照', 'E99887766', '13000130000', 'wangshier@example.com', NOW(), NOW());

-- 为部分订单项添加机票数据（共15张机票）
INSERT INTO "tickets" ("ticket_number", "order_item_id", "passenger_id", "flight_id", "seat_number", "status", "issue_date", "creation_date", "update_date") VALUES
('2234567890123', 1, 1, 1, 'B2', 'ISSUED', NOW(), NOW(), NOW()),
('3234567890123', 2, 2, 1, 'C3', 'ISSUED', NOW(), NOW(), NOW()),
('4234567890123', 3, 3, 2, 'D4', 'ISSUED', NOW(), NOW(), NOW()),
('5234567890123', 4, 4, 2, 'E5', 'ISSUED', NOW(), NOW(), NOW()),
('6234567890123', 5, 5, 3, 'F6', 'ISSUED', NOW(), NOW(), NOW()),
('7234567890123', 6, 6, 3, 'G7', 'ISSUED', NOW(), NOW(), NOW()),
('8234567890123', 7, 7, 4, 'H8', 'ISSUED', NOW(), NOW(), NOW()),
('9234567890123', 8, 8, 4, 'I9', 'ISSUED', NOW(), NOW(), NOW()),
('1034567890123', 9, 9, 5, 'J10', 'ISSUED', NOW(), NOW(), NOW()),
('1134567890123', 10, 10, 5, 'K11', 'ISSUED', NOW(), NOW(), NOW()),
('1234567890124', 11, 1, 1, 'L12', 'ISSUED', NOW(), NOW(), NOW()),
('1234567890125', 12, 2, 2, 'M13', 'ISSUED', NOW(), NOW(), NOW()),
('1234567890126', 13, 3, 3, 'N14', 'ISSUED', NOW(), NOW(), NOW()),
('1234567890127', 14, 4, 4, 'O15', 'ISSUED', NOW(), NOW(), NOW()),
('1234567890128', 15, 5, 5, 'P16', 'ISSUED', NOW(), NOW(), NOW());


-- 打印成功信息
SELECT '数据库和测试数据初始化成功！' AS "状态";
SELECT COUNT(*) AS "用户总数" FROM "app_users";
SELECT status, COUNT(*) AS "订单数量" FROM "orders" GROUP BY status;

SELECT * FROM "app_users";
SELECT * FROM "orders";

-- 重置 orders 表的自增 ID 计数器
ALTER SEQUENCE "orders_id_seq" RESTART WITH 1;
ALTER SEQUENCE "order_items_id_seq" RESTART WITH 1;
ALTER SEQUENCE "tickets_id_seq" RESTART WITH 1;

-- 步骤 8: 补充乘客信息表
CREATE TABLE "passengers" (
  "id" BIGINT PRIMARY KEY,
  "user_id" BIGINT NOT NULL,
  "name" VARCHAR(100) NOT NULL,
  "id_type" VARCHAR(20) NOT NULL,
  "id_number" VARCHAR(50) NOT NULL,
  "phone" VARCHAR(20) NOT NULL,
  "email" VARCHAR(100),
  "creation_date" TIMESTAMP NOT NULL,
  "update_date" TIMESTAMP NOT NULL,
  CONSTRAINT "fk_passengers_user_id" FOREIGN KEY ("user_id") REFERENCES "app_users" ("id")
);

INSERT INTO "passengers" VALUES
(1, 1, '张三', '身份证', '110101199001011234', '13800138000', 'zhangsan@example.com', NOW(), NOW()),
(2, 1, '李四', '护照', 'E12345678', '13900139000', 'lisi@example.com', NOW(), NOW());

-- 步骤 9: 补充机场表
CREATE TABLE "airports" (
  "iata_code" VARCHAR(3) PRIMARY KEY,
  "name" VARCHAR(100) NOT NULL,
  "city" VARCHAR(50) NOT NULL,
  "country" VARCHAR(50) NOT NULL,
  "creation_date" TIMESTAMP NOT NULL,
  "update_date" TIMESTAMP NOT NULL
);

INSERT INTO "airports" VALUES
('PEK', '北京首都国际机场', '北京', '中国', NOW(), NOW()),
('SHA', '上海虹桥国际机场', '上海', '中国', NOW(), NOW()),
('CAN', '广州白云国际机场', '广州', '中国', NOW(), NOW());

-- 步骤 10: 补充航空公司表
CREATE TABLE "airlines" (
  "icao_code" VARCHAR(2) PRIMARY KEY,
  "name" VARCHAR(100) NOT NULL,
  "logo_url" VARCHAR(255),
  "creation_date" TIMESTAMP NOT NULL,
  "update_date" TIMESTAMP NOT NULL
);

INSERT INTO "airlines" VALUES
('CA', '中国国际航空', 'https://example.com/logos/ca.png', NOW(), NOW()),
('MU', '中国东方航空', 'https://example.com/logos/mu.png', NOW(), NOW());

-- 步骤 10.5: 补充订单项表
CREATE TABLE "order_items" (
  "id" BIGSERIAL PRIMARY KEY,
  "order_id" BIGINT NOT NULL,
  "flight_id" BIGINT NOT NULL,
  "passenger_id" BIGINT NOT NULL,
  "price" DECIMAL(19, 2) NOT NULL,
  "quantity" INT NOT NULL,
  "creation_date" TIMESTAMP NOT NULL,
  "update_date" TIMESTAMP NOT NULL,
  CONSTRAINT "fk_order_items_order_id" FOREIGN KEY ("order_id") REFERENCES "orders" ("id"),
  CONSTRAINT "fk_order_items_passenger_id" FOREIGN KEY ("passenger_id") REFERENCES "passengers" ("id")
);

-- 步骤 10.7: 补充航班表
CREATE TABLE "flights" (
  "id" BIGSERIAL PRIMARY KEY,
  "flight_number" VARCHAR(10) NOT NULL,
  "departure_airport" VARCHAR(3) NOT NULL,
  "arrival_airport" VARCHAR(3) NOT NULL,
  "departure_time" TIMESTAMP NOT NULL,
  "arrival_time" TIMESTAMP NOT NULL,
  "airline_code" VARCHAR(2) NOT NULL,
  "status" VARCHAR(20) NOT NULL CHECK ("status" IN ('SCHEDULED', 'DELAYED', 'CANCELLED', 'COMPLETED')),
  "creation_date" TIMESTAMP NOT NULL,
  "update_date" TIMESTAMP NOT NULL,
  CONSTRAINT "fk_flights_departure_airport" FOREIGN KEY ("departure_airport") REFERENCES "airports" ("iata_code"),
  CONSTRAINT "fk_flights_arrival_airport" FOREIGN KEY ("arrival_airport") REFERENCES "airports" ("iata_code"),
  CONSTRAINT "fk_flights_airline_code" FOREIGN KEY ("airline_code") REFERENCES "airlines" ("icao_code")
);

INSERT INTO "flights" VALUES
(1, 'CA123', 'PEK', 'SHA', NOW() + INTERVAL '1 day', NOW() + INTERVAL '1 day 2 hours', 'CA', 'SCHEDULED', NOW(), NOW());

-- 步骤 11: 补充机票表
CREATE TABLE "tickets" (
  "ticket_number" VARCHAR(13) PRIMARY KEY,
  "order_item_id" BIGINT NOT NULL,
  "passenger_id" BIGINT NOT NULL,
  "flight_id" BIGINT NOT NULL,
  "seat_number" VARCHAR(10) NOT NULL,
  "status" VARCHAR(20) NOT NULL CHECK ("status" IN ('ISSUED', 'USED', 'REFUNDED', 'VOID')),
  "issue_date" TIMESTAMP NOT NULL,
  "creation_date" TIMESTAMP NOT NULL,
  "update_date" TIMESTAMP NOT NULL,
  CONSTRAINT "fk_tickets_order_item_id" FOREIGN KEY ("order_item_id") REFERENCES "order_items" ("id"),
  CONSTRAINT "fk_tickets_passenger_id" FOREIGN KEY ("passenger_id") REFERENCES "passengers" ("id"),
  CONSTRAINT "fk_tickets_flight_id" FOREIGN KEY ("flight_id") REFERENCES "flights" ("id")
);

INSERT INTO "tickets" VALUES
('1234567890123', 1, 1, 1, 'A1', 'ISSUED', NOW(), NOW(), NOW());

-- 步骤 12: 补充退改签记录表
CREATE TABLE "refunds" (
  "id" BIGINT PRIMARY KEY,
  "original_order_id" BIGINT NOT NULL,
  "refund_type" VARCHAR(20) NOT NULL CHECK ("refund_type" IN ('REFUND', 'CHANGE', 'CANCEL')),
  "fee" DECIMAL(19, 2) NOT NULL,
  "status" VARCHAR(20) NOT NULL CHECK ("status" IN ('PENDING', 'PROCESSING', 'COMPLETED', 'REJECTED')),
  "reason" TEXT,
  "creation_date" TIMESTAMP NOT NULL,
  "update_date" TIMESTAMP NOT NULL,
  CONSTRAINT "fk_refunds_original_order_id" FOREIGN KEY ("original_order_id") REFERENCES "orders" ("id")
);

INSERT INTO "refunds" VALUES
(1, 1, 'REFUND', 200.00, 'COMPLETED', '行程变更', NOW(), NOW(), 1, NULL);