package com.team15gijo.follow.domain.model;

import com.team15gijo.common.exception.CustomException;
import com.team15gijo.common.model.base.BaseEntity;
import com.team15gijo.follow.domain.exception.FollowDomainExceptionCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@Table(name = "p_follows")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE p_follows SET deleted_at = now(), deleted_by = updated_by WHERE follow_id = ?")
public class FollowEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "follow_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "follower_id", nullable = false)
    private Long followerId;

    @Column(name = "followee_id", nullable = false)
    private Long followeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "follow_status", nullable = false)
    private FollowStatus followStatus = FollowStatus.FOLLOW;

    @Builder
    public FollowEntity(Long followerId, Long followeeId, FollowStatus followStatus) {
        if (followStatus == null) {
            throw new CustomException(FollowDomainExceptionCode.FOLLOW_STATUS_NOT_FOUND);
        }
        this.followerId = followerId;
        this.followeeId = followeeId;
        this.followStatus = followStatus;
    }

    public void follow() {
        this.followStatus = FollowStatus.FOLLOW;
    }

    public void unfollow() {
        this.followStatus = FollowStatus.UNFOLLOW;
    }

    public void block() {
        this.followStatus = FollowStatus.BLOCKED;
    }

}
