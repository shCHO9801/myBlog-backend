package com.shcho.myBlog.comment.dto;

import com.shcho.myBlog.comment.entity.Reply;

import java.time.LocalDateTime;

public record ReplyResponseDto(
        Long commentId,
        Long replyId,
        String userNickName,
        String content,
        boolean isDeleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReplyResponseDto from(Reply reply) {
        return new ReplyResponseDto(
                reply.getComment().getId(),
                reply.getId(),
                reply.getUser().getNickname(),
                reply.getContent(),
                reply.isDeleted(),
                reply.getCreatedAt(),
                reply.getUpdatedAt()
        );
    }
}
