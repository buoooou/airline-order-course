package com.airline.security;

import com.airline.entity.User;
import com.airline.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public AuthenticationUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + usernameOrEmail))
                );

        if (user.getStatus() != User.Status.ACTIVE) {
            throw new UsernameNotFoundException("用户账户已被停用: " + usernameOrEmail);
        }

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + id));

        if (user.getStatus() != User.Status.ACTIVE) {
            throw new UsernameNotFoundException("用户账户已被停用: " + id);
        }

        return UserPrincipal.create(user);
    }
}