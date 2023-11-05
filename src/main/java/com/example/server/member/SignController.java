package com.example.server.member;

import com.example.server.common.CommonResponse;
import com.example.server.member.dto.SignRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SignController {

    private final SignService memberService;

    @PostMapping(value = "/login")
    public ResponseEntity<CommonResponse> signin(@RequestBody SignRequest request) throws Exception {
        return new ResponseEntity<>(memberService.login(request), HttpStatus.OK);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<CommonResponse> signup(@RequestBody SignRequest request) throws Exception {
        return new ResponseEntity<>(memberService.register(request), HttpStatus.OK);
    }

    @GetMapping("/{account}/exists/account")
    public ResponseEntity<CommonResponse> checkAccountDuplicate(@PathVariable String account) {
        return new ResponseEntity<>(memberService.checkAccountDuplicate(account), HttpStatus.OK);
    }

    @GetMapping("/{nickname}/exists/nickname")
    public ResponseEntity<CommonResponse> checkNickNameDuplicate(@PathVariable String nickname) {
        return new ResponseEntity<>(memberService.checkNickNameDuplicate(nickname), HttpStatus.OK);
    }
}