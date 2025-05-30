package com.shcho.myBlog.comment.controller;

import com.shcho.myBlog.comment.dto.*;
import com.shcho.myBlog.comment.entity.Comment;
import com.shcho.myBlog.comment.service.CommentService;
import com.shcho.myBlog.common.dto.PageResponseDto;
import com.shcho.myBlog.user.service.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequestDto request
    ) {
        Comment newComment = commentService.createComment(userDetails, postId, request);

        return ResponseEntity.ok(CommentResponseDto.from(newComment));
    }

    @GetMapping
    public ResponseEntity<PageResponseDto<CommentWithRepliesDto>> getAllComments(
            @PathVariable Long postId,
            Pageable pageable) {
        Page<CommentWithRepliesDto> commentsWithReplies = commentService.getCommentsWithReplies(postId, pageable);

        return ResponseEntity.ok(PageResponseDto.from(commentsWithReplies));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequestDto request
    ) {
        Comment updatedComment = commentService.updateComment(userDetails, commentId, request);
        return ResponseEntity.ok(CommentResponseDto.from(updatedComment));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> deleteComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentDeleteRequestDto request
    ) {
        commentService.deleteComment(userDetails, commentId, request);
        return ResponseEntity.noContent().build();
    }
}
