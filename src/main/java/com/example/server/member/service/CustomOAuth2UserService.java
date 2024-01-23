package com.example.server.member.service;

import com.example.server.member.dto.OAuth2Attribute;
import com.example.server.member.repository.CustomMemberRepository;
import com.example.server.member.repository.Member;
import com.example.server.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final CustomMemberRepository customMemberRepository;
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();

        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        OAuth2Attribute attributes = OAuth2Attribute
                .of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Member member = saveOrUpdateMember(attributes);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRoles().get(0).getName())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private Member saveOrUpdateMember(OAuth2Attribute attributes) {
        Member member = customMemberRepository.findMemberByAccount(attributes.getEmail());

        if(Objects.nonNull(member)) {

        } else {

        }

        return memberRepository.save(member);
    }
}
