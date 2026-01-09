package com.yash.service;

import com.yash.dto.MobileNotificationRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class NotificationCacheService {

    private final RedisTemplate<String, String> redisTemplate;

    public NotificationCacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveNotification(String key, String otp) {
        redisTemplate.opsForValue()
                .set(key, otp, Duration.ofMinutes(1)); // TTL = 1 min
    }

    public String getNotification(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
