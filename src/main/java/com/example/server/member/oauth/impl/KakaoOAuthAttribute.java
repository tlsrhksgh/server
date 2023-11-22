package com.example.server.member.oauth.impl;

import com.example.server.member.oauth.OAuthInterface;

import java.util.Map;
import com.example.server.member.oauth.dto.OAuthAttributes;

public class KakaoOAuthAttribute implements OAuthInterface {

    @Override
    public OAuthAttributes create(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) attributes.get("profile");

        return OAuthAttributes.builder()
                .name((String) kakaoProfile.get("author"))
                .email((String) kakaoAccount.get("email"))
                .img((String) kakaoProfile.get("profile_image_url"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
}
