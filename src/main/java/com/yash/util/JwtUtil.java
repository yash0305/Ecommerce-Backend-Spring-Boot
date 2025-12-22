package com.yash.util;

import com.yash.enums.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import com.yash.entity.User;

import org.springframework.security.core.userdetails.UserDetails;

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

    public String generateToken(User user) {
    return Jwts.builder()
        .setSubject(user.getUsername())
        .claim("role", user.getRole())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
        .signWith(key)
        .compact();
}

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public Role extractRoleEnum(String token) {
        String role = extractRole(token);
        return Role.valueOf(role);
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
