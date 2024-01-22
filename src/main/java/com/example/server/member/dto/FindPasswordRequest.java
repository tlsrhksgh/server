package com.example.server.member.dto;

import lombok.Getter;

@Getter
public class FindPasswordRequest {
    private String account;
    private String password;

    public void setPassword(String password) {
        this.password = password;
    }
}
