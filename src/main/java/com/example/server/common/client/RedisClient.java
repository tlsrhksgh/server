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

    public void tokenPut(String account, String token) {
        deviceTokenPut(account, token);
    }

    private void deviceTokenPut(String key, String deviceToken) {
        try {
            redisTemplate.opsForHash().put(DEVICE_TOKEN_KEY.getKey(), key, mapper.writeValueAsString(deviceToken));
        } catch (JsonProcessingException e) {
            log.error("RedisClient deviceTokenPut exception: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
