package com.shcho.myBlog.post.dto;

import com.shcho.myBlog.post.entity.Post;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostResponseDto(
        Long postId,
        String title,
        String content,
        String categoryName,
        Long userId,
        LocalDateTime createdAt
) {
    public static PostResponseDto of(Post post) {
        return PostResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .categoryName(post.getCategory().getName())
                .userId(post.getUser().getId())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
