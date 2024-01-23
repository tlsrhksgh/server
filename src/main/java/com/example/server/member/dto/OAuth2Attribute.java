package com.example.server.member.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class OAuth2Attribute {
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;
    private final String picture;
    private final String provider;

    public static OAuth2Attribute of(String provider, String registrationId, Map<String, Object> attributes) {
        switch (provider) {
            case "kakao":
                return ofKakao(provider, "email", attributes);
            default:
                throw new RuntimeException("Invalid OAuth2Attribute");
        }
    }

    private static OAuth2Attribute ofKakao(String provider,
                                           String attributeKey,
                                           Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        return OAuth2Attribute.builder()
                .email((String) kakaoAccount.get("email"))
                .provider(provider)
                .attributes(kakaoAccount)
                .nameAttributeKey(attributeKey)
                .build();
    }
}
