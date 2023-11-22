package com.example.server.member.service;

import com.example.server.member.Authority;
import com.example.server.member.Member;
import com.example.server.member.MemberRepository;
import com.example.server.member.oauth.dto.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CumtomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 사이트 식별 id
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuthAttributes attributes = OAuthAttributes.of(
                registrationId, oAuth2User.getAttributes());

        Member member = saveMember(attributes);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRoles().get(0).getName())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }

    private Member saveMember(OAuthAttributes attributes) {
        Optional<Member> member = memberRepository.findByAccount(attributes.getEmail());

        if (member.isEmpty()) {
            member = Optional.of(attributes.toEntity());
            member.get().setRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));
        }

        return memberRepository.save(member.get());
    }
}
