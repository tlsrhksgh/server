package com.example.server.post.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class AllNoticeResponse {
    private final Long id;
    private final String title;
    private final String createdDate;
    private final String postType;

    @QueryProjection
    public AllNoticeResponse(Long id, String title, String createdDate, String postType) {
       this.id = id;
       this.title = title;
       this.createdDate = createdDate;
       this.postType = postType;
    }
}
