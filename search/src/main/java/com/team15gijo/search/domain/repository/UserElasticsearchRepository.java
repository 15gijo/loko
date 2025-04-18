package com.team15gijo.search.domain.repository;

import com.team15gijo.search.domain.model.UserDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserElasticsearchRepository extends ElasticsearchRepository<UserDocument, Long> {
    List<UserDocument> findByNicknameContaining(String keyword);
}
