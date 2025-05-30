package com.shcho.myBlog.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReplyCreateRequestDto(
        @NotBlank
        @Size(max = 500)
        String content,

        String anonymousName,
        String anonymousPassword
) {
}
