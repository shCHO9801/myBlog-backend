package com.shcho.myBlog.post.service;

import com.shcho.myBlog.libs.exception.CustomException;
import com.shcho.myBlog.post.dto.CategoryResponseDto;
import com.shcho.myBlog.post.entity.Category;
import com.shcho.myBlog.post.repository.CategoryRepository;
import com.shcho.myBlog.user.entity.User;
import com.shcho.myBlog.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.shcho.myBlog.libs.exception.ErrorCode.DUPLICATE_CATEGORY;
import static com.shcho.myBlog.libs.exception.ErrorCode.UNAUTHORIZED_CATEGORY_ACCESS;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryResponseDto createCategory(Long userId, String name) {
        User user = userRepository.getReferenceById(userId);

        if (categoryRepository.existsByUserIdAndName(user.getId(), name)) {
            throw new CustomException(DUPLICATE_CATEGORY);
        }

        Category category = Category.of(name, user);
        categoryRepository.save(category);

        return CategoryResponseDto.from(category);
    }

    public List<CategoryResponseDto> getCategories(Long userId) {
        return categoryRepository.findAllByUserId(userId)
                .stream()
                .map(CategoryResponseDto::from)
                .toList();
    }

    @Transactional
    public String deleteCategory(Long userId, Long categoryId) {
        User user = userRepository.getReferenceById(userId);
        Category category = categoryRepository.getReferenceById(categoryId);

        if (!user.equals(category.getUser())) {
            throw new CustomException(UNAUTHORIZED_CATEGORY_ACCESS);
        }

        categoryRepository.deleteById(categoryId);
        return "카테고리가 삭제되었습니다.";
    }
}
