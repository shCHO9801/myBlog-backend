package com.shcho.myBlog.post.service;

import com.shcho.myBlog.libs.exception.CustomException;
import com.shcho.myBlog.libs.exception.ErrorCode;
import com.shcho.myBlog.post.dto.PostCreateRequestDto;
import com.shcho.myBlog.post.dto.PostUpdateRequestDto;
import com.shcho.myBlog.post.entity.Category;
import com.shcho.myBlog.post.entity.Post;
import com.shcho.myBlog.post.repository.CategoryRepository;
import com.shcho.myBlog.post.repository.PostRepository;
import com.shcho.myBlog.user.entity.User;
import com.shcho.myBlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.shcho.myBlog.libs.exception.ErrorCode.UNAUTHORIZED_POST_ACCESS;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public Post createPost(Long userId, PostCreateRequestDto request) {
        User user = userRepository.getReferenceById(userId);
        Category category = getCategory(request.categoryId());

        Post newPosts = Post.of(request.title(), request.content(), category, user);
        postRepository.save(newPosts);

        return newPosts;
    }

    public Post updatePost(Long userId, Long postId, PostUpdateRequestDto request) {
        User user = userRepository.getReferenceById(userId);
        Post post = getPost(postId);
        Category category = getCategory(request.categoryId());

        if (!user.equals(post.getUser())) {
            throw new CustomException(UNAUTHORIZED_POST_ACCESS);
        }

        post.update(request.title(), request.content(), category);
        postRepository.save(post);

        return post;
    }

    public Post getPost(Long postId) {
        Post post = postRepository.getReferenceById(postId);

        if(post.isDeleted()) {
            throw new CustomException(ErrorCode.ALREADY_DELETED_POST);
        }

        return post;
    }

    // TODO : 페이지네이션 구현
    public List<Post> getPosts(Long userId) {
        return postRepository.findAllByUserIdAndDeletedAtIsNull(userId);
    }

    public String deletePost(Long userId, Long postId) {
        User user = userRepository.getReferenceById(userId);
        Post post = getPost(postId);

        if (!user.equals(post.getUser())) {
            throw new CustomException(UNAUTHORIZED_POST_ACCESS);
        }

        post.delete();
        postRepository.save(post);

        return "게시글이 성공적으로 삭제되었습니다.";
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.getReferenceById(categoryId);
    }
}
