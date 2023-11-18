package com.example.server.member.controller;

import com.example.server.common.CommonResponse;
import com.example.server.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping(value = "/member/info")
    public ResponseEntity<CommonResponse> signin(Authentication authentication) throws Exception {
        return new ResponseEntity<>(memberService.getInfo(authentication), HttpStatus.OK);
    }
}
