package com.example.server.member;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface MemberInterface {

    String getId();

    String getNickname();

    String getImg();

    String getLevel();
}
