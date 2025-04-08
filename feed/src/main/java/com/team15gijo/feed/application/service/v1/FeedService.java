package com.team15gijo.feed.application.service.v1;

import com.team15gijo.feed.presentation.dto.v1.FeedResponseDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedService {
    /**
     * 동일 지역 정보를 가진 피드 최신순 조회
     * @return f
     */
    public List<FeedResponseDto> getRecentFeedBase(LocalDateTime cursor, int pageSize) {

        // TODO: 요청한 유저의 지역 가져오기 (token or user-service 요청)
        String region = "강남구"; //임시 지역

        // TODO: feign client 요청
        /**
         * 요청
         * 1. 지역
         * 2. cursor
         * 3. pageSize
         */
        return null;

    }
}
