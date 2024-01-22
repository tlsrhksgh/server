package com.example.server.member.controller;

import com.example.server.common.CodeConst;
import com.example.server.common.CommonResponse;
import com.example.server.member.service.SignService;
import com.example.server.member.dto.SignRequest;
import com.example.server.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SignController {
    private final SignService signService;
    private final JwtProvider jwtProvider;

    @PostMapping(value = "/login")
    public ResponseEntity<CommonResponse> signin(@RequestBody SignRequest request) {
        CommonResponse response = signService.login(request);

        return ResponseEntity.status(Integer.parseInt(response.getResultCode()))
                .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<CommonResponse> refresh(@RequestBody Map<String, String> bodyJson) {
        String reIssuedAccessToken = jwtProvider.validateRefreshToken(bodyJson.get("refreshToken"));

        CommonResponse commonResponse;

        if(Objects.isNull(reIssuedAccessToken) || reIssuedAccessToken.equals("jwt error")) {
            commonResponse = CommonResponse.builder()
                    .resultCode(CodeConst.REQUIRED_LOGIN_CODE)
                    .resultMessage(CodeConst.REQUIRED_LOGIN_MESSAGE)
                    .build();
            return ResponseEntity.badRequest().body(commonResponse);
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("accessToken", reIssuedAccessToken);

        commonResponse = CommonResponse.builder()
                .resultCode(CodeConst.ACCESS_TOKEN_ISSUED_SUCCESS_CODE)
                .resultMessage(CodeConst.ACCESS_TOKEN_ISSUED_SUCCESS_MESSAGE)
                .data(resultMap)
                .build();

        return ResponseEntity.ok(commonResponse);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<CommonResponse> signup(@ModelAttribute SignRequest request) throws Exception {
        return ResponseEntity.ok(signService.register(request));
    }

    @PostMapping(value = "/login/oauth")
    public ResponseEntity<CommonResponse> oAuthSignInOrSignUp(@RequestBody SignRequest request) throws Exception {
        return ResponseEntity.ok(signService.register(request));
    }

    @GetMapping("/{account}/exists/account")
    public ResponseEntity<CommonResponse> checkAccountDuplicate(@PathVariable String account) {

        return ResponseEntity.ok(signService.checkAccountDuplicate(account));
    }

    @GetMapping("/{nickname}/exists/nickname")
    public ResponseEntity<CommonResponse> checkNickNameDuplicate(@PathVariable String nickname) {
        return ResponseEntity.ok(signService.checkNickNameDuplicate(nickname));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<CommonResponse> checkVerifyCode(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(signService.sendVerifyCode(request.get("account")));
    }
}