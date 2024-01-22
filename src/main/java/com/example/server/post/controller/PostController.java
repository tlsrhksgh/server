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
        CommonResponse response = postService.savePost(saveRequest);

        return ResponseEntity.status(Integer.parseInt(response.getResultCode()))
                .body(response);
    }

    @PostMapping("/inquiry/reply")
    public ResponseEntity<CommonResponse> saveReply(@RequestBody ReplySaveRequest saveRequest) {
        CommonResponse response = postService.saveReply(saveRequest);

        return ResponseEntity.status(Integer.parseInt(response.getResultCode()))
                .body(response);
    }

    @GetMapping("/inquiry/list/{account}")
    public ResponseEntity<CommonResponse> inquiryReplyList(
            @PathVariable String account,
            @RequestParam(required = false) String statusType,
            @RequestParam(required = false, defaultValue = "0") Integer period) {
        if(period > 12) {
            throw new RuntimeException("조회 기간을 초과하였습니다.");
        }

        return ResponseEntity.ok(postService.findInquiryWithReplyList(account, statusType, period));
    }

    @GetMapping("/notice/all")
    public ResponseEntity<CommonResponse> findAllNotice() {
        return ResponseEntity.ok(postService.findAllTypeNotice());
    }
}
