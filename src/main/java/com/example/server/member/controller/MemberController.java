package com.example.server.member.controller;

import com.example.server.common.CommonResponse;
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

    @GetMapping(value = "/info")
    public ResponseEntity<CommonResponse> signIn(Authentication authentication) throws Exception {
        return new ResponseEntity<>(memberService.getInfo(authentication), HttpStatus.OK);
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<CommonResponse> updateUserInfo(
            @ModelAttribute UpdateRequest request,
            Authentication authentication) {
        System.out.println(request.getNickname() + request.getPassword() + request.getImg());
        return ResponseEntity.ok(memberService.updateMember(request, authentication));
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<CommonResponse> withdrawMember(Authentication authentication) {
        return ResponseEntity.ok(memberService.deleteMember(authentication));
    }
}
