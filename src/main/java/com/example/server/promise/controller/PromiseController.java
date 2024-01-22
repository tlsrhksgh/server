package com.example.server.promise.controller;

import com.example.server.common.CommonResponse;
import com.example.server.promise.dto.PromiseRequestDto;
import com.example.server.promise.service.PromiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PromiseController {

    private final PromiseService promiseService;

    // 약속 생성
    @PostMapping(value = "/promise/create")
    public ResponseEntity<CommonResponse> createPromise(@RequestBody HashMap<String, Object> request, Authentication authentication) throws Exception {
        return ResponseEntity.ok(promiseService.createPromise(request, authentication));
    }

    // 약속 목록 조회
    @GetMapping(value = "promise/getPromiseList")
    public ResponseEntity<CommonResponse> getPromiseList(@RequestParam(value = "completed") String completed ,@RequestParam(value = "startDateTime") String startDateTime, @RequestParam(value = "endDateTime") String endDateTime, Authentication authentication) throws Exception {
        return ResponseEntity.ok(promiseService.getPromiseList(startDateTime, endDateTime, completed,authentication));
    }

    // 약속 조회
    @GetMapping(value = "promise/getPromiseInfo")
    public ResponseEntity<CommonResponse> getPromiseInfo(@RequestParam(value = "promiseId") String promiseId, Authentication authentication) throws Exception {
        CommonResponse response = promiseService.getPromiseInfo(promiseId, authentication);

        return ResponseEntity.status(Integer.parseInt(response.getResultCode()))
                .body(response);
    }

    // 약속 탈퇴
    @PostMapping(value = "promise/exitPromise")
    public ResponseEntity<CommonResponse> exitPromise(@RequestBody Map<String, String> request, Authentication authentication) {
        CommonResponse response = promiseService.exitPromise(request.get("promiseId"), authentication);

        return ResponseEntity.status(Integer.parseInt(response.getResultCode()))
                .body(response);
    }

    // 약속 삭제
    @PostMapping(value = "promise/deletePromise")
    public ResponseEntity<CommonResponse> deletePromise(@RequestBody HashMap<String, String> request, Authentication authentication) throws Exception {
        CommonResponse response = promiseService.deletePromise(request, authentication);

        return ResponseEntity.status(Integer.parseInt(response.getResultCode()))
                .body(response);
    }

    // 약속 초대 요청 조회
    @GetMapping(value = "promise/getPromiseRequestList")
    public ResponseEntity<CommonResponse> getPromiseRequestList(Authentication authentication) throws Exception {
        return ResponseEntity.ok(promiseService.getPromiseRequestList(authentication));
    }

    // 약속 초대 요청 수락
    @PostMapping(value = "promise/acceptPromiseRequest")
    public ResponseEntity<CommonResponse> acceptPromiseRequest(@RequestBody PromiseRequestDto request, Authentication authentication) throws Exception {
        CommonResponse response = promiseService.acceptPromiseRequest(request, authentication);

        return ResponseEntity.status(Integer.parseInt(response.getResultCode()))
                .body(response);
    }

    // 약속 초대 요청 거절
    @PostMapping(value = "promise/rejectPromiseRequest")
    public ResponseEntity<CommonResponse> rejectPromiseRequest(@RequestBody PromiseRequestDto request, Authentication authentication) throws Exception {
        CommonResponse response = promiseService.rejectPromiseRequest(request, authentication);

        return ResponseEntity.status(Integer.parseInt(response.getResultCode()))
                .body(response);
    }

    // 약속 초대
    @PostMapping(value = "promise/inviteFriend")
    public ResponseEntity<CommonResponse> inviteFriend(@RequestBody HashMap<String, Object> request) throws Exception {
        CommonResponse response = promiseService.inviteFriend(request);

        return ResponseEntity.status(Integer.parseInt(response.getResultCode()))
                .body(response);
    }

    @PostMapping(value = "promise/editPromise")
    public ResponseEntity<CommonResponse> editPromise(@RequestBody Map<String, String> request, Authentication authentication) throws Exception {
        return ResponseEntity.ok(promiseService.editPromise(request, authentication));
    }

    @PostMapping(value = "promise/result")
    public ResponseEntity<CommonResponse> result(@RequestBody Map<String, Object> request, Authentication authentication) throws Exception {
        return ResponseEntity.ok(promiseService.result(request, authentication));
    }
}
