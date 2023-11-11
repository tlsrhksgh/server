package com.example.server.member;

import com.example.server.chat.service.dto.CreateRoomForm;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.example.server.member.QMember.member;
import static com.example.server.member.QAuthority.authority;

@Repository
public class CustomMemberRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public CustomMemberRepository(JPAQueryFactory queryFactory) {
        super(Member.class);
        this.queryFactory = queryFactory;
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
}
