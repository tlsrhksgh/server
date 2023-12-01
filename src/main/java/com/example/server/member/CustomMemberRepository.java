package com.example.server.member;

import com.example.server.chat.service.dto.CreateRoomForm;
import com.querydsl.core.types.dsl.BooleanExpression;
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

@Transactional
@Repository
public class CustomMemberRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;
    private final JdbcTemplate jdbcTemplate;

    public CustomMemberRepository(JPAQueryFactory queryFactory, JdbcTemplate jdbcTemplate) {
        super(Member.class);
        this.queryFactory = queryFactory;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Member findMemberByAccount(String account) {
        return queryFactory
                .selectFrom(member)
                .where(member.account.eq(account))
                .leftJoin(member.roles, authority)
                .fetchJoin()
                .fetchOne();
    }


    public Member findMemberByNickname(String nickname) {
        return queryFactory
                .selectFrom(member)
                .where(member.nickname.eq(nickname))
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

    @Modifying(clearAutomatically = true)
    public void updateMemberWithOrderEntities(Map<String, String> request, Member member) {
        String oldNickname = member.getNickname();

        String newNickname = request.get("nickname") == null ?
                oldNickname : request.get("nickname");

        String newPassword = request.get("password") == null ?
                member.getPassword() : request.get("password");

        String newImg = request.get("img") == null ? member.getImg() : request.get("img");

        String memberUpdateQuery = "UPDATE Member m " +
                "SET m.nickname = ?, m.password = ?, m.img = ? " +
                "WHERE m.memberId = ?";

        String memberRelatedEntitiesUpdateQuery = "UPDATE Member m " +
                "JOIN Friend f ON m.nickname = f.requester OR m.nickname = f.respondent " +
                "JOIN Post po ON m.nickname = po.author " +
                "JOIN Promise pr ON m.nickname = pr.leader " +
                "JOIN PromiseMember pm ON pm.promise_id = pr.id " +
                "SET " +
                "    f.requester = CASE WHEN f.requester = ? THEN ? ELSE f.requester END, " +
                "    f.respondent = CASE WHEN f.respondent = ? THEN ? ELSE f.respondent END, " +
                "    po.author = CASE WHEN po.author = ? THEN ? ELSE po.author END, " +
                "    pr.leader = CASE WHEN pr.leader = ? THEN ? ELSE pr.leader END, " +
                "    pm.nickname = CASE WHEN pm.nickname = ? THEN ? ELSE pm.nickname END " +
                "    WHERE m.memberId = ?";

        jdbcTemplate.update(memberRelatedEntitiesUpdateQuery,
                oldNickname, newNickname,
                oldNickname, newNickname,
                oldNickname, newNickname,
                oldNickname, newNickname,
                oldNickname, newNickname,
                member.getMemberId());
        jdbcTemplate.update(memberUpdateQuery, newNickname, newPassword, newImg, member.getMemberId());

    }

    private BooleanExpression inEqMemberAccount(String user1, String user2) {
        if (Objects.isNull(user1) || Objects.isNull(user2)) {
            return null;
        }

        return member.account.in(user1, user2);
    }
}
