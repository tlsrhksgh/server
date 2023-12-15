package com.example.server.push.dto;

import lombok.Getter;

@Getter
public class PushRequestForm {
    private String title;
    private String subTitle;
    private String body;
}
