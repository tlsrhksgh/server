package com.example.server.member.oauth;

import com.example.server.member.oauth.impl.KakaoOAuthAttribute;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
public enum OAuthAttributesFactory {
    KAKAO(KakaoOAuthAttribute::new);

    private final Supplier<OAuthInterface> type;
}
