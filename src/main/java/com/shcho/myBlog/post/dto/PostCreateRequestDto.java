package com.shcho.myBlog.post.dto;

import lombok.Builder;

@Builder
public record PostCreateRequestDto(
        String title,
        String content,
        Long categoryId
) {
}
