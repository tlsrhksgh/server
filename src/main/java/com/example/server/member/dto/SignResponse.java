package com.example.server.member.dto;

import com.example.server.member.Authority;
import com.example.server.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignResponse {

    private Long id;

    private String account;

    private String nickname;

//    private String name;

//    private String email;

    private List<Authority> roles = new ArrayList<>();

    private String token;

    public SignResponse(Member member) {
        this.id = member.getMemberId();
        this.account = member.getAccount();
        this.nickname = member.getNickname();
//        this.name = member.getName();
//        this.email = member.getEmail();
        this.roles = member.getRoles();
    }
}