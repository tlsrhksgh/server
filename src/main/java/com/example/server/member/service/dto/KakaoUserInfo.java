package com.example.server.member.service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class KakaoUserInfo {
    String nickname;
    String email;
    String img;

    @Builder
    public KakaoUserInfo(String nickname, String email, String img) {
        this.nickname = nickname;
        this.email = email;
        this.img = img;
    }
}

