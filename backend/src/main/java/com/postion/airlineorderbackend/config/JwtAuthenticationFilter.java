package com.postion.airlineorderbackend.config;

import com.postion.airlineorderbackend.service.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器
 * <p>
 * 该过滤器负责拦截所有HTTP请求，验证JWT令牌的有效性，
 * 并在验证成功后设置Spring Security的认证上下文
 * </p>
 * <p>
 * 过滤器工作流程：
 * 1. 从请求头中提取Authorization Bearer令牌
 * 2. 解析令牌获取用户名
 * 3. 验证令牌有效性
 * 4. 设置SecurityContextHolder认证信息
 * </p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * JWT工具类
     * <p>
     * 用于JWT令牌的生成、解析和验证
     * </p>
     */
    private final JwtUtil jwtUtil;

    /**
     * 用户详情服务
     * <p>
     * 根据用户名加载用户详情信息
     * </p>
     */
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * 执行JWT认证过滤
     * <p>
     * 该方法在每个HTTP请求时执行一次，完成JWT令牌的提取、验证和认证设置
     * </p>
     * 
     * @param request     HTTP请求对象
     * @param response    HTTP响应对象
     * @param filterChain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // 从Authorization头中提取JWT令牌
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                // 从令牌中提取用户名
                username = jwtUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                logger.error("无法获取JWT令牌");
            } catch (ExpiredJwtException e) {
                logger.error("JWT令牌已过期");
            } catch (MalformedJwtException e) {
                logger.error("无效的JWT令牌");
            }
        } else {
            logger.warn("JWT令牌格式不正确，未以Bearer开头");
        }

        // 验证令牌并设置认证上下文
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 加载用户详情
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 验证令牌有效性
            if (jwtUtil.validateToken(jwtToken, userDetails)) {
                // 创建认证令牌
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 设置SecurityContextHolder认证信息
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}