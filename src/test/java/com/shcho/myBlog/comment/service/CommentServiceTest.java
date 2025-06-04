package com.shcho.myBlog.comment.service;

import com.shcho.myBlog.comment.dto.*;
import com.shcho.myBlog.comment.entity.Comment;
import com.shcho.myBlog.comment.entity.Reply;
import com.shcho.myBlog.comment.repository.CommentRepository;
import com.shcho.myBlog.libs.exception.CustomException;
import com.shcho.myBlog.libs.exception.ErrorCode;
import com.shcho.myBlog.post.entity.Category;
import com.shcho.myBlog.post.entity.Post;
import com.shcho.myBlog.post.repository.PostRepository;
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
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("댓글 서비스 테스트")
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CommentService commentService;

    public CommentServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    private User user;
    private Post post;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("username")
                .password("password")
                .nickname("nickname")
                .role(Role.USER)
                .build();

        Category category = Category.builder()
                .id(1L)
                .user(user)
                .name("post")
                .build();

        post = Post.builder()
                .id(1L)
                .title("title")
                .content("content")
                .user(user)
                .category(category)
                .build();

        userDetails = new CustomUserDetails(user);
    }

    @Test
    @DisplayName("댓글 생성 성공 - 일반 유저")
    void createCommentSuccess() {
        // given
        CommentCreateRequestDto request =
                new CommentCreateRequestDto("create comment", null, null);

        when(postRepository.getReferenceById(post.getId())).thenReturn(post);
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);

        Comment expected = Comment.of(request.content(), user, post);
        when(commentRepository.save(any(Comment.class))).thenReturn(expected);

        // when
        Comment result = commentService.createComment(
                userDetails,
                post.getId(),
                request);

        // then
        assertNotNull(result);
        assertEquals(request.content(), result.getContent());
        assertEquals(user, result.getUser());
        assertEquals(post, result.getPost());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 생성 성공 - 게스트 유저")
    void createCommentSuccessGuest() {
        // given
        CommentCreateRequestDto request =
                new CommentCreateRequestDto(
                        "create comment", "anonymous", "anonymous");

        when(postRepository.getReferenceById(post.getId())).thenReturn(post);
        when(passwordEncoder.encode(request.anonymousPassword())).thenReturn("encodedPassword");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Comment result = commentService.createComment(null, post.getId(), request);

        // then
        assertNotNull(result);

        User guest = userCaptor.getValue();
        assertEquals(request.anonymousName(), guest.getNickname());
        assertEquals("encodedPassword", guest.getPassword());
        assertEquals(Role.GUEST, guest.getRole());

        assertEquals(request.content(), result.getContent());
        assertEquals(guest, result.getUser());
        assertEquals(post, result.getPost());

        verify(passwordEncoder).encode(request.anonymousPassword());
        verify(userRepository).save(userCaptor.capture());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 및 대댓글 조회 성공")
    void getCommentWithRepliesSuccess() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").ascending());

        Comment comment1 = makeComment(1L, "1st comment", user, post);
        Comment comment2 = makeComment(2L, "2st comment", user, post);

        makeReply(comment1);
        makeReply(comment2);

        List<Comment> comments = List.of(comment1, comment2);
        Page<Comment> page = new PageImpl<>(comments, pageable, comments.size());
        when(commentRepository.findAllByPostIdOrderByCreatedAtAsc(post.getId(), pageable))
                .thenReturn(page);

        // when
        Page<CommentWithRepliesDto> result = commentService.getCommentsWithReplies(post.getId(), pageable);

        // then
        assertEquals(2, result.getTotalElements());

        // 댓글1 검증
        CommentWithRepliesDto dto1 = result.getContent().get(0);
        assertEquals(comment1.getId(), dto1.commentId());
        assertEquals(comment1.getReplies().size(), dto1.replies().size());

        assertEquals(comment1.getId(), dto1.commentId());
        assertEquals(comment1.getContent(), dto1.content());
        assertEquals(comment1.getUser().getNickname(), dto1.userNickName());
        assertFalse(dto1.isDeleted());

        // 댓글2 검증
        CommentWithRepliesDto dto2 = result.getContent().get(1);
        assertEquals(comment2.getId(), dto2.commentId());
        assertEquals(comment2.getReplies().size(), dto2.replies().size());

        assertEquals(comment2.getId(), dto2.commentId());
        assertEquals(comment2.getContent(), dto2.content());
        assertEquals(comment2.getUser().getNickname(), dto2.userNickName());
        assertFalse(dto2.isDeleted());

        // 대댓글 검증
        ReplyResponseDto rd = dto1.replies().get(0);
        assertEquals("1-1 reply", rd.content());
        assertEquals(user.getNickname(), rd.userNickName());
        assertFalse(rd.isDeleted());

        ReplyResponseDto rd2 = dto2.replies().get(0);
        assertEquals("2-1 reply", rd2.content());
        assertEquals(user.getNickname(), rd2.userNickName());
        assertFalse(rd2.isDeleted());

        verify(commentRepository).findAllByPostIdOrderByCreatedAtAsc(post.getId(), pageable);
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void updateCommentSuccess() {
        // given
        Long commentId = 1L;
        Comment comment = makeComment(commentId, "1st comment", user, post);
        CommentUpdateRequestDto updateRequest = new CommentUpdateRequestDto("update comment", null);

        when(commentRepository.getReferenceById(commentId)).thenReturn(comment);
        when(userRepository.getReferenceById(userDetails.getUserId())).thenReturn(user);

        // when
        Comment result = commentService.updateComment(userDetails, commentId, updateRequest);

        // then
        assertNotNull(result);
        assertEquals(updateRequest.content(), result.getContent());
        assertNotEquals("1st comment", result.getContent());
        verify(userRepository).getReferenceById(userDetails.getUserId());
    }

    @Test
    @DisplayName("댓글 수정 성공 - 게스트 유저")
    void updateCommentSuccessGuest() {
        Long commentId = 1L;
        String password = "password";
        String encodedPassword = passwordEncoder.encode(password);

        User guest = User.builder()
                .id(10L)
                .username("guest-" + UUID.randomUUID())
                .nickname("guest")
                .password(encodedPassword)
                .role(Role.GUEST)
                .build();

        Comment comment = makeComment(commentId, "1st comment", guest, post);

        CommentUpdateRequestDto updateRequest =
                new CommentUpdateRequestDto("update comment", password);

        when(commentRepository.getReferenceById(commentId)).thenReturn(comment);
        when(passwordEncoder.matches(updateRequest.anonymousPassword(), encodedPassword))
                .thenReturn(true);

        // when
        Comment result = commentService.updateComment(null, commentId, updateRequest);

        // then
        assertNotNull(result);
        assertEquals(updateRequest.content(), result.getContent());
        verify(passwordEncoder).matches(updateRequest.anonymousPassword(), encodedPassword);
    }

    @Test
    @DisplayName("댓글 수정 실패 - 이미 삭제된 댓글")
    void updateCommentFailAlreadyDeletedComment() {
        // given
        Long commentId = 1L;
        Comment comment = makeComment(commentId, "1st comment", user, post);
        comment.delete();
        CommentUpdateRequestDto updateRequest = new CommentUpdateRequestDto("update comment", null);
        when(commentRepository.getReferenceById(commentId)).thenReturn(comment);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> commentService.updateComment(userDetails, commentId, updateRequest));

        assertEquals(ErrorCode.ALREADY_DELETED_COMMENT, exception.getErrorCode());
        verify(commentRepository).getReferenceById(commentId);
    }

    @Test
    @DisplayName("댓글 수정 실패 - 일반 유저 권한 없음")
    void updateCommentFailUserUnauthorizedCommentAccess() {
        Long commentId = 1L;
        Comment comment = makeComment(commentId, "1st comment", user, post);
        User anotherUser = User.builder()
                .id(999L)
                .build();
        CustomUserDetails anotherUserDetails = new CustomUserDetails(anotherUser);
        CommentUpdateRequestDto updateRequest = new CommentUpdateRequestDto("update comment", null);
        when(commentRepository.getReferenceById(commentId)).thenReturn(comment);
        when(userRepository.getReferenceById(anotherUserDetails.getUserId())).thenReturn(anotherUser);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> commentService.updateComment(anotherUserDetails, commentId, updateRequest));

        assertEquals(ErrorCode.UNAUTHORIZED_COMMENT_ACCESS, exception.getErrorCode());
        verify(commentRepository).getReferenceById(commentId);
        verify(userRepository).getReferenceById(anotherUserDetails.getUserId());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 게스트 유저 권한 없음")
    void updateCommentFailGuestUnauthorizedCommentAccess() {
        // given
        Long commentId = 1L;

        User guest = User.builder()
                .id(10L)
                .username("guest-" + UUID.randomUUID())
                .nickname("guest")
                .password("encodedPassword")
                .role(Role.GUEST)
                .build();

        Comment comment = makeComment(commentId, "1st comment", guest, post);
        CommentUpdateRequestDto updateRequest = new CommentUpdateRequestDto("update comment", "hi");
        when(commentRepository.getReferenceById(commentId)).thenReturn(comment);
        when(passwordEncoder.matches(updateRequest.anonymousPassword(), comment.getUser().getPassword()))
                .thenReturn(false);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> commentService.updateComment(null, commentId, updateRequest));

        assertEquals(ErrorCode.UNAUTHORIZED_COMMENT_ACCESS, exception.getErrorCode());
        verify(commentRepository).getReferenceById(commentId);
        verify(passwordEncoder).matches(updateRequest.anonymousPassword(), comment.getUser().getPassword());
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteCommentSuccess() {
        // given
        Long commentId = 1L;
        Comment comment = makeComment(commentId, "1st comment", user, post);
        CommentDeleteRequestDto deleteRequest = new CommentDeleteRequestDto(null);
        when(commentRepository.getReferenceById(commentId)).thenReturn(comment);
        when(userRepository.getReferenceById(userDetails.getUserId())).thenReturn(user);

        // when
        commentService.deleteComment(userDetails, commentId, deleteRequest);
        verify(commentRepository).getReferenceById(commentId);
        verify(userRepository).getReferenceById(userDetails.getUserId());
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 게스트 패스워드 null")
    void deleteCommentFailNullGuest() {
        // given
        Long commentId = 1L;
        Comment comment = makeComment(commentId, "1st comment", user, post);
        CommentDeleteRequestDto deleteRequest = new CommentDeleteRequestDto(null);
        when(commentRepository.getReferenceById(commentId)).thenReturn(comment);

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> commentService.deleteComment(null, commentId, deleteRequest));

        assertEquals(ErrorCode.UNAUTHORIZED_COMMENT_ACCESS, exception.getErrorCode());
        verify(commentRepository).getReferenceById(commentId);
    }

    private Comment makeComment(long commentId, String content, User user, Post post) {
        return Comment.builder()
                .id(commentId)
                .content(content)
                .user(user)
                .post(post)
                .replies(new ArrayList<>())
                .build();
    }

    private void makeReply(Comment comment) {
        for (int i = 1; i <= 3; i++) {
            Reply r = Reply.of(comment.getId() + "-" + i + " reply", user, comment);
            comment.getReplies().add(r);
        }
    }
}