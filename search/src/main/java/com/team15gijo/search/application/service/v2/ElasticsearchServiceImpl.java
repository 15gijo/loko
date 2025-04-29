package com.team15gijo.search.application.service.v2;

import com.team15gijo.common.exception.CustomException;
import com.team15gijo.search.application.dto.v1.CursorResultDto;
import com.team15gijo.search.domain.exception.SearchDomainExceptionCode;
import com.team15gijo.search.domain.model.PostDocument;
import com.team15gijo.search.domain.model.UserDocument;
import com.team15gijo.search.domain.repository.PostElasticsearchRepository;
import com.team15gijo.search.domain.repository.UserElasticsearchRepository;
import com.team15gijo.search.application.dto.v2.PostSearchResponseDto;
import com.team15gijo.search.application.dto.v2.UserSearchResponseDto;
import com.team15gijo.search.infrastructure.kafka.dto.v1.PostElasticsearchRequestDto;
import com.team15gijo.search.infrastructure.kafka.dto.v1.UserElasticsearchRequestDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ElasticsearchServiceImpl implements ElasticsearchService {

    private final PostElasticsearchRepository postElasticsearchRepository;
    private final UserElasticsearchRepository userElasticsearchRepository;

    @Override
    public void createElasticPost(PostElasticsearchRequestDto requestDto) {
        try {
            PostDocument post = PostDocument.from(requestDto);
            postElasticsearchRepository.save(post);
            log.info("ElasticSearch에 게시글 저장 완료 - postId: {}", post.getPostId());
        } catch (Exception e) {
            log.error("ElasticSearch 게시글 저장 실패", e);
            throw new CustomException(SearchDomainExceptionCode.POST_SAVE_FAIL);
        }
    }

    @Override
    public CursorResultDto<PostSearchResponseDto> searchPost(
            String keyword,
            String nickname,
            String region,
            LocalDateTime lastCreatedAt,
            int size
    ) {
        try {
            if (lastCreatedAt == null) {
                lastCreatedAt = LocalDateTime.now();
            }

            String createdAtStr = lastCreatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
            Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));

            List<PostDocument> results = postElasticsearchRepository
                    .searchPosts(keyword, nickname, region, createdAtStr, pageable);

            List<PostSearchResponseDto> items = results.stream()
                    .map(PostSearchResponseDto::from)
                    .toList();

            boolean hasNext = items.size() == size;
            LocalDateTime nextCursor = hasNext ? items.get(items.size() - 1).getCreatedAt() : null;

            return CursorResultDto.<PostSearchResponseDto>builder()
                    .items(items)
                    .hasNext(hasNext)
                    .nextCursor(nextCursor)
                    .build();
        } catch (Exception e) {
            log.error("게시글 검색 실패", e);
            throw new CustomException(SearchDomainExceptionCode.POST_NOT_FOUND);
        }
    }

    @Override
    public void createElasticUser(UserElasticsearchRequestDto requestDto) {
        try {
            UserDocument user = UserDocument.from(requestDto);
            userElasticsearchRepository.save(user);
            log.info("ElasticSearch에 유저 저장 완료 - userId: {}", user.getUserId());
        } catch (Exception e) {
            log.error("ElasticSearch 유저 저장 실패", e);
            throw new CustomException(SearchDomainExceptionCode.USER_SAVE_FAIL);
        }

    }

    @Override
    public CursorResultDto<UserSearchResponseDto> searchUser(String keyword, Long userId,
            String region, Long lastUserId, int size) {
        try {
            if (lastUserId == null) {
                lastUserId = Long.MAX_VALUE;
            }

            Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "userId"));

            List<UserDocument> users = userElasticsearchRepository.searchUsers(
                    keyword, region, userId, lastUserId, pageable
            );

            List<UserSearchResponseDto> items = users.stream()
                    .map(UserSearchResponseDto::from)
                    .toList();

            boolean hasNext = items.size() == size;
            Long nextCursor = hasNext ? items.get(items.size() - 1).getUserId() : null;

            return CursorResultDto.<UserSearchResponseDto>builder()
                    .items(items)
                    .hasNext(hasNext)
                    .nextCursor(nextCursor)
                    .build();
        } catch (Exception e) {
            log.error("유저 검색 실패", e);
            throw new CustomException(SearchDomainExceptionCode.USER_NOT_FOUND);
        }
    }
}