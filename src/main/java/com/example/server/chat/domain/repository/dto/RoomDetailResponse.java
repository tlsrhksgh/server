package com.example.server.chat.domain.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class RoomDetailResponse {
    private String message;
    private String nickname;
    private String sendDate;
    private String memberImg;
    private Integer level;

    @QueryProjection
    public RoomDetailResponse(String message, String nickname, String sendDate, String memberImg, Integer level) {
        this.message = message;
        this.nickname = nickname;
        this.sendDate = sendDate;
        this.memberImg = memberImg;
        this.level = level;
    }
}
