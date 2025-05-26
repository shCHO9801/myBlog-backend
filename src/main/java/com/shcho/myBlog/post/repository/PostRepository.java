package com.shcho.myBlog.post.repository;

import com.shcho.myBlog.post.entity.Post;
import com.shcho.myBlog.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> user(User user);

    List<Post> findAllByUserIdAndDeletedAtIsNull(Long userId);
}
