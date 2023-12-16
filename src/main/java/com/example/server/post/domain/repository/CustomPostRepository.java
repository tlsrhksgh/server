package com.example.server.post.domain.repository;

import com.example.server.common.entity.DateFormatExpression;
import com.example.server.post.domain.Post;
import com.example.server.post.domain.constants.PostStatusType;
import com.example.server.post.domain.constants.PostType;
import com.example.server.post.domain.repository.dto.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.example.server.post.domain.QPost.post;
import static com.example.server.post.domain.QReply.reply;
import static com.example.server.post.domain.constants.PostType.INQUIRY;
import static com.example.server.post.domain.constants.PostType.NOTICE;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

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
                        post.content,
                        DateFormatExpression.formatDateTime(post.createdDate),
                        post.postType.stringValue()
                ))
                .from(post)
                .where(eqPostType(NOTICE.getPostType()))
                .orderBy(post.id.desc())
                .fetch();
    }

    public List<InquiryListResponse> findInquiryListByAccount(String nickname, String statusType, Integer period) {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime selectStartDate = period == 0 ? null : currentDate.minusMonths(period);


        return queryFactory
                .selectFrom(post)
                .leftJoin(reply)
                .on(post.id.eq(reply.post().id))
                .where(
                        eqPostAuthor(nickname),
                        eqPostType(INQUIRY.getPostType()),
                        eqStatusType(statusType),
                        post.modifiedDate.between(selectStartDate, currentDate)
                )
                .orderBy(post.id.desc())
                .transform(
                        groupBy(post.id).list(
                                new QInquiryListResponse(
                                        post.id,
                                        post.title,
                                        post.content,
                                        post.author,
                                        DateFormatExpression.formatDateTime(post.createdDate),
                                        post.statusType.stringValue(),
                                        list(new QReplyDto(
                                                reply.id,
                                                reply.title,
                                                reply.content,
                                                reply.author,
                                                DateFormatExpression.formatDateTime(reply.modifiedDate))
                                                .skipNulls()
                                        ))
                        )
                );
    }

    private BooleanExpression eqPostType(String type) {
        if (Objects.isNull(type)) {
            return null;
        }

        return post.postType.eq(PostType.valueOf(type));
    }

    private BooleanExpression eqStatusType(String type) {
        if (Objects.isNull(type)) {
            return null;
        }

        return post.statusType.eq(PostStatusType.valueOf(type));
    }

    private BooleanExpression eqPostAuthor(String nickname) {
        if (Objects.isNull(nickname)) {
            return null;
        }

        return post.author.eq(nickname);
    }
}
