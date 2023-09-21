package com.example.server.friend.controller;

import com.example.server.friend.dto.FriendRequestDto;
import com.example.server.friend.dto.FriendResponseDto;
import com.example.server.friend.service.FriendService;
import com.example.server.member.dto.SignResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // 친구 요청 보내기
    @PostMapping(value = "/friend/request")
    public ResponseEntity<FriendResponseDto> addRequest(@RequestBody FriendRequestDto request) throws Exception {
        return new ResponseEntity<>(friendService.addRequest(request), HttpStatus.OK);
    }

    // 친구 요청 목록 조회
    @GetMapping(value = "/friend/requestList/{account}")
    public ResponseEntity<FriendResponseDto> selectRequestList(@PathVariable String account) throws Exception {
        return new ResponseEntity<>(friendService.selectRequestList(account), HttpStatus.OK);
    }

    // 친구 요청 수락
    @PostMapping(value = "/friend/acceptRequest")
    public ResponseEntity<FriendResponseDto> acceptRequest(@RequestBody FriendRequestDto request) throws Exception {
        return new ResponseEntity<>(friendService.acceptRequest(request), HttpStatus.OK);
    }

    // 친구 요청 거절
    @PostMapping(value = "/friend/rejectRequest")
    public ResponseEntity<FriendResponseDto> rejectRequest(@RequestBody FriendRequestDto request) throws Exception {
        return new ResponseEntity<>(friendService.rejectRequest(request), HttpStatus.OK);
    }

    // 친구 목록 조회
    @GetMapping(value = "/friend/getList/{account}")
    public ResponseEntity<FriendResponseDto> getFriendList(@PathVariable String account) throws Exception {
        return new ResponseEntity<>(friendService.getFriendList(account), HttpStatus.OK);
    }
}
