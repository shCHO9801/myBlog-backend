package com.shcho.myBlog.mypage.service;

import com.shcho.myBlog.mypage.dto.GetMyPageResponseDto;
import com.shcho.myBlog.user.entity.User;
import com.shcho.myBlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;

    public GetMyPageResponseDto getMyPage(Long userId) {
        User user = userRepository.getReferenceById(userId);
        return GetMyPageResponseDto.from(user);
    }
}
