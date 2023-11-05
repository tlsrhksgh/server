package com.example.server.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByAccount(String account);

    boolean existsByAccount(String account);

    boolean existsByNickname(String nickname);

    Member findMemberByAccount(String account);
}
