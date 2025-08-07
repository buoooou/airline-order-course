package com.postion.airlineorderbackend.config;

import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 只有当用户表为空时才初始化数据
        if (userRepository.count() == 0) {
            // 创建管理员用户
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);

            // 创建普通用户
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole("ROLE_USER");
            userRepository.save(user);

            System.out.println("初始化用户数据完成");
        }
    }
}