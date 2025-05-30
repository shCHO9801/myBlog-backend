package com.shcho.myBlog.comment.entity;

import com.shcho.myBlog.libs.entity.BaseEntity;
import com.shcho.myBlog.post.entity.Post;
import com.shcho.myBlog.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<Reply> replies = new ArrayList<>();

    private LocalDateTime deletedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public static Comment of(String content, User user, Post post) {
        return Comment.builder()
                .content(content)
                .user(user)
                .post(post)
                .build();
    }

    public void update(String content) {
        this.content = content;
    }

    public void delete() {
        this.content = "삭제된 댓글입니다.";
        this.deletedAt = LocalDateTime.now();
    }
}
