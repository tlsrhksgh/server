package com.example.server.member.service;

import com.example.server.common.CodeConst;
import com.example.server.common.CommonResponse;
import com.example.server.common.client.RedisClient;
import com.example.server.file.FileService;
import com.example.server.member.component.MailComponent;
import com.example.server.member.dto.SignRequest;
import com.example.server.member.repository.Authority;
import com.example.server.member.repository.CustomMemberRepository;
import com.example.server.member.repository.Member;
import com.example.server.member.repository.MemberRepository;
import com.example.server.security.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SignService {
    private final MemberRepository memberRepository;
    private final CustomMemberRepository customMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final MailComponent mailComponent;
    private final ObjectMapper mapper;
    private final RedisClient redisClient;
    private final FileService fileService;

    // 로그인
    public CommonResponse login(SignRequest request) {
        Member member = customMemberRepository.findMemberByAccount(request.getAccount());

        if(Objects.isNull(member)) {
            return CommonResponse.builder()
                    .resultCode(CodeConst.LOGIN_FAIL_ID_CODE)
                    .resultMessage(CodeConst.LOGIN_FAIL_ID_MESSAGE)
                    .build();
        }

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            return CommonResponse.builder()
                    .resultCode(CodeConst.LOGIN_FAIL_PW_CODE)
                    .resultMessage(CodeConst.LOGIN_FAIL_PW_MESSAGE)
                    .build();
        }

        redisClient.tokenPut(request.getAccount(), request.getDeviceToken());
        log.info("deviceToken: " + redisClient.getDeviceToken(request.getAccount()));

        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> userInfo = mapper.convertValue(member, Map.class);
        userInfo.remove("password");
        resultMap.put("userInfo", userInfo);
        resultMap.put("accessToken", jwtProvider.createAccessToken(member.getAccount(), member.getRoles()));
        resultMap.put("refreshToken", jwtProvider.createRefreshToken(member.getAccount(), member.getRoles()));

        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .data(resultMap)
                .build();
    }

    // 회원가입
    public CommonResponse register(SignRequest request) throws Exception {
        log.info("SignService - register : START");
        if (memberRepository.existsByAccount(request.getAccount())) {
            log.info("SignService - register : ACCOUNT ALREADY EXISTS");
            return CommonResponse.builder()
                    .resultCode(CodeConst.DUPLICATED_ACCOUNT_CODE)
                    .resultMessage(CodeConst.DUPLICATED_ACCOUNT_MESSAGE)
                    .build();
        }
        if (memberRepository.existsByNickname(request.getNickname())) {
            log.info("SignService - register : NICKNAME ALREADY EXISTS");
            return CommonResponse.builder()
                    .resultCode(CodeConst.DUPLICATED_NICKNAME_CODE)
                    .resultMessage(CodeConst.DUPLICATED_NICKNAME_MESSAGE)
                    .build();
        }
        try {
            Member member = Member.builder()
                    .account(request.getAccount())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .nickname(request.getNickname())
                    .level(1)
                    .exp(0)
                    .img(fileService.memberImgFileUpload(request.getImg()))
                    .build();

            member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));
            memberRepository.save(member);
            return CommonResponse.builder()
                    .resultCode(CodeConst.SUCCESS_CODE)
                    .resultMessage(CodeConst.SUCCESS_MESSAGE)
                    .build();

        } catch (Exception e) {
            log.error("SignService - register Error: {}", e.getMessage());
            throw new Exception("잘못된 요청입니다.");
        }
    }

    public CommonResponse sendVerifyCode(String account) {
        String verifyCode = String.valueOf(getRandomCode());

        mailComponent.sendMail(account, getVerificationEmailBody(verifyCode));

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("verifyCode", verifyCode);
        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .data(resultMap)
                .build();
    }

    private int getRandomCode() {
        return ThreadLocalRandom.current().nextInt(100000, 1000000);
    }

    private String getVerificationEmailBody(String verifyCode) {
        StringBuilder sb = new StringBuilder();
        return sb.append("아래의 인증 번호를 확인해주세요.\n\n")
                .append(verifyCode).toString();
    }

    // 계정 중복 체크
    public CommonResponse checkAccountDuplicate(String account) {
        boolean result = memberRepository.existsByAccount(account);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("isDuplicated", result);
        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .data(resultMap)
                .build();
    }

    // 닉네임 중복 체크
    public CommonResponse checkNickNameDuplicate(String nickname) {
        boolean result = memberRepository.existsByNickname(nickname);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("isDuplicated", result);
        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .data(resultMap)
                .build();
    }
}