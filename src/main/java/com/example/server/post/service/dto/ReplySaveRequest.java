package com.example.server.post.service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReplySaveRequest {
    private final Long postId;
    private final String author;
    private final String title;
    private final String content;
}
