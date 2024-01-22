package com.example.server.member.dto;

import com.example.server.member.repository.Authority;
import com.example.server.member.repository.Member;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignResponse {

    private Long id;

    private String account;

    private String nickname;

    private int resultCode;

    private String resultMessage;

    private List<Authority> roles = new ArrayList<>();

    private String token;

    public SignResponse(Member member) {
        this.id = member.getMemberId();
        this.account = member.getAccount();
        this.nickname = member.getNickname();
        this.roles = member.getRoles();
    }
}