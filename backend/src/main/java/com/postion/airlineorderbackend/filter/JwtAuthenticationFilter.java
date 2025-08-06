package com.postion.airlineorderbackend.filter;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.postion.airlineorderbackend.dto.UserDto;
import com.postion.airlineorderbackend.util.JwtUtil;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtTokenUtil;

  private final UserDetailsService userDetailsService;

  public JwtAuthenticationFilter(JwtUtil jwtTokenUtil, UserDetailsService userDetailsService) {
    this.jwtTokenUtil = jwtTokenUtil;
    this.userDetailsService = userDetailsService;
  }

  private String extractJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String jwt = extractJwtFromRequest(request);
    if (!StringUtils.hasText(jwt)) {
      filterChain.doFilter(request, response);
      return;
    }

    UserDto userInfo = jwtTokenUtil.extractUserInfo(jwt);
    if (userInfo == null) {
      filterChain.doFilter(request, response);
      return;
    }

    boolean isExpired = jwtTokenUtil.isExpired(jwt);
    if (isExpired) {
      filterChain.doFilter(request, response);
      return;
    }

    UserDetails userDetails = userDetailsService.loadUserByUsername(userInfo.getUsername());
    if (!jwtTokenUtil.vlidateToken(jwt, userDetails)) {
      filterChain.doFilter(request, response);
      return;
    }

    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        userDetails, null, Collections.singletonList(new SimpleGrantedAuthority(userInfo.getRole())));
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    filterChain.doFilter(request, response);
  }

}
