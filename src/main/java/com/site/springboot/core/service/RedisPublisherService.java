package com.site.springboot.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * created by liush on 2024/5/28
 **/
@Service
public class RedisPublisherService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void publish(String channel, String message) {
        stringRedisTemplate.convertAndSend(channel, message);
    }

    public void publishDelay(String channel, String message, long delay) {
        stringRedisTemplate.opsForZSet().add(channel,
                message,
                System.currentTimeMillis() + delay);
    }
}

