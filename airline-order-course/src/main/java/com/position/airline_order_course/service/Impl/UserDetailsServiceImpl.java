package com.position.airline_order_course.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.position.airline_order_course.model.User;
import com.position.airline_order_course.repo.UserRepository;

import lombok.RequiredArgsConstructor;

/*
 * 用户服务层实现类
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

        private final UserRepository userRepository;

        // 用于根据用户名加载用户信息
        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

                // 将List<String>roles转换为List<GrantedAuthority>
                List<GrantedAuthority> authorities = user.getRoles().stream()
                                .map(role -> "ROLE_" + role) // 添加前缀
                                .map(SimpleGrantedAuthority::new) // 转为GrantedAuthority
                                .collect(Collectors.toList());

                // 构建并返回标准的UserDetails对象
                return org.springframework.security.core.userdetails.User.builder()
                                .username(user.getUsername())
                                .password(user.getPassword())
                                .authorities(authorities)
                                .build();
        }
}
