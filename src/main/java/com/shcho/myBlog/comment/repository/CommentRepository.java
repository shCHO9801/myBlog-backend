package com.shcho.myBlog.comment.repository;

import com.shcho.myBlog.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = "replies")
    Page<Comment> findAllByPostIdOrderByCreatedAtAsc(Long postId, Pageable pageable);
}
