package com.shcho.myBlog.comment.dto;

import com.shcho.myBlog.comment.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;

public record CommentWithRepliesDto(
        Long commentId,
        String userNickName,
        String content,
        boolean isDeleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ReplyResponseDto> replies
) {
    public static CommentWithRepliesDto from (Comment comment, List<ReplyResponseDto> replies) {
        return new CommentWithRepliesDto(
                comment.getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.isDeleted(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                replies
        );
    }
}
