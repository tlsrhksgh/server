package com.example.server.common.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static com.example.server.common.client.constants.RedisKeys.DEVICE_TOKEN_KEY;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisClient {
    private final ObjectMapper mapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public String getDeviceToken(String key) {
        return (String) redisTemplate.opsForHash().get(DEVICE_TOKEN_KEY.getKey(), key);
    }

    public void deviceTokenPut(String account, String deviceToken) {
        put(account, deviceToken);
    }

    private void put(String key, Object value) {
        try {
            redisTemplate.opsForHash().put(DEVICE_TOKEN_KEY.getKey(), key, mapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            log.error("RedisClient put exception: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
