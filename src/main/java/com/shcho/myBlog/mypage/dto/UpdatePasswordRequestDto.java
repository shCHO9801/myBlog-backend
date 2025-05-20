package com.shcho.myBlog.mypage.dto;

public record UpdatePasswordRequestDto(
        String currentPassword,
        String newPassword
) {
}
