package com.shcho.myBlog.post.dto;

import com.shcho.myBlog.post.entity.Category;

public record CategoryResponseDto(
        Long categoryId,
        String name
) {
    public static CategoryResponseDto from(Category category) {
        return new CategoryResponseDto(category.getId(), category.getName());
    }
}
