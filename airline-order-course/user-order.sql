--------------------------users表-------------------------------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID 自动递增主键',
    `username` VARCHAR(255) NOT NULL UNIQUE COMMENT '用户名 不为空且唯一',
    `password` VARCHAR(255) NOT NULL COMMENT '密码 不为空',
    `role` VARCHAR(50) NOT NULL COMMENT '用户角色'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
--------------------------orders表-------------------------------------------------
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
        2
    ) --------------------------select orders表----------------------------------
select *
from orders