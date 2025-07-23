package com.redis.redissessionmanagement.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
public class TokenCacheService {

    private final StringRedisTemplate redisTemplate;

    public TokenCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklistToken(String token, Date expirationDate) {
        Duration expiration = Duration.between(
                new Date().toInstant(), expirationDate.toInstant()
        );
        redisTemplate.opsForValue().set(token, "blacklisted", expiration);
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }
}
