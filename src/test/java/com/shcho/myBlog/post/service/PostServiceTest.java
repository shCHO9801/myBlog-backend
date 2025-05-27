package com.shcho.myBlog.post.service;

import com.shcho.myBlog.libs.exception.CustomException;
import com.shcho.myBlog.post.dto.PostCreateRequestDto;
import com.shcho.myBlog.post.dto.PostResponseDto;
import com.shcho.myBlog.post.dto.PostUpdateRequestDto;
import com.shcho.myBlog.post.entity.Category;
import com.shcho.myBlog.post.entity.Post;
import com.shcho.myBlog.post.repository.CategoryRepository;
import com.shcho.myBlog.post.repository.PostQueryDslRepository;
import com.shcho.myBlog.post.repository.PostRepository;
import com.shcho.myBlog.user.entity.Role;
import com.shcho.myBlog.user.entity.User;
import com.shcho.myBlog.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static com.shcho.myBlog.libs.exception.ErrorCode.ALREADY_DELETED_POST;
import static com.shcho.myBlog.libs.exception.ErrorCode.UNAUTHORIZED_POST_ACCESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("게시글 서비스 테스트")
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PostQueryDslRepository postQueryDslRepository;

    @InjectMocks
    private PostService postService;

    public PostServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("username")
                .password("password")
                .nickname("nickname")
                .role(Role.USER)
                .build();

        category = Category.builder()
                .id(1L)
                .user(user)
                .name("post")
                .build();
    }

    @Test
    @DisplayName("게시글 생성 성공")
    void createPostSuccess() {
        // given

        Post post = Post.builder()
                .id(1L)
                .title("newPostTitle")
                .content("newPostContent")
                .category(category)
                .user(user)
                .build();


        PostCreateRequestDto request = PostCreateRequestDto.builder()
                .title("newPostTitle")
                .content("newPostContent")
                .categoryId(category.getId())
                .build();

        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(categoryRepository.getReferenceById(1L)).thenReturn(category);

        // when
        Post result = postService.createPost(user.getId(), request);

        // then
        assertNotNull(request);
        assertEquals(post.getTitle(), result.getTitle());
        assertEquals(post.getContent(), result.getContent());
        assertEquals(post.getUser().getId(), result.getUser().getId());
        assertEquals(post.getCategory().getId(), result.getCategory().getId());
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void updatePostSuccess() {
        // given
        Post oldPost = Post.builder()
                .id(1L)
                .title("oldTitle")
                .content("oldContent")
                .category(category)
                .user(user)
                .build();

        Category newCategory = Category.builder()
                .id(2L)
                .user(user)
                .name("newCategory")
                .build();

        PostUpdateRequestDto request = PostUpdateRequestDto.builder()
                .title("newTitle")
                .content("newContent")
                .categoryId(newCategory.getId())
                .build();

        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(postRepository.getReferenceById(1L)).thenReturn(oldPost);
        when(categoryRepository.getReferenceById(2L)).thenReturn(newCategory);

        // when
        Post updatedPost = postService.updatePost(user.getId(), oldPost.getId(), request);

        // then
        assertNotNull(request);
        assertEquals(request.title(), updatedPost.getTitle());
        assertEquals(request.content(), updatedPost.getContent());
        assertEquals(request.categoryId(), updatedPost.getCategory().getId());
    }

    @Test
    @DisplayName("게시글 수정 실패 - 유저 권한 없음")
    void updatePostFailUnauthorizedPostAccess() {
        // given
        User anotherUser = User.builder()
                .id(999L)
                .username("anotherUsername")
                .password("anotherPassword")
                .nickname("anotherNickname")
                .build();

        Post oldPost = Post.builder()
                .id(1L)
                .title("oldTitle")
                .content("oldContent")
                .category(category)
                .user(user)
                .build();

        PostUpdateRequestDto request = PostUpdateRequestDto.builder()
                .title("newTitle")
                .content("newContent")
                .categoryId(category.getId())
                .build();

        when(userRepository.getReferenceById(999L)).thenReturn(anotherUser);
        when(postRepository.getReferenceById(1L)).thenReturn(oldPost);
        when(categoryRepository.getReferenceById(1L)).thenReturn(category);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> postService.updatePost(anotherUser.getId(), oldPost.getId(), request));

        assertEquals(UNAUTHORIZED_POST_ACCESS, exception.getErrorCode());
    }

    @Test
    @DisplayName("게시글 단건 조회")
    void getPostSuccess() {
        // given

        Post post = Post.builder()
                .id(1L)
                .title("title")
                .content("content")
                .category(category)
                .user(user)
                .build();

        when(postRepository.getReferenceById(1L)).thenReturn(post);

        // when
        Post result = postService.getPost(1L);

        // then
        assertNotNull(result);
        assertEquals(post.getTitle(), result.getTitle());
        assertEquals(post.getContent(), result.getContent());
        assertEquals(post.getUser().getId(), result.getUser().getId());
        assertEquals(post.getCategory().getId(), result.getCategory().getId());
    }

    @Test
    @DisplayName("게시글 단건 조회 실패 - 삭제된 게시글")
    void getPostFailAlreadyDeletedPost() {
        // when
        Post deletedPost = Post.builder()
                .id(999L)
                .title("deletedTitle")
                .content("deletedContent")
                .category(category)
                .user(user)
                .deletedAt(LocalDateTime.now())
                .build();

        when(postRepository.getReferenceById(999L)).thenReturn(deletedPost);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> postService.getPost(999L));

        assertEquals(ALREADY_DELETED_POST, exception.getErrorCode());
    }

    @Test
    @DisplayName("게시글 조회 성공")
    void getPostsSuccess() {
        // given
        Pageable pageable = Pageable.ofSize(10);
        String keyword = "제목";
        String sort = "latest";
        Long categoryId = null;

        Page<PostResponseDto> mockPage = new PageImpl<>(
                List.of(
                        PostResponseDto.of(Post.of("제목", "내용", category, user)),
                        PostResponseDto.of(Post.of("제목2", "내용2", category, user))
                )
        );

        when(postQueryDslRepository.findAllByFilter(user.getId(), keyword, categoryId, sort, pageable))
                .thenReturn(mockPage);

        // when
        Page<PostResponseDto> result = postService.getPosts(user.getId(), keyword, categoryId, sort, pageable);

        // then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("제목", result.getContent().get(0).title());
        assertEquals("제목2", result.getContent().get(1).title());
        assertEquals("내용", result.getContent().get(0).content());
        assertEquals("내용2", result.getContent().get(1).content());
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void deletePostSuccess() {
        // given
        Post post = Post.builder()
                .id(1L)
                .title("title")
                .content("content")
                .category(category)
                .user(user)
                .build();

        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(postRepository.getReferenceById(1L)).thenReturn(post);

        // when
        String result = postService.deletePost(user.getId(), post.getId());

        // then
        assertNotNull(result);
        assertEquals("게시글이 성공적으로 삭제되었습니다.", result);
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 유저 권한 없음")
    void deletePostFailUnauthorizedPostAccess() {
        // given
        User anotherUser = User.builder()
                .id(999L)
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("title")
                .content("content")
                .category(category)
                .user(user)
                .build();

        when(userRepository.getReferenceById(999L)).thenReturn(anotherUser);
        when(postRepository.getReferenceById(1L)).thenReturn(post);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> postService.deletePost(anotherUser.getId(), post.getId()));

        assertEquals(UNAUTHORIZED_POST_ACCESS, exception.getErrorCode());
    }
}