package com.example.server.member.handler;

import com.example.server.member.repository.Authority;
import com.example.server.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain chain,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        log.info("토큰 발행 시작");
        String accessToken = jwtProvider.createAccessToken(email, List.of(Authority.builder().name("ROLE_USER").build()));
        String refreshToken = jwtProvider.createRefreshToken(email, List.of(Authority.builder().name("ROLE_USER").build()));

        MultiValueMap<String, String> tokensMap = new LinkedMultiValueMap<>();
        tokensMap.add("accessToken", accessToken);
        tokensMap.add("refreshToken", refreshToken);
        String targetUrl = UriComponentsBuilder.fromUriString("/")
                .queryParams(tokensMap)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
