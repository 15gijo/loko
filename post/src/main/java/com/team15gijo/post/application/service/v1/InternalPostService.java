package com.team15gijo.post.application.service.v1;

import com.team15gijo.post.domain.model.Post;
import com.team15gijo.post.domain.repository.PostRepository;
import com.team15gijo.post.presentation.dto.v1.PostFeedPageResponseDto;
import com.team15gijo.post.presentation.dto.v1.PostFeedResponseDto;
import com.team15gijo.post.presentation.dto.v1.PostSearchResponseDto;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
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
    public List<PostSearchResponseDto> searchPost(String keyword, String region, UUID lastPostId, int size) {
        List<Post> posts;

        if (lastPostId == null) {
            posts = postRepository.findPostByKeyword(keyword, region, PageRequest.of(0, size));
        } else {
            posts = postRepository.findPostByKeywordAfter(keyword, region, lastPostId, PageRequest.of(0, size));
        }

        return posts.stream()
                .map(PostSearchResponseDto::from)
                .toList();

    }
}
