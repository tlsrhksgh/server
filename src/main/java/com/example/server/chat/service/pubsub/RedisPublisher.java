package com.example.server.chat.service.pubsub;

import com.example.server.chat.service.dto.ChatMessageForm;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    public void sendMessage(String topic, ChatMessageForm messageForm) {
        redisTemplate.convertAndSend(topic, messageForm);
    }
}
