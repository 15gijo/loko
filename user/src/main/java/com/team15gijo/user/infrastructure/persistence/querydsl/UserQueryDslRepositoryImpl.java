package com.team15gijo.user.infrastructure.persistence.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team15gijo.user.application.dto.v1.AdminUserSearchCommand;
import com.team15gijo.user.domain.model.QUserEntity;
import com.team15gijo.user.domain.model.UserEntity;
import com.team15gijo.user.presentation.dto.v1.AdminUserReadResponseDto;
import com.team15gijo.user.presentation.dto.v1.QAdminUserReadResponseDto;
import com.team15gijo.user.presentation.dto.v1.QUserReadsResponseDto;
import com.team15gijo.user.presentation.dto.v1.UserReadsResponseDto;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class UserQueryDslRepositoryImpl implements UserQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QUserEntity user = QUserEntity.userEntity;

    @Override
    public List<UserEntity> searchUsers(String keyword, Long userId, String nickname, String region,
            Long lastUserId, int size) {

        BooleanBuilder builder = new BooleanBuilder();

        // 본인 제외
        builder.and(user.id.ne(userId));
        builder.and(user.nickname.ne(nickname));

        // 지역 일치
        builder.and(user.region.eq(region));

        // 검색 키워드 포함 (username or nickname)
        if (StringUtils.hasText(keyword)) {
            String likeKeyword = "%" + keyword.toLowerCase() + "%";
            builder.and(
                    user.nickname.lower().like(likeKeyword)
                            .or(user.username.lower().like(likeKeyword))
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

    @Override
    public Page<AdminUserReadResponseDto> searchUsersForAdmin(
            AdminUserSearchCommand adminUserSearchCommand, Pageable pageable) {

        List<OrderSpecifier<?>> orderSpecifiers = getAllOrderSpecifiers(pageable);

        BooleanExpression[] predicates = new BooleanExpression[]{
                eqIfPresent(user.id, adminUserSearchCommand.getUserId()),
                containsIfPresent(user.username, adminUserSearchCommand.getUsername()),
                containsIfPresent(user.nickname, adminUserSearchCommand.getNickname()),
                containsIfPresent(user.email, adminUserSearchCommand.getEmail()),
                eqIfPresent(user.status, adminUserSearchCommand.getUserStatus()),
                containsIfPresent(user.region, adminUserSearchCommand.getRegion())
        };

        //cotent 쿼리
        List<AdminUserReadResponseDto> content = jpaQueryFactory
                .select(new QAdminUserReadResponseDto(
                        user.id,
                        user.username,
                        user.nickname,
                        user.email,
                        user.profile,
                        user.status.stringValue(),
                        user.region
                ))
                .from(user)
                .where(predicates)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //count 쿼리
        Long total = jpaQueryFactory
                .select(user.count())
                .from(user)
                .where(predicates)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public Page<UserReadsResponseDto> searchUsersForUser(String nickname, String username,
            String region, Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = getAllOrderSpecifiers(pageable);

        BooleanExpression[] predicates = new BooleanExpression[]{
                containsIfPresent(user.nickname, nickname),
                containsIfPresent(user.username, username),
                containsIfPresent(user.region, region)
        };

        List<UserReadsResponseDto> content = jpaQueryFactory
                .select(new QUserReadsResponseDto(
                        user.nickname,
                        user.username,
                        user.profile,
                        user.region
                ))
                .from(user)
                .where(predicates)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(user.count())
                .from()
                .where(predicates)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0); //total관련 예외?
    }


    private BooleanExpression containsIfPresent(StringPath path, String value) {
        return (value != null && !value.isBlank()) ? path.containsIgnoreCase(value) : null;
    }

    private BooleanExpression eqIfPresent(StringPath path, String value) {
        return (value != null && !value.isBlank()) ? path.eq(value) : null;
    }

    //long 용
    private BooleanExpression eqIfPresent(NumberPath<Long> path, Long value) {
        return (value != null) ? path.eq(value) : null;
    }

    //enum 용
    private <T extends Enum<T>> BooleanExpression eqIfPresent(EnumPath<T> path, T value) {
        return value != null ? path.eq(value) : null;
    }

    //sort 용
    private List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if (pageable.getSort() != null) {
            for (Sort.Order sortedOrder : pageable.getSort()) {
                Order direction = sortedOrder.isAscending() ? Order.ASC : Order.DESC;

                switch (sortedOrder.getProperty()) {
                    case "id":
                        orderSpecifiers.add(new OrderSpecifier<>(direction, user.id));
                        break;
                    case "username":
                        orderSpecifiers.add(new OrderSpecifier<>(direction, user.username));
                        break;
                    case "nickname":
                        orderSpecifiers.add(new OrderSpecifier<>(direction, user.nickname));
                        break;
                    case "email":
                        orderSpecifiers.add(new OrderSpecifier<>(direction, user.email));
                        break;
                    case "region":
                        orderSpecifiers.add(new OrderSpecifier<>(direction, user.region));
                        break;
                    case "status":
                        orderSpecifiers.add(new OrderSpecifier<>(direction, user.status));
                        break;
                    case "createdAt":
                        orderSpecifiers.add(new OrderSpecifier<>(direction, user.createdAt));
                        break;
                    case "updatedAt":
                        orderSpecifiers.add(new OrderSpecifier<>(direction, user.updatedAt));
                        break;
                    default:
                        break;

                }
            }
        }
        return orderSpecifiers;
    }
}
