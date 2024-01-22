package com.example.server.friend.controller;

import com.example.server.common.CommonResponse;
import com.example.server.friend.dto.FriendRequestDto;
import com.example.server.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // 친구 요청 보내기
    @PostMapping(value = "/friend/request")
    public ResponseEntity<CommonResponse> addRequest(@RequestBody FriendRequestDto request, Authentication authentication) throws Exception {
        CommonResponse response = friendService.addRequest(request, authentication);

        return ResponseEntity.status(Integer.parseInt(response.getResultCode()))
                .body(response);
    }

    // 친구 요청 목록 조회
    @GetMapping(value = "/friend/requestList")
    public ResponseEntity<CommonResponse> selectRequestList(Authentication authentication) throws Exception {
        return ResponseEntity.ok(friendService.selectRequestList(authentication));
    }

    // 친구 요청 수락
    @PostMapping(value = "/friend/acceptRequest")
    public ResponseEntity<CommonResponse> acceptRequest(@RequestBody FriendRequestDto request, Authentication authentication) throws Exception {
        CommonResponse response = friendService.acceptRequest(request, authentication);

        return ResponseEntity.status(Integer.parseInt(response.getResultCode()))
                .body(response);
    }

    // 친구 요청 거절
    @PostMapping(value = "/friend/rejectRequest")
    public ResponseEntity<CommonResponse> rejectRequest(@RequestBody FriendRequestDto request, Authentication authentication) throws Exception {
        CommonResponse response = friendService.rejectRequest(request, authentication);

        return ResponseEntity.status(Integer.parseInt(response.getResultCode()))
                .body(response);
    }

    // 친구 목록 조회
    @GetMapping(value = "/friend/getList")
    public ResponseEntity<CommonResponse> getFriendList(Authentication authentication) throws Exception {
        return ResponseEntity.ok(friendService.getFriendList(authentication));
    }
}
