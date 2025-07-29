package airline.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    /**
     * 从配置文件读取 base64 或明文密钥
     */
    @Value("${jwt.secret}")
    private String secret;

    @Value("${spring.security.jwt.expiration:86400000}") // 默认 24h
    private long expirationMs;

    /**
     * 生成安全的 HS256 Key（jjwt 0.12.x 推荐方式）
     */
    private SecretKey getSigningKey() {
        // 长度必须 ≥ 256 位（32 字节），否则 HS256 会报错
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成 JWT
     */
    public String generateToken(String username, Map<String, String> extraClaims) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .claims(extraClaims)            // 自定义声明
                .subject(username)              // 用户名
                .issuedAt(now)                  // 签发时间
                .expiration(exp)                // 过期时间
                .signWith(getSigningKey())      // 签名算法 & 密钥
                .compact();
    }

    /**
     * 解析并验证 JWT，返回负载
     */
    public Claims parseToken(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(getSigningKey())    // 设置验证密钥
                .build()
                .parseSignedClaims(token)       // 解析 + 签名校验
                .getPayload();                  // 等价于 getBody()
    }

    /**
     * 从 token 中取出用户名
     */
    public String extractUsername(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 判断 token 是否有效（未过期 & 用户名匹配）
     */
    private boolean isTokenExpired(String token) {
        return parseToken(token).getExpiration().before(new Date());
    }
}