package com.team15gijo.post.application.service.v2;

import com.team15gijo.post.domain.model.v2.PostV2;
import com.team15gijo.post.domain.repository.v2.PostRepositoryV2;
import com.team15gijo.post.presentation.dto.v2.PostFeedPageResponseDtoV2;
import com.team15gijo.post.presentation.dto.v2.PostFeedResponseDtoV2;
import com.team15gijo.post.presentation.dto.v2.PostSearchResponseDtoV2;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j(topic = "게시글 Internal Service")
@RequiredArgsConstructor
public class InternalPostServiceV2 {

    private final PostRepositoryV2 postRepository;

    public PostFeedPageResponseDtoV2 getPostsByRegion(String region, LocalDateTime cursor, int pageSize) {
        if (cursor == null) {
            cursor = LocalDateTime.now();
        }

        List<PostV2> posts = postRepository
                .findByRegionAndCreatedAtBeforeOrderByCreatedAtDesc(region, cursor, PageRequest.of(0, pageSize));

        List<PostFeedResponseDtoV2> postFeedResponsDtos = posts.stream()
                .map(PostFeedResponseDtoV2::from)
                .toList();

        return PostFeedPageResponseDtoV2.of(postFeedResponsDtos);
    }


    @Transactional(readOnly = true)
    public List<PostSearchResponseDtoV2> searchPost(String keyword, String region, LocalDateTime lastCreatedAt, int size) {
        List<PostV2> posts;

        if (lastCreatedAt == null) {
            lastCreatedAt = LocalDateTime.now();
        }
        log.info("게시글 검색 QueryDsl 시작");
        posts = postRepository.searchPostsV2(keyword, region, lastCreatedAt, size);
        log.info("게시글 검색 QueryDsl 종료");
        return posts.stream()
                .map(PostSearchResponseDtoV2::from)
                .toList();

    }
}
