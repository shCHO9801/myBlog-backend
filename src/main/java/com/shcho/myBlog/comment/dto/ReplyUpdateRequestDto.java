package com.shcho.myBlog.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReplyUpdateRequestDto(
        @NotBlank
        @Size(max = 500)
        String content,
        String anonymousPassword
) {
}
