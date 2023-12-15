package com.example.server.chat.controller;

import com.example.server.chat.constants.RedisTopicType;
import com.example.server.chat.service.ChatService;
import com.example.server.chat.service.dto.ChatMessageForm;
import com.example.server.chat.service.pubsub.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatController {
    private final ChatService chatService;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisSubscriber redisSubscriber;

    @MessageMapping("/chat/message")
    public void message(ChatMessageForm form) {
        String channelTopic = RedisTopicType.CHATTING.getType() + form.getRoomId();
        redisMessageListenerContainer.addMessageListener(redisSubscriber, new ChannelTopic(channelTopic));
        chatService.sendMessage(channelTopic, form);
    }
}
