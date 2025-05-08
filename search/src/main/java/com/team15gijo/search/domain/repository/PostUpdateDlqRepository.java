package com.team15gijo.search.domain.repository;

import com.team15gijo.search.domain.model.PostUpdateDlq;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostUpdateDlqRepository extends JpaRepository<PostUpdateDlq, Long> {
    List<PostUpdateDlq> findByResolvedFalse();
}
