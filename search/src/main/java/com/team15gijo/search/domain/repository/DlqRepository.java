package com.team15gijo.search.domain.repository;

import com.team15gijo.search.domain.model.DlqEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DlqRepository extends JpaRepository<DlqEntity, Long> {
    List<DlqEntity> findByResolvedFalse();
}
