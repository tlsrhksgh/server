package com.example.server.common;

public class CodeConst {

    // 로그인 성공
    public static final String LOGIN_SUCCESS_CODE = "200";
    public static final String LOGIN_SUCCESS_MESSAGE = "LOGIN SUCCESS";

    // 로그인 실패 - 아이디 없음
    public static final String LOGIN_FAIL_ID_CODE = "420";
    public static final String LOGIN_FAIL_ID_MESSAGE = "LOGIN FAIL : ID DOES NOT EXIST";

    // 로그인 실패 - 비밀번호 불일치
    public static final String LOGIN_FAIL_PW_CODE = "421";
    public static final String LOGIN_FAIL_PW_MESSAGE = "LOGIN FAIL : PW DOES NOT MATCH";

    // 친구 추가 성공
    public static final String FRIEND_REQUEST_SUCCESS_CODE = "200";
    public static final String FRIEND_REQUEST_SUCCESS_MESSAGE = "FRIEND REQUEST SUCCESS";

    // 친구 추가 실패
    public static final String FRIEND_REQUEST_FAIL_01_CODE = "422";
    public static final String FRIEND_REQUEST_FAIL_01_MESSAGE = "FRIEND REQUEST FAIL : NO SUCH ACCOUNT";
    public static final String FRIEND_REQUEST_FAIL_02_CODE = "423";
    public static final String FRIEND_REQUEST_FAIL_02_MESSAGE = "FRIEND REQUEST FAIL : REQUEST ALREADY SENT OR RECEIVED";

    // 친구 추가 요청 목록 조회
    public static final String FRIEND_REQUEST_LIST_SUCCESS_CODE = "200";
    public static final String FRIEND_REQUEST_LIST_SUCCESS_MESSAGE = "FRIEND REQUEST SUCCESS";

    // 친구 추가 요청 수락
    public static final String FRIEND_REQUEST_ACCEPT_SUCCESS_CODE = "200";
    public static final String FRIEND_REQUEST_ACCEPT_SUCCESS_MESSAGE = "ACCEPT REQUEST SUCCESS";
    public static final String FRIEND_REQUEST_ACCEPT_FAIL_CODE = "424";
    public static final String FRIEND_REQUEST_ACCEPT_FAIL_MESSAGE = "ACCEPT REQUEST FAIL";

    // 친구 추가 요청 거절
    public static final String FRIEND_REQUEST_REJECT_SUCCESS_CODE = "200";
    public static final String FRIEND_REQUEST_REJECT_SUCCESS_MESSAGE = "REJECT REQUEST SUCCESS";
    public static final String FRIEND_REQUEST_REJECT_FAIL_CODE = "425";
    public static final String FRIEND_REQUEST_REJECT_FAIL_MESSAGE = "REJECT REQUEST FAIL";

    // 친구 목록 조회
    public static final String FRIEND_LIST_SUCCESS_CODE = "200";
    public static final String FRIEND_LIST_SUCCESS_MESSAGE = "FRIEND LIST SELECT SUCCESS";
}
