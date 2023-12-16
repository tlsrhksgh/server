package com.example.server.chat.domain.repository.custom;

import com.example.server.chat.domain.model.entity.ChatRoom;
import com.example.server.chat.domain.repository.dto.ChatRoomListResponse;
import com.example.server.chat.domain.repository.dto.QChatRoomListResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.server.chat.domain.model.entity.QMemberChatRoom.memberChatRoom;

@Repository
public class CustomMemberChatRoomRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public CustomMemberChatRoomRepository(JPAQueryFactory queryFactory) {
        super(ChatRoom.class);
        this.queryFactory = queryFactory;
    }

    public List<ChatRoomListResponse> findAllChatRoom(String account) {
        return queryFactory
                .select(new QChatRoomListResponse(
                        memberChatRoom.chatRoom().title.as("title"),
                        memberChatRoom.chatRoom().total.as("total"),
                        memberChatRoom.chatRoom().lastMessage.as("lastMessage"),
                        memberChatRoom.chatRoom().modifiedDate.as("modifiedDate")
                        ))
                .from(memberChatRoom)
                .where(memberChatRoom.member().account.eq(account))
                .orderBy(memberChatRoom.chatRoom().id.desc())
                .fetch();
    }
}
