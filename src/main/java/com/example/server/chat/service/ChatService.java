package com.example.server.chat.service;

import com.example.server.chat.domain.model.entity.ChatMessage;
import com.example.server.chat.domain.model.entity.ChatRoom;
import com.example.server.chat.domain.repository.ChatMessageRepository;
import com.example.server.chat.domain.repository.ChatRoomRepository;
import com.example.server.chat.service.dto.ChatMessageForm;
import com.example.server.chat.service.pubsub.RedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RedisPublisher publisher;

    public void sendMessage(String channelTopic, ChatMessageForm form) {
        ChatRoom chatRoom = chatRoomRepository.findById(form.getRoomId())
                .orElse(null);

        if(Objects.isNull(chatRoom)) {
            throw new RuntimeException("존재하지 않는 채팅방이므로 메시지를 전송할 수 없습니다.");
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .message(form.getMessage())
                .author(form.getSenderNickname())
                .sentDate(form.getSendDate())
                .build();

        chatMessageRepository.save(chatMessage);

        publisher.sendMessage(channelTopic, form);
    }
}
