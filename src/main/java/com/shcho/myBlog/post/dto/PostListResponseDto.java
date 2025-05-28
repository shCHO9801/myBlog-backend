package com.shcho.myBlog.post.dto;

import com.shcho.myBlog.post.entity.Post;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostListResponseDto(
        Long postId,
        String title,
        String thumbnailUrl,
        String userNickname,
        LocalDateTime createdAt
) {
    public static PostListResponseDto of(Post post) {
        return new PostListResponseDto(
                post.getId(),
                post.getTitle(),
                post.getThumbnailUrl(),
                post.getUser().getNickname(),
                post.getCreatedAt()
        );
    }
}
