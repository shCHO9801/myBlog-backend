package com.shcho.myBlog.user.dto;

import com.shcho.myBlog.user.entity.User;

import java.time.LocalDateTime;

public record UserSignUpResponseDto(
        String username,
        String nickname,
        LocalDateTime createdAt
) {
    public static UserSignUpResponseDto from(User user) {
        return new UserSignUpResponseDto(
                user.getUsername(),
                user.getNickname(),
                user.getCreatedAt()
        );
    }
}
