package com.example.server.promise.repository;

import com.example.server.promise.PromiseMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface PromiseMemberRepository extends JpaRepository<PromiseMember, Long> {

    Optional<PromiseMember> findByPromiseIdAndAccount(Long promiseId, String account) throws Exception;

    @Modifying
    Integer deletePromiseMemberByPromiseIdAndAccount(Long promiseId, String account) throws Exception;
}
