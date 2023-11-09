package com.example.server.chat.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CreateRoomForm {
    List<String> memberNicknames;
}
