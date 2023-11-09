package com.example.server.chat.domain.repository;

import com.example.server.chat.domain.model.entity.MemberChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {
}
