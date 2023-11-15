package com.example.server.post.service.dto;

import com.example.server.post.domain.constants.PostType;
import lombok.*;

@Getter
@AllArgsConstructor
public class PostSaveRequest {
    private final String author;
    private final String title;
    private final String content;
    private final PostType type;
}
