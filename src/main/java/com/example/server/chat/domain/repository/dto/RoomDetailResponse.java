package com.example.server.chat.domain.repository.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class RoomDetailResponse {
    private List<String> message;
    private String sender;
    private List<String> receiver;
    private LocalDateTime sendDate;
}
