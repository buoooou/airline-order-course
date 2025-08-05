package com.postion.airlineorderbackend.config;

import com.postion.airlineorderbackend.filter.JwtRequestFilter;
import com.postion.airlineorderbackend.security.CustomAuthenticationProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final UserDetailsService userDetailsService;

    /**
     * 配置Spring Security的过滤器链，用于定义安全规则和认证流程。
    */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // 禁用CSRF（REST API通常不需要） ，如果使用JWT可禁用CSRF
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 启用CORS配置
            // 配置默认规则
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/api/public/**", //开放性的API接口免认证
                    "/swagger-ui.html", "/swagger-ui/**",
                    "/v3/api-docs", "/v3/api-docs/**",
                    "/webjars/**", "/swagger-resources/**",
                    "/favicon.ico",
                    "/actuator/**"  // 显式放行Actuator监控路径
                ).permitAll()
                .anyRequest().authenticated()
            )
            // .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
            // 使用了JWT，会话管理配置为无状态
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    /**
     * 配置自定义的认证提供者。
     * 该方法创建一个 {@link CustomAuthenticationProvider} 实例，用于处理用户认证逻辑。
     * 依赖 {@link UserDetailsService} 和密码编码器 {@link PasswordEncoder} 进行认证。
     * @return 返回一个自定义的认证提供者实例。 
    */
    @Bean
    AuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider(userDetailsService, passwordEncoder());
    }

    /**
     * 配置并返回一个 AuthenticationManager 实例。
     * 该方法通过 AuthenticationConfiguration 获取 AuthenticationManager，并在初始化失败时记录错误日志。
     * @param authConfig 认证配置对象，用于获取 AuthenticationManager。
     * @return 返回配置的 AuthenticationManager 实例。
     * @throws Exception 如果初始化 AuthenticationManager 失败，抛出异常。 
    */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        try {
            return authConfig.getAuthenticationManager();
        } catch (Exception e) {
            log.error("Failed to initialize AuthenticationManager", e);
            throw e;
        }
    }

    /**
     * 配置跨域资源共享（CORS）的配置源。
     * 该方法创建一个全局的CORS配置，允许所有来源、指定HTTP方法、所有请求头，并支持凭据。
     * @return CorsConfigurationSource 返回一个基于URL的CORS配置源，适用于所有路径（/**）。 
    */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        // 如果设置为 true，则 allowedOrigins 不能为 "*"（必须明确指定域名）。
        configuration.setAllowCredentials(true);
        // configuration.setAllowCredentials(false);  // 明确禁用
        // configuration.setAllowedOrigins(List.of("*"));  // 允许所有来源
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 配置密码编码器，使用BCrypt算法对密码进行加密。
     * 这是一个Spring Bean，用于在应用启动时提供密码编码器的实例。
     *
     * @return 返回一个BCryptPasswordEncoder实例，用于密码加密和验证。
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
