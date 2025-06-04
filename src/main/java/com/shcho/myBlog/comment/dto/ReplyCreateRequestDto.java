package com.shcho.myBlog.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ReplyCreateRequestDto(
        @NotBlank
        @Size(max = 500)
        String content,

        String anonymousName,
        String anonymousPassword
) {
}
