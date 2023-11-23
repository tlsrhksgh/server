package com.example.server.promise.service;

import com.example.server.common.CodeConst;
import com.example.server.common.CommonResponse;
import com.example.server.member.CustomMemberRepository;
import com.example.server.member.Member;
import com.example.server.member.MemberInterface;
import com.example.server.member.MemberRepository;
import com.example.server.promise.Promise;
import com.example.server.promise.PromiseMember;
import com.example.server.promise.dto.PromiseInterface;
import com.example.server.promise.dto.PromiseRequestDto;
import com.example.server.promise.repository.PromiseMemberRepository;
import com.example.server.promise.repository.PromiseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.classfile.Code;
import org.checkerframework.checker.units.qual.C;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PromiseService {

    private final PromiseRepository promiseRepository;
    private final PromiseMemberRepository promiseMemberRepository;
    private final CustomMemberRepository customMemberRepository;
    private final MemberRepository memberRepository;

    // 약속 생성
    public CommonResponse createPromise(HashMap<String, Object> request, Authentication authentication) throws Exception {
        log.info("PromiseService - createPromise : START");
        Member currentUser = customMemberRepository.findMemberByAccount(authentication.getName());
        try {
            ObjectMapper mapper = new ObjectMapper();
            Promise promise = mapper.convertValue(request.get("info"), Promise.class);
            promise.setLeader(currentUser.getNickname());
            promise.setCompleted("N");
            List<Map<String, String>> members = mapper.convertValue(request.get("members"), List.class);
            List<PromiseMember> promiseMembers = new ArrayList<>();
            promiseMembers.add(PromiseMember.builder().nickname(currentUser.getNickname()).accepted("Y").build());
            if (!members.isEmpty()) {
                for (Map<String, String> member : members) {
                    promiseMembers.add(PromiseMember.builder().nickname(member.get("nickname")).accepted("N").build());
                }
            }
            promise.setMembers(promiseMembers);

            Map<String, Object> resultMap = new HashMap<>();
            Promise promiseInfo = promiseRepository.save(promise);
            resultMap.put("info", promiseInfo);
            log.info("PromiseService - createPromise : SUCCESS");
            return CommonResponse.builder()
                    .resultCode(CodeConst.SUCCESS_CODE)
                    .resultMessage(CodeConst.SUCCESS_MESSAGE)
                    .data(resultMap)
                    .build();
        } catch (Exception e) {
            log.error("PromiseService - createPromise : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    // 약속 목록 조회
    public CommonResponse getPromiseList(String startDateTime, String endDateTime, String completed,Authentication authentication) throws Exception {
        log.info("PromiseService - getPromiseList : START");
        Member currentUser = customMemberRepository.findMemberByAccount(authentication.getName());
        try {
            List<PromiseInterface> result = promiseRepository.selectPromiseList(currentUser.getNickname(), startDateTime, endDateTime, completed);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("list", result);
            log.info("PromiseService - getPromiseList : SUCCESS");
            return CommonResponse.builder()
                    .resultCode(CodeConst.SUCCESS_CODE)
                    .resultMessage(CodeConst.SUCCESS_MESSAGE)
                    .data(resultMap)
                    .build();
        } catch (Exception e) {
            log.error("PromiseService - getPromiseList : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    // 약속 단건 조회
    public CommonResponse getPromiseInfo(String promiseId, Authentication authentication) throws Exception {
        log.info("PromiseService - getPromiseInfo : START");
        try {
            Promise promise = promiseRepository.findPromiseById(Long.parseLong(promiseId));
            if (Objects.isNull(promise)) {
                log.error("PromiseService - getPromiseInfo : FAIL");
                return CommonResponse.builder()
                        .resultCode(CodeConst.PROMISE_INFO_FAIL_CODE)
                        .resultMessage(CodeConst.PROMISE_INFO_FAIL_MESSAGE)
                        .build();
            }
            List<MemberInterface> members = promiseMemberRepository.findMembers(promiseId);
            Map<String, Object> resultMap = new HashMap<>();

            resultMap.put("promiseInfo", promise);
            resultMap.put("membersInfo", members);
            log.info("PromiseService - getPromiseInfo : SUCCESS");
            return CommonResponse.builder()
                    .resultCode(CodeConst.SUCCESS_CODE)
                    .resultMessage(CodeConst.SUCCESS_MESSAGE)
                    .data(resultMap)
                    .build();
        } catch (Exception e) {
            log.error("PromiseService - getPromiseInfo : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    // 약속 탈퇴
    @Transactional
    public CommonResponse exitPromise(Map<String, String> request, Authentication authentication) throws Exception {
        log.info("PromiseService - exitPromise : START");
        Member currentUser = customMemberRepository.findMemberByAccount(authentication.getName());
        try {
            Promise promise = promiseRepository.findPromiseById(Long.parseLong(request.get("promiseId")));
            if (Objects.isNull(promise)) {
                log.error("PromiseService - deletePromise : FAIL");
                return CommonResponse.builder()
                        .resultCode(CodeConst.PROMISE_INFO_FAIL_CODE)
                        .resultMessage(CodeConst.PROMISE_INFO_FAIL_MESSAGE)
                        .build();
            }
            // 방장이 나갈 때
            if (currentUser.getNickname().equals(promise.getLeader())) {
                List<MemberInterface> members = promiseMemberRepository.findMembers(request.get("promiseId"));
                String curLeader = currentUser.getNickname();
                String newLeader = "";
                for (MemberInterface member : members) {
                    if (!member.getNickname().equals(curLeader)) {
                        newLeader = member.getNickname();
                        break;
                    }
                }
                if (promiseRepository.updateLeader(request.get("promiseId"), newLeader) == 1 && promiseMemberRepository.deletePromiseMemberByPromiseIdAndNickname(Long.parseLong(request.get("promiseId")), currentUser.getNickname()) == 1 ) {
                    log.info("PromiseService - deletePromise : SUCCESS");
                    return CommonResponse.builder()
                            .resultCode(CodeConst.SUCCESS_CODE)
                            .resultMessage(CodeConst.SUCCESS_MESSAGE)
                            .build();
                }
                log.info("PromiseService - deletePromise : SUCCESS");
                return CommonResponse.builder()
                        .resultCode(CodeConst.SUCCESS_CODE)
                        .resultMessage(CodeConst.SUCCESS_MESSAGE)
                        .build();
            } else {
                if (promiseMemberRepository.deletePromiseMemberByPromiseIdAndNickname(Long.parseLong(request.get("promiseId")), currentUser.getNickname()) == 1 ) {
                    log.info("PromiseService - deletePromise : SUCCESS");
                    return CommonResponse.builder()
                            .resultCode(CodeConst.SUCCESS_CODE)
                            .resultMessage(CodeConst.SUCCESS_MESSAGE)
                            .build();
                }
                else {
                    return CommonResponse.builder()
                            .resultCode(CodeConst.PROMISE_EXIT_FAIL_CODE)
                            .resultMessage(CodeConst.PROMISE_EXIT_FAIL_MESSAGE)
                            .build();
                }
            }
        } catch (Exception e) {
            log.error("PromiseService - exitPromise : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    // 약속 삭제
    public CommonResponse deletePromise(HashMap<String, String> request, Authentication authentication) throws Exception {
        log.info("PromiseService - deletePromise : START");
        Member currentUser = customMemberRepository.findMemberByAccount(authentication.getName());
        try {
            Promise promise = promiseRepository.findPromiseById(Long.parseLong(request.get("promiseId")));
            if (Objects.isNull(promise)) {
                log.error("PromiseService - deletePromise : FAIL");
                return CommonResponse.builder()
                        .resultCode(CodeConst.PROMISE_INFO_FAIL_CODE)
                        .resultMessage(CodeConst.PROMISE_INFO_FAIL_MESSAGE)
                        .build();
            }
            if (currentUser.getNickname().equals(promise.getLeader())) {
                promiseMemberRepository.deletePromiseMembersByPromiseId(Long.parseLong(request.get("promiseId")));
                promiseRepository.deleteById(Long.parseLong(request.get("promiseId")));
                log.info("PromiseService - deletePromise : SUCCESS");
                return CommonResponse.builder()
                        .resultCode(CodeConst.SUCCESS_CODE)
                        .resultMessage(CodeConst.SUCCESS_MESSAGE)
                        .build();
            } else {
                log.error("PromiseService - deletePromise : FAIL");
                return CommonResponse.builder()
                        .resultCode(CodeConst.PROMISE_DELETE_FAIL_CODE)
                        .resultMessage(CodeConst.PROMISE_DELETE_FAIL_MESSAGE)
                        .build();
            }
        } catch (Exception e) {
            log.error("PromiseService - deletePromise : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    // 약속 초대 요청 목록 조회
    public CommonResponse getPromiseRequestList(Authentication authentication) throws Exception {
        log.info("PromiseService - getPromiseRequestList : START");
        Member currentUser = customMemberRepository.findMemberByAccount(authentication.getName());
        try {
            List<PromiseInterface> result = promiseRepository.selectPromiseRequestList(currentUser.getNickname());
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("list", result);
            log.info("PromiseService - getPromiseRequestList : SUCCESS");
            return CommonResponse.builder()
                    .resultCode(CodeConst.SUCCESS_CODE)
                    .resultMessage(CodeConst.SUCCESS_MESSAGE)
                    .data(resultMap)
                    .build();
        } catch (Exception e) {
            log.error("PromiseService - getPromiseRequestList : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    @Transactional
    // 약속 초대 요청 수락
    public CommonResponse acceptPromiseRequest(PromiseRequestDto request, Authentication authentication) throws Exception {
        log.info("PromiseService - acceptPromiseRequest : START");
        Member currentUser = customMemberRepository.findMemberByAccount(authentication.getName());
        try {
            if (promiseMemberRepository.updateAcceptedY(request.getId(), currentUser.getNickname()) == 1) {
                log.info("PromiseService - acceptPromiseRequest : SUCCESS");
                return CommonResponse.builder()
                        .resultCode(CodeConst.SUCCESS_CODE)
                        .resultMessage(CodeConst.SUCCESS_MESSAGE)
                        .build();
            }
            else {
                log.info("PromiseService - acceptPromiseRequest : FAIL");
                return CommonResponse.builder()
                        .resultCode(CodeConst.FRIEND_REQUEST_ACCEPT_FAIL_CODE)
                        .resultMessage(CodeConst.FRIEND_REQUEST_ACCEPT_FAIL_MESSAGE)
                        .build();
            }
        } catch (Exception e) {
            log.error("PromiseService - acceptPromiseRequest : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    // 약속 초대 요청 거절
    public CommonResponse rejectPromiseRequest(PromiseRequestDto request, Authentication authentication) throws Exception {
        log.info("PromiseService - rejectPromiseRequest : START");
        Member currentUser = customMemberRepository.findMemberByAccount(authentication.getName());
        try {
            if (promiseMemberRepository.deletePromiseMemberByPromiseIdAndNickname(Long.parseLong(request.getId()), currentUser.getNickname()) == 1) {
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

    // 약속 초대
    public CommonResponse inviteFriend(HashMap<String, Object> request) throws Exception {
        log.info("PromiseService - inviteFriend : START");
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> members = mapper.convertValue(request.get("members"), List.class);
        try {
            for (Map<String, String> info : members) {
                String nickname = info.get("nickname");
                if (promiseMemberRepository.countByPromiseIdAndNickname(mapper.convertValue(request.get("promiseId"), Long.class), nickname) > 0) {
                    // 이미 요청이 되었거나 멤버임
                    log.info("PromiseService - inviteFriend : FAIL");
                    return CommonResponse.builder()
                            .resultCode(CodeConst.INVITE_FRIEND_FAIL_CODE)
                            .resultMessage(CodeConst.INVITE_FRIEND_FAIL_MESSAGE)
                            .build();
                } else {
                    Promise promise = promiseRepository.findById(mapper.convertValue(request.get("promiseId"), Long.class)).get();
                    PromiseMember friend = PromiseMember.builder().accepted("N").nickname(nickname).build();
                    friend.setPromise(promise);
                    promise.getMembers().add(friend);
                    promiseRepository.save(promise);
                }
            }
            log.info("PromiseService - inviteFriend : SUCCESS");
            return CommonResponse.builder()
                    .resultCode(CodeConst.SUCCESS_CODE)
                    .resultMessage(CodeConst.SUCCESS_MESSAGE)
                    .build();
        } catch (Exception e) {
            log.error("PromiseService - inviteFriend : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    // 약속 수정 -- 방장만 하도록 추후 수정
    public CommonResponse editPromise(Map<String, String> request, Authentication authentication) throws Exception {
        log.info("PromiseService - editPromise : START");
        try {
            String promiseId = request.get("promiseId");
            String title = request.get("title");
            String date = request.get("date");
            String memo = request.get("memo");
            String penalty = request.get("penalty");
            String location = request.get("location");
            promiseRepository.updateInfo(promiseId, title, date, memo, penalty, location);
            return CommonResponse.builder()
                    .resultCode(CodeConst.SUCCESS_CODE)
                    .resultMessage(CodeConst.SUCCESS_MESSAGE)
                    .build();
        } catch (Exception e) {
            log.error("PromiseService - editPromise : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    // 약속 결과 처리 -- 방장만 하도록 추후 수정
    public CommonResponse result(Map<String, Object> request, Authentication authentication) throws Exception {
        log.info("PromiseService - result : START");
        try {

            String requestId = String.valueOf(request.get("promiseId"));
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, String>> result = mapper.convertValue(request.get("result"), List.class);
            for (Map<String, String> map : result) {
                promiseMemberRepository.updateIsSucceed(requestId, map.get("nickname"), map.get("isSucceed"));
                if ("Y".equals(map.get("isSucceed"))) {
                    memberRepository.updateExp(map.get("nickname"));
                }
            }
            promiseRepository.updateCompleted(requestId);

            return CommonResponse.builder()
                    .resultCode(CodeConst.SUCCESS_CODE)
                    .resultMessage(CodeConst.SUCCESS_MESSAGE)
                    .build();
        } catch (Exception e) {
            log.error("PromiseService - result : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }
}
