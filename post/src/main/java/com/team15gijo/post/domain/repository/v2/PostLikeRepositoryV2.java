package com.team15gijo.post.domain.repository.v2;


import com.team15gijo.post.domain.model.v2.PostV2;
import com.team15gijo.post.domain.model.v2.PostLikeV2;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepositoryV2 extends JpaRepository<PostLikeV2, UUID> {
    Optional<PostLikeV2> findByPostAndUserId(PostV2 post, long userId);
    List<PostLikeV2> findByPost(PostV2 post);
    List<PostLikeV2> findByUserId(long userId);
}
