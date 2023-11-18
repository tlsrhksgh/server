package com.example.server.post.controller;

import com.example.server.common.CommonResponse;
import com.example.server.post.service.PostService;
import com.example.server.post.service.dto.PostSaveRequest;
import com.example.server.post.service.dto.ReplySaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/post")
@RestController
public class PostController {
    private final PostService postService;

    @PostMapping("/inquiry")
    public ResponseEntity<CommonResponse> savePost(@RequestBody PostSaveRequest saveRequest) {
        return ResponseEntity.ok(postService.savePost(saveRequest));
    }

    @PostMapping("/inquiry/reply")
    public ResponseEntity<CommonResponse> saveReply(@RequestBody ReplySaveRequest saveRequest) {
        return ResponseEntity.ok(postService.saveReply(saveRequest));
    }

    @GetMapping("/inquiry/list/{account}")
    public ResponseEntity<CommonResponse> inquiryReplyList(@PathVariable String account) {
        return ResponseEntity.ok(postService.findInquiryWithReplyList(account));
    }

    @GetMapping("/notice/all")
    public ResponseEntity<CommonResponse> findAllNotice() {
        return ResponseEntity.ok(postService.findAllTypeNotice());
    }

    @GetMapping("/notice/find/{id}")
    public ResponseEntity<CommonResponse> noticeDetail(@PathVariable Long id) {
        return ResponseEntity.ok(postService.findNoticeContent(id));
    }
}
