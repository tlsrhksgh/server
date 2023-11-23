package com.example.server.member;

import com.example.server.common.CodeConst;
import com.example.server.common.CommonResponse;
import com.example.server.member.component.MailComponent;
import com.example.server.member.dto.SignRequest;
import com.example.server.security.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SignService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final MailComponent mailComponent;

    // 로그인
    public CommonResponse login(SignRequest request) throws Exception {

        Member member = memberRepository.findByAccount(request.getAccount()).orElseThrow(() ->
                new BadCredentialsException("잘못된 계정정보입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("잘못된 계정정보입니다.");
        }
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> userInfo = mapper.convertValue(member, Map.class);
        userInfo.remove("password");
        resultMap.put("userInfo", userInfo);
        resultMap.put("token", jwtProvider.createToken(member.getAccount(), member.getRoles()));
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
                    .img(request.getImg())
                    .build();

            member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));
            memberRepository.save(member);
            log.info("SignService - register : SUCCESS");
            return CommonResponse.builder()
                    .resultCode(CodeConst.SUCCESS_CODE)
                    .resultMessage(CodeConst.SUCCESS_MESSAGE)
                    .build();

        } catch (Exception e) {
            log.error("SignService - register : EXCEPTION");
            System.out.println(e.getMessage());
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