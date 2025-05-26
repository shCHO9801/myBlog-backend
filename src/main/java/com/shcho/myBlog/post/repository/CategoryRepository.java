package com.shcho.myBlog.post.repository;

import com.shcho.myBlog.post.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByUserId(Long userId);

    boolean existsByUserIdAndName(Long userId, String name);
}
