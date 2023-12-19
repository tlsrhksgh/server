package com.example.server.chat.domain.repository.custom;

import com.example.server.chat.domain.model.entity.ChatMessage;
import com.example.server.chat.domain.repository.dto.QRoomDetailResponse;
import com.example.server.chat.domain.repository.dto.RoomDetailResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.example.server.chat.domain.model.entity.QChatMessage.chatMessage;
import static com.example.server.chat.domain.model.entity.QMemberChatRoom.memberChatRoom;


@Repository
public class CustomChatMessageRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public CustomChatMessageRepository(JPAQueryFactory queryFactory) {
        super(ChatMessage.class);
        this.queryFactory = queryFactory;
    }

    @Transactional(readOnly = true)
    public List<RoomDetailResponse> findRoomDetailByRoomId(Long roomId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//        return queryFactory
//                .select(new QRoomDetailResponse(
//                        chatMessage.message,
//                        memberChatRoom.member().nickname,
//                        chatMessage.sentDate,
//                        memberChatRoom.member().img,
//                        memberChatRoom.member().level
//                ))
//                .from(chatMessage)
//                .innerJoin(memberChatRoom)
//                .on(chatMessage.chatRoom().id.eq(memberChatRoom.chatRoom().id))
//                .where(chatMessage.chatRoom().id.eq(roomId)
//                        .and(memberChatRoom.participateDate.lt(LocalDateTime.parse(chatMessage.sentDate, formatter))))
//                .orderBy(chatMessage.id.desc())
//                .fetch();
        return null;
    }
}
