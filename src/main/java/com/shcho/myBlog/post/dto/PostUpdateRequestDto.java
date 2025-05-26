package com.shcho.myBlog.post.dto;

public record PostUpdateRequestDto(
        String title,
        String content,
        Long categoryId
) {
}
