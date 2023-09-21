package com.example.server.friend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendRequestDto {

    private String requester;

    private String respondent;

    // 친구 요청 목록 조회
    private String account;

    // 친구 요청 수락
    private String requestId;
}
