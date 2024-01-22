package com.example.server.post.service;

import com.example.server.common.CodeConst;
import com.example.server.common.CommonResponse;
import com.example.server.member.repository.CustomMemberRepository;
import com.example.server.member.repository.Member;
import com.example.server.post.domain.Post;
import com.example.server.post.domain.Reply;
import com.example.server.post.domain.constants.PostStatusType;
import com.example.server.post.domain.repository.CustomPostRepository;
import com.example.server.post.domain.repository.PostRepository;
import com.example.server.post.domain.repository.ReplyRepository;
import com.example.server.post.domain.repository.dto.AllNoticeResponse;
import com.example.server.post.domain.repository.dto.InquiryListResponse;
import com.example.server.post.service.dto.PostSaveRequest;
import com.example.server.post.service.dto.ReplySaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.server.post.domain.constants.PostStatusType.*;

@RequiredArgsConstructor
@Service
public class PostService {
    private final CustomPostRepository customPostRepository;
    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;
    private final CustomMemberRepository customMemberRepository;

    public CommonResponse savePost(PostSaveRequest saveRequest) {
        Member member = customMemberRepository.findMemberByNickname(saveRequest.getAuthor());

        if(Objects.isNull(member)) {
            return CommonResponse.builder()
                    .resultCode(CodeConst.MEMBER_NOT_FOUND_CODE)
                    .resultMessage(CodeConst.MEMBER_NOT_FOUND_MESSAGE)
                    .build();
        }

        PostStatusType statusType = saveRequest.getType().getPostType().equals("NOTICE") ?
                POSTED : WAITING;

        postRepository.save(Post.builder()
                        .content(saveRequest.getContent())
                        .title(saveRequest.getTitle())
                        .postType(saveRequest.getType())
                        .statusType(statusType)
                        .author(member.getNickname())
                        .build()
        );

        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .build();
    }

    public CommonResponse saveReply(ReplySaveRequest saveRequest) {
        Member member = customMemberRepository.findMemberByNickname(saveRequest.getAuthor());
        Post post = postRepository.findById(saveRequest.getPostId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 글 입니다."));

        if(Objects.isNull(member)) {
            return CommonResponse.builder()
                    .resultCode(CodeConst.MEMBER_NOT_FOUND_CODE)
                    .resultMessage(CodeConst.MEMBER_NOT_FOUND_MESSAGE)
                    .build();
        }

        post.updateStatusType(COMPLETE);

        replyRepository.save(Reply.builder()
                        .content(saveRequest.getContent())
                        .title(saveRequest.getTitle())
                        .author(member.getNickname())
                        .post(post)
                        .build());

        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .build();
    }

    public CommonResponse findInquiryWithReplyList(String account, String statusType, Integer period) {
        Member member = customMemberRepository.findMemberByAccount(account);

        List<InquiryListResponse> responses = new ArrayList<>();

        if(Objects.nonNull(member) && member.getRoles().get(0).getName().equals("ROLE_ADMIN")) {
            responses = customPostRepository.findInquiryListByAccount(null, null, period);
        } else if(Objects.nonNull(member)) {
            responses = customPostRepository.findInquiryListByAccount(member.getNickname(), statusType, period);
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("inquiryList", responses);
        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .data(resultMap)
                .build();
    }

    public CommonResponse findAllTypeNotice() {
        List<AllNoticeResponse> responses = customPostRepository.findAllNotice();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("noticeList", responses);
        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .data(resultMap)
                .build();
    }
}
