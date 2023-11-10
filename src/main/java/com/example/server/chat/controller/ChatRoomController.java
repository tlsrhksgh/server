package com.example.server.chat.controller;

import com.example.server.chat.dto.CreateRoomForm;
import com.example.server.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/chat")
@RequiredArgsConstructor
@RestController
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/room")
    public ResponseEntity<Long> createChatRoom(@RequestBody CreateRoomForm form) {
        return ResponseEntity.ok(chatRoomService.createRoom(form));
    }


}
