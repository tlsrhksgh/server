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
}
