package com.shcho.myBlog.comment.entity;

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
public class Reply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    private LocalDateTime deletedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public static Reply of(String content, User user, Comment comment) {
        return Reply.builder()
                .content(content)
                .user(user)
                .comment(comment)
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
