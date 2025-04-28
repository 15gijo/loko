package com.team15gijo.follow.infrastructure.persistence.querydsl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team15gijo.follow.application.dto.v2.AdminFollowSearchCommand;
import com.team15gijo.follow.application.dto.v2.FollowCursorCandidateResult;
import com.team15gijo.follow.domain.model.FollowEntity;
import com.team15gijo.follow.domain.model.QFollowEntity;
import com.team15gijo.follow.presentation.dto.response.v2.AdminFollowSearchResponseDto;
import com.team15gijo.follow.presentation.dto.response.v2.QAdminFollowSearchResponseDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FollowQueryDslRepositoryImpl implements FollowQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QFollowEntity follow = QFollowEntity.followEntity;


    @Override
    public Page<AdminFollowSearchResponseDto> searchAllFollowsForAdmin(
            AdminFollowSearchCommand adminFollowSearchCommand, Pageable validatePageable) {

        List<OrderSpecifier<?>> orderSpecifiers = getAllOrderSpecifiers(validatePageable);

        List<BooleanExpression> predicates = new ArrayList<>();
        if (adminFollowSearchCommand.getFollowId() != null) {
            predicates.add(follow.id.eq(adminFollowSearchCommand.getFollowId()));
        }
        if (adminFollowSearchCommand.getFollowerId() != null) {
            predicates.add(follow.followerId.eq(adminFollowSearchCommand.getFollowerId()));
        }
        if (adminFollowSearchCommand.getFolloweeId() != null) {
            predicates.add(follow.followeeId.eq(adminFollowSearchCommand.getFolloweeId()));
        }
        if (adminFollowSearchCommand.getFollowStatus() != null) {
            predicates.add(follow.followStatus.eq(adminFollowSearchCommand.getFollowStatus()));
        }

        List<AdminFollowSearchResponseDto> content = jpaQueryFactory
                .select(new QAdminFollowSearchResponseDto(
                        follow.id,
                        follow.followerId,
                        follow.followeeId,
                        follow.followStatus
                ))
                .from(follow)
                .where(predicates.toArray(new BooleanExpression[0]))
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(validatePageable.getOffset())
                .limit(validatePageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(follow.id.count())
                .from(follow)
                .where(predicates.toArray(new BooleanExpression[0]))
                .fetchOne();

        return new PageImpl<>(content, validatePageable, total != null ? total : 0);
    }

    @Override
    public FollowCursorCandidateResult find2HopCandidateUserIds(Long userId, Long lastUserId,
            Pageable validatePageable) {
        //내가 팔로우 한 유저 리스트
        List<Long> followingUserIds = jpaQueryFactory
                .select(follow.followeeId)
                .from(follow)
                .where(follow.followerId.eq(userId))
                .fetch();
        if (followingUserIds.isEmpty()) {
            return new FollowCursorCandidateResult(Collections.emptyList(), false);
        }

        //정렬 조건에 id 무조건 붙이기
        List<OrderSpecifier<?>> orderSpecifiers = getAllOrderSpecifiers(validatePageable);
        orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, follow.followeeId));

        //2hop 유저 리스트 가져오기
        List<Long> candidateIds = jpaQueryFactory
                .select(follow.followeeId)
                .from(follow)
                .where(
                        follow.followerId.in(followingUserIds),
                        follow.followeeId.ne(userId),
                        follow.followeeId.notIn(followingUserIds),
                        getlastUserId(lastUserId)
                )
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .limit(validatePageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = candidateIds.size() > validatePageable.getPageSize();

        List<Long> sliceUserIds = hasNext
                ? candidateIds.subList(0, validatePageable.getPageSize())
                : candidateIds;

        return new FollowCursorCandidateResult(sliceUserIds, hasNext);
    }

    private BooleanExpression getlastUserId(Long lastUserId) {
        return lastUserId != null ? follow.followeeId.gt(lastUserId) : null;
    }

    private List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable validatePageable) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        PathBuilder<FollowEntity> entityPathBuilder = new PathBuilder<>(FollowEntity.class,
                follow.getMetadata());

        if (validatePageable.getSort() != null) {
            for (Sort.Order order : validatePageable.getSort()) {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                orderSpecifiers.add(new OrderSpecifier<>(
                        direction,
                        entityPathBuilder.getComparable(order.getProperty(), Comparable.class)
                ));
            }
        }
        return orderSpecifiers;
    }


}
