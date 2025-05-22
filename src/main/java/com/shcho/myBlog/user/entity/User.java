package com.shcho.myBlog.user.entity;

import com.shcho.myBlog.libs.entity.BaseEntity;
import com.shcho.myBlog.libs.exception.CustomException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static com.shcho.myBlog.libs.exception.ErrorCode.ALREADY_DELETED_USER;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String nickname;

    @Column
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)")
    private Role role;

    private LocalDateTime deletedAt;

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateProfileImage(String fileUrl) {
        this.profileImageUrl = fileUrl;
    }

    public void withdraw() {
        if (this.deletedAt != null) {
            throw new CustomException(ALREADY_DELETED_USER);
        }
        this.deletedAt = LocalDateTime.now();
        this.username = null;
        this.password = "";
        this.nickname = null;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
