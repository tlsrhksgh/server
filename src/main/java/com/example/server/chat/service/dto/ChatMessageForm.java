package com.example.server.chat.service.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ChatMessageForm {
    private Long roomId;
    private String senderNickname;
    private String memberImg;
    private String message;
    private String sendDate;
}
