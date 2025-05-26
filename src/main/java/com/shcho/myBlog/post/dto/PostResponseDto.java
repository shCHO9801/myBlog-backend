package com.shcho.myBlog.post.dto;

import com.shcho.myBlog.post.entity.Category;

import java.time.LocalDateTime;

public record PostResponseDto(
        Long postId,
        String title,
        String content,
        Category category,
        Long userId,
        LocalDateTime createdAt
) {
}
