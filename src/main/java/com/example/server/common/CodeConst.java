package com.example.server.common;

public class CodeConst {

    // 성공 공통 코드
    public static final String SUCCESS_CODE                                     = "200";
    public static final String SUCCESS_MESSAGE                                  = "REQUEST PROCESSED SUCCESSFULLY";

    // 계정 없음
    public static final String MEMBER_NOT_FOUND_CODE = "404";
    public static final String MEMBER_NOT_FOUND_MESSAGE = "MEMBER NOT FOUND";

    // 계정 중복
    public static final String DUPLICATED_ACCOUNT_CODE = "409";
    public static final String DUPLICATED_ACCOUNT_MESSAGE = "DUPLICATED ACCOUNT";

    // 닉네임 중복
    public static final String DUPLICATED_NICKNAME_CODE = "409";
    public static final String DUPLICATED_NICKNAME_MESSAGE = "DUPLICATED NICKNAME";

    // 이미지 변경 실패
    public static final String IMAGE_CHANGE_FAIL_CODE = "412";
    public static final String IMAGE_CHANGE_FAIL_MESSAGE = "CHANGING IMAGE REQUEST FAILED";

    // 로그인 실패 - 아이디 없음
    public static final String LOGIN_FAIL_ID_CODE = "420";
    public static final String LOGIN_FAIL_ID_MESSAGE = "LOGIN FAIL : ID DOES NOT EXIST";

    // 로그인 실패 - 비밀번호 불일치
    public static final String LOGIN_FAIL_PW_CODE = "421";
    public static final String LOGIN_FAIL_PW_MESSAGE = "LOGIN FAIL : PW DOES NOT MATCH";

    // 친구 추가 실패
    public static final String FRIEND_REQUEST_FAIL_01_CODE = "422";
    public static final String FRIEND_REQUEST_FAIL_01_MESSAGE = "FRIEND REQUEST FAIL : NO SUCH NICKNAME";
    public static final String FRIEND_REQUEST_FAIL_02_CODE = "423";
    public static final String FRIEND_REQUEST_FAIL_02_MESSAGE = "FRIEND REQUEST FAIL : REQUEST ALREADY SENT OR RECEIVED";

    // 친구 추가 요청 수락
    public static final String FRIEND_REQUEST_ACCEPT_FAIL_CODE = "424";
    public static final String FRIEND_REQUEST_ACCEPT_FAIL_MESSAGE = "ACCEPT REQUEST FAIL";

    // 친구 추가 요청 거절
    public static final String FRIEND_REQUEST_REJECT_FAIL_CODE = "425";
    public static final String FRIEND_REQUEST_REJECT_FAIL_MESSAGE = "REJECT REQUEST FAIL";

    // 약속 초대 불가능
    public static final String INVITE_FRIEND_FAIL_CODE = "426";
    public static final String INVITE_FRIEND_FAIL_MESSAGE = "CANNOT SEND REQUEST";

    // 약속 탈퇴 실패
    public static final String PROMISE_EXIT_FAIL_CODE = "427";
    public static final String PROMISE_EXIT_FAIL_MESSAGE = "CANNOT EXIT PROMISE";

    // 약속 삭제 실패
    public static final String PROMISE_DELETE_FAIL_CODE = "428";
    public static final String PROMISE_DELETE_FAIL_MESSAGE = "CANNOT DELETE PROMISE";

    // 약속 단건 조회 실패
    public static final String PROMISE_INFO_FAIL_CODE = "430";
    public static final String PROMISE_INFO_FAIL_MESSAGE = "NO THAT PROMISE";

    // 채팅방 없음
    public static final String CHATROOM_NOT_FOUND_CODE = "404";
    public static final String CHATROOM_NOT_FOUND_MESSAGE = "CHATROOM NOT FOUND";

    // 참가 되지 않았거나 채팅방이 존재하지 않음.
    public static final String IS_NOT_PARTICIPANT_OR_CHATROOM_NOT_FOUND_CODE = "400";
    public static final String IS_NOT_PARTICIPANT_OR_CHATROOM_NOT_FOUND_MESSAGE = "IS NOT PARTICIPANT OR CHATROOM NOT FOUND";
}
