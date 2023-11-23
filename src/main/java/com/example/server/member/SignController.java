package com.example.server.member;

import com.example.server.common.CommonResponse;
import com.example.server.member.dto.SignRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SignController {
    private final SignService signService;

    @PostMapping(value = "/login")
    public ResponseEntity<CommonResponse> signin(@RequestBody SignRequest request) throws Exception {
        return new ResponseEntity<>(signService.login(request), HttpStatus.OK);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<CommonResponse> signup(@RequestBody SignRequest request) throws Exception {
        return new ResponseEntity<>(signService.register(request), HttpStatus.OK);
    }

    @GetMapping("/{account}/exists/account")
    public ResponseEntity<CommonResponse> checkAccountDuplicate(@PathVariable String account) {
        return new ResponseEntity<>(signService.checkAccountDuplicate(account), HttpStatus.OK);
    }

    @GetMapping("/{nickname}/exists/nickname")
    public ResponseEntity<CommonResponse> checkNickNameDuplicate(@PathVariable String nickname) {
        return new ResponseEntity<>(signService.checkNickNameDuplicate(nickname), HttpStatus.OK);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<CommonResponse> checkVerifyCode(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(signService.sendVerifyCode(request.get("account")));
    }
}