package com.example.server.member.service;

import com.example.server.common.CodeConst;
import com.example.server.common.CommonResponse;
import com.example.server.member.repository.Authority;
import com.example.server.member.repository.CustomMemberRepository;
import com.example.server.member.repository.Member;
import com.example.server.member.repository.MemberRepository;
import com.example.server.security.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService{
    private final InMemoryClientRegistrationRepository inMemoryRepository;
    private final CustomMemberRepository customMemberRepository;
    private final ObjectMapper mapper;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public CommonResponse login(String providerName, String token) {
        ClientRegistration provider = inMemoryRepository.findByRegistrationId(providerName);

        Member member = getMemberProfile(providerName, token, provider);

        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> userInfo = mapper.convertValue(member, Map.class);
        resultMap.put("userInfo", userInfo);
        resultMap.put("accessToken", jwtProvider.createAccessToken(member.getAccount(), member.getRoles()));
        resultMap.put("refreshToken", jwtProvider.createRefreshToken(member.getAccount(), member.getRoles()));

        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .data(resultMap)
                .build();
    }

    private MultiValueMap<String, String> tokenRequest(String code, ClientRegistration provider) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", provider.getRedirectUri());
        formData.add("client_id",provider.getClientId());
        return formData;
    }

    private Map<String, Object> getMemberAttributes(ClientRegistration provider, String token) {
        log.info("kakao_accessToken: " + token);
        return WebClient.create()
                .get()
                .uri(provider.getProviderDetails().getUserInfoEndpoint().getUri())
                .headers(header -> header.setBearerAuth(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    private Member getMemberProfile(String providerName, String token, ClientRegistration provider) {
        Map<String, Object> userAttributes = getMemberAttributes(provider, token);
        String userNameAttributeName = provider.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        if(!userAttributes.containsKey("email") || Objects.isNull(userAttributes.get("email"))) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Member member = customMemberRepository.findMemberByAccount((String) userAttributes.get("email"));;

        if(Objects.isNull(member)){
            member = saveMember(userAttributes);
        }

        return member;
    }

    private Member saveMember(Map<String, Object> attributes) {
        return memberRepository.save(Member.builder()
                .account((String) attributes.get("email"))
                .roles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()))
                .nickname((String)attributes.get("name"))
                .build());
    }
}
