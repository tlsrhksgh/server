package com.example.server.member;

import com.example.server.common.CodeConst;
import com.example.server.common.CommonResponse;
import com.example.server.member.dto.SignRequest;
import com.example.server.security.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SignService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CustomMemberRepository customMemberRepository;

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

    // 이미지 변경
//    public CommonResponse changeImage(Map<String, String> request, Authentication authentication) throws Exception {
//        String image = request.get("image");
//        HashMap<String, Object> resultMap = new HashMap<>();
//
//        if (customMemberRepository.updateUserImage(image, authentication.getName()) == 1) {
//            resultMap.put("image", image);
//            return CommonResponse.builder()
//                    .resultCode(CodeConst.SUCCESS_CODE)
//                    .resultMessage(CodeConst.SUCCESS_MESSAGE)
//                    .data(resultMap)
//                    .build();
//        }
//        else {
//            return CommonResponse.builder()
//                    .resultCode(CodeConst.IMAGE_CHANGE_FAIL_CODE)
//                    .resultMessage(CodeConst.IMAGE_CHANGE_FAIL_MESSAGE)
//                    .build();
//        }
//    }

    // 닉네임, 패스워드, 이미지 변경
    @Transactional
    public CommonResponse updateUser(Map<String, String> request, Authentication authentication) {
        Member member = memberRepository.findMemberByAccount(authentication.getName());

        if (Objects.isNull(member)) {
            return CommonResponse.builder()
                    .resultCode(CodeConst.MEMBER_NOT_FOUND_CODE)
                    .resultMessage(CodeConst.MEMBER_NOT_FOUND_MESSAGE)
                    .build();
        }
        member.update(request);

        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .build();
    }

    public void deleteMember(Authentication authentication) {
        Member member = memberRepository.findMemberByAccount(authentication.getName());
        memberRepository.delete(member);
    }
}