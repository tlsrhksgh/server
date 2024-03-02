package com.example.server.member.service;

import com.example.server.common.CodeConst;
import com.example.server.common.CommonResponse;
import com.example.server.member.dto.apple.ApplePublicKeyResponse;
import com.example.server.member.repository.Authority;
import com.example.server.member.repository.CustomMemberRepository;
import com.example.server.member.repository.Member;
import com.example.server.member.repository.MemberRepository;
import com.example.server.member.service.apple.ApplePublicKeyGenerator;
import com.example.server.member.service.dto.KakaoUserInfo;
import com.example.server.security.JwtProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.parser.JSONParser;
import com.nimbusds.jose.shaded.json.parser.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.naming.AuthenticationException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService{
    private final CustomMemberRepository customMemberRepository;
    private final MemberRepository memberRepository;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final JwtProvider jwtProvider;

    @Value("${apple.auth.public-key-url}")
    private String appleAuthUrl;

    @Value("${spring.security.oauth2.client.provider.kakao.token_uri}")
    private String KAKAO_TOKEN_URI;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String KAKAO_USER_INFO_URL;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String REDIRECT_URL;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String KAKAO_API_KEY;

    public Map<String, Object> login(String providerName, String token) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("client_id", KAKAO_API_KEY);
        map.add("redirect_uri", REDIRECT_URL);
        map.add("code", token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

        HashMap<String, Object> response = restTemplate.postForObject(KAKAO_TOKEN_URI, requestEntity, HashMap.class);

        return response;
    }

    private Map<String, Object> getMemberAttributes(ClientRegistration provider, String token) {
        return WebClient.create()
                .get()
                .uri(provider.getProviderDetails().getUserInfoEndpoint().getUri())
                .headers(header -> header.setBearerAuth(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    public CommonResponse getUserInfoAndSave(String accessToken) throws ParseException {
        RestTemplate restTemplate = new RestTemplate();

        log.info("getUserInfo accessToken is {}", accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);

        String response = restTemplate.postForObject(KAKAO_USER_INFO_URL, requestEntity, String.class);

        JSONParser jsonParser = new JSONParser();
        JSONObject rootObject = (JSONObject) jsonParser.parse(response);
        JSONObject accountObject = (JSONObject) rootObject.get("kakao_account");
        JSONObject properties = (JSONObject) rootObject.get("properties");

        KakaoUserInfo kakaoUserInfo = KakaoUserInfo.builder()
                .nickname((String) properties.get("nickname"))
                .email((String) accountObject.get("email"))
                .img((String) properties.get("profile_image"))
                .build();

        Member member = this.saveMember(kakaoUserInfo);

        return makeTokenAndGet(kakaoUserInfo, member);
    }

    private Mono<ApplePublicKeyResponse> getAppleAuthClient() {
        return (Mono<ApplePublicKeyResponse>) WebClient.create()
                .get()
                .uri(appleAuthUrl)
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

        Member member = customMemberRepository.findMemberByAccount((String) userAttributes.get("email"));

        if(Objects.isNull(member)){
//            member = saveMember(userAttributes);
        }

        return member;
    }

    public String getAppleAccountId(String identityToken)
            throws JsonProcessingException, AuthenticationException, NoSuchAlgorithmException,
            InvalidKeySpecException {
        Map<String, String> headers = jwtProvider.parseHeaders(identityToken);
        PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(headers, getAppleAuthClient().block());

        return jwtProvider.getTokenClaims(identityToken, publicKey).getSubject();
    }

    private Member saveMember(KakaoUserInfo info) {
        Member member = customMemberRepository.findMemberByAccount(info.getEmail());

        if(Objects.isNull(member)) {
            return memberRepository.save(Member.builder()
                    .account(info.getEmail())
                    .roles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()))
                    .nickname(info.getNickname())
                    .img(info.getImg())
                    .build());
        }

        return member;
    }

    private CommonResponse makeTokenAndGet(KakaoUserInfo kakaoUserInfo, Member member) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("userInfo", kakaoUserInfo);
        resultMap.put("accessToken", jwtProvider.createAccessToken(member.getAccount(), member.getRoles()));
        resultMap.put("refreshToken", jwtProvider.createRefreshToken(member.getAccount(), member.getRoles()));

        CommonResponse response = CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .data(resultMap)
                .build();

        return response;
    }
}