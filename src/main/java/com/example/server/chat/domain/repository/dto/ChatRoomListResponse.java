package com.example.server.chat.domain.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatRoomListResponse {
    private String title;
    private int total;
    private String lastMessage;
    private LocalDateTime lastSendDate;

    @QueryProjection
    public ChatRoomListResponse(String title, int total, String lastMessage, LocalDateTime lastSendDate) {
        this.title = title;
        this.total = total;
        this.lastMessage = lastMessage;
        this.lastSendDate = lastSendDate;
    }
}
