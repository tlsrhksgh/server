package com.example.server.member.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateRequest {
    private String nickname;
    private String password;
    private MultipartFile img;
    private boolean imgUpdate;
}
