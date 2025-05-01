package com.team15gijo.user.infrastructure.persistence.nativequery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserNativeRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public void bulkUpdateFollowerCounts(Map<Long, Long> followerMap) {
        if (followerMap == null || followerMap.isEmpty()) {
            return;
        }

        String caseStatement = followerMap.entrySet().stream()
                .map(e -> "WHEN " + e.getKey() + " THEN follower_count + " + e.getValue())
                .collect(Collectors.joining(" "));

        String userIdList = followerMap.keySet().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        String sql = String.format(
                """
                        UPDATE p_users
                        SET follower_count = CASE user_id %s END
                        WHERE user_id IN (%s);
                        """
                , caseStatement, userIdList
        );
        entityManager.createNativeQuery(sql).executeUpdate();

    }

    public void bulkUpdateFollowingCounts(Map<Long, Long> followingMap) {
        if (followingMap == null || followingMap.isEmpty()) {
            return;
        }

        String caseStatement = followingMap.entrySet().stream()
                .map(e -> "WHEN " + e.getKey() + " THEN following_count + " + e.getValue())
                .collect(Collectors.joining(" "));

        String userIdList = followingMap.keySet().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        String sql = String.format(
                """
                        UPDATE p_users
                        SET following_count = CASE user_id %s END
                        WHERE user_id IN (%s);
                        """
                , caseStatement, userIdList
        );
        entityManager.createNativeQuery(sql).executeUpdate();

    }
}
