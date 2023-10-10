package com.example.server.friend.controller;

import com.example.server.common.CommonResponse;
import com.example.server.friend.dto.FriendRequestDto;
import com.example.server.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // 친구 요청 보내기
    @PostMapping(value = "/friend/request")
    public ResponseEntity<CommonResponse> addRequest(@RequestBody FriendRequestDto request) throws Exception {
        return new ResponseEntity<>(friendService.addRequest(request), HttpStatus.OK);
    }

    // 친구 요청 목록 조회
    @GetMapping(value = "/friend/requestList/{account}")
    public ResponseEntity<CommonResponse> selectRequestList(Authentication authentication) throws Exception {
        return new ResponseEntity<>(friendService.selectRequestList(authentication), HttpStatus.OK);
    }

    // 친구 요청 수락
    @PostMapping(value = "/friend/acceptRequest")
    public ResponseEntity<CommonResponse> acceptRequest(@RequestBody FriendRequestDto request) throws Exception {
        return new ResponseEntity<>(friendService.acceptRequest(request), HttpStatus.OK);
    }

    // 친구 요청 거절
    @PostMapping(value = "/friend/rejectRequest")
    public ResponseEntity<CommonResponse> rejectRequest(@RequestBody FriendRequestDto request) throws Exception {
        return new ResponseEntity<>(friendService.rejectRequest(request), HttpStatus.OK);
    }

    // 친구 목록 조회
    @GetMapping(value = "/friend/getList")
    public ResponseEntity<CommonResponse> getFriendList(Authentication authentication) throws Exception {
        return new ResponseEntity<>(friendService.getFriendList(authentication), HttpStatus.OK);
    }
}
