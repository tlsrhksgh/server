package com.example.server.member.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberUpdateRequest {
    private final String nickname;
    private final String img;
    private final String password;
}
