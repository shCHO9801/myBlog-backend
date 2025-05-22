package com.shcho.myBlog.common.dto;

import lombok.Builder;

@Builder
public record FileUploadResponseDto(
        String url
) {
    public static FileUploadResponseDto from(String url) {
        return FileUploadResponseDto.builder()
                .url(url)
                .build();
    }
}
