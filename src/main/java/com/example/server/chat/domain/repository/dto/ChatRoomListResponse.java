package com.example.server.chat.domain.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ChatRoomListResponse {
    private Long roomId;
    private String title;
    private Long totalMember;
    private String promiseDate;
    private String lastSendDate;

    @QueryProjection
    public ChatRoomListResponse(Long roomId, String title, Long totalMember, String promiseDate, String lastSendDate) {
        this.roomId = roomId;
        this.title = title;
        this.totalMember = totalMember;
        this.promiseDate = promiseDate;
        this.lastSendDate = lastSendDate;
    }
}
