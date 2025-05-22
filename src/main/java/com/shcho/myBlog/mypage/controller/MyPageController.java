package com.shcho.myBlog.mypage.controller;

import com.shcho.myBlog.mypage.dto.GetMyPageResponseDto;
import com.shcho.myBlog.mypage.dto.UpdateNicknameRequestDto;
import com.shcho.myBlog.mypage.dto.UpdatePasswordRequestDto;
import com.shcho.myBlog.mypage.dto.UpdateProfileImageRequestDto;
import com.shcho.myBlog.mypage.service.MyPageService;
import com.shcho.myBlog.user.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<?> existsNickname(
            @PathVariable String nickname
    ) {
        return ResponseEntity.ok(myPageService.existsNickname(nickname));
    }

    @PatchMapping("/nickname")
    public ResponseEntity<String> updateNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdateNicknameRequestDto request
    ) {
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(myPageService.updateNickname(userId, request.nickname()));
    }

    @PatchMapping("/password")
    public ResponseEntity<String> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdatePasswordRequestDto request
    ) {
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(myPageService.updatePassword(userId, request.currentPassword(), request.newPassword()));
    }

    @PatchMapping("/profile-image")
    public ResponseEntity<String> updateProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdateProfileImageRequestDto request
    ) {
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(myPageService.updateProfileImage(userId, request.fileUrl()));
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(myPageService.withdraw(userId));
    }
}
