package com.example.server.chat.service.dto;

import lombok.Getter;

@Getter
public class CreateRoomForm {
    private String sender;
    private String receiver;
}
