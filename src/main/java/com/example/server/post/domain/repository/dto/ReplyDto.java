package com.example.server.post.domain.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class ReplyDto {
    private final Long id;
    private final String author;
    private final String title;
    private final String content;
    private final String modifiedDate;

    @QueryProjection
    public ReplyDto(Long id, String title, String content, String author, String modifiedDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.modifiedDate = modifiedDate;
    }
}
