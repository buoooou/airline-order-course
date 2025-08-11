package com.postion.airlineorderbackend.config;

import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 * 在应用启动时创建默认用户
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 检查数据库中是否已有用户数据
        long userCount = userRepository.count();
        
        if (userCount == 0) {
            System.out.println("数据库为空，创建默认用户...");
            
            // 创建默认管理员用户
            createUserIfNotExists("admin", "admin123", "ADMIN");
            
            // 创建默认普通用户
            createUserIfNotExists("user", "user123", "USER");
            
            // 创建测试用户
            createUserIfNotExists("test", "test123", "USER");
            
            System.out.println("=== 默认用户已创建 ===");
            System.out.println("管理员: admin/admin123");
            System.out.println("普通用户: user/user123");
            System.out.println("测试用户: test/test123");
            System.out.println("=========================");
        } else {
            System.out.println("数据库已有用户数据，跳过默认用户创建");
            System.out.println("当前用户数量: " + userCount);
        }
    }

    private void createUserIfNotExists(String username, String password, String role) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            userRepository.save(user);
            System.out.println("创建用户: " + username);
        }
    }
} 