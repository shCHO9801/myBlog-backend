package com.shcho.myBlog.user.controller;

import com.shcho.myBlog.user.dto.UserLoginRequestDto;
import com.shcho.myBlog.user.dto.UserLoginResponseDto;
import com.shcho.myBlog.user.dto.UserSignUpRequestDto;
import com.shcho.myBlog.user.dto.UserSignUpResponseDto;
import com.shcho.myBlog.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserSignUpResponseDto> signUp(
            @RequestBody UserSignUpRequestDto requestDto
    ) {
        return ResponseEntity.ok(userService.signUp(requestDto));
    }


    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(
            @RequestBody UserLoginRequestDto requestDto
    ) {
        return ResponseEntity.ok(userService.login(requestDto));
    }
}
