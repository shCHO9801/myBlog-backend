package com.shcho.myBlog.common.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponseDto<T>(
        List<T> data,
        PaginationDto paginationDto
) {
    public static <T> PageResponseDto<T> from(Page<T> page) {
        return new PageResponseDto<>(page.getContent(), PaginationDto.from(page));
    }
}
