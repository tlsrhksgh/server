package com.example.server.chat.domain.repository.custom;

import com.example.server.chat.domain.model.entity.ChatRoom;
import com.example.server.chat.domain.model.entity.MemberChatRoom;
import com.example.server.chat.domain.repository.dto.ChatRoomListResponse;
import com.example.server.chat.domain.repository.dto.QChatRoomListResponse;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.server.chat.domain.model.entity.QChatMessage.chatMessage;
import static com.example.server.chat.domain.model.entity.QMemberChatRoom.memberChatRoom;
import static com.example.server.member.repository.QMember.member;
import static com.example.server.chat.domain.model.entity.QChatRoom.chatRoom;
import static com.example.server.promise.QPromiseMember.promiseMember;

@Repository
@Transactional(readOnly = true)
public class CustomMemberChatRoomRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public CustomMemberChatRoomRepository(JPAQueryFactory queryFactory) {
        super(ChatRoom.class);
        this.queryFactory = queryFactory;
    }

    public List<ChatRoomListResponse> findAllChatRoomByAccount(String nickname) {
        return queryFactory
                .select(new QChatRoomListResponse(
                        memberChatRoom.chatRoom().id,
                        memberChatRoom.chatRoom().title,
                        ExpressionUtils.as(
                                JPAExpressions.select(promiseMember.count())
                                        .from(promiseMember)
                                        .where(promiseMember.promise().id.eq(memberChatRoom.chatRoom().promiseId)
                                                .and(promiseMember.accepted.eq("Y"))),
                                "totalMember"
                        ),
                        promiseMember.promise().date,
                        chatMessage.sentDate.max()
                ))
                .from(memberChatRoom)
                .innerJoin(promiseMember)
                .on(promiseMember.promise().id.eq(memberChatRoom.chatRoom().promiseId))
                .leftJoin(chatMessage)
                .on(memberChatRoom.chatRoom().id.eq(chatMessage.chatRoom().id))
                .where(memberChatRoom.member().nickname.eq(nickname)
                        .and(promiseMember.nickname.eq(nickname))
                        .and(promiseMember.accepted.eq("Y")))
                .groupBy(memberChatRoom.chatRoom().id)
                .orderBy(chatMessage.sentDate.max().desc())
                .fetch();
    }

    public Long findMemberChatRoomByPromiseIdAndNickname(Long promiseId, String memberNickname) {
        return queryFactory
                .select(memberChatRoom.count())
                .from(memberChatRoom)
                .innerJoin(member)
                .on(memberChatRoom.member().eq(member))
                .innerJoin(chatRoom)
                .on(memberChatRoom.chatRoom().eq(chatRoom))
                .where(memberChatRoom.member().nickname.eq(memberNickname)
                        .and(memberChatRoom.chatRoom().promiseId.eq(promiseId))
                )
                .fetchOne();
    }

    @Transactional
    public List<MemberChatRoom> findParticipatedMembers(Long roomId) {
        return queryFactory
                .selectFrom(memberChatRoom)
                .where(memberChatRoom.chatRoom().id.eq(roomId))
                .fetch();
    }

    @Transactional
    public Integer countParticipatedMemberByChatRoomId(Long promiseId) {
        return Math.toIntExact(queryFactory
                .select(memberChatRoom.member().memberId.count())
                .from(memberChatRoom)
                .where(memberChatRoom.chatRoom().promiseId.eq(promiseId))
                .fetchFirst());
    }

    @Modifying(clearAutomatically = true)
    @Transactional
    public void deleteMemberChatroomByRoomId(Long roomId) {
        queryFactory
                .delete(memberChatRoom)
                .where(memberChatRoom.chatRoom().id.eq(roomId))
                .execute();
    }
}
