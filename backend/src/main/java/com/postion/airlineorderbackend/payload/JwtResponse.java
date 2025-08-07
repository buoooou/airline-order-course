package com.postion.airlineorderbackend.payload;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;

import com.postion.airlineorderbackend.dto.LoginUserDetails;

public class JwtResponse {

    private String token;
    private String type = "Bearer";

    private Long userId;
    private String username;
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, LoginUserDetails userDetails) {
        this.token = accessToken;
        this.userId = userDetails.getId();
        this.username = userDetails.getUsername();
        this.roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    public JwtResponse(String accessToken) {
        this.token = accessToken;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }



    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles(){
        return roles;
    }
    public void setRoles(List<String> roles){
        this.roles = roles;
    }
}  