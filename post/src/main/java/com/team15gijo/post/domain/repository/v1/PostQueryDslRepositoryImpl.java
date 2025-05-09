package com.team15gijo.post.domain.repository.v1;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team15gijo.post.domain.model.v1.Post;
import com.team15gijo.post.domain.model.v1.QHashtag;
import com.team15gijo.post.domain.model.v1.QPost;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class PostQueryDslRepositoryImpl implements PostQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public PostQueryDslRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Post> searchPosts(String keyword, String region, LocalDateTime cursor, int size) {
        QPost post = QPost.post;
        QHashtag hashtag = QHashtag.hashtag;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.region.eq(region));
        builder.and(post.createdAt.lt(cursor));

        BooleanBuilder keywordBuilder = new BooleanBuilder();
        keywordBuilder.or(post.username.containsIgnoreCase(keyword));
        keywordBuilder.or(post.postContent.containsIgnoreCase(keyword));
        keywordBuilder.or(hashtag.hashtagName.containsIgnoreCase(keyword));

        builder.and(keywordBuilder);

        return queryFactory
                .selectFrom(post)
                .leftJoin(post.hashtags, hashtag)
                .where(builder)
                .orderBy(post.createdAt.desc())
                .limit(size)
                .fetch();
    }
}
