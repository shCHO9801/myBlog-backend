package com.shcho.myBlog.user.dto;

public record UserLoginRequestDto(
        String username,
        String password
) {
}
