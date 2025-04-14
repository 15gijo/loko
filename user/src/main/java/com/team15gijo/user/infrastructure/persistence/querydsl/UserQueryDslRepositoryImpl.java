package com.team15gijo.user.infrastructure.persistence.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team15gijo.user.domain.model.QUserEntity;
import com.team15gijo.user.domain.model.UserEntity;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class UserQueryDslRepositoryImpl implements UserQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public UserQueryDslRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<UserEntity> searchUsers(String keyword, Long userId, String nickname, String region, Long lastUserId, int size) {
        QUserEntity user = QUserEntity.userEntity;

        BooleanBuilder builder = new BooleanBuilder();

        // 본인 제외
        builder.and(user.id.ne(userId));
        builder.and(user.nickName.ne(nickname));

        // 지역 일치
        builder.and(user.region.eq(region));

        // 검색 키워드 포함 (username or nickname)
        if (StringUtils.hasText(keyword)) {
            String likeKeyword = "%" + keyword.toLowerCase() + "%";
            builder.and(
                    user.nickName.lower().like(likeKeyword)
                            .or(user.userName.lower().like(likeKeyword))
            );
        }

        // 커서 페이징 조건
        if (lastUserId != null) {
            builder.and(user.id.gt(lastUserId));
        }

        return jpaQueryFactory
                .selectFrom(user)
                .where(builder)
                .orderBy(user.id.asc())
                .limit(size)
                .fetch();
    }
}
