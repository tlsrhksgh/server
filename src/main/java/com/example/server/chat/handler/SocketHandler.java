package com.example.server.chat.handler;

import com.example.server.member.MemberRepository;
import com.example.server.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class SocketHandler implements ChannelInterceptor {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if(StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("웹소켓 연결 요청");
            String jwtToken = accessor.getFirstNativeHeader("Authorization");

            if(!jwtProvider.validateToken(jwtToken)) {
                throw new RuntimeException("로그인이 필요합니다.");
            }

            String account = jwtProvider.getAccount(jwtToken.split(" ")[1].trim());
            if(Objects.nonNull(account)) {
                boolean isExistAccount = memberRepository.existsByAccount(account);
                if(!isExistAccount) {
                    throw new UsernameNotFoundException("존재하지 않는 사용자입니다.");
                }
            }

            log.info("인증 완료!");
        }

        return message;
    }
}
