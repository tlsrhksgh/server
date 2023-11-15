package com.example.server.post.service;

import com.example.server.post.domain.Post;
import com.example.server.post.domain.Reply;
import com.example.server.post.domain.constants.PostStatusType;
import com.example.server.post.domain.dto.AllNoticeResponse;
import com.example.server.post.domain.dto.InquiryListResponse;
import com.example.server.post.domain.repository.CustomPostRepository;
import com.example.server.post.domain.repository.PostRepository;
import com.example.server.post.domain.repository.ReplyRepository;
import com.example.server.post.service.dto.PostSaveRequest;
import com.example.server.post.service.dto.ReplySaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.server.post.domain.constants.PostStatusType.*;

@RequiredArgsConstructor
@Service
public class PostService {
    private final CustomPostRepository customPostRepository;
    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;

    public void savePost(PostSaveRequest saveRequest) {
        PostStatusType statusType = saveRequest.getType().getPostType().equals("NOTICE") ?
                POSTED : WAITING;

        postRepository.save(
                Post.builder()
                        .author(saveRequest.getAuthor())
                        .content(saveRequest.getContent())
                        .title(saveRequest.getTitle())
                        .postType(saveRequest.getType())
                        .statusType(statusType)
                        .build()
        );
    }

    @Transactional
    public void saveReply(ReplySaveRequest saveRequest) {
        Post post = postRepository.findById(saveRequest.getPostId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 글 입니다."));

        post.updateStatusType(COMPLETE);

        replyRepository.save(
                Reply.builder()
                        .author(saveRequest.getAuthor())
                        .content(saveRequest.getContent())
                        .title(saveRequest.getTitle())
                        .post(post)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<InquiryListResponse> findInquiryWithReplyList(String account) {
        return customPostRepository.findInquiryListByAccount(account);
    }

    @Transactional(readOnly = true)
    public List<AllNoticeResponse> findAllTypeNotice() {

        return customPostRepository.findAllNotice();
    }

    @Transactional(readOnly = true)
    public String findNoticeContent(Long postId) {

        return customPostRepository.findNoticeContentByIdAndType(postId);
    }
}
