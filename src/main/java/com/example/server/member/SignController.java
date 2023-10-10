package com.example.server.member;

import com.example.server.member.dto.SignRequest;
import com.example.server.member.dto.SignResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SignController {

    private final SignService memberService;

    @PostMapping(value = "/login")
    public ResponseEntity<SignResponse> signin(@RequestBody SignRequest request) throws Exception {
        return new ResponseEntity<>(memberService.login(request), HttpStatus.OK);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<SignResponse> signup(@RequestBody SignRequest request) throws Exception {
        return new ResponseEntity<>(memberService.register(request), HttpStatus.OK);
    }

    @GetMapping("/{account}/exists")
    public ResponseEntity<SignResponse> checkAccountDuplicate(@PathVariable String account) {
        return new ResponseEntity<>(memberService.checkAccountDuplicate(account), HttpStatus.OK);
    }

}