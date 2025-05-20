package com.shcho.myBlog.mypage.controller;

import com.shcho.myBlog.mypage.dto.GetMyPageResponseDto;
import com.shcho.myBlog.mypage.service.MyPageService;
import com.shcho.myBlog.user.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping
    public ResponseEntity<GetMyPageResponseDto> getMyPage(
            @AuthenticationPrincipal CustomUserDetails userDetail
    ) {
        Long userId = userDetail.getUserId();
        return ResponseEntity.ok(myPageService.getMyPage(userId));
    }
}
