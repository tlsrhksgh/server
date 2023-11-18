package com.example.server.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByAccount(String account);

    boolean existsByAccount(String account);

    boolean existsByNickname(String nickname);

    Member findMemberByAccount(String account);

    Member findMemberByNickname(String nickname);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Member " +
            "SET exp = exp + 1 " +
            "WHERE nickname = ?1"
            , nativeQuery = true)
    Integer updateExp(String nickname) throws Exception;
}
