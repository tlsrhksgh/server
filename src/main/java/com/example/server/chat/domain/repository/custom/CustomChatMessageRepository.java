package com.example.server.chat.domain.repository.custom;

import com.example.server.chat.domain.model.entity.ChatMessage;
import com.example.server.chat.domain.repository.dto.QRoomDetailResponse;
import com.example.server.chat.domain.repository.dto.RoomDetailResponse;
import com.example.server.common.entity.DateFormatExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.server.chat.domain.model.entity.QChatMessage.chatMessage;
import static com.example.server.member.QMember.member;
import static com.example.server.promise.QPromise.promise;


@Repository
public class CustomChatMessageRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public CustomChatMessageRepository(JPAQueryFactory queryFactory) {
        super(ChatMessage.class);
        this.queryFactory = queryFactory;
    }

    @Transactional(readOnly = true)
    public List<RoomDetailResponse> findRoomDetailByRoomId(Long roomId) {
        return queryFactory
                .select(new QRoomDetailResponse(
                        chatMessage.message,
                        member.nickname,
                        chatMessage.sentDate,
                        member.img,
                        member.level
                ))
                .from(chatMessage)
                .leftJoin(member)
                .on(chatMessage.author.eq(member.nickname))
                .leftJoin(promise)
                .on(promise.id.eq(roomId))
                .where(chatMessage.chatRoom().id.eq(roomId))
                .orderBy(chatMessage.id.desc())
                .fetch();
    }
}
