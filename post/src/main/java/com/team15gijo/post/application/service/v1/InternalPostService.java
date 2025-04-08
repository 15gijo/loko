package com.team15gijo.post.application.service.v1;

import com.team15gijo.post.domain.model.Post;
import com.team15gijo.post.domain.repository.PostRepository;
import com.team15gijo.post.presentation.dto.v1.PostFeedPageResponseDto;
import com.team15gijo.post.presentation.dto.v1.PostFeedResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
}
