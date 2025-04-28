package com.team15gijo.post.domain.repository.v2;

import static com.team15gijo.post.domain.model.v2.QPostV2.postV2;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team15gijo.post.domain.model.v2.QHashtagV2;
import com.team15gijo.post.domain.model.v2.QPostV2;
import com.team15gijo.post.domain.model.v2.PostV2;
import com.team15gijo.post.domain.repository.v2.dto.PostSummaryDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class PostQueryDslRepositoryV2CustomImpl implements PostQueryDslRepositoryV2Custom {

    private final JPAQueryFactory queryFactory;

    public PostQueryDslRepositoryV2CustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<PostSummaryDto> findPostSummaries(Pageable pageable) {
        List<PostSummaryDto> contents = queryFactory
                .select(Projections.constructor(PostSummaryDto.class,
                        postV2.postId,
                        postV2.username,
                        postV2.region,
                        postV2.views,
                        postV2.commentCount,
                        postV2.likeCount))
                .from(postV2)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(postV2.postId.desc())
                .fetch();

        Long count = queryFactory.select(postV2.count())
                .from(postV2)
                .fetchOne();

        return new PageImpl<>(contents, pageable, count);
    }

    @Override
    public List<PostV2> searchPostsV2(String keyword, String region, LocalDateTime cursor, int size) {
        QPostV2 post = postV2;
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
