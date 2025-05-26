package com.shcho.myBlog.post.dto;

import com.shcho.myBlog.post.entity.Category;

public record PostCreateRequestDto (
        String title,
        String content,
        Category category
){
}
