package com.example.server.friend.repository;

import com.example.server.friend.Friend;
import com.example.server.friend.dto.FriendInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query(value = "SELECT COUNT(1) " +
            "FROM friend f " +
            "WHERE (f.requester = ?1 and f.respondent = ?2) " +
            "OR (f.requester = ?2 AND f.respondent = ?1) " +
            "LIMIT 1"
            , nativeQuery = true)
    Integer isAlreadyRequested(String requester, String respondent) throws Exception;

    @Query(value = "SELECT id, requester, respondent, accepted " +
            "FROM friend " +
            "WHERE respondent = ?1 " +
            "AND accepted = 'N'"
            , nativeQuery = true)
    List<FriendInterface> selectRequestList(String account) throws Exception;

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE friend " +
            "SET accepted = 'Y' " +
            "WHERE id = ?1 " +
            "AND accepted = 'N'"
            , nativeQuery = true)
    Integer updateAcceptedY(String requestId) throws Exception;

    @Modifying
    Integer deleteFriendById(Long id) throws Exception;

    @Query(value = "SELECT m.member_id as id, m.account, m.nickname " +
            "FROM member m " +
            "WHERE m.account " +
            "IN (SELECT " +
            "CASE WHEN f.respondent =?1 THEN f.requester " +
            "ELSE f.respondent END " +
            "FROM friend f " +
            "WHERE (f.respondent = ?1 or f.requester = ?1) " +
            "AND f.accepted = 'Y')"
            ,nativeQuery = true)
    List<FriendInterface> selectFriendList(String account) throws Exception;
}
