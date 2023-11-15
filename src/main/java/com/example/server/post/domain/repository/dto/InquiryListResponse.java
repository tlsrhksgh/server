package com.example.server.post.domain.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.util.List;

@Getter
public class InquiryListResponse {
    private final Long id;
    private final String title;
    private final String createdDate;
    private final String statusType;
    private final List<ReplyDto> replies;

    @QueryProjection
    public InquiryListResponse(Long id, String title, String createdDate, String statusType, List<ReplyDto> replies) {
        this.id = id;
        this.title = title;
        this.createdDate = createdDate;
        this.statusType = statusType;
        this.replies = replies;
    }
}
