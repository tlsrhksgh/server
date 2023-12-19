package com.example.server.member.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class UpdateRequest {
    private String nickname;
    private String password;
    private MultipartFile img;
    private boolean isImgUpdate;

    public void setPassword(String password) {
        this.password = password;
    }
}
