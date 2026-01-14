package com.yash.util;

import com.yash.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

    public String generateToken(User user, long expiry) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().setSigningKey(key)
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token){
        return Jwts.parser().setSigningKey(key)
                .parseClaimsJws(token).getBody();
    }

    public String generateAccessToken(User user){
        return generateToken(user, 15 * 60 * 1000);
//        return generateToken(user, 2 * 60 * 1000);

    }

    public String generateRefreshToken(User user)
    {
        return generateToken(user, 7L * 24 * 60 * 60 * 1000);
    }
}
