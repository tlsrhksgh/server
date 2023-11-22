package com.example.server.promise.repository;

import com.example.server.member.Member;
import com.example.server.member.MemberInterface;
import com.example.server.promise.PromiseMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PromiseMemberRepository extends JpaRepository<PromiseMember, Long> {

    Long countByPromiseIdAndNickname(Long promiseId, String nickname) throws Exception;

    @Modifying
    Integer deletePromiseMemberByPromiseIdAndNickname(Long promiseId, String nickname) throws Exception;

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE PromiseMember " +
            "SET accepted = 'Y' " +
            "WHERE promise_id = ?1 " +
            "AND nickname = ?2 " +
            "AND accepted = 'N'"
            , nativeQuery = true)
    Integer updateAcceptedY(String requestId, String nickname) throws Exception;

    @Query(value = "SELECT nickname, img, level " +
            "FROM Member m " +
            "WHERE m.nickname IN ( " +
            "SELECT pm.nickname FROM PromiseMember pm WHERE pm.promise_id = ?1 )"
            , nativeQuery = true)
    List<MemberInterface> findMembers(String promiseId) throws Exception;

    @Query(value = "SELECT nickname, img, level " +
            "FROM Member m " +
            "WHERE m.nickname IN ( " +
            "SELECT pm.nickname FROM PromiseMember pm WHERE pm.promise_id = ?1 AND pm.accepted = 'Y')"
            , nativeQuery = true)
    List<MemberInterface> findAcceptedMembers(String promiseId) throws Exception;

    @Modifying
    Integer deletePromiseMembersByPromiseId(Long promiseId) throws Exception;

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE PromiseMember " +
            "SET isSucceed = ?3 " +
            "WHERE promise_id = ?1 " +
            "AND nickname = ?2 "
            , nativeQuery = true)
    Integer updateIsSucceed(String requestId, String nickname, String isSucceed) throws Exception;
}
