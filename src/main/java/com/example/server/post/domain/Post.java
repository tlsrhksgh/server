package com.example.server.post.domain;

import com.example.server.common.entity.BaseTimeEntity;
import com.example.server.post.domain.constants.PostStatusType;
import com.example.server.post.domain.constants.PostType;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType postType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatusType statusType;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies;

    public void updateStatusType(PostStatusType statusType) {
        this.statusType = statusType;
    }
}
