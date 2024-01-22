package com.example.server.member.controller;

import com.example.server.common.CommonResponse;
import com.example.server.member.dto.FindPasswordRequest;
import com.example.server.member.dto.UpdateRequest;
import com.example.server.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/info")
    public ResponseEntity<CommonResponse> signIn(Authentication authentication) {
        return ResponseEntity.ok(memberService.getInfo(authentication));
    }

    @PostMapping("/find-password")
    public ResponseEntity<CommonResponse> findPassword(@RequestBody FindPasswordRequest request) {
        CommonResponse response = memberService.updateMemberPassword(request);

        if(!response.getResultCode().equals("200")) {
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getResultCode()));
        }

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<CommonResponse> updateUserInfo(
            @ModelAttribute UpdateRequest request,
            Authentication authentication) {
        CommonResponse response = memberService.updateMember(request, authentication);

        if(!response.getResultCode().equals("200")) {
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getResultCode()));
        }

        return ResponseEntity.ok(memberService.updateMember(request, authentication));
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<CommonResponse> withdrawMember(Authentication authentication) {
        return ResponseEntity.ok(memberService.deleteMember(authentication));
    }
}
