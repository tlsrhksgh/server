package com.example.server.chat.service.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatRoomInviteForm {
    private Long roomId;
    private List<String> memberNicknames;
}
