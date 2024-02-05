package com.example.server.friend.service;

import com.example.server.common.CodeConst;
import com.example.server.common.CommonResponse;
import com.example.server.friend.Friend;
import com.example.server.friend.dto.FriendInterface;
import com.example.server.friend.dto.FriendRequestDto;
import com.example.server.friend.repository.FriendRepository;
import com.example.server.member.repository.CustomMemberRepository;
import com.example.server.member.repository.Member;
import com.example.server.member.repository.MemberRepository;
import com.example.server.push.contatns.PushCategory;
import com.example.server.push.service.PushService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;
    private final CustomMemberRepository customMemberRepository;
    private final PushService pushService;

    // 친구 추가 요청
    public CommonResponse addRequest(FriendRequestDto request, Authentication authentication) throws Exception {
        log.info("FriendService - addRequest : START");
        Member currentUser = customMemberRepository.findMemberByAccount(authentication.getName());

        try {
            if (!memberRepository.existsByNickname(request.getRespondent())) {
                log.info("FriendService - addRequest : FAIL => NO SUCH RESPONDENT");
                return CommonResponse.builder()
                        .resultCode(CodeConst.FRIEND_REQUEST_FAIL_01_CODE)
                        .resultMessage(CodeConst.FRIEND_REQUEST_FAIL_01_MESSAGE)
                        .build();
            }
            else if (friendRepository.isAlreadyRequested(currentUser.getNickname(), request.getRespondent()) > 0) {
                log.info("FriendService - addRequest : FAIL => REQUEST ALREADY SENT OR RECEIVED");
                return CommonResponse.builder()
                        .resultCode(CodeConst.FRIEND_REQUEST_FAIL_02_CODE)
                        .resultMessage(CodeConst.FRIEND_REQUEST_FAIL_02_MESSAGE)
                        .build();
            }
            else {
                Friend friend = new Friend();
                friend.setRequester(currentUser.getNickname());
                friend.setRespondent(request.getRespondent());
                friend.setAccepted("N");
                friendRepository.save(friend);

                Member member = customMemberRepository.findMemberByNickname(request.getRespondent());
                pushService.makeAndSendPushNotification(PushCategory.FRIEND_REQUEST, member.getAccount(), null);

                log.info("FriendService - addRequest : SUCCESS");
                return CommonResponse.builder()
                        .resultCode(CodeConst.SUCCESS_CODE)
                        .resultMessage(CodeConst.SUCCESS_MESSAGE)
                        .build();
            }
        } catch (Exception e) {
            log.error("FriendService - addRequest : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    // 친구 추가 요청 목록 조회
    public CommonResponse selectRequestList(Authentication authentication) throws Exception {
        log.info("FriendService - selectRequestList : START");
        Member currentUser = customMemberRepository.findMemberByAccount(authentication.getName());
        try {
            List<FriendInterface> requestList = friendRepository.selectRequestList(currentUser.getNickname());
            Map<String, Object> resultMap = new HashMap<>();
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (FriendInterface friendInterface : requestList) {
                Map<String, Object> info = new HashMap<>();
                info.put("requestInfo", friendInterface);
                Member member = memberRepository.findMemberByNickname(friendInterface.getRequester());
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> memberInfo = mapper.convertValue(member, Map.class);
                if (!memberInfo.isEmpty()) {
                    memberInfo.remove("password");
                    memberInfo.remove("roles");
                    memberInfo.remove("id");
                    info.put("memberInfo", memberInfo);
                    resultList.add(info);
                }
            }
            resultMap.put("info", resultList);
            log.info("FriendService - selectRequestList : SUCCESS => " + requestList.size());
            return CommonResponse.builder()
                    .resultCode(CodeConst.SUCCESS_CODE)
                    .resultMessage(CodeConst.SUCCESS_MESSAGE)
                    .data(resultMap)
                    .build();
        } catch (Exception e) {
            log.error("FriendService - selectRequestList : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    // 친구 추가 요청 수락
    public CommonResponse acceptRequest(FriendRequestDto request, Authentication authentication) throws Exception {
        log.info("FriendService - acceptRequest : START");
        Member currentUser = customMemberRepository.findMemberByAccount(authentication.getName());
        try {
            if (friendRepository.updateAcceptedY(request.getRequestId(), currentUser.getNickname()) == 1) {
                log.info("FriendService - acceptRequest : SUCCESS");
                return CommonResponse.builder()
                        .resultCode(CodeConst.SUCCESS_CODE)
                        .resultMessage(CodeConst.SUCCESS_MESSAGE)
                        .build();
            }
            else {
                log.info("FriendService - acceptRequest : FAIL");
                return CommonResponse.builder()
                        .resultCode(CodeConst.FRIEND_REQUEST_ACCEPT_FAIL_CODE)
                        .resultMessage(CodeConst.FRIEND_REQUEST_ACCEPT_FAIL_MESSAGE)
                        .build();
            }
        } catch (Exception e) {
            log.error("FriendService - acceptRequest : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    // 친구 추가 요청 거절
    public CommonResponse rejectRequest(FriendRequestDto request, Authentication authentication) throws Exception {
        log.info("FriendService - rejectRequest : START");
        Member currentUser = customMemberRepository.findMemberByAccount(authentication.getName());
        try {
            if (friendRepository.deleteFriendByIdAndRespondent(Long.parseLong(request.getRequestId()), currentUser.getNickname()) == 1) {
                log.info("FriendService - rejectRequest : SUCCESS");
                return CommonResponse.builder()
                        .resultCode(CodeConst.SUCCESS_CODE)
                        .resultMessage(CodeConst.SUCCESS_MESSAGE)
                        .build();
            }
            else {
                log.info("FriendService - rejectRequest : FAIL");
                return CommonResponse.builder()
                        .resultCode(CodeConst.FRIEND_REQUEST_REJECT_FAIL_CODE)
                        .resultMessage(CodeConst.FRIEND_REQUEST_REJECT_FAIL_MESSAGE)
                        .build();
            }
        } catch (Exception e) {
            log.error("FriendService - rejectRequest : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    // 친구 목록 조회
    public CommonResponse getFriendList(Authentication authentication) throws Exception {
        log.info("FriendService - getFriendList : START");
        Member currentUser = customMemberRepository.findMemberByAccount(authentication.getName());
        try {
            List<FriendInterface> result = friendRepository.selectFriendList(currentUser.getNickname());
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("list", result);
            log.info("FriendService - getFriendList : SUCCESS => " + result.size());
            return CommonResponse.builder()
                    .resultCode(CodeConst.SUCCESS_CODE)
                    .resultMessage(CodeConst.SUCCESS_MESSAGE)
                    .data(resultMap)
                    .build();
        } catch (Exception e) {
            log.error("FriendService - getFriendList : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }
}
