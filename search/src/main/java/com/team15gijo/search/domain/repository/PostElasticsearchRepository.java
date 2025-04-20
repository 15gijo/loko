package com.team15gijo.search.domain.repository;

import com.team15gijo.search.domain.model.PostDocument;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostElasticsearchRepository extends ElasticsearchRepository<PostDocument, UUID> {
    List<PostDocument> findByPostContentContainingAndUsernameContainingAndRegion(String content, String username, String region);

    @Query("""
{
  "bool": {
    "must": [
      {
        "term": {
          "region.keyword": "?1"
        }
      },
      {
        "bool": {
          "should": [
            { "match": { "username": "?0" } },
            { "match": { "postContent": "?0" } },
            { "match": { "hashtags": "?0" } }
          ]
        }
      }
    ],
    "filter": [
      {
        "range": {
          "createdAt": {
            "lt": "?2"
          }
        }
      }
    ]
  }
}
""")
    List<PostDocument> searchPosts(String keyword, String region, String createdAt, Pageable pageable);
}
