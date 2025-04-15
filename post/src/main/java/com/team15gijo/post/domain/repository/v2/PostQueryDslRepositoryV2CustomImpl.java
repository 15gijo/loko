package com.team15gijo.post.domain.repository.v2;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team15gijo.post.domain.model.v2.QHashtagV2;
import com.team15gijo.post.domain.model.v2.QPostV2;
import com.team15gijo.post.domain.model.v2.PostV2;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class PostQueryDslRepositoryImplV2 implements PostQueryDslRepositoryV2Custom {

    private final JPAQueryFactory queryFactory;

    public PostQueryDslRepositoryImplV2(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<PostV2> searchPostsV2(String keyword, String region, LocalDateTime cursor, int size) {
        QPostV2 post = QPostV2.postV2;
        QHashtagV2 hashtag = QHashtagV2.hashtagV2;

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
