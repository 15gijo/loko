package com.team15gijo.user.domain.model;

import com.team15gijo.common.model.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


@Entity
@Table(name = "p_users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE p_users SET deleted_at = now(), deleted_by = updated_by WHERE user_id = ?")
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "profile")
    private String profile;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private UserRegionEntity regionId;

    @Column(name = "region", nullable = false)
    private String region;

    @Builder
    public UserEntity(
            String username,
            String nickname,
            String email,
            String profile,
            UserStatus status,
            String region,
            UserRegionEntity regionId) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.profile = profile;
        this.status = status;
        this.region = region;
        this.regionId = regionId;
    }

    public void updateUsername(String newUsername) {
        this.username = newUsername;
    }

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void updateProfile(String newProfile) {
        this.profile = newProfile;
    }

    public void updateRegion(String newRegion) {
        this.region = newRegion;
    }

    public void updateEmail(String newEmail) {
        this.email = newEmail;
    }

    public void updateUserStatus(UserStatus newUserStatus) {
        this.status = newUserStatus;
    }
}

