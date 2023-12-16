package com.example.server.member.service;

import com.example.server.common.CodeConst;
import com.example.server.common.CommonResponse;
import com.example.server.member.CustomMemberRepository;
import com.example.server.member.Member;
import com.example.server.member.MemberRepository;
import com.example.server.promise.Promise;
import com.example.server.promise.PromiseMember;
import com.example.server.promise.service.PromiseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final CustomMemberRepository customMemberRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final PromiseService promiseService;

    public CommonResponse getInfo(Authentication authentication) throws Exception {
        Member member = customMemberRepository.findMemberByAccount(authentication.getName());

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> userInfo = mapper.convertValue(member, Map.class);
        userInfo.remove("password");
        resultMap.put("userInfo", userInfo);
        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .data(resultMap)
                .build();
    }

    // 닉네임, 패스워드, 이미지 변경
    @Transactional
    public CommonResponse updateMember(Map<String, String> request, Authentication authentication) {
        Member member = customMemberRepository.findMemberByAccount(authentication.getName());

        if (Objects.isNull(member)) {
            return CommonResponse.builder()
                    .resultCode(CodeConst.MEMBER_NOT_FOUND_CODE)
                    .resultMessage(CodeConst.MEMBER_NOT_FOUND_MESSAGE)
                    .build();
        }

        if(Objects.nonNull(request.get("nickname"))) {
            boolean isExistMember = memberRepository.existsByNickname(request.get("nickname"));
            if(isExistMember) {
                return CommonResponse.builder()
                        .resultCode(CodeConst.DUPLICATED_NICKNAME_CODE)
                        .resultMessage(CodeConst.DUPLICATED_NICKNAME_MESSAGE)
                        .build();
            }
        }

        if(Objects.nonNull(request.get("password"))) {
            request.put("password", passwordEncoder.encode(request.get("password")));
        }

        customMemberRepository.updateMemberWithRelatedEntities(request, member);

        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .build();
    }

    @Transactional
    public CommonResponse deleteMember(Authentication authentication) {
        Member member = customMemberRepository.findMemberByAccount(authentication.getName());

        if(Objects.nonNull(member)) {
            List<PromiseMember> promiseMembers = customMemberRepository.
                    findAllParticipatedPromiseByNickname(member.getNickname());

            for(PromiseMember promiseMember : promiseMembers) {
                promiseService.exitPromise(String.valueOf(promiseMember.getPromise().getId()), authentication);
            }
            memberRepository.delete(member);

            return CommonResponse.builder()
                    .resultMessage(CodeConst.SUCCESS_CODE)
                    .resultMessage(CodeConst.SUCCESS_MESSAGE)
                    .build();
        }

        return CommonResponse.builder()
                .resultCode(CodeConst.MEMBER_NOT_FOUND_CODE)
                .resultMessage(CodeConst.MEMBER_NOT_FOUND_MESSAGE)
                .build();
    }
}
