package com.team15gijo.search.domain.model;

import com.team15gijo.common.model.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "p_search_dlq")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE p_search_dlq SET deleted_at = now(), deleted_by = updated_by WHERE id = ?")
public class PostUpdateDlq extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String type;

    private String payload; // Kafka 메시지 JSON

    private String errorMessage;

    private boolean resolved; // 재처리 성공 여부

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }
}
