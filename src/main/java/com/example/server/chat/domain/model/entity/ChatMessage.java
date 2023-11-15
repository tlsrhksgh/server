package com.example.server.chat.domain.model.entity;

import com.example.server.common.entity.BaseTimeEntity;

import javax.persistence.*;

@Entity
public class ChatMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author;
    private String message;

    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;
}
