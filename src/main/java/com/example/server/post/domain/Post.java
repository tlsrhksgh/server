package com.example.server.post.domain;

import com.example.server.common.entity.BaseTimeEntity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType type;
}
