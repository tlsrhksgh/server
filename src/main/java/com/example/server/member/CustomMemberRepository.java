package com.example.server.member;

import com.example.server.promise.PromiseMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public List<Member> findMembersByNicknames(List<String> nicknames) {
        return queryFactory
                .selectFrom(member)
                .where(member.nickname.in(nicknames))
                .innerJoin(member.roles, authority)
                .fetchJoin()
                .fetch();
    }

    @Transactional
    @Modifying(clearAutomatically = true)
    public void updateMemberWithRelatedEntities(Map<String, String> request, Member member) {
        String oldNickname = member.getNickname();

        String newNickname = request.get("nickname") == null ?
                oldNickname : request.get("nickname");

        String newPassword = request.get("password") == null ?
                member.getPassword() : request.get("password");

        String newImg;
        if(request.get("img").equals("")) {
            newImg = null;
        } else if(Objects.isNull(request.get("img"))) {
            newImg = member.getImg();
        } else {
            newImg = request.get("img");
        }

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
                "    m.nickname = ?, m.password = ?, m.img = ? " +
                "    WHERE m.memberId = ?";

        jdbcTemplate.update(memberRelatedEntitiesUpdateQuery,
                oldNickname, newNickname,
                oldNickname, newNickname,
                oldNickname, newNickname,
                oldNickname, newNickname,
                oldNickname, newNickname,
                newNickname, newPassword, newImg,
                member.getMemberId());
    }
}
