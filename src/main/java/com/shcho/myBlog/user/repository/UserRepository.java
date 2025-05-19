package com.shcho.myBlog.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.shcho.myBlog.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
