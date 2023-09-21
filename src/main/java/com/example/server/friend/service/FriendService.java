package com.example.server.friend.service;

import com.example.server.common.CodeConst;
import com.example.server.friend.Friend;
import com.example.server.friend.dto.FriendInterface;
import com.example.server.friend.dto.FriendRequestDto;
import com.example.server.friend.dto.FriendResponseDto;
import com.example.server.friend.repository.FriendRepository;
import com.example.server.member.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public FriendResponseDto addRequest(FriendRequestDto request) throws Exception {
        log.info("FriendService - addRequest : START");
        try {
            if (!memberRepository.existsByAccount(request.getRespondent())) {
                log.info("FriendService - addRequest : FAIL => NO SUCH RESPONDENT");
                return FriendResponseDto.builder()
                        .resultCode(CodeConst.FRIEND_REQUEST_FAIL_01_CODE)
                        .resultMessage(CodeConst.FRIEND_REQUEST_FAIL_01_MESSAGE)
                        .build();
            }
            else if (friendRepository.isAlreadyRequested(request.getRequester(), request.getRespondent()) > 0) {
                log.info("FriendService - addRequest : FAIL => REQUEST ALREADY SENT OR RECEIVED");
                return FriendResponseDto.builder()
                        .resultCode(CodeConst.FRIEND_REQUEST_FAIL_02_CODE)
                        .resultMessage(CodeConst.FRIEND_REQUEST_FAIL_02_MESSAGE)
                        .build();
            }
            else {
                Friend friend = new Friend();
                friend.setRequester(request.getRequester());
                friend.setRespondent(request.getRespondent());
                friend.setAccepted("N");
                friendRepository.save(friend);
                log.info("FriendService - addRequest : SUCCESS");
                return FriendResponseDto.builder()
                        .resultCode(CodeConst.FRIEND_REQUEST_SUCCESS_CODE)
                        .resultMessage(CodeConst.FRIEND_REQUEST_SUCCESS_MESSAGE)
                        .build();
            }
        } catch (Exception e) {
            log.error("FriendService - addRequest : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    // 친구 추가 요청 목록 조회
    public FriendResponseDto selectRequestList(String account) throws Exception {
        log.info("FriendService - selectRequestList : START");
        try {
            List<FriendInterface> requestList = friendRepository.selectRequestList(account);
            ObjectMapper mapper = new ObjectMapper();
            log.info("FriendService - selectRequestList : SUCCESS => " + requestList.size());
            return FriendResponseDto.builder()
                    .resultCode(CodeConst.FRIEND_REQUEST_LIST_SUCCESS_CODE)
                    .resultMessage(CodeConst.FRIEND_REQUEST_LIST_SUCCESS_MESSAGE)
                    .data(mapper.convertValue(requestList, List.class))
                    .build();
        } catch (Exception e) {
            log.error("FriendService - selectRequestList : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    // 친구 추가 요청 수락
    public FriendResponseDto acceptRequest(FriendRequestDto request) throws Exception {
        log.info("FriendService - acceptRequest : START");
        try {
            if (friendRepository.updateAcceptedY(request.getRequestId()) == 1) {
                log.info("FriendService - acceptRequest : SUCCESS");
                return FriendResponseDto.builder()
                        .resultCode(CodeConst.FRIEND_REQUEST_ACCEPT_SUCCESS_CODE)
                        .resultMessage(CodeConst.FRIEND_REQUEST_ACCEPT_SUCCESS_MESSAGE)
                        .build();
            }
            else {
                log.info("FriendService - acceptRequest : FAIL");
                return FriendResponseDto.builder()
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
    public FriendResponseDto rejectRequest(FriendRequestDto request) throws Exception {
        log.info("FriendService - rejectRequest : START");
        try {
            if (friendRepository.deleteFriendById(Long.parseLong(request.getRequestId())) == 1) {
                log.info("FriendService - rejectRequest : SUCCESS");
                return FriendResponseDto.builder()
                        .resultCode(CodeConst.FRIEND_REQUEST_REJECT_SUCCESS_CODE)
                        .resultMessage(CodeConst.FRIEND_REQUEST_REJECT_SUCCESS_MESSAGE)
                        .build();
            }
            else {
                log.info("FriendService - rejectRequest : FAIL");
                return FriendResponseDto.builder()
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
    public FriendResponseDto getFriendList(String account) throws Exception {
        log.info("FriendService - getFriendList : START");
        try {
            List<FriendInterface> result = friendRepository.selectFriendList(account);
            ObjectMapper mapper = new ObjectMapper();
            log.info("FriendService - getFriendList : SUCCESS => " + result.size());
            return FriendResponseDto.builder()
                    .resultCode(CodeConst.FRIEND_LIST_SUCCESS_CODE)
                    .resultMessage(CodeConst.FRIEND_LIST_SUCCESS_MESSAGE)
                    .data(mapper.convertValue(result, List.class))
                    .build();
        } catch (Exception e) {
            log.error("FriendService - getFriendList : Exception");
            e.printStackTrace();
            throw new Exception(e);
        }
    }
}
