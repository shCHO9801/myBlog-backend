package com.shcho.myBlog.comment.service;

import com.shcho.myBlog.comment.dto.ReplyCreateRequestDto;
import com.shcho.myBlog.comment.dto.ReplyDeleteRequestDto;
import com.shcho.myBlog.comment.dto.ReplyUpdateRequestDto;
import com.shcho.myBlog.comment.entity.Comment;
import com.shcho.myBlog.comment.entity.Reply;
import com.shcho.myBlog.comment.repository.CommentRepository;
import com.shcho.myBlog.comment.repository.ReplyRepository;
import com.shcho.myBlog.libs.exception.CustomException;
import com.shcho.myBlog.post.entity.Category;
import com.shcho.myBlog.post.entity.Post;
import com.shcho.myBlog.user.entity.Role;
import com.shcho.myBlog.user.entity.User;
import com.shcho.myBlog.user.repository.UserRepository;
import com.shcho.myBlog.user.service.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static com.shcho.myBlog.libs.exception.ErrorCode.ALREADY_DELETED_REPLY;
import static com.shcho.myBlog.libs.exception.ErrorCode.UNAUTHORIZED_REPLY_ACCESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("대댓글 서비스 테스트")
class ReplyServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ReplyRepository replyRepository;

    @InjectMocks
    ReplyService replyService;

    private Comment comment;
    private User user;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(1L)
                .username("username")
                .password("password")
                .nickname("nickname")
                .role(Role.USER)
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("title")
                .content("content")
                .user(user)
                .category(Category.builder()
                        .id(1L)
                        .user(user)
                        .name("post")
                        .build())
                .build();

        userDetails = new CustomUserDetails(user);

        comment = Comment.builder()
                .id(1L)
                .content("content")
                .user(user)
                .post(post)
                .replies(new ArrayList<>())
                .build();
    }

    @DisplayName("대댓글 작성 성공 - 일반 유저")
    @Test
    void createReplySuccess() {
        // given
        ReplyCreateRequestDto request = ReplyCreateRequestDto.builder()
                .content("test reply")
                .build();

        when(commentRepository.getReferenceById(comment.getId())).thenReturn(comment);
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);

        Reply expected = Reply.of(request.content(), user, comment);
        when(replyRepository.save(any(Reply.class))).thenReturn(expected);

        // when
        Reply createdReply = replyService.createReply(userDetails, comment.getId(), request);

        // then
        assertNotNull(createdReply);
        assertEquals(request.content(), createdReply.getContent());
        assertEquals(user, createdReply.getUser());
        assertEquals(comment, createdReply.getComment());
        verify(replyRepository).save(any(Reply.class));
    }

    @DisplayName("대댓글 작성 성공 - 게스트 유저")
    @Test
    void createReplySuccessGuest() {
        // given
        ReplyCreateRequestDto request = ReplyCreateRequestDto.builder()
                .content("guest reply")
                .anonymousName("guest")
                .anonymousPassword("guestPassword")
                .build();

        when(commentRepository.getReferenceById(comment.getId())).thenReturn(comment);
        when(passwordEncoder.encode(request.anonymousPassword())).thenReturn("encodedGuestPassword");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(replyRepository.save(any(Reply.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Reply createdReply = replyService.createReply(null, comment.getId(), request);

        // then
        assertNotNull(createdReply);

        User guest = userCaptor.getValue();
        assertEquals(request.anonymousName(), guest.getNickname());
        assertEquals("encodedGuestPassword", guest.getPassword());
        assertEquals(Role.GUEST, guest.getRole());

        assertEquals(request.content(), createdReply.getContent());
        assertEquals(guest, createdReply.getUser());
        assertEquals(comment, createdReply.getComment());

        verify(passwordEncoder).encode(request.anonymousPassword());
        verify(userRepository).save(userCaptor.capture());
        verify(replyRepository).save(any(Reply.class));
    }

    @Test
    @DisplayName("대댓글 수정 성공 - 일반 유저")
    void updateReplySuccess() {
        // given
        Reply reply = Reply.builder()
                .id(1L)
                .content("old reply")
                .user(user)
                .comment(comment)
                .build();

        ReplyUpdateRequestDto request = ReplyUpdateRequestDto.builder()
                .content("test reply")
                .build();

        when(replyRepository.getReferenceById(reply.getId())).thenReturn(reply);
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);

        // when
        Reply updatedReply = replyService.updateReply(reply.getId(), userDetails, request);

        // then
        assertNotNull(updatedReply);
        assertEquals(request.content(), updatedReply.getContent());
        verify(replyRepository).getReferenceById(reply.getId());
        verify(userRepository).getReferenceById(user.getId());
    }

    @Test
    @DisplayName("대댓글 수정 성공 - 게스트 유저")
    void updateReplySuccessGuest() {
        // given
        String password = "guestPassword";
        String encodedPassword = passwordEncoder.encode(password);

        User guest = User.builder()
                .id(10L)
                .username("guest-" + UUID.randomUUID())
                .nickname("guest")
                .password(encodedPassword)
                .role(Role.GUEST)
                .build();

        Reply reply = Reply.builder()
                .id(1L)
                .content("guest reply")
                .user(guest)
                .comment(comment)
                .build();

        ReplyUpdateRequestDto request = ReplyUpdateRequestDto.builder()
                .content("new guest reply")
                .anonymousPassword(password)
                .build();

        when(replyRepository.getReferenceById(reply.getId())).thenReturn(reply);
        when(passwordEncoder.matches(request.anonymousPassword(), encodedPassword))
                .thenReturn(true);

        // when
        Reply updatedReply = replyService.updateReply(reply.getId(), null, request);

        // then
        assertNotNull(updatedReply);
        assertEquals(request.content(), updatedReply.getContent());
        verify(replyRepository).getReferenceById(reply.getId());
        verify(passwordEncoder).matches(request.anonymousPassword(), encodedPassword);
    }

    @Test
    @DisplayName("대댓글 수정 실패 - 권한이 없는 유저")
    void updateReplyFailUnauthorized() {
        // given
        User anotherUser = User.builder()
                .id(999L)
                .build();

        CustomUserDetails anotherUserDetails = new CustomUserDetails(anotherUser);

        Reply reply = Reply.builder()
                .id(1L)
                .content("old reply")
                .user(user)
                .comment(comment)
                .build();

        ReplyUpdateRequestDto request = ReplyUpdateRequestDto.builder()
                .content("test reply")
                .build();

        when(replyRepository.getReferenceById(reply.getId())).thenReturn(reply);
        when(userRepository.getReferenceById(anotherUser.getId())).thenReturn(anotherUser);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> replyService.updateReply(reply.getId(), anotherUserDetails, request));

        assertNotNull(exception);
        assertEquals(UNAUTHORIZED_REPLY_ACCESS, exception.getErrorCode());
        verify(replyRepository).getReferenceById(reply.getId());
        verify(userRepository).getReferenceById(anotherUser.getId());
    }

    @Test
    @DisplayName("대댓글 수정 실패 - 권한이 없는 게스트 유저")
    void updateReplyFailGuestUnauthorized() {
        // given
        String password = "guestPassword";
        String encodedPassword = passwordEncoder.encode(password);

        User guest = User.builder()
                .id(10L)
                .username("guest-" + UUID.randomUUID())
                .nickname("guest")
                .password(encodedPassword)
                .role(Role.GUEST)
                .build();

        Reply reply = Reply.builder()
                .id(1L)
                .content("guest reply")
                .user(guest)
                .comment(comment)
                .build();

        ReplyUpdateRequestDto request = ReplyUpdateRequestDto.builder()
                .content("new guest reply")
                .anonymousPassword(password)
                .build();

        when(replyRepository.getReferenceById(reply.getId())).thenReturn(reply);
        when(passwordEncoder.matches(request.anonymousPassword(), encodedPassword))
                .thenReturn(false);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> replyService.updateReply(reply.getId(), null, request));

        assertNotNull(exception);
        assertEquals(UNAUTHORIZED_REPLY_ACCESS, exception.getErrorCode());
        verify(replyRepository).getReferenceById(reply.getId());
        verify(passwordEncoder).matches(request.anonymousPassword(), encodedPassword);
    }

    @Test
    @DisplayName("대댓글 수정 실패 - anonymousPassword is null")
    void updateReplyFailNullAnonymousPassword() {
        // given
        Reply reply = Reply.builder().id(1L).build();
        ReplyUpdateRequestDto request = ReplyUpdateRequestDto.builder()
                .content("test reply")
                .build();

        when(replyRepository.getReferenceById(reply.getId())).thenReturn(reply);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> replyService.updateReply(reply.getId(), null, request));

        assertNotNull(exception);
        assertEquals(UNAUTHORIZED_REPLY_ACCESS, exception.getErrorCode());
        verify(replyRepository).getReferenceById(reply.getId());
    }

    @Test
    @DisplayName("대댓글 삭제 성공")
    void deleteReplySuccess() {
        // given
        Reply reply = Reply.builder()
                .id(10L)
                .user(user)
                .build();
        ReplyDeleteRequestDto request = ReplyDeleteRequestDto.builder()
                .build();

        when(replyRepository.getReferenceById(reply.getId())).thenReturn(reply);
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);

        // when
        replyService.deleteReply(reply.getId(), userDetails, request);

        // then
        assertTrue(reply.isDeleted());
        verify(replyRepository).getReferenceById(reply.getId());
        verify(userRepository).getReferenceById(user.getId());
    }

    @Test
    @DisplayName("대댓글 삭제 실패 - 이미 삭제된 대댓글")
    void deleteReplyFailAlreadyDeleted() {
        // given
        Reply deletedReply = Reply.builder()
                .id(999L)
                .deletedAt(LocalDateTime.now().minusDays(1))
                .build();

        ReplyDeleteRequestDto request = ReplyDeleteRequestDto.builder().build();

        when(replyRepository.getReferenceById(deletedReply.getId())).thenReturn(deletedReply);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> replyService.deleteReply(deletedReply.getId(), userDetails, request));

        assertNotNull(exception);
        assertEquals(ALREADY_DELETED_REPLY, exception.getErrorCode());
        verify(replyRepository).getReferenceById(deletedReply.getId());
    }
}