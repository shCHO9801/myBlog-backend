package com.shcho.myBlog.user.service;

import com.shcho.myBlog.common.util.JwtProvider;
import com.shcho.myBlog.libs.exception.CustomException;
import com.shcho.myBlog.user.dto.UserLoginRequestDto;
import com.shcho.myBlog.user.dto.UserLoginResponseDto;
import com.shcho.myBlog.user.dto.UserSignUpRequestDto;
import com.shcho.myBlog.user.dto.UserSignUpResponseDto;
import com.shcho.myBlog.user.entity.Role;
import com.shcho.myBlog.user.entity.User;
import com.shcho.myBlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.shcho.myBlog.libs.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

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

        userRepository.save(user);

        return UserSignUpResponseDto.from(user);
    }

    private boolean existUserName(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private boolean existNickName(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public UserLoginResponseDto login(UserLoginRequestDto request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new CustomException(INVALID_CREDENTIAL));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(INVALID_CREDENTIAL);
        }

        // 인증 객체 생성
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성
        String token = jwtProvider.createToken(user.getUsername(), user.getRole().name());

        return new UserLoginResponseDto(token);
    }
}
