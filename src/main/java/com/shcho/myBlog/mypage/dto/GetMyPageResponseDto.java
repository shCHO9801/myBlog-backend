package com.shcho.myBlog.mypage.dto;

import com.shcho.myBlog.user.entity.Role;
import com.shcho.myBlog.user.entity.User;
import lombok.Builder;

@Builder
public record GetMyPageResponseDto(
        String username,
        String nickname,
        Role role
) {
    public static GetMyPageResponseDto from(User user) {
        return GetMyPageResponseDto.builder()
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }
}
