package com.example.server.security;

import com.example.server.member.repository.CustomMemberRepository;
import com.example.server.member.repository.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {
    private final CustomMemberRepository customMemberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = customMemberRepository.findMemberByAccount(username);

        if(Objects.isNull(member)) {
            throw new UsernameNotFoundException("Invalid authentication");
        }

        return new CustomUserDetails(member);
    }
}
