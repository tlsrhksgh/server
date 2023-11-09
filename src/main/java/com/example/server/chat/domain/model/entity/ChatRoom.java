package com.example.server.chat.domain.model.entity;

import lombok.Getter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "chatRoom")
    private List<ChatMessage> messages;

    @OneToMany(mappedBy = "chatRoom")
    private Set<MemberChatRoom> memberChatRooms = new HashSet<>();
}
