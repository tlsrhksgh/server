package com.example.server.chat.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisTopicType {
    CHATTING("chatting");

    private final String type;
}
