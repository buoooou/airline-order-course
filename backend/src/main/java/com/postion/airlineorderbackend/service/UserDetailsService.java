package com.postion.airlineorderbackend.service;

public interface UserDetailsService {
    org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username);
}
