package com.example.server.member.repository;

import com.example.server.member.dto.UpdateRequest;
import com.example.server.promise.PromiseMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.example.server.member.QAuthority.authority;
import static com.example.server.member.QMember.member;
import static com.example.server.promise.QPromiseMember.promiseMember;


@Repository
public class CustomMemberRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;
    private final JdbcTemplate jdbcTemplate;

    public CustomMemberRepository(JPAQueryFactory queryFactory, JdbcTemplate jdbcTemplate) {
        super(Member.class);
        this.queryFactory = queryFactory;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public Member findMemberByAccount(String account) {
        return queryFactory
                .selectFrom(member)
                .where(member.account.eq(account))
                .leftJoin(member.roles, authority)
                .fetchJoin()
                .fetchOne();
    }

    @Transactional(readOnly = true)
    public Member findMemberByNickname(String nickname) {
        return queryFactory
                .selectFrom(member)
                .where(member.nickname.eq(nickname))
                .leftJoin(member.roles, authority)
                .fetchJoin()
                .fetchOne();
    }

    @Transactional(readOnly = true)
    public List<PromiseMember> findAllParticipatedPromiseByNickname(String nickname) {
        return queryFactory
                .selectFrom(promiseMember)
                .where(promiseMember.nickname.in(nickname))
                .fetch();
    }

    public List<Member> findMembersByNicknames(Set<String> nicknames) {
        return queryFactory
                .selectFrom(member)
                .where(member.nickname.in(nicknames))
                .innerJoin(member.roles, authority)
                .fetchJoin()
                .fetch();
    }

    @Transactional
    @Modifying(clearAutomatically = true)
    public void updateMemberWithRelatedEntities(UpdateRequest request, Member member) {
        String oldNickname = member.getNickname();

        String newNickname = request.getNickname() == null ?
                oldNickname : request.getNickname();

        String newPassword = request.getPassword() == null ?
                member.getPassword() : request.getPassword();

        String memberRelatedEntitiesUpdateQuery = "UPDATE Member m " +
                "LEFT JOIN Friend f ON m.nickname = f.requester OR m.nickname = f.respondent " +
                "LEFT JOIN Post po ON m.nickname = po.author " +
                "LEFT JOIN Promise pr ON m.nickname = pr.leader " +
                "LEFT JOIN PromiseMember pm ON m.nickname = pm.nickname " +
                "SET " +
                "    f.requester = CASE WHEN f.requester = ? THEN ? ELSE f.requester END, " +
                "    f.respondent = CASE WHEN f.respondent = ? THEN ? ELSE f.respondent END, " +
                "    po.author = CASE WHEN po.author = ? THEN ? ELSE po.author END, " +
                "    pr.leader = CASE WHEN pr.leader = ? THEN ? ELSE pr.leader END, " +
                "    pm.nickname = CASE WHEN pm.nickname = ? THEN ? ELSE pm.nickname END, " +
                "    m.nickname = ?, m.password = ? " +
                "    WHERE m.memberId = ?";

        jdbcTemplate.update(memberRelatedEntitiesUpdateQuery,
                oldNickname, newNickname,
                oldNickname, newNickname,
                oldNickname, newNickname,
                oldNickname, newNickname,
                oldNickname, newNickname,
                newNickname, newPassword,
                member.getMemberId());
    }

    @Transactional
    @Modifying(clearAutomatically = true)
    public long updateMemberPassword(String password, String account) {
        return queryFactory
                .update(member)
                .set(member.password, password)
                .where(member.account.eq(account))
                .execute();
    }
}
