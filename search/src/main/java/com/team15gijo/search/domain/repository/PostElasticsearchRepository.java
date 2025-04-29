package com.team15gijo.search.domain.repository;

import com.team15gijo.search.domain.model.PostDocument;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostElasticsearchRepository extends ElasticsearchRepository<PostDocument, UUID> {
    @Query("""
            {
              "bool": {
                "must": [
                  {
                    "term": {
                      "region.keyword": "?2"
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
                "must_not": [
                  {
                    "term": {
                      "username.keyword": "?1"
                    }
                  }
                ],
                "filter": [
                  {
                    "range": {
                      "createdAt": {
                        "lt": "?3"
                      }
                    }
                  }
                ]
              }
            }
            """)
    List<PostDocument> searchPosts(String keyword, String nickname, String region, String createdAt, Pageable pageable);
}
