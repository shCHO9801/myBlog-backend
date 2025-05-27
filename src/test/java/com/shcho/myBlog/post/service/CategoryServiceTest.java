package com.shcho.myBlog.post.service;

import com.shcho.myBlog.libs.exception.CustomException;
import com.shcho.myBlog.post.dto.CategoryCreateRequestDto;
import com.shcho.myBlog.post.dto.CategoryResponseDto;
import com.shcho.myBlog.post.entity.Category;
import com.shcho.myBlog.post.repository.CategoryRepository;
import com.shcho.myBlog.user.entity.Role;
import com.shcho.myBlog.user.entity.User;
import com.shcho.myBlog.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.shcho.myBlog.libs.exception.ErrorCode.DUPLICATE_CATEGORY;
import static com.shcho.myBlog.libs.exception.ErrorCode.UNAUTHORIZED_CATEGORY_ACCESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("카테고리 서비스 테스트")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    public CategoryServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("username")
                .password("password")
                .nickname("nickname")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("카테고리 생성 성공")
    void createCategorySuccess() {
        // given
        CategoryCreateRequestDto request = CategoryCreateRequestDto.builder()
                .name("newCategory")
                .build();

        when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        when(categoryRepository.existsByUserIdAndName(user.getId(), request.name())).thenReturn(false);

        // when
        CategoryResponseDto category = categoryService.createCategory(user.getId(), request.name());

        // then
        assertNotNull(category);
        assertEquals(request.name(), category.name());
    }

    @Test
    @DisplayName("카테고리 생성 실패 - 존재하지 않는 카테고리")
    void createCategoryFailDuplicateCategory() {
        // given
        CategoryCreateRequestDto request = CategoryCreateRequestDto.builder()
                .name("newCategory")
                .build();

        when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        when(categoryRepository.existsByUserIdAndName(user.getId(), request.name())).thenReturn(true);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> categoryService.createCategory(user.getId(), request.name()));

        assertEquals(DUPLICATE_CATEGORY, exception.getErrorCode());
    }

    @Test
    @DisplayName("카테고리 조회 성공")
    void getCategoriesSuccess() {
        // given
        List<Category> mockCategories = List.of(
                Category.of("분류1", user),
                Category.of("분류2", user)
        );

        when(categoryRepository.findAllByUserId(user.getId())).thenReturn(mockCategories);

        // when
        List<CategoryResponseDto> result = categoryService.getCategories(user.getId());

        // then
        assertNotNull(result);
        assertEquals(mockCategories.size(), result.size());
        assertEquals("분류1", result.get(0).name());
        assertEquals("분류2", result.get(1).name());

    }

    @Test
    @DisplayName("카테고리 삭제 성공")
    void deleteCategorySuccess() {
        // given
        Category category = Category.builder()
                .id(1L)
                .name("category")
                .user(user)
                .build();

        when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        when(categoryRepository.getReferenceById(category.getId())).thenReturn(category);

        // when
        String result = categoryService.deleteCategory(user.getId(), category.getId());

        // then
        assertNotNull(result);
        assertEquals("카테고리가 삭제되었습니다.", result);
    }

    @Test
    @DisplayName("카테고리 삭제 실패 - 유저 권한 없음")
    void deleteCategoryFailUnauthorizedCategoryAccess() {
        // given
        User anotherUser = User.builder()
                .id(999L)
                .build();

        Category category = Category.builder()
                .id(1L)
                .name("category")
                .build();

        when(userRepository.getReferenceById(anotherUser.getId())).thenReturn(anotherUser);
        when(categoryRepository.getReferenceById(category.getId())).thenReturn(category);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> categoryService.deleteCategory(anotherUser.getId(), category.getId()));

        assertEquals(UNAUTHORIZED_CATEGORY_ACCESS, exception.getErrorCode());

    }
}