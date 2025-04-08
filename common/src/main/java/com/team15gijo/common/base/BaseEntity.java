package com.team15gijo.common.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreRemove;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 엔티티의 감사 기록 추상 클래스
 * <p>
 * 이 클래스는 모든 엔티티가 생성, 수정, 삭제 시의 정보를 기록하는 데 사용
 * 감사 필드에는 생성 일시, 생성자, 수정 일시, 수정자, 삭제 일시, 삭제자 등의 정보가 포함
 * 이 클래스는 `@MappedSuperclass`로 선언되어, 해당 엔티티를 상속받은 클래스에 대해 자동으로 감사 기록을 관리
 * <p>
 * 이 클래스는 AuditingEntityListener를 통해 Spring Data JPA의 감사 기능을 사용하여
 * 자동으로 생성 시간, 수정 시간 등의 메타데이터를 관리
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private Long createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    @Column(name = "deleted_by")
    private Long deletedBy;

    @PreRemove
    private void softDelete() {
        if (deletedAt == null) {
            deletedAt = LocalDateTime.now();
            if (deletedBy == null) {
                deletedBy = updatedBy;
            }
        }
    }

    //복구용 메소드
    //public void restore(){}
}
