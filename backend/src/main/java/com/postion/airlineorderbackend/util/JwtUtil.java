package com.postion.airlineorderbackend.util;

import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.postion.airlineorderbackend.dto.UserDto;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;

/**
 * Utility for JWT.
 */
@Component
public class JwtUtil {
  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration.ms}")
  private Long expiration;

  private SecretKey secretKey;

  @PostConstruct
  private void init() {
    secretKey = Keys.hmacShaKeyFor(secret.getBytes());
  }

  public String genToken(UserDto userInfo) {
    return Jwts.builder()
        .subject(userInfo.getUsername())
        .claim("userInfo", userInfo)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(secretKey, SIG.HS256).compact();
  }

  public boolean validateToken(UserDto extractedUserInfo, UserDetails userDetails) {
    if (extractedUserInfo == null || userDetails == null) {
      return false;
    }
    try {
      return extractedUserInfo.getUsername().equals(userDetails.getUsername());
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  public boolean vlidateToken(String token, UserDetails userDetails) {
    try {
      if (userDetails == null) {
        return false;
      }

      JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
      boolean expired = jwtParser.parseSignedClaims(token).getPayload().getExpiration().before(new Date());
      if (expired) {
        return false;
      }

      UserDto parsedUserInfo = claimsToUserInfo(jwtParser.parseSignedClaims(token).getPayload().get("userInfo"));
      return parsedUserInfo.getUsername().equals(userDetails.getUsername());
    } catch (JwtException | IllegalArgumentException e) {
      e.printStackTrace();
      return false;
    }
  }

  private UserDto claimsToUserInfo(Object claims) {
    @SuppressWarnings("rawtypes")
    Map claimsMap = (Map) claims;
    UserDto userInfo = new UserDto();
    if (claimsMap.containsKey("userid")) {
      userInfo.setUserid(Long.valueOf(claimsMap.get("userid").toString()));
    }
    if (claimsMap.containsKey("username")) {
      userInfo.setUsername(claimsMap.get("username").toString());
    }
    if (claimsMap.containsKey("role")) {
      userInfo.setRole(claimsMap.get("role").toString());
    }
    return userInfo;
  }

  public boolean isExpired(String token) {
    try {
      JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
      return jwtParser.parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    } catch (JwtException | IllegalArgumentException e) {
      e.printStackTrace();
      return false;
    }
  }

  public UserDto extractUserInfo(String token) {
    try {
      JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
      UserDto parsedUserInfo = claimsToUserInfo(jwtParser.parseSignedClaims(token).getPayload().get("userInfo"));
      return parsedUserInfo;
    } catch (JwtException | IllegalArgumentException e) {
      e.printStackTrace();
      return null;
    }

  }
}
