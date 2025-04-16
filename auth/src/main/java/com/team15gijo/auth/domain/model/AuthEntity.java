package com.team15gijo.auth.domain.model;

import com.team15gijo.common.model.base.BaseEntity;
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

@Entity
@Table(name = "p_auth")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE p_auth SET deleted_at = now(), deleted_by = updated_by WHERE auth_id = ?")
public class AuthEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "auth_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "nickname", nullable = false, updatable = false)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "identifier", nullable = false, unique = true)
    private String identifier;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    private LoginType loginType;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Builder
    public AuthEntity(
            Long userId,
            String nickname,
            String password,
            String identifier,
            LoginType loginType,
            Role role) {
        this.userId = userId;
        this.nickname = nickname;
        this.password = password;
        this.identifier = identifier;
        this.loginType = loginType;
        this.role = role;
    }

    public void updateRole(Role userRole) {
        this.role = userRole;
    }

    public void updateIdentifier(String newIdentifier) {
        this.identifier = newIdentifier;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
