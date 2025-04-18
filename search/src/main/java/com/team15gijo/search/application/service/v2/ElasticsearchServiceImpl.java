package com.team15gijo.search.application.service.v2;

import com.team15gijo.common.exception.CustomException;
import com.team15gijo.search.domain.exception.SearchDomainExceptionCode;
import com.team15gijo.search.domain.model.PostDocument;
import com.team15gijo.search.domain.repository.PostElasticsearchRepository;
import com.team15gijo.search.infrastructure.client.post.PostSearchResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "검색 Service")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ElasticsearchServiceImpl implements ElasticsearchService {

    private final PostElasticsearchRepository postElasticsearchRepository;

    @Override
    public String createElasticPost(PostSearchResponseDto responseDto) {
        PostDocument post = PostDocument.from(responseDto);
        postElasticsearchRepository.save(post);
        return "성공";
    }

    @Override
    public List<PostSearchResponseDto> searchPost(String keyword, String region) {
        List<PostDocument> posts = postElasticsearchRepository
                .findByPostContentContainingAndRegion(keyword, region);

        return posts.stream()
                .map(post -> new PostSearchResponseDto(
                        post.getPostId(),
                        post.getUsername(),
                        post.getPostContent(),
                        post.getHashtags(),
                        post.getRegion(),
                        post.getCreatedAt()
                ))
                .toList();
    }
}
