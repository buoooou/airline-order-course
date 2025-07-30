package com.postion.airlineorderbackend.service.impl;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.postion.airlineorderbackend.Exception.AirlineBusinessException;
import com.postion.airlineorderbackend.constants.Constants;
import com.postion.airlineorderbackend.model.User;
import com.postion.airlineorderbackend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AirlineBusinessException(HttpStatus.SERVICE_UNAVAILABLE.value(), Constants.MSG_USER_NOT_FOUND));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

}
