package com.team15gijo.post.application.service.v1;

import com.team15gijo.post.domain.model.Post;
import com.team15gijo.post.domain.repository.PostRepository;
import com.team15gijo.post.presentation.dto.v1.PostFeedPageResponseDto;
import com.team15gijo.post.presentation.dto.v1.PostFeedResponseDto;
import com.team15gijo.post.presentation.dto.v1.PostSearchResponseDto;
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
public class InternalPostService {

    private final PostRepository postRepository;

    public PostFeedPageResponseDto getPostsByRegion(String region, LocalDateTime cursor, int pageSize) {
        if (cursor == null) {
            cursor = LocalDateTime.now();
        }

        List<Post> posts = postRepository
                .findByRegionAndCreatedAtBeforeOrderByCreatedAtDesc(region, cursor, PageRequest.of(0, pageSize));

        List<PostFeedResponseDto> postFeedResponsDtos = posts.stream()
                .map(PostFeedResponseDto::from)
                .toList();

        return PostFeedPageResponseDto.of(postFeedResponsDtos);
    }


    @Transactional(readOnly = true)
    public List<PostSearchResponseDto> searchPost(String keyword, String region, LocalDateTime lastCreatedAt, int size) {
        List<Post> posts;

        if (lastCreatedAt == null) {
            lastCreatedAt = LocalDateTime.now();
        }
        log.info("게시글 검색 QueryDsl 시작");
        posts = postRepository.searchPosts(keyword, region, lastCreatedAt, size);
        log.info("게시글 검색 QueryDsl 종료");
        return posts.stream()
                .map(PostSearchResponseDto::from)
                .toList();

    }
}
