-- 初始化用户数据
-- 密码都是 "password"，使用BCrypt加密

-- 管理员用户
INSERT INTO app_users (username, email, password, role) VALUES 
('admin', 'admin@airline.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN');

-- 普通用户
INSERT INTO app_users (username, email, password, role) VALUES 
('user1', 'user1@airline.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER'),
('user2', 'user2@airline.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER');

-- 注意：密码 "password" 的BCrypt哈希值
-- 您可以使用在线BCrypt生成器生成其他密码的哈希值 