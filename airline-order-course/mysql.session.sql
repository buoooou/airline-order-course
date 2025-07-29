------------------users------------------------------------------
INSERT INTO `users` (`id`, `username`, `password`, `role`)
VALUES (1, 'admin', '11111111111', 'ADMIN'),
    (2, 'user', '22222222222', 'USER');
select *
from users
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
    )
select *
from orders