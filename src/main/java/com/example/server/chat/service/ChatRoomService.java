package com.example.server.chat.service;

import com.example.server.chat.domain.model.entity.ChatRoom;
import com.example.server.chat.domain.model.entity.MemberChatRoom;
import com.example.server.chat.domain.repository.ChatRoomRepository;
import com.example.server.chat.domain.repository.MemberChatRoomRepository;
import com.example.server.chat.dto.CreateRoomForm;
import com.example.server.member.CustomMemberRepository;
import com.example.server.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final CustomMemberRepository customMemberRepository;

    public Long createRoom(CreateRoomForm form) {
        ChatRoom room = new ChatRoom();

        chatRoomRepository.save(room);

        List<Member> members = customMemberRepository.findTwoMember(form);

        if(members.size() <= 1) {
            throw new RuntimeException("초대하려는 사용자가 존재하지 않습니다.");
        }

        List<MemberChatRoom> memberChatRooms = Arrays.asList(
                MemberChatRoom.builder().member(members.get(0)).chatRoom(room).build(),
                MemberChatRoom.builder().member(members.get(1)).chatRoom(room).build()
        );

        memberChatRoomRepository.saveAll(memberChatRooms);

        return room.getId();
    }
}
