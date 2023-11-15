package com.example.server.post.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ReplyDto {
    private final Long id;
    private final String title;
    private final String content;
    private final String modifiedDate;

    @QueryProjection
    public ReplyDto(Long id, String title, String content, String modifiedDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.modifiedDate = modifiedDate;
    }
}
