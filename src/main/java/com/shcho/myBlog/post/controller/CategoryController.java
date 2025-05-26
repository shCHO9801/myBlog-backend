package com.shcho.myBlog.post.controller;

import com.shcho.myBlog.post.dto.CategoryCreateRequestDto;
import com.shcho.myBlog.post.dto.CategoryResponseDto;
import com.shcho.myBlog.post.service.CategoryService;
import com.shcho.myBlog.user.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CategoryCreateRequestDto request
    ) {
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(categoryService.createCategory(userId, request.name()));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getCategories(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(categoryService.getCategories(userId));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long categoryId
    ) {
        Long userId = userDetails.getUserId();
        return ResponseEntity.ok(categoryService.deleteCategory(userId, categoryId));
    }
}
