package com.shcho.myBlog.comment.dto;

import lombok.Builder;

@Builder
public record ReplyDeleteRequestDto(
        String anonymousPassword
) {
}
