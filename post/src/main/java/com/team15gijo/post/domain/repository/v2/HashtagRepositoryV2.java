package com.team15gijo.post.domain.repository.v2;

import com.team15gijo.post.domain.model.v2.Hashtag;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagRepository extends JpaRepository<Hashtag, UUID> {

    /**
     * 해시태그 이름으로 해시태그를 조회하기 위한 메서드
     * 예: SELECT * FROM p_hashtags WHERE hashtag_name = ?
     */
    Optional<Hashtag> findByHashtagName(String hashtagName);
}
