package com.yash.util;

import com.yash.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "THIS_IS_MY_256_BIT_SECRET_KEY_FOR_JWT_123456";
    private final Key key;

    public JwtUtil() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // 🔹 Generate token
    public String generateToken(User user, long expiry) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole().name()) // store role as String
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 🔹 Extract username
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // 🔹 Check expiry
    public boolean isExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // 🔹 Validate token
    public boolean isTokenValid(String token) {
        return !isExpired(token);
    }

    // 🔹 Core parser
    private Claims extractClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    // 🔹 Access token (15 min)
    public String generateAccessToken(User user){
        return generateToken(user, 15 * 60 * 1000);
    }

    // 🔹 Refresh token (7 days)
    public String generateRefreshToken(User user){
        return generateToken(user, 7L * 24 * 60 * 60 * 1000);
    }
}
