package com.shcho.myBlog.common.dto;

import org.springframework.data.domain.Page;

public record PaginationDto(
        int currentPage,
        int limit,
        long totalItems,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {
    public static PaginationDto from(Page<?> page) {
        return new PaginationDto(
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}
