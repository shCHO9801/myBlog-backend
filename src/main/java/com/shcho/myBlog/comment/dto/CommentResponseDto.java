package com.shcho.myBlog.comment.dto;

import com.shcho.myBlog.comment.entity.Comment;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentResponseDto(
        Long commentId,
        String userNickName,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CommentResponseDto from (Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
