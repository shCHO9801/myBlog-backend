package com.shcho.myBlog.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shcho.myBlog.post.dto.PostResponseDto;
import com.shcho.myBlog.post.entity.QCategory;
import com.shcho.myBlog.post.entity.QPost;
import com.shcho.myBlog.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.shcho.myBlog.post.entity.QPost.post;

@Repository
@RequiredArgsConstructor
public class PostQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public Page<PostResponseDto> findAllByFilter(Long userId, String keyword, Long categoryId, String sort, Pageable pageable) {
        QPost p = post;
        QCategory c = QCategory.category;
        QUser user = QUser.user;

        var query = queryFactory
                .selectFrom(p)
                .join(p.category, c).fetchJoin()
                .join(p.user, user).fetchJoin()
                .where(
                        p.user.id.eq(userId),
                        p.deletedAt.isNull(),
                        keyword != null ? (p.title.containsIgnoreCase(keyword)
                                .or(p.content.containsIgnoreCase(keyword))) : null,
                        categoryId == null ? null : p.category.id.eq(categoryId)
                );

        if ("oldest".equals(sort)) {
            query.orderBy(p.createdAt.asc());
        } else {
            query.orderBy(p.createdAt.desc());
        }

        List<PostResponseDto> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(PostResponseDto::of)
                .toList();

        Long total = queryFactory
                .select(p.count())
                .from(p)
                .where(
                        p.user.id.eq(userId),
                        p.deletedAt.isNull(),
                        keyword != null ? (p.title.containsIgnoreCase(keyword)
                                .or(p.content.containsIgnoreCase(keyword))) : null,
                        categoryId != null ? p.category.id.eq(categoryId) : null
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
