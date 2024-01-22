package com.example.server.chat.controller;

import com.example.server.chat.service.ChatRoomService;
import com.example.server.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/chat/room")
@RequiredArgsConstructor
@RestController
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping("/list")
    public ResponseEntity<CommonResponse> chatRoomList(Authentication authentication) {
        return ResponseEntity.ok(chatRoomService.findAllChatRoom(authentication.getName()));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<CommonResponse> chatRoomDetail(@PathVariable Long roomId, Authentication authentication) {
        CommonResponse response = chatRoomService.findChatRoomDetail(roomId, authentication.getName());

        return ResponseEntity.status(Integer.parseInt(response.getResultCode()))
                .body(response);
    }
}
