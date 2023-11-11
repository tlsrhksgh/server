package com.example.server.chat.domain.model.entity;

import com.example.server.chat.domain.model.BaseTimeEntity;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private int total;

    private String lastMessage;

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatMessage> messages;

    @OneToMany(mappedBy = "chatRoom")
    private Set<MemberChatRoom> memberChatRooms = new HashSet<>();
}
