package com.example.server.chat.service;

import com.example.server.chat.constants.DeleteRoomType;
import com.example.server.common.CommonResponse;
import com.example.server.member.Member;
import com.example.server.promise.Promise;

import java.util.List;

public interface ChatRoomService {
    void createChatRoom(Promise promise, List<Member> currentUser);
    void deleteChatroom(Long roomId, Member member, DeleteRoomType type);
    void inviteMembersToChatRoom(Long roomId, String memberNickname);
    CommonResponse findChatRoomDetail(Long roomId, String account);
    public CommonResponse findAllChatRoom(String account);
}
