package com.example.server.chat.controller;

import com.example.server.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/chat")
@RequiredArgsConstructor
@RestController
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/room")
    public ResponseEntity<Long> createRoom() {
        return ResponseEntity.ok(chatRoomService.createRoom());
    }
}
