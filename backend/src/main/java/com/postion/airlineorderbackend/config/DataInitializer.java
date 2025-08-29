package com.postion.airlineorderbackend.config;

import com.postion.airlineorderbackend.model.Order;
import com.postion.airlineorderbackend.model.OrderStatus;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repo.OrderRepository;
import com.postion.airlineorderbackend.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepository, OrderRepository orderRepository, PasswordEncoder encoder) {
        return args -> {
            if (!userRepository.findByUsername("admin").isPresent()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                userRepository.save(admin);

                User u = new User();
                u.setUsername("user");
                u.setPassword(encoder.encode("user123"));
                u.setRole("ROLE_USER");
                userRepository.save(u);

                Order demo = new Order();
                demo.setOrderNumber("ORD-" + System.currentTimeMillis());
                demo.setStatus(OrderStatus.PENDING_PAYMENT);
                demo.setAmount(new BigDecimal("999.00"));
                demo.setCreationDate(LocalDateTime.now());
                demo.setUser(admin);
                orderRepository.save(demo);
            }
        };
    }
}
