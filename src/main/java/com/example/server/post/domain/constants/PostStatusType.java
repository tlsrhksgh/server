package com.example.server.post.domain.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostStatusType {
    WAITING("waiting", "접수"),
    POSTED("posted", "공지사항 게시"),
    COMPLETE("complete", "답변완료");

    private final String statusType;
    private final String name;
}
