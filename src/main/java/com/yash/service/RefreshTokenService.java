package com.yash.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final long REFRESH_EXPIRY = 7 * 24 * 60 * 60; // 7 days

    public void save(String token, String username) {
        redisTemplate.opsForValue()
                .set("refresh:" + token, username, REFRESH_EXPIRY, TimeUnit.SECONDS);
    }

    public String getUsername(String token) {

        return redisTemplate.opsForValue().get("refresh:" + token);
    }

    public void delete(String token) {
        redisTemplate.delete("refresh:" + token);
    }
}
