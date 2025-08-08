package com.position.airline_order_course.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

/*
 * 配置类（开启自动配置）
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        /*
         * 密码的加密与比对
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        /*
         * 自定义的认证提供者UserPwdAuthProvider来处理认证逻辑
         */
        @Bean
        public AuthenticationManager authenticationManager(
                        UserPwdAuthProvider userPasswordAuthenticationProvider) {
                return new ProviderManager(List.of(userPasswordAuthenticationProvider));
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                return http
                                // 关闭CSRF保护
                                .csrf(csrf -> csrf.disable())
                                // 配置URL请求的访问权限（放行公开接口）
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/api/auth/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/v3/api-docs/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                // 设置会话管理策略为无状态
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .build();
        }
}