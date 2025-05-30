package com.shcho.myBlog.comment.repository;

import com.shcho.myBlog.comment.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
