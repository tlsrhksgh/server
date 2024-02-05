package com.example.server.chat.service;

import com.example.server.chat.domain.model.entity.ChatMessage;
import com.example.server.chat.domain.model.entity.MemberChatRoom;
import com.example.server.chat.domain.repository.ChatMessageRepository;
import com.example.server.chat.domain.repository.custom.CustomMemberChatRoomRepository;
import com.example.server.chat.service.dto.ChatMessageForm;
import com.example.server.chat.service.pubsub.RedisPublisher;
import com.example.server.push.contatns.PushCategory;
import com.example.server.push.service.PushService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ChatService {
    private final CustomMemberChatRoomRepository customMemberChatRoomRepository;
    private final PushService pushService;
    private final ChatMessageRepository chatMessageRepository;
    private final RedisPublisher publisher;

    public void sendMessage(String channelTopic, ChatMessageForm form) {
        List<MemberChatRoom> memberChatRooms = customMemberChatRoomRepository
                .findParticipatedMembers(form.getRoomId());

        if(memberChatRooms.size() == 0) {
            throw new RuntimeException("존재하지 않는 채팅방이므로 메시지를 전송할 수 없습니다.");
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(memberChatRooms.get(0).getChatRoom())
                .message(form.getMessage())
                .author(form.getSenderNickname())
                .sentDate(form.getSendDate())
                .build();

        chatMessageRepository.save(chatMessage);

        publisher.sendMessage(channelTopic, form);

        memberChatRooms.stream()
                .filter(memberChatRoom -> !Objects.equals(memberChatRoom.getMember().getNickname(),
                        form.getSenderNickname()))
                .forEach(memberChatRoom ->
                pushService.makeAndSendPushNotification(
                        PushCategory.CHAT_MESSAGE_NOTIFICATION, memberChatRoom.getMember().getAccount(), form));
    }
}
