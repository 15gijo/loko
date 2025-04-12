package com.team15gijo.feed.application.service.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team15gijo.common.dto.ApiResponse;
import com.team15gijo.feed.domain.model.Feed;
import com.team15gijo.feed.domain.repository.FeedRepository;
import com.team15gijo.feed.presentation.dto.v1.PostFeedPageResponseDto;
import com.team15gijo.feed.presentation.dto.v1.PostFeedResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private static final String redisKeyPrefix = "feed:recent:region:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final FeedRepository feedRepository;

    /**
     * 동일 지역 정보를 가진 피드 최신순 조회 (DB)
     */
    public ApiResponse<PostFeedPageResponseDto> getRecentFeedsByRegion(LocalDateTime cursor, int pageSize, String region) {

        // TODO: 요청한 유저의 지역 가져오기 (token or user-service 요청)
//        String token = "";
//        String region = getRegionFromToken(token); //임시 지역

        if (cursor == null) {
            cursor = LocalDateTime.now();
        }

        List<Feed> feeds = feedRepository
                .findByRegionAndCreatedAtBeforeOrderByCreatedAtDesc(region, cursor, PageRequest.of(0, pageSize));

        List<PostFeedResponseDto> postFeedResponsDtos = feeds.stream()
                .map(PostFeedResponseDto::from)
                .toList();

        return ApiResponse.success("피드 조회 성공", PostFeedPageResponseDto.of(postFeedResponsDtos));

    }

    /**
     * 동일 지역 정보를 가진 피드 최신순 조회 (cache)
     */
    public ApiResponse<PostFeedPageResponseDto> getRecentCachedFeedByRegion(LocalDateTime cursor, int pageSize, String region) throws JsonProcessingException {
        // TODO: 요청한 유저의 지역 가져오기 (token or user-service 요청)
//        String token = "";
//        String region = getRegionFromToken(token); //임시 지역

        log.info("getRecentFeedCache called, region: {}", region);
        if(cursor == null) { //최초 요청만 캐싱
            // 캐시 조회
            String cacheKey = redisKeyPrefix + region;
            String cachedValue = (String) redisTemplate.opsForValue().get(cacheKey);

            // 캐시 hit
            if (cachedValue != null) {
                PostFeedPageResponseDto dto = objectMapper.readValue(cachedValue, PostFeedPageResponseDto.class);
                log.info("Recent feed cache hit: {}", dto);
                return ApiResponse.success("캐시에서 피드 조회 성공", dto);
            }

            // 캐시 hit x
            log.info("Recent feed cache miss");
            ApiResponse<PostFeedPageResponseDto> response = getRecentFeedsByRegion(cursor, pageSize, region);
            if (Objects.equals(response.getStatus(), "SUCCESS")) {
                String value = objectMapper.writeValueAsString(response.getData());
                redisTemplate.opsForValue().set(cacheKey, value, Duration.ofSeconds(60));
            }
            return response;
        } else {
            ApiResponse<PostFeedPageResponseDto> response = getRecentFeedsByRegion(cursor, pageSize, region);
            return response;
        }
    }

    public ApiResponse<PostFeedPageResponseDto> getPopularFeedsByRegion(Double cursor, int pageSize, String region) {
        // TODO: 요청한 유저의 지역 가져오기 (token or user-service 요청)
//        String token = "";
//        String region = getRegionFromToken(token); //임시 지역

        if (cursor == null) {
            cursor = Double.MAX_VALUE;
        }

        List<Feed> feeds = feedRepository
                .findByRegionAndPopularityScoreBeforeOrderByPopularityScoreDesc(region, cursor, PageRequest.of(0, pageSize));

        List<PostFeedResponseDto> postFeedResponsDtos = feeds.stream()
                .map(PostFeedResponseDto::from)
                .toList();

        return ApiResponse.success("피드 조회 성공", PostFeedPageResponseDto.of(postFeedResponsDtos));
    }


    private String getRegionFromToken(String token) {
        // TODO: token 혹은 header에서 값 가져오기
        return "송파구";
    }


}
