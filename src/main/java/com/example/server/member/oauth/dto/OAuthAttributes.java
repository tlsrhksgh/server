package com.example.server.member.oauth.dto;

import com.example.server.member.Authority;
import com.example.server.member.Member;
import com.example.server.member.oauth.OAuthAttributesFactory;
import lombok.*;

import java.util.Collections;
import java.util.Map;

@Getter
@Builder
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String img;

    public static OAuthAttributes of(String registrationId, Map<String, Object> attributes) {
        return OAuthAttributesFactory.valueOf(registrationId.toUpperCase())
                .getType()
                .get()
                .create("id", attributes);
    }

    public Member toEntity() {
        return Member.builder()
                .account(email)
                .img(img)
                .nickname(name)
                .roles(null)
                .level(1)
                .exp(0)
                .roles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()))
                .build();
    }
}
