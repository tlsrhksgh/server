package com.example.server.member.oauth;

import com.example.server.member.oauth.dto.OAuthAttributes;

import java.util.Map;

public interface OAuthInterface {
    OAuthAttributes create(String userNameAttributeName, Map<String, Object> attributes);
}
