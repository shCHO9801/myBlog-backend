package com.shcho.myBlog.post.dto;

import lombok.Builder;

@Builder
public record CategoryCreateRequestDto(
        String name
) {
}
