package com.example.server.post.domain.repository;

import com.example.server.post.domain.Post;
import com.example.server.post.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

}
