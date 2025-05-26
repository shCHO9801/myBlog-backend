package com.shcho.myBlog.post.entity;

import com.shcho.myBlog.libs.entity.BaseEntity;
import com.shcho.myBlog.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime deletedAt;

    public boolean isDeleted(){
        return deletedAt != null;
    }

    public static Post of(String title, String content, Category category, User user) {
        return Post.builder()
                .title(title)
                .content(content)
                .category(category)
                .user(user)
                .deletedAt(null)
                .build();
    }

    public void update(String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
