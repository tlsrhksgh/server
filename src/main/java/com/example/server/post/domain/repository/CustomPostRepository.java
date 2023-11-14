package com.example.server.post.domain.repository;

import com.example.server.common.entity.DateFormatExpression;
import com.example.server.post.domain.Post;
import com.example.server.post.domain.PostType;
import com.example.server.post.domain.dto.AllNoticeResponse;
import com.example.server.post.domain.dto.QAllNoticeResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.example.server.post.domain.PostType.NOTICE;
import static com.example.server.post.domain.QPost.post;

@Repository
public class CustomPostRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public CustomPostRepository(JPAQueryFactory queryFactory) {
        super(Post.class);
        this.queryFactory = queryFactory;
    }

    public List<AllNoticeResponse> findAllNotice() {
        return queryFactory
                .select(new QAllNoticeResponse(
                        post.id,
                        post.title,
                        DateFormatExpression.formatDateTime(post.createdDate),
                        post.type.stringValue()
                ))
                .from(post)
                .where(eqType(NOTICE.getType()))
                .orderBy(post.id.desc())
                .fetch();
    }

    public String findNoticeContentByIdAndType(Long postId, String type) {
        return queryFactory
                .select(post.content)
                .from(post)
                .where(
                        post.id.eq(postId),
                        eqType(type)
                )
                .fetchOne();
    }

    private BooleanExpression eqType(String type) {
        if (Objects.isNull(type)) {
            return null;
        }

        return post.type.eq(PostType.valueOf(type));
    }
}
