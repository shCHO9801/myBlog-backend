package com.shcho.myBlog.post.dto;

import com.shcho.myBlog.post.entity.Category;
import com.shcho.myBlog.post.entity.Post;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostResponseDto(
        Long postId,
        String title,
        String content,
        Category category,
        Long userId,
        LocalDateTime createdAt
) {
    public static PostResponseDto of(Post post) {
        return PostResponseDto.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .userId(post.getUser().getId())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
