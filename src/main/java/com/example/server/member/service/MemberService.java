package com.example.server.member.service;

import com.example.server.common.CodeConst;
import com.example.server.common.CommonResponse;
import com.example.server.file.FileService;
import com.example.server.member.repository.CustomMemberRepository;
import com.example.server.member.repository.Member;
import com.example.server.member.repository.MemberRepository;
import com.example.server.member.dto.FindPasswordRequest;
import com.example.server.member.dto.UpdateRequest;
import com.example.server.promise.PromiseMember;
import com.example.server.promise.service.PromiseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    private final PromiseService promiseService;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public CommonResponse getInfo(Authentication authentication) {
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
    public CommonResponse updateMember(UpdateRequest request, Authentication authentication) {
        Member member = customMemberRepository.findMemberByAccount(authentication.getName());

        if (Objects.isNull(member)) {
            return CommonResponse.builder()
                    .resultCode(CodeConst.MEMBER_NOT_FOUND_CODE)
                    .resultMessage(CodeConst.MEMBER_NOT_FOUND_MESSAGE)
                    .build();
        }

        if(Objects.nonNull(request.getNickname())) {
            boolean isExistMember = memberRepository.existsByNickname(request.getNickname());
            if(isExistMember) {
                return CommonResponse.builder()
                        .resultCode(CodeConst.DUPLICATED_NICKNAME_CODE)
                        .resultMessage(CodeConst.DUPLICATED_NICKNAME_MESSAGE)
                        .build();
            }
        }

        if(StringUtils.hasText(request.getPassword())) {
            request.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        customMemberRepository.updateMemberWithRelatedEntities(request, member);

        Map<String, Object> updateImgMap = new HashMap<>();
        if(request.isImgUpdate()) {
            updateImgMap.put("img", fileService.updateMemberImgFile(request.getImg(), member));
        }

        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .data(updateImgMap)
                .build();
    }

    public CommonResponse updateMemberPassword(FindPasswordRequest request) {
        if(StringUtils.hasText(request.getPassword())) {
            request.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        long result = customMemberRepository.updateMemberPassword(request.getPassword(), request.getAccount());

        if(result == 0) {
            return CommonResponse.builder()
                    .resultCode(CodeConst.MEMBER_NOT_FOUND_CODE)
                    .resultMessage(CodeConst.MEMBER_NOT_FOUND_MESSAGE)
                    .build();
        }

        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .build();
    }

    public CommonResponse deleteMember(Authentication authentication) {
        Member member = customMemberRepository.findMemberByAccount(authentication.getName());

        if(Objects.nonNull(member)) {
            List<PromiseMember> promiseMembers = customMemberRepository.
                    findAllParticipatedPromiseByNickname(member.getNickname());

            for(PromiseMember promiseMember : promiseMembers) {
                promiseService.exitPromise(String.valueOf(promiseMember.getPromise().getId()), authentication);
            }
            fileService.deleteMemberImgFile(member.getImg());
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
