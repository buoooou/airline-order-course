package com.postion.airlineorderbackend.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=test-secret-key-for-testing-purposes-only",
    "jwt.expiration.ms=3600000"
})
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void testGenerateToken() {
        String username = "testuser";
        String role = "USER";
        
        String token = jwtUtil.generateToken(username, role);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGetUsernameFromToken() {
        String username = "testuser";
        String role = "USER";
        
        String token = jwtUtil.generateToken(username, role);
        String extractedUsername = jwtUtil.getUsernameFromToken(token);
        
        assertEquals(username, extractedUsername);
    }

    @Test
    void testGetRoleFromToken() {
        String username = "testuser";
        String role = "ADMIN";
        
        String token = jwtUtil.generateToken(username, role);
        String extractedRole = jwtUtil.getRoleFromToken(token);
        
        assertEquals(role, extractedRole);
    }

    @Test
    void testValidateToken() {
        String username = "testuser";
        String role = "USER";
        
        String token = jwtUtil.generateToken(username, role);
        boolean isValid = jwtUtil.validateToken(token, username);
        
        assertTrue(isValid);
    }

    @Test
    void testValidateTokenWithWrongUsername() {
        String username = "testuser";
        String role = "USER";
        
        String token = jwtUtil.generateToken(username, role);
        boolean isValid = jwtUtil.validateToken(token, "wronguser");
        
        assertFalse(isValid);
    }
} 