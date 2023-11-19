package com.example.server.post.service;

import com.example.server.common.CodeConst;
import com.example.server.common.CommonResponse;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.server.post.domain.constants.PostStatusType.*;

@RequiredArgsConstructor
@Service
public class PostService {
    private final CustomPostRepository customPostRepository;
    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;

    public CommonResponse savePost(PostSaveRequest saveRequest) {
        PostStatusType statusType = saveRequest.getType().getPostType().equals("NOTICE") ?
                POSTED : WAITING;

        postRepository.save(Post.builder()
                        .author(saveRequest.getAuthor())
                        .content(saveRequest.getContent())
                        .title(saveRequest.getTitle())
                        .postType(saveRequest.getType())
                        .statusType(statusType)
                        .build()
        );

        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .build();
    }

    @Transactional
    public CommonResponse saveReply(ReplySaveRequest saveRequest) {
        Post post = postRepository.findById(saveRequest.getPostId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 글 입니다."));

        post.updateStatusType(COMPLETE);

        replyRepository.save(Reply.builder()
                        .author(saveRequest.getAuthor())
                        .content(saveRequest.getContent())
                        .title(saveRequest.getTitle())
                        .post(post)
                        .build()
        );

        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .build();
    }

    @Transactional(readOnly = true)
    public CommonResponse findInquiryWithReplyList(String account, String statusType, Integer period) {
        List<InquiryListResponse> responses = customPostRepository
                .findInquiryListByAccount(account, statusType, period);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("inquiryList", responses);
        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .data(resultMap)
                .build();
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public CommonResponse findNoticeContent(Long postId) {
        String noticeContent = customPostRepository.findNoticeContentByIdAndType(postId);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("noticeContent", noticeContent);
        return CommonResponse.builder()
                .resultCode(CodeConst.SUCCESS_CODE)
                .resultMessage(CodeConst.SUCCESS_MESSAGE)
                .data(resultMap)
                .build();
    }
}
