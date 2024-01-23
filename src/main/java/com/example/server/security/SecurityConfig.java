package com.example.server.security;

import com.example.server.member.handler.OAuthAuthenticationSuccessHandler;
import com.example.server.member.service.CustomOAuth2UserService;
import com.example.server.security.handler.CustomAccessDeniedHandler;
import com.example.server.security.handler.CustomAuthenticationEntryPoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final CustomAccessDeniedHandler deniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuthAuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .cors(c -> {
                    CorsConfigurationSource source = request -> {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(List.of("*"));
                        config.setAllowedMethods(List.of("*"));
                        return config;
                    };
                    c.configurationSource(source);
                })
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/register", "/login", "/**/exists/**", "/ws/**", "/verify-code", "/refresh",
                        "/member/find-password")
                .permitAll()
                .antMatchers("/admin/**")
                .hasRole("ADMIN")
                .antMatchers("/user/**", "/friend/**", "/promise/**", "/member/**", "/post/**", "/chat/**")
                .authenticated()
                .anyRequest().denyAll()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .accessDeniedHandler(deniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .and()
                .redirectionEndpoint()
                .baseUri("/login/oauth2/code/*")
                .and()
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and()
                .successHandler(successHandler);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}