package com.example.server.chat.service;

import com.example.server.chat.domain.model.entity.ChatRoom;
import com.example.server.chat.domain.model.entity.MemberChatRoom;
import com.example.server.chat.domain.repository.ChatRoomRepository;
import com.example.server.chat.domain.repository.MemberChatRoomRepository;
import com.example.server.member.Member;
import com.example.server.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final MemberRepository memberRepository;

    public Long createRoom() {
        ChatRoom room = new ChatRoom();

        chatRoomRepository.save(room);

        Member member = memberRepository.findByAccount("testuser1")
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자 입니다."));

        Member member2 = memberRepository.findByAccount("testuser2")
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자 입니다."));

        MemberChatRoom memberChatRoom = MemberChatRoom.builder()
                .member(member)
                .chatRoom(room)
                .build();

        MemberChatRoom memberChatRoom2 = MemberChatRoom.builder()
                .member(member2)
                .chatRoom(room)
                .build();

        memberChatRoomRepository.save(memberChatRoom);
        memberChatRoomRepository.save(memberChatRoom2);

        return room.getId();
    }
}
