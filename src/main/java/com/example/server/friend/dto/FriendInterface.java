package com.example.server.friend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface FriendInterface {

    String getId();
    String getRequester();
    String getRespondent();
    String getAccepted();

    String getMemberId();
    String getAccount();
    String getNickname();
    String getName();

    String getExp();

    String getLevel();

    String getImg();
}