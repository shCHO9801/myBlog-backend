package com.shcho.myBlog.mypage.service;

import com.shcho.myBlog.libs.exception.CustomException;
import com.shcho.myBlog.user.entity.Role;
import com.shcho.myBlog.user.entity.User;
import com.shcho.myBlog.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.shcho.myBlog.libs.exception.ErrorCode.DUPLICATE_NICKNAME;
import static com.shcho.myBlog.libs.exception.ErrorCode.INVALID_PASSWORD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("마이페이지 서비스 테스트")
class MyPageServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MyPageService myPageService;

    public MyPageServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("닉네임이 존재 여부 - 존재할 경우 true 반환")
    void existsNicknameTrue() {
        // given
        String nickname = "nickname1";
        when(userRepository.existsByNickname(nickname)).thenReturn(true);

        // when
        boolean result = myPageService.existsNickname(nickname);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("닉네임이 존재 여부 - 존재하지 않을 경우 false 반환")
    void existsNicknameFalse() {
        // given
        String nickname = "nonExistNickname";
        when(userRepository.existsByNickname(nickname)).thenReturn(false);

        // when
        boolean result = myPageService.existsNickname(nickname);

        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("닉네임 수정 - 중복되지 않으면 수정 성공")
    void updateNicknameSuccess() {
        // given
        Long userId = 1L;
        String newNickname = "newNickname";
        User user = User.builder()
                .username("username")
                .password("password")
                .nickname("oldNickname")
                .role(Role.USER)
                .build();

        when(userRepository.existsByNickname(newNickname)).thenReturn(false);
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        // when
        String updatedNickname = myPageService.updateNickname(userId, newNickname);

        // then
        assertEquals("닉네임이 성공적으로 변경되었습니다.", updatedNickname);
    }

    @Test
    @DisplayName("닉네임 수정 - 중복일 경우 예외 발생")
    void updateNicknameFail() {
        // given
        Long userId = 1L;
        String newNickname = "newNickname";
        User user = User.builder()
                .username("username")
                .password("password")
                .nickname("oldNickname")
                .role(Role.USER)
                .build();

        when(userRepository.existsByNickname(newNickname)).thenReturn(true);
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> myPageService.updateNickname(userId, newNickname));

        assertEquals(DUPLICATE_NICKNAME, exception.getErrorCode());
    }

    @Test
    @DisplayName("비밀번호 변경 - 현재 비밀번호 일치 시 변경 성공")
    void updatePasswordSuccess() {
        // given
        Long userId = 1L;
        String currentPassword = "oldPassword";
        String newPassword = "newPassword";

        User user = User.builder()
                .username("username")
                .password("encoded_old_password")
                .nickname("nickname")
                .role(Role.USER)
                .build();

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(passwordEncoder.matches(currentPassword, user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encoded_new_password");

        // when
        String result = myPageService.updatePassword(userId, currentPassword, newPassword);

        // then
        assertEquals("비밀번호가 성공적으로 변경되었습니다.", result);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("비밀번호 변경 - 현재 비밀번호 불일치 시 예외 발생")
    void updatePasswordFail() {
        // given
        Long userId = 1L;
        String currentPassword = "wrongPassword";
        String newPassword = "newPassword";

        User user = User.builder()
                .username("username")
                .password("encoded_old_password")
                .nickname("nickname")
                .role(Role.USER)
                .build();

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(passwordEncoder.matches(currentPassword, user.getPassword())).thenReturn(false);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> myPageService.updatePassword(userId, currentPassword, newPassword));

        assertEquals(INVALID_PASSWORD, exception.getErrorCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("회원탈퇴 - 정상 처리 시 deletedAt, username, password, nickname 변경")
    void withdrawSuccess() {
        // given
        Long userId = 1L;

        User user = User.builder()
                .username("username")
                .password("password")
                .nickname("nickname")
                .role(Role.USER)
                .build();

        when(userRepository.getReferenceById(userId)).thenReturn(user);

        // when
        myPageService.withdraw(userId);

        // then
        assertNotNull(user.getDeletedAt());
        assertNull(user.getUsername());
        assertEquals("", user.getPassword());
        assertNull(user.getNickname());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("프로필 이미지 업데이트 성공")
    void updateProfileImageSuccess() {
        // given
        Long userId = 1L;
        String newImageUrl = "http://new-image-url";

        User user = User.builder()
                .username("username")
                .password("password")
                .nickname("nickname")
                .role(Role.USER)
                .build();

        when(userRepository.getReferenceById(userId)).thenReturn(user);

        // when
        String result = myPageService.updateProfileImage(userId, newImageUrl);

        // then
        assertEquals("프로필 사진이 성공적으로 변경되었습니다.", result);
        assertEquals(newImageUrl, user.getProfileImageUrl());
        verify(userRepository).save(user);
    }
}