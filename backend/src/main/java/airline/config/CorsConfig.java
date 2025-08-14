package airline.config;

import airline.interceptor.LoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //定义拦截对象
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/auth/login")
                .excludePathPatterns("/login", // 前端静态资源
                        "/",
                        "/*.html",
                        "/*/*.html",
                        "/*.js",
                        "/*.css",
                        "/static/**",
                        "/assets/**",
                        "/images/**",
                        "/favicon.ico",
                        // Swagger-UI
                        "/swagger-ui/**",
                        "/v3/api-docs/**",   // 如果也用了 springdoc-openapi
                        "/swagger-resources/**",
                        "/api-docs/swagger-config",
                        "/api-docs");
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 所有路径
                .allowedOrigins("*")   // 允许所有来源
                .allowedMethods("*")   // 允许所有HTTP方法
                .allowedHeaders("*");  // 允许所有请求头
        // 注意: 使用通配符(*)时不能设置allowCredentials(true)
    }


}