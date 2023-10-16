package com.example.server.friend.service;

import com.example.server.common.CodeConst;
import com.example.server.common.CommonResponse;
import com.example.server.friend.Friend;
import com.example.server.friend.dto.FriendInterface;
import com.example.server.friend.dto.FriendRequestDto;
import com.example.server.friend.repository.FriendRepository;
import com.example.server.member.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;

    // 친구 추가 요청
    public CommonResponse addRequest(FriendRequestDto request, Authentication authentication) throws Exception {
        log.info("FriendService - addRequest : START");
        try {
            if (!memberRepository.existsByAccount(request.getRespondent())) {
                log.info("FriendService - addRequest : FAIL => NO SUCH RESPONDENT");
                return CommonResponse.builder()
                        .resultCode(CodeConst.FRIEND_REQUEST_FAIL_01_CODE)
                        .resultMessage(CodeConst.FRIEND_REQUEST_FAIL_01_MESSAGE)
                        .build();
            }
            else if (friendRepository.isAlreadyRequested(authentication.getName(), request.getRespondent()) > 0) {
                log.info("FriendService - addRequest : FAIL => REQUEST ALREADY SENT OR RECEIVED");
                return CommonResponse.builder()
                        .resultCode(CodeConst.FRIEND_REQUEST_FAIL_02_CODE)
                        .resultMessage(CodeConst.FRIEND_REQUEST_FAIL_02_MESSAGE)
                        .build();
            }
            else {
                Friend friend = new Friend();
                friend.setRequester(authentication.getName());
                friend.setRespondent(request.getRespondent());
                friend.setAccepted("N");
                friendRepository.save(friend);
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
        try {
            List<FriendInterface> requestList = friendRepository.selectRequestList(authentication.getName());
            ObjectMapper mapper = new ObjectMapper();
            log.info("FriendService - selectRequestList : SUCCESS => " + requestList.size());
            return CommonResponse.builder()
                    .resultCode(CodeConst.SUCCESS_CODE)
                    .resultMessage(CodeConst.SUCCESS_MESSAGE)
                    .data(mapper.convertValue(requestList, List.class))
                    .build();
        } catch (Exception e) {
            log.error("FriendService - selectRequestList : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    // 친구 추가 요청 수락
    public CommonResponse acceptRequest(FriendRequestDto request) throws Exception {
        log.info("FriendService - acceptRequest : START");
        try {
            if (friendRepository.updateAcceptedY(request.getRequestId()) == 1) {
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
    public CommonResponse rejectRequest(FriendRequestDto request) throws Exception {
        log.info("FriendService - rejectRequest : START");
        try {
            if (friendRepository.deleteFriendById(Long.parseLong(request.getRequestId())) == 1) {
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
        try {
            List<FriendInterface> result = friendRepository.selectFriendList(authentication.getName());
            ObjectMapper mapper = new ObjectMapper();
            log.info("FriendService - getFriendList : SUCCESS => " + result.size());
            return CommonResponse.builder()
                    .resultCode(CodeConst.SUCCESS_CODE)
                    .resultMessage(CodeConst.SUCCESS_MESSAGE)
                    .data(mapper.convertValue(result, List.class))
                    .build();
        } catch (Exception e) {
            log.error("FriendService - getFriendList : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }
}
