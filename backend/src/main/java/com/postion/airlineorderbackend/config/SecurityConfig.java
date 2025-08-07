package com.postion.airlineorderbackend.config;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import com.postion.airlineorderbackend.utils.JwtAuthorizationFilter;
import java.util.Arrays;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @Autowired
    private AuthenticationEntryPoint unauthorizedHandler;

    // 创建BCryptPasswordEncoder注入容器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 登录时需要调用AuthenticationManager.authenticate执行一次校验
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // @Bean
	// public CorsConfigurationSource corsConfigurationSource() {
	//     CorsConfiguration config = new CorsConfiguration();
	//     config.setAllowedOrigins(List.of("*"));
	//     config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	//     config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
	//     config.setAllowCredentials(false);

	//     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	//     source.registerCorsConfiguration("/**", config);
	//     return source;
	// }

    // 配置SecurityFilterChain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 启用 CORS 并禁用 CSRF
        http
            // .cors(cors -> cors.configurationSource(request -> {
            //     CorsConfiguration config = new CorsConfiguration();
            //     // config.setAllowedOrigins(Collections.singletonList("http://13.239.30.175")); 
            //     // config.setAllowedOrigins(Collections.singletonList("*")); 
            //     config.addAllowedOriginPattern("*");
            //     config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            //     config.setAllowedHeaders(Collections.singletonList("*"));
            //     config.setAllowCredentials(false);
            //     return config;
            // }))
            .csrf(csrf -> csrf.disable());
        // 配置异常处理
        http.exceptionHandling(exception -> exception
            .authenticationEntryPoint(unauthorizedHandler)
        );
        
        // 配置请求的拦截方式
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 放行 OPTIONS 请求
            .requestMatchers("/api/auth/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/user/register").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/airports**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/flights/search").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/flights/**").permitAll()
            .anyRequest().authenticated()
        );
        
        // 禁用session
        http.sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        
        // 添加JWT过滤器
        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }


}    
