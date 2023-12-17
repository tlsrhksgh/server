package com.example.server.chat.service.pubsub;

import com.example.server.chat.service.dto.ChatMessageForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            ChatMessageForm chatMessage = objectMapper.readValue(message.getBody(), ChatMessageForm.class);
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
            log.info("보내는 사용자: {}", message.getBody());
            log.info("메시지 채널: {}", message.getChannel());
        } catch (IOException e) {
            log.error("메시지 수신 실패: {}" + e.getMessage());
        }
    }
}
