package com.example.server.promise.repository;

import com.example.server.promise.PromiseMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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

}
