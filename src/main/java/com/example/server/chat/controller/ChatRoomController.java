package com.example.server.chat.controller;

import com.example.server.chat.domain.repository.dto.ChatRoomListResponse;
import com.example.server.chat.service.ChatRoomService;
import com.example.server.chat.service.dto.CreateRoomForm;
import com.example.server.chat.service.dto.RoomDetailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/chat")
@RequiredArgsConstructor
@RestController
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/room")
    public ResponseEntity<Long> createChatRoom(@RequestBody CreateRoomForm form) {
        return ResponseEntity.ok(chatRoomService.createRoom(form));
    }

    @GetMapping("/room/list/{account}")
    public ResponseEntity<List<ChatRoomListResponse>> findAllChatRoom(@PathVariable String account) {
        return ResponseEntity.ok(chatRoomService.findAllChatRoom(account));
    }

    @GetMapping("/room/{id}")
    public ResponseEntity<Void> chatRoomDetail(@PathVariable Long roomId,
                                               @RequestBody RoomDetailRequest request) {

        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<Void> deleteChatRoom() {

        return ResponseEntity.ok(null);
    }
}
