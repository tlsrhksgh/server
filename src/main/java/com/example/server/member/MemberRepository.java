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

    Member findMemberByNickname(String nickname);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Member " +
            "SET level = CASE WHEN exp = 9 THEN level + 1 ELSE level END, exp = CASE WHEN exp = 9 THEN 0 ELSE exp + 1 END " +
            "WHERE nickname = ?1"
            , nativeQuery = true)
    Integer updateExp(String nickname) throws Exception;
}
