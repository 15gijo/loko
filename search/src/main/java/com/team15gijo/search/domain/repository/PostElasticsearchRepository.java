package com.team15gijo.search.domain.repository;

import com.team15gijo.search.domain.model.PostDocument;
import java.util.List;
import java.util.UUID;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostElasticsearchRepository extends ElasticsearchRepository<PostDocument, UUID> {
    List<PostDocument> findByPostContentContainingAndRegion(String keyword, String region);
}
