package com.example.server.promise.controller;

import com.example.server.common.CommonResponse;
import com.example.server.promise.dto.PromiseRequestDto;
import com.example.server.promise.service.PromiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class PromiseController {

    private final PromiseService promiseService;

    // 약속 생성
    @PostMapping(value = "/promise/create")
    public ResponseEntity<CommonResponse> createPromise(@RequestBody HashMap<String, Object> request) throws Exception {
        return new ResponseEntity<>(promiseService.createPromise(request), HttpStatus.OK);
    }

    // 약속 목록 조회
    @GetMapping(value = "promise/getPromiseList")
    public ResponseEntity<CommonResponse> getPromiseList(@RequestParam(value = "option", defaultValue = "T") String option ,@RequestParam(value = "startDateTime") String startDateTime, @RequestParam(value = "endDateTime") String endDateTime, Authentication authentication) throws Exception {
        return new ResponseEntity<>(promiseService.getPromiseList(startDateTime, endDateTime, authentication), HttpStatus.OK);
    }

    // 약속 초대 요청 조회
    @GetMapping(value = "promise/getPromiseRequestList")
    public ResponseEntity<CommonResponse> getPromiseRequestList(Authentication authentication) throws Exception {
        return new ResponseEntity<>(promiseService.getPromiseRequestList(authentication), HttpStatus.OK);
    }

    // 약속 초대 요청 수락
    @PostMapping(value = "promise/acceptPromiseRequest")
    public ResponseEntity<CommonResponse> acceptPromiseRequest(@RequestBody PromiseRequestDto request, Authentication authentication) throws Exception {
        return new ResponseEntity<>(promiseService.acceptPromiseRequest(request, authentication), HttpStatus.OK);
    }

    // 약속 초대 요청 거절
    @PostMapping(value = "promise/rejectPromiseRequest")
    public ResponseEntity<CommonResponse> rejectPromiseRequest(@RequestBody PromiseRequestDto request, Authentication authentication) throws Exception {
        return new ResponseEntity<>(promiseService.rejectPromiseRequest(request, authentication), HttpStatus.OK);
    }
}
