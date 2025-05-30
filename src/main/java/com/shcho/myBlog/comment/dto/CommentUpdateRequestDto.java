package com.shcho.myBlog.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequestDto(
        @NotBlank
        @Size(max = 500)
        String content,
        String anonymousPassword
) {
}
