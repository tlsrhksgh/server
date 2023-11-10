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
            "FROM Friend f " +
            "WHERE (f.requester = ?1 and f.respondent = ?2) " +
            "OR (f.requester = ?2 AND f.respondent = ?1) " +
            "LIMIT 1"
            , nativeQuery = true)
    Integer isAlreadyRequested(String requester, String respondent) throws Exception;

    @Query(value = "SELECT id, requester, respondent, accepted " +
            "FROM Friend " +
            "WHERE respondent = ?1 " +
            "AND accepted = 'N'"
            , nativeQuery = true)
    List<FriendInterface> selectRequestList(String nickname) throws Exception;

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Friend " +
            "SET accepted = 'Y' " +
            "WHERE id = ?1 " +
            "AND respondent = ?2 " +
            "AND accepted = 'N'"
            , nativeQuery = true)
    Integer updateAcceptedY(String requestId, String nickname
    ) throws Exception;

    @Modifying
    Integer deleteFriendByIdAndRespondent(Long id, String nickname) throws Exception;

    @Query(value = "SELECT m.account, m.nickname, m.img, m.level, m.exp " +
            "FROM Member m " +
            "WHERE m.nickname " +
            "IN (SELECT " +
            "CASE WHEN f.respondent =?1 THEN f.requester " +
            "ELSE f.respondent END " +
            "FROM Friend f " +
            "WHERE (f.respondent = ?1 or f.requester = ?1) " +
            "AND f.accepted = 'Y')"
            ,nativeQuery = true)
    List<FriendInterface> selectFriendList(String nickname) throws Exception;
}
