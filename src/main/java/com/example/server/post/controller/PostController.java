package com.example.server.post.controller;

import com.example.server.post.domain.repository.dto.AllNoticeResponse;
import com.example.server.post.domain.repository.dto.InquiryListResponse;
import com.example.server.post.service.PostService;
import com.example.server.post.service.dto.PostSaveRequest;
import com.example.server.post.service.dto.ReplySaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/post")
@RestController
public class PostController {
    private final PostService postService;

    @PostMapping("/inquiry")
    public ResponseEntity<Void> savePost(@RequestBody PostSaveRequest saveRequest) {
        postService.savePost(saveRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/inquiry/reply")
    public ResponseEntity<Void> saveReply(@RequestBody ReplySaveRequest saveRequest) {
        postService.saveReply(saveRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/inquiry/list/{account}")
    public ResponseEntity<List<InquiryListResponse>> inquiryReplyList(@PathVariable String account) {
        return ResponseEntity.ok(postService.findInquiryWithReplyList(account));
    }

    @GetMapping("/notice/all")
    public ResponseEntity<List<AllNoticeResponse>> findAllNotice() {
        return ResponseEntity.ok(postService.findAllTypeNotice());
    }

    @GetMapping("/notice/find/{id}")
    public ResponseEntity<String> noticeDetail(@PathVariable Long id) {
        return ResponseEntity.ok(postService.findNoticeContent(id));
    }
}
