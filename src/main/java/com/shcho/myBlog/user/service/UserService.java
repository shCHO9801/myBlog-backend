package com.shcho.myBlog.user.service;

import com.shcho.myBlog.libs.exception.CustomException;
import com.shcho.myBlog.user.dto.UserSignUpRequestDto;
import com.shcho.myBlog.user.dto.UserSignUpResponseDto;
import com.shcho.myBlog.user.entity.Role;
import com.shcho.myBlog.user.entity.User;
import com.shcho.myBlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.shcho.myBlog.libs.exception.ErrorCode.NICKNAME_ALREADY_EXISTS;
import static com.shcho.myBlog.libs.exception.ErrorCode.USER_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSignUpResponseDto signUp(UserSignUpRequestDto request) {

        if (existUserName(request.username())) {
            throw new CustomException(USER_ALREADY_EXISTS);
        }

        if (existNickName(request.nickname())) {
            throw new CustomException(NICKNAME_ALREADY_EXISTS);
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .role(Role.USER)
                .build();

        return UserSignUpResponseDto.from(user);
    }

    private boolean existUserName(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private boolean existNickName(String nickname) {
        return userRepository.findAll().stream()
                .anyMatch(u -> u.getNickname().equals(nickname));
    }
}
