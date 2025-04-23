package com.team15gijo.search.domain.repository;

import com.team15gijo.search.domain.model.UserDocument;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserElasticsearchRepository extends ElasticsearchRepository<UserDocument, Long> {
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
                { "match": { "nickname": "?0" } }
              ]
            }
          }
        ],
        "must_not": [
          {
            "bool": {
              "must": [
                { "term": { "userId": "?2" } },
                { "term": { "nickname": "?4" } }
              ]
            }
          }
        ],
        "filter": [
          {
            "range": {
              "userId": {
                "lt": "?3"
              }
            }
          }
        ]
      }
    }
    """)
    List<UserDocument> searchUsers(String keyword, String region, Long userId, Long lastUserId, String nickname, Pageable pageable);

}
