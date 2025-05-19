package com.shcho.myBlog.user.dto;

public record UserSignUpRequestDto(
        String username,
        String password,
        String nickname
) {
}
