package com.postion.airlineorderbackend.config;

import com.postion.airlineorderbackend.common.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置类
 * <p>
 * 配置应用程序的安全设置，包括：
 * 1. HTTP请求的安全规则
 * 2. JWT认证过滤器
 * 3. 密码编码器
 * 4. 认证管理器
 * </p>
 * <p>
 * 采用无状态会话策略，使用JWT令牌进行用户认证
 * </p>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * 用户详情服务
     * <p>
     * 用于加载用户特定数据，实现用户认证
     * </p>
     */
    private final UserDetailsService userDetailsService;

    /**
     * JWT认证过滤器
     * <p>
     * 拦截HTTP请求，验证JWT令牌的有效性
     * </p>
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 配置安全过滤器链
     * <p>
     * 定义HTTP请求的安全规则：
     * - 禁用CSRF保护（适用于REST API）
     * - 配置URL访问权限
     * - 设置无状态会话策略
     * - 添加JWT认证过滤器
     * </p>
     * 
     * @param http HTTP安全配置对象
     * @return SecurityFilterChain 安全过滤器链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // 禁用 CSRF（Cross-Site Request Forgery，跨站请求伪造）防护机制
                .formLogin(form -> form.disable()) // 禁用表单登录
                .httpBasic(basic -> basic.disable()) // 禁用 HTTP Basic
                .authorizeHttpRequests(requests -> requests
                        // 1. 放行静态资源（关键！）
                        .antMatchers("/", "/index.html", "/css/**", "/js/**", "/img/**").permitAll()
                        // 允许认证相关接口无需认证即可访问
                        .antMatchers(Constants.ApiPath.AUTH_PREFIX + "/**").permitAll()
                        // 允许Swagger相关接口无需认证即可访问
                        .antMatchers(Constants.ApiPath.SWAGGER_UI, Constants.ApiPath.SWAGGER_API_DOCS).permitAll()
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated())
                // 配置无状态会话（不创建HTTP会话）
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 在UsernamePasswordAuthenticationFilter之前添加JWT认证过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * 配置密码编码器
     * <p>
     * 使用BCrypt强哈希算法进行密码加密
     * 该算法会自动加盐，提供良好的安全性
     * </p>
     * 
     * @return PasswordEncoder BCrypt密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置认证管理器
     * <p>
     * 设置用户详情服务和密码编码器，用于用户认证
     * </p>
     * 
     * @param http HTTP安全配置对象
     * @return AuthenticationManager 认证管理器
     * @throws Exception 配置异常
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }
}
