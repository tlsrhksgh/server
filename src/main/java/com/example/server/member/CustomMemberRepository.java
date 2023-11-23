package com.example.server.member;

import com.example.server.chat.service.dto.CreateRoomForm;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

import static com.example.server.member.QMember.member;
import static com.example.server.member.QAuthority.authority;

@Repository
public class CustomMemberRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    public CustomMemberRepository(JPAQueryFactory queryFactory, EntityManager entityManager) {
        super(Member.class);
        this.queryFactory = queryFactory;
        this.entityManager = entityManager;
    }

    public Member findMemberByAccount(String account) {
        return queryFactory
                .selectFrom(member)
                .where(member.account.eq(account))
                .leftJoin(member.roles, authority)
                .fetchJoin()
                .fetchOne();
    }

    public List<Member> findTwoMember(CreateRoomForm form) {
        return queryFactory
                .selectFrom(member)
                .where(
                        inEqMemberAccount(form.getSender(), form.getReceiver())
                )
                .leftJoin(member.roles, authority)
                .fetchJoin()
                .fetch();
    }

    private BooleanExpression inEqMemberAccount(String user1, String user2) {
        if (Objects.isNull(user1) || Objects.isNull(user2)) {
            return null;
        }

        return member.account.in(user1, user2);
    }

    public long updateUserImage(String image, String account) {
        return queryFactory
                .update(member)
                .set(member.img, image)
                .where(member.account.eq(account))
                .execute();
    }
}
