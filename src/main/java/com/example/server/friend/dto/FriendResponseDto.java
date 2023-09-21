package com.example.server.friend.dto;

import com.example.server.friend.Friend;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FriendResponseDto {

    private String resultCode;

    private String resultMessage;

    private List<Friend> list;

    private List<Object> data;

}
