--------------------------创建orders表-------------------------------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID 自动递增主键',
    `order_number` VARCHAR(255) NOT NULL UNIQUE COMMENT '订单号 不为空且唯一',
    `status` ENUM(
        'PENDING_PAYMENT',
        'PAID',
        'TICKETING_IN_PROGRESS',
        'TICKETING_FAILED',
        'TICKETED',
        'CANCELLED'
    ) NOT NULL COMMENT '订单状态',
    `amount` VARCHAR(255) NOT NULL COMMENT '订单金额',
    `creation_date` DATETIME NOT NULL COMMENT '订单创建时间',
    `user_id` BIGINT COMMENT '外键 关联到users表的id字段',
    FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE
    SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
--------------------------insert into orders表----------------------------------
INSERT INTO `orders` (
        `order_number`,
        `status`,
        `amount`,
        `creation_date`,
        `user_id`
    )
VALUES (
        '123456',
        'PAID',
        333333,
        NOW() - INTERVAL 1 DAY,
        1
    ) --------------------------创建users表-------------------------------------------------
    DROP TABLE IF EXISTS `users`;
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    roles JSON NOT NULL
);
--------------------------创建user_roles表-------------------------------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    roles VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, roles),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
--------------------------insert into user表,user_roles表---------------------
INSERT INTO users (username, password, roles)
VALUES (
        'ADMIN',
        '$2a$10$ppdefP2rfhTvqm3QFVIiLOA2GU/TT.2lIf3ot/O2P/5B5pFJAGrIa',
        '["ADMIN", "USER"]'
    );
INSERT INTO user_roles (user_id, roles)
VALUES (1, 'ADMIN');
---------------------创建shedlock--------------------------------
CREATE TABLE shedlock(
    name VARCHAR(64),
    lock_until TIMESTAMP(3) NULL,
    locked_at TIMESTAMP(3) NULL,
    locked_by VARCHAR(255),
    PRIMARY KEY (name)
);
SELECT *
from orders