package com.example.server.post.controller;

import com.example.server.post.domain.dto.AllNoticeResponse;
import com.example.server.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostService postService;

    @PostMapping("/inquiry")
    public void saveInquiry() {

    }

    @GetMapping("/inquiry/list/{account}")
    public void inquiryList(@PathVariable String account) {

    }

    @GetMapping("/inquiry/{id}")
    public void inquiryDetail(@PathVariable Long id) {

    }

    @GetMapping("/notice/all")
    public ResponseEntity<List<AllNoticeResponse>> findAllNotice() {
        return ResponseEntity.ok(postService.findAllTypeNotice());
    }

    @GetMapping("/notice/find/{id}")
    public ResponseEntity<String> noticeDetail(@PathVariable Long id,
                                               @RequestParam("type") String type) {
        return ResponseEntity.ok(postService.findNoticeContent(id, type));
    }
}
