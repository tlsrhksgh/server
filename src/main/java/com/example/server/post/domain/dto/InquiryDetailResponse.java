package com.example.server.post.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class InquiryDetailResponse {
    private final Long id;
    private final String title;
    private final String content;

    @QueryProjection
    public InquiryDetailResponse(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
}
