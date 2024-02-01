package com.example.server.member.dto;

import lombok.Getter;

@Getter
public class OAuthLoginRequest {
    private String provider;
    private String accessToken;
}
