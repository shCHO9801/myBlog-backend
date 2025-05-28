package com.shcho.myBlog.post.controller;

import com.shcho.myBlog.common.dto.PageResponseDto;
import com.shcho.myBlog.post.dto.PostCreateRequestDto;
import com.shcho.myBlog.post.dto.PostListResponseDto;
import com.shcho.myBlog.post.dto.PostResponseDto;
import com.shcho.myBlog.post.dto.PostUpdateRequestDto;
import com.shcho.myBlog.post.entity.Post;
import com.shcho.myBlog.post.service.PostService;
import com.shcho.myBlog.user.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PostCreateRequestDto request
    ) {
        Long userId = userDetails.getUserId();

        Post newPost = postService.createPost(userId, request);
        PostResponseDto response = PostResponseDto.of(newPost);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<PostListResponseDto>> getPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "latest") String sort,
            Pageable pageable
    ) {
        Page<PostListResponseDto> filteredPosts = postService.getPosts(keyword, categoryId, sort, pageable);

        return ResponseEntity.ok(PageResponseDto.from(filteredPosts));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPost(
            @PathVariable Long postId) {
        Post getPost = postService.getPost(postId);

        PostResponseDto response = PostResponseDto.of(getPost);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @RequestBody PostUpdateRequestDto request
    ) {
        Long userId = userDetails.getUserId();

        Post updatedPost = postService.updatePost(userId, postId, request);
        PostResponseDto response = PostResponseDto.of(updatedPost);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId
    ) {
        Long userId = userDetails.getUserId();
        String response = postService.deletePost(userId, postId);

        return ResponseEntity.ok(response);
    }
}
