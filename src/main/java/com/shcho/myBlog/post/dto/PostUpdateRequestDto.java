package com.shcho.myBlog.post.dto;

import lombok.Builder;

@Builder
public record PostUpdateRequestDto(
        String title,
        String content,
        Long categoryId
) {
}
