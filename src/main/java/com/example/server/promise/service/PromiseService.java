package com.example.server.promise.service;

import com.example.server.common.CodeConst;
import com.example.server.common.CommonResponse;
import com.example.server.member.Member;
import com.example.server.promise.Promise;
import com.example.server.promise.PromiseMember;
import com.example.server.promise.dto.PromiseInterface;
import com.example.server.promise.dto.PromiseRequestDto;
import com.example.server.promise.repository.PromiseMemberRepository;
import com.example.server.promise.repository.PromiseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PromiseService {

    private final PromiseRepository promiseRepository;
    private final PromiseMemberRepository promiseMemberRepository;

    // 약속 생성
    public CommonResponse createPromise(HashMap<String, Object> request) throws Exception {
        log.info("PromiseService - createPromise : START");
        String currentAccount = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            ObjectMapper mapper = new ObjectMapper();
            Promise promise = mapper.convertValue(request.get("info"), Promise.class);
            promise.setOrganizer(currentAccount);
            List<Map<String, String>> members = mapper.convertValue(request.get("members"), List.class);
            List<PromiseMember> promiseMembers = new ArrayList<>();
            promiseMembers.add(PromiseMember.builder().account(currentAccount).accepted("Y").build());
            for (Map<String, String> member : members) {
                promiseMembers.add(PromiseMember.builder().account(member.get("account")).accepted("N").build());
            }
            promise.setMembers(promiseMembers);
            promiseRepository.save(promise);
            log.info("PromiseService - createPromise : SUCCESS");
            return CommonResponse.builder()
                    .resultCode(CodeConst.SUCCESS_CODE)
                    .resultMessage(CodeConst.SUCCESS_MESSAGE)
                    .build();
        } catch (Exception e) {
            log.error("PromiseService - createPromise : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    // 약속 목록 조회
    public CommonResponse getPromiseList(String startDateTime, String endDateTime, Authentication authentication) throws Exception {
        log.info("PromiseService - getPromiseList : START");
        try {
            List<PromiseInterface> result = promiseRepository.selectPromiseList(authentication.getName(), startDateTime, endDateTime);
            ObjectMapper mapper = new ObjectMapper();
            log.info("PromiseService - getPromiseList : SUCCESS");
            return CommonResponse.builder()
                    .resultCode(CodeConst.SUCCESS_CODE)
                    .resultMessage(CodeConst.SUCCESS_MESSAGE)
                    .data(mapper.convertValue(result, List.class))
                    .build();
        } catch (Exception e) {
            log.error("PromiseService - getPromiseList : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    // 약속 초대 요청 목록 조회
    public CommonResponse getPromiseRequestList(Authentication authentication) throws Exception {
        log.info("PromiseService - getPromiseRequestList : START");
        try {
            List<PromiseInterface> result = promiseRepository.selectPromiseRequestList(authentication.getName());
            ObjectMapper mapper = new ObjectMapper();
            log.info("PromiseService - getPromiseRequestList : SUCCESS");
            return CommonResponse.builder()
                    .resultCode(CodeConst.SUCCESS_CODE)
                    .resultMessage(CodeConst.SUCCESS_MESSAGE)
                    .data(mapper.convertValue(result, List.class))
                    .build();
        } catch (Exception e) {
            log.error("PromiseService - getPromiseRequestList : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    // 약속 초대 요청 수락
    public CommonResponse acceptPromiseRequest(PromiseRequestDto request, Authentication authentication) throws Exception {
        log.info("PromiseService - acceptPromiseRequest : START");
        try {
            PromiseMember promiseMember = promiseMemberRepository.findByPromiseIdAndAccount(Long.parseLong(request.getId()),authentication.getName()).orElseThrow(() ->
                    new Exception("서버 오류입니다."));
            promiseMember.setAccepted("Y");
            promiseMemberRepository.save(promiseMember);

            log.info("PromiseService - acceptPromiseRequest : SUCCESS");
            return CommonResponse.builder()
                    .resultCode(CodeConst.SUCCESS_CODE)
                    .resultMessage(CodeConst.SUCCESS_MESSAGE)
                    .build();
        } catch (Exception e) {
            log.error("PromiseService - acceptPromiseRequest : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    // 약속 초대 요청 거절
    public CommonResponse rejectPromiseRequest(PromiseRequestDto request, Authentication authentication) throws Exception {
        log.info("PromiseService - rejectPromiseRequest : START");
        try {
            if (promiseMemberRepository.deletePromiseMemberByPromiseIdAndAccount(Long.parseLong(request.getId()), authentication.getName()) == 1) {
                log.info("PromiseService - rejectPromiseRequest : SUCCESS");
                return CommonResponse.builder()
                        .resultCode(CodeConst.SUCCESS_CODE)
                        .resultMessage(CodeConst.SUCCESS_MESSAGE)
                        .build();
            }
            else {
                log.info("PromiseService - rejectPromiseRequest : FAIL");
                return CommonResponse.builder()
                        .resultCode(CodeConst.FRIEND_REQUEST_REJECT_FAIL_CODE)
                        .resultMessage(CodeConst.FRIEND_REQUEST_REJECT_FAIL_MESSAGE)
                        .build();
            }
        } catch (Exception e) {
            log.error("PromiseService - rejectPromiseRequest : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }
}
