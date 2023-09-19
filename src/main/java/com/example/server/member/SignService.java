package com.example.server.member;

import com.example.server.common.CodeConst;
import com.example.server.member.dto.SignRequest;
import com.example.server.member.dto.SignResponse;
import com.example.server.security.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SignService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 로그인
    public SignResponse login(SignRequest request) throws Exception {

        Member member = memberRepository.findByAccount(request.getAccount()).orElseThrow(() ->
                new BadCredentialsException("잘못된 계정정보입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BadCredentialsException("잘못된 계정정보입니다.");
        }

        return SignResponse.builder()
                .id(member.getMemberId())
                .account(member.getAccount())
                .nickname(member.getNickname())
                .roles(member.getRoles())
                .token(jwtProvider.createToken(member.getAccount(), member.getRoles()))
                .build();
    }


    // 회원가입
    public SignResponse register(SignRequest request) throws Exception {
        log.debug("SignService - register : START");
        if (memberRepository.existsByAccount(request.getAccount())) {
            log.debug("SignService - register : ACCOUNT ALREADY EXISTS");
            return SignResponse.builder()
                    .resultCode(410)
                    .resultMessage("계정이 존재합니다.")
                    .build();
        }
        try {
            Member member = Member.builder()
                    .account(request.getAccount())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .nickname(request.getNickname())
                    .build();

            member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));
            memberRepository.save(member);
            log.debug("SignService - register : SUCCESS");
            return SignResponse.builder()
                    .id(member.getMemberId())
                    .account(member.getAccount())
                    .nickname(member.getNickname())
                    .roles(member.getRoles())
                    .build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("잘못된 요청입니다.");
        }
    }

    // 계정 중복 체크
    public SignResponse checkAccountDuplicate(String account) {
        return SignResponse.builder()
                .resultCode(200)
                .resultMessage(String.valueOf(memberRepository.existsByAccount(account)))
                .build();
    }

//    public SignResponse getMember(String account) throws Exception {
//        Member member = memberRepository.findByAccount(account)
//                .orElseThrow(() -> new Exception("계정을 찾을 수 없습니다."));
//        return new SignResponse(member);
//    }

}