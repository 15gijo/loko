package com.team15gijo.post.domain.repository.v1;

import com.team15gijo.post.domain.model.v1.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HashtagRepository extends JpaRepository<Hashtag, UUID> {

    /**
     * 해시태그 이름으로 해시태그를 조회하기 위한 메서드
     * 예: SELECT * FROM p_hashtags WHERE hashtag_name = ?
     */
    Optional<Hashtag> findByHashtagName(String hashtagName);
}
