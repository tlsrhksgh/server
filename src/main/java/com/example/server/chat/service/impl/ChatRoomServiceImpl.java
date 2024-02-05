package com.example.server.chat.service.impl;

import com.example.server.chat.constants.DeleteRoomType;
import com.example.server.chat.domain.model.entity.ChatRoom;
import com.example.server.chat.domain.model.entity.MemberChatRoom;
import com.example.server.chat.domain.repository.ChatRoomRepository;
import com.example.server.chat.domain.repository.MemberChatRoomRepository;
import com.example.server.chat.domain.repository.custom.CustomChatMessageRepository;
import com.example.server.chat.domain.repository.custom.CustomMemberChatRoomRepository;
import com.example.server.chat.domain.repository.dto.ChatRoomListResponse;
import com.example.server.chat.domain.repository.dto.RoomDetailResponse;
import com.example.server.chat.service.ChatRoomService;
import com.example.server.common.CodeConst;
import com.example.server.common.CommonResponse;
import com.example.server.member.repository.CustomMemberRepository;
import com.example.server.member.repository.Member;
import com.example.server.promise.Promise;
import com.example.server.promise.repository.PromiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.server.chat.constants.DeleteRoomType.*;

@RequiredArgsConstructor
@Service
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final CustomMemberRepository customMemberRepository;
    private final CustomMemberChatRoomRepository customMemberChatRoomRepository;
    private final CustomChatMessageRepository customChatMessageRepository;
    private final PromiseRepository promiseRepository;

    @Override
    @Transactional
    public void createChatRoom(Promise promise, List<Member> promiseMembers) {
        ChatRoom room = ChatRoom.builder()
                .title(promise.getTitle())
                .promiseId(promise.getId())
                .build();

        chatRoomRepository.save(room);

        List<MemberChatRoom> memberChatRooms = new ArrayList<>();

        promiseMembers.forEach(m -> memberChatRooms.add(MemberChatRoom.builder()
                .member(m)
                .chatRoom(room)
                .participateDate(LocalDateTime.now())
                .build()));

        memberChatRoomRepository.saveAll(memberChatRooms);
    }

    @Override
    public CommonResponse findAllChatRoom(String account) {
        if(Objects.isNull(account)) {
            return CommonResponse.builder()
                    .resultCode(CodeConst.MEMBER_NOT_FOUND_CODE)
                    .resultMessage(CodeConst.MEMBER_NOT_FOUND_MESSAGE)
                    .build();
        }
        Member member = customMemberRepository.findMemberByAccount(account);

        List<ChatRoomListResponse> chatRoomList = customMemberChatRoomRepository.
                findAllChatRoomByAccount(member.getNickname());
        Map<String, Object> data = new HashMap<>();
        data.put("chatRoomList", chatRoomList);

        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .data(data)
                .build();
    }

    @Override
    public CommonResponse findChatRoomDetail(Long roomId, String account) {
        MemberChatRoom memberChatRoom = memberChatRoomRepository.findByChatRoom_IdAndMember_Account(roomId, account);

        if(Objects.isNull(memberChatRoom)) {
            return CommonResponse.builder()
                    .resultCode(CodeConst.IS_NOT_PARTICIPANT_OR_CHATROOM_NOT_FOUND_CODE)
                    .resultMessage(CodeConst.IS_NOT_PARTICIPANT_OR_CHATROOM_NOT_FOUND_MESSAGE)
                    .build();
        }

        List<RoomDetailResponse> roomDetailResponses = customChatMessageRepository.findRoomDetailByRoomId(roomId);
        String leader = promiseRepository.findLeaderByPromiseId(roomId);

        Map<String, Object> result = new HashMap<>();
        result.put("leader", leader);
        result.put("messages", roomDetailResponses);

        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .data(result)
                .build();
    }


    @Override
    @Transactional
    public void deleteChatroom(Long promiseId, Member member, DeleteRoomType type) {
        if(type.equals(DELETE)) {
            customMemberChatRoomRepository.deleteMemberChatroomByRoomId(promiseId);
            chatRoomRepository.deleteById(promiseId);
        } else if(type.equals(EXIT)) {
            int participatedMemberCount = customMemberChatRoomRepository.
                    countParticipatedMemberByChatRoomId(promiseId);

            memberChatRoomRepository.deleteMemberChatRoomByChatRoom_IdAndMember_MemberId(promiseId, member.getMemberId());

            if(participatedMemberCount <= 1) {
                chatRoomRepository.deleteById(promiseId);
            }
        }
    }

    @Override
    @Transactional
    public void inviteMembersToChatRoom(Long promiseId, String memberNickname) {
        Long countMemberChatRoom = customMemberChatRoomRepository.
                findMemberChatRoomByPromiseIdAndNickname(promiseId, memberNickname);

        if(countMemberChatRoom == 0) {
            ChatRoom chatRoom = chatRoomRepository.findByPromiseId(promiseId);
            Member member = customMemberRepository.findMemberByNickname(memberNickname);

            memberChatRoomRepository.save(
                    MemberChatRoom.builder()
                            .member(member)
                            .chatRoom(chatRoom)
                            .participateDate(LocalDateTime.now())
                            .build());
        }
    }
}
