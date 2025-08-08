package com.position.airline_order_course.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class myTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "123456";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("===========" + encodedPassword);
    }
}
