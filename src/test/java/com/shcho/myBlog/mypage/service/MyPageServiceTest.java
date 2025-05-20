package com.shcho.myBlog.mypage.service;

import com.shcho.myBlog.libs.exception.CustomException;
import com.shcho.myBlog.libs.exception.ErrorCode;
import com.shcho.myBlog.user.entity.Role;
import com.shcho.myBlog.user.entity.User;
import com.shcho.myBlog.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.shcho.myBlog.libs.exception.ErrorCode.DUPLICATE_NICKNAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MyPageServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MyPageService myPageService;

    public MyPageServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("닉네임이 존재할 경우 true 반환")
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
    @DisplayName("닉네임이 존재하지 않을 경우 false 반환")
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
        assertEquals(newNickname, updatedNickname);
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

        // when&&then
        CustomException exception = assertThrows(CustomException.class,
                () -> myPageService.updateNickname(userId, newNickname));

        assertEquals(DUPLICATE_NICKNAME, exception.getErrorCode());
    }
}